document.addEventListener('DOMContentLoaded', () => {
    // --- GREETING LOGIC ---
    const now = new Date();
    const hour = now.getHours();
    let greeting = "Good day";
    let icon = "fa-sun";

    if (hour >= 5 && hour < 12) {
        greeting = "Good morning";
        icon = "fa-cloud-sun";
    } else if (hour >= 12 && hour < 17) {
        greeting = "Good afternoon";
        icon = "fa-sun";
    } else if (hour >= 17 && hour < 21) {
        greeting = "Good evening";
        icon = "fa-moon";
    } else {
        greeting = "Good night";
        icon = "fa-moon";
    }

    const greetingText = document.getElementById('greetingText');
    const greetingIcon = document.getElementById('greetingIcon');
    const greetingSubtext = document.getElementById('greetingSubtext');

    if (greetingText) greetingText.textContent = greeting;
    if (greetingIcon) greetingIcon.className = `fa-solid ${icon}`;

    // Get current time display
    const timeStr = now.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });
    if (greetingSubtext) greetingSubtext.textContent = `It's ${timeStr} - Welcome back to your farm dashboard`;

    // --- WEATHER LOGIC ---
    const locationData = document.getElementById('location-data');
    let userLat = null;
    let userLon = null;
    let userCity = null;

    if (locationData && locationData.dataset.lat) {
        userLat = parseFloat(locationData.dataset.lat);
        userLon = parseFloat(locationData.dataset.lon);
        userCity = locationData.dataset.city;

        console.log('✅ Using saved location:', userCity, `(${userLat}, ${userLon})`);
        const dashLoc = document.getElementById('dashLocation');
        if (dashLoc) dashLoc.textContent = userCity || "My Farm";
        dashLoadWeather(userLat, userLon);
    }
    else if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(dashSuccess, dashError);
    } else {
        dashFallback();
    }

    function dashSuccess(position) {
        const lat = position.coords.latitude;
        const lon = position.coords.longitude;
        console.log('📍 Using browser geolocation:', `(${lat.toFixed(2)}, ${lon.toFixed(2)})`);
        const dashLoc = document.getElementById('dashLocation');
        if (dashLoc) dashLoc.textContent = lat.toFixed(2) + ", " + lon.toFixed(2);
        dashLoadWeather(lat, lon);
    }

    function dashError() {
        console.warn('⚠️ Geolocation failed, using fallback location');
        dashFallback();
    }

    function dashFallback() {
        console.log('🔄 Using default location: Nairobi');
        const dashLoc = document.getElementById('dashLocation');
        if (dashLoc) dashLoc.textContent = "Nairobi (Default)";
        dashLoadWeather(-1.2921, 36.8219);
    }

    function dashLoadWeather(lat, lon) {
        fetch(`/api/weather?lat=${lat}&lon=${lon}`)
            .then(response => response.json())
            .then(data => {
                safeSetText('dashTemp', Math.round(data.temp));
                safeSetText('dashHumidity', data.humidity);
                safeSetText('dashWind', data.windSpeed);
                safeSetText('dashUV', data.uvIndex);

                const code = data.weatherCode;
                console.log('🌤️ Weather code:', code);
                let desc = "Clear Sky";
                let iconClass = "fa-sun";

                // WMO Weather interpretation codes
                if (code === 0) { desc = "Clear Sky"; iconClass = "fa-sun"; }
                else if (code >= 1 && code <= 3) { desc = code === 1 ? "Mainly Clear" : (code === 2 ? "Partly Cloudy" : "Overcast"); iconClass = "fa-cloud-sun"; }
                else if (code >= 45 && code <= 48) { desc = "Foggy"; iconClass = "fa-smog"; }
                else if (code >= 51 && code <= 55) { desc = "Drizzle"; iconClass = "fa-cloud-rain"; }
                else if (code >= 56 && code <= 57) { desc = "Freezing Drizzle"; iconClass = "fa-snowflake"; }
                else if (code >= 61 && code <= 65) { desc = code === 61 ? "Light Rain" : (code === 63 ? "Moderate Rain" : "Heavy Rain"); iconClass = "fa-cloud-rain"; }
                else if (code >= 66 && code <= 67) { desc = "Freezing Rain"; iconClass = "fa-snowflake"; }
                else if (code >= 71 && code <= 75) { desc = "Snow"; iconClass = "fa-snowflake"; }
                else if (code === 77) { desc = "Snow Grains"; iconClass = "fa-snowflake"; }
                else if (code >= 80 && code <= 82) { desc = "Rain Showers"; iconClass = "fa-cloud-showers-heavy"; }
                else if (code >= 85 && code <= 86) { desc = "Snow Showers"; iconClass = "fa-snowflake"; }
                else if (code >= 95 && code <= 99) { desc = "Thunderstorm"; iconClass = "fa-cloud-bolt"; }

                safeSetText('dashCondition', desc);
                const dashIcon = document.getElementById('dashIcon');
                if (dashIcon) dashIcon.className = `fa-solid ${iconClass} text-6xl text-yellow-400`;
            })
            .catch(err => console.error('Dashboard weather error:', err));
    }

    function safeSetText(id, text) {
        const el = document.getElementById(id);
        if (el) el.textContent = text;
    }

    // --- CHART LOGIC ---
    const cropContainer = document.getElementById('crop-data');
    const labels = [];
    const data = [];

    if (cropContainer) {
        const items = cropContainer.querySelectorAll('.crop-data-item');
        items.forEach(item => {
            labels.push(item.dataset.label);
            data.push(parseInt(item.dataset.value));
        });
    }

    const ctx = document.getElementById('productionChart');
    if (ctx) {
        const bgColors = [
            'rgba(76, 175, 80, 0.8)',
            'rgba(255, 193, 7, 0.8)',
            'rgba(33, 150, 243, 0.8)',
            'rgba(156, 39, 176, 0.8)',
            'rgba(244, 67, 54, 0.8)'
        ];

        new Chart(ctx.getContext('2d'), {
            type: 'bar', // Can be 'doughnut' or 'bar'
            data: {
                labels: labels.length > 0 ? labels : ['No Crops'],
                datasets: [{
                    label: 'Crop Count by Type',
                    data: data.length > 0 ? data : [0],
                    backgroundColor: bgColors,
                    borderRadius: 6,
                    borderSkipped: false,
                    barThickness: 40,
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    },
                    tooltip: {
                        backgroundColor: '#1f2937',
                        padding: 12,
                        cornerRadius: 8,
                        titleFont: {
                            size: 14,
                            family: 'Inter'
                        },
                        bodyFont: {
                            size: 13,
                            family: 'Inter'
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: {
                            color: '#f3f4f6',
                            drawBorder: false,
                        },
                        ticks: {
                            font: {
                                family: 'Inter'
                            },
                            color: '#6b7280'
                        }
                    },
                    x: {
                        grid: {
                            display: false,
                            drawBorder: false,
                        },
                        ticks: {
                            font: {
                                family: 'Inter'
                            },
                            color: '#6b7280'
                        }
                    }
                }
            }
        });
    }
});
