document.addEventListener('DOMContentLoaded', () => {
    // Field Data from hidden container
    const fieldData = document.getElementById('field-data');
    if (!fieldData) return;

    const fieldLat = parseFloat(fieldData.dataset.lat);
    const fieldLng = parseFloat(fieldData.dataset.lng);
    const fieldName = fieldData.dataset.name;

    // Map Initialization
    if (!isNaN(fieldLat) && !isNaN(fieldLng)) {
        const fieldMap = L.map('fieldMap').setView([fieldLat, fieldLng], 14);
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '© OpenStreetMap contributors'
        }).addTo(fieldMap);
        L.marker([fieldLat, fieldLng]).addTo(fieldMap)
            .bindPopup(fieldName || 'Field').openPopup();
    }

    // Charts Data
    const soilPHTrendData = JSON.parse(fieldData.dataset.soilPhTrend || '[]');
    const nutrients = JSON.parse(fieldData.dataset.nutrients || '{}');

    // Soil pH Trend Chart
    const soilChartEl = document.getElementById('soilPHChart');
    if (soilChartEl && soilPHTrendData.length > 0) {
        new Chart(soilChartEl, {
            type: 'line',
            data: {
                labels: soilPHTrendData.map(d => d.date),
                datasets: [{
                    label: 'pH Level',
                    data: soilPHTrendData.map(d => d.pH),
                    borderColor: '#10b981',
                    backgroundColor: 'rgba(16, 185, 129, 0.1)',
                    fill: true,
                    tension: 0.4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { display: false } },
                scales: {
                    y: { beginAtZero: true, max: 14 }
                }
            }
        });
    }

    // Nutrient Levels Chart
    const nutrientChartEl = document.getElementById('nutrientChart');
    if (nutrientChartEl) {
        new Chart(nutrientChartEl, {
            type: 'bar',
            data: {
                labels: ['Nitrogen (N)', 'Phosphorus (P)', 'Potassium (K)'],
                datasets: [{
                    data: [nutrients.nitrogen || 0, nutrients.phosphorus || 0, nutrients.potassium || 0],
                    backgroundColor: ['#3b82f6', '#f59e0b', '#10b981']
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { display: false } },
                scales: { y: { beginAtZero: true, title: { display: true, text: 'Percentage (%)' } } }
            }
        });
    }
});

// Tab Switching
function switchTab(tab) {
    document.querySelectorAll('.tab-content').forEach(el => el.classList.add('hidden'));
    document.querySelectorAll('.tab-button').forEach(el => {
        el.classList.remove('active', 'border-green-600', 'text-green-600');
        el.classList.add('border-transparent', 'text-gray-500');
    });

    const content = document.getElementById('content-' + tab);
    if (content) content.classList.remove('hidden');

    const activeTab = document.getElementById('tab-' + tab);
    if (activeTab) {
        activeTab.classList.add('active', 'border-green-600', 'text-green-600');
        activeTab.classList.remove('border-transparent', 'text-gray-500');
    }
}
window.switchTab = switchTab;
