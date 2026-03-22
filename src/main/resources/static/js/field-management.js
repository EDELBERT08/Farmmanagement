document.addEventListener('DOMContentLoaded', () => {
    // Main overview map
    const map = L.map('map').setView([-1.2864, 36.8172], 11);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors',
        maxZoom: 19
    }).addTo(map);

    // Load fields from DOM data attributes
    const farmsContainer = document.getElementById('farms-data');
    if (farmsContainer) {
        const farmItems = farmsContainer.querySelectorAll('.farm-data-item');
        farmItems.forEach(item => {
            const boundary = item.dataset.boundary;
            const name = item.dataset.name;

            if (boundary && boundary.length > 0) {
                try {
                    const coords = JSON.parse(boundary);
                    const polygon = L.polygon(coords, {
                        color: '#10b981',
                        fillColor: '#34d399',
                        fillOpacity: 0.3
                    }).addTo(map);
                    polygon.bindPopup(`<strong>${name}</strong>`);
                } catch (e) {
                    console.error('Error parsing boundary coordinates:', e);
                }
            }
        });
    }

    // Drawing map and variables
    let drawingMap = null;
    let drawnItems = null;
    let drawControl = null;
    let currentPolygon = null;

    // Open modal - make it globally accessible
    window.showAddModal = function () {
        console.log('showAddModal called');
        const modal = document.getElementById('addFieldModal');
        if (!modal) {
            console.error('Modal element not found!');
            return;
        }
        modal.classList.remove('hidden');
        const form = document.getElementById('addFieldForm');
        if (form) form.reset();

        const areaDisplay = document.getElementById('areaDisplay');
        if (areaDisplay) areaDisplay.classList.add('hidden');

        // Initialize drawing map
        setTimeout(() => {
            if (!drawingMap) {
                initDrawingMap();
            } else {
                drawingMap.invalidateSize();
            }
        }, 100);
    }

    // Close modal - make it globally accessible
    window.closeAddModal = function () {
        console.log('closeAddModal called');
        const modal = document.getElementById('addFieldModal');
        if (modal) modal.classList.add('hidden');
    }

    // Initialize drawing map with polygon tools
    function initDrawingMap() {
        if (document.getElementById('drawingMap')) {
            drawingMap = L.map('drawingMap').setView([-1.2864, 36.8172], 13);

            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '© OpenStreetMap',
                maxZoom: 19
            }).addTo(drawingMap);

            drawnItems = new L.FeatureGroup();
            drawingMap.addLayer(drawnItems);

            // Drawing controls
            drawControl = new L.Control.Draw({
                draw: {
                    polygon: {
                        shapeOptions: {
                            color: '#10b981',
                            fillColor: '#34d399',
                            fillOpacity: 0.4
                        },
                        showArea: true
                    },
                    polyline: false,
                    rectangle: {
                        shapeOptions: {
                            color: '#10b981',
                            fillOpacity: 0.4
                        }
                    },
                    circle: false,
                    marker: false,
                    circlemarker: false
                },
                edit: {
                    featureGroup: drawnItems,
                    remove: true
                }
            });
            drawingMap.addControl(drawControl);

            // Handle polygon creation
            drawingMap.on('draw:created', function (e) {
                // Remove previous polygon
                drawnItems.clearLayers();

                const layer = e.layer;
                drawnItems.addLayer(layer);
                currentPolygon = layer;

                // Get coordinates
                const latlngs = layer.getLatLngs()[0];
                const coords = latlngs.map(ll => [ll.lat, ll.lng]);

                // Calculate area in acres (Leaflet gives square meters)
                const areaMeters = L.GeometryUtil.geodesicArea(latlngs);
                const areaAcres = (areaMeters * 0.000247105).toFixed(2);

                // Get center for lat/lng
                const center = layer.getBounds().getCenter();

                // Update form fields
                safeSetValue('fieldLatitude', center.lat.toFixed(6));
                safeSetValue('fieldLongitude', center.lng.toFixed(6));
                safeSetValue('fieldBoundary', JSON.stringify(coords));
                safeSetValue('fieldAreaSize', areaAcres);

                // Show area
                const calcArea = document.getElementById('calculatedArea');
                if (calcArea) calcArea.textContent = `Area: ${areaAcres} acres`;

                const areaDisplay = document.getElementById('areaDisplay');
                if (areaDisplay) areaDisplay.classList.remove('hidden');
            });

            // Handle polygon deletion
            drawingMap.on('draw:deleted', function () {
                currentPolygon = null;
                const areaDisplay = document.getElementById('areaDisplay');
                if (areaDisplay) areaDisplay.classList.add('hidden');
                safeSetValue('fieldBoundary', '');
                safeSetValue('fieldAreaSize', '');
            });
        }
    }

    function safeSetValue(id, value) {
        const el = document.getElementById(id);
        if (el) el.value = value;
    }

    // Close modal on escape
    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') {
            closeAddModal();
        }
    });
});
