document.addEventListener('DOMContentLoaded', () => {
    // Time-based greeting
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

    const greetingText = document.getElementById('weatherGreetingText');
    const greetingIcon = document.getElementById('weatherGreetingIcon');

    if (greetingText) greetingText.textContent = greeting;
    if (greetingIcon) greetingIcon.className = `fa-solid ${icon}`;

    // Update date and time
    const dateEl = document.getElementById('currentDate');
    const timeEl = document.getElementById('currentTime');

    if (dateEl) dateEl.textContent = now.toLocaleDateString('en-GB', { weekday: 'short', day: 'numeric', month: 'short' });
    if (timeEl) timeEl.textContent = now.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' });

    const weatherData = document.getElementById('weather-data');

    if (weatherData && weatherData.dataset.lat) {
        const userLat = parseFloat(weatherData.dataset.lat);
        const userLon = parseFloat(weatherData.dataset.lon);
        const userCity = weatherData.dataset.city || "My Farm";

        console.log('✅ Using saved location:', userCity, `(${userLat}, ${userLon})`);
        safeSetText('locationName', userCity);
        safeSetText('currentLocation', userCity);
        loadWeather(userLat, userLon);
    }
    else if (navigator.geolocation) {
        const options = { enableHighAccuracy: true, timeout: 5000, maximumAge: 0 };
        navigator.geolocation.getCurrentPosition(success, error, options); // NOSONAR
    } else {
        console.warn("Geolocation not supported.");
        fallback();
    }

    function success(position) {
        const lat = position.coords.latitude;
        const lon = position.coords.longitude;
        console.log('📍 Using browser geolocation:', `(${lat.toFixed(2)}, ${lon.toFixed(2)})`);
        safeSetText('locationName', lat.toFixed(2) + ", " + lon.toFixed(2));
        loadWeather(lat, lon);
    }

    function error() {
        console.warn("Unable to retrieve location.");
        fallback();
    }

    function fallback() {
        const fallbackCity = "Nairobi (Default)";
        console.log('🔄 Using default location: Nairobi');
        safeSetText('locationName', fallbackCity);
        safeSetText('currentLocation', "Nairobi");
        loadWeather(-1.2921, 36.8219);
    }

    function loadWeather(lat, lon) {
        fetch(`/api/weather?lat=${lat}&lon=${lon}`)
            .then(response => response.json())
            .then(data => updateUI(data))
            .catch(err => console.error('Error fetching weather:', err));
    }

    function safeSetText(id, text) {
        const el = document.getElementById(id);
        if (el) el.textContent = text;
    }

    function updateUI(data) {
        // Update Current
        safeSetText('currentTemp', Math.round(data.temp) + '°');
        safeSetText('windSpeed', data.windSpeed);
        safeSetText('humidity', data.humidity);
        safeSetText('uvIndex', data.uvIndex);
        safeSetText('sunrise', data.sunrise);
        safeSetText('sunset', data.sunset);
        safeSetText('feelsLike', Math.round(data.temp + 2)); // Mock feels like

        // WMO Weather interpretation codes (Open-Meteo standard)
        const code = data.weatherCode;
        console.log('🌤️ Weather code:', code);
        let desc = "Clear Sky";
        let icon = "fa-sun";

        if (code === 0) { desc = "Clear Sky"; icon = "fa-sun"; }
        else if (code >= 1 && code <= 3) { desc = code === 1 ? "Mainly Clear" : (code === 2 ? "Partly Cloudy" : "Overcast"); icon = "fa-cloud-sun"; }
        else if (code >= 45 && code <= 48) { desc = "Foggy"; icon = "fa-smog"; }
        else if (code >= 51 && code <= 55) { desc = "Drizzle"; icon = "fa-cloud-rain"; }
        else if (code >= 56 && code <= 57) { desc = "Freezing Drizzle"; icon = "fa-snowflake"; }
        else if (code >= 61 && code <= 65) { desc = code === 61 ? "Light Rain" : (code === 63 ? "Moderate Rain" : "Heavy Rain"); icon = "fa-cloud-rain"; }
        else if (code >= 66 && code <= 67) { desc = "Freezing Rain"; icon = "fa-snowflake"; }
        else if (code >= 71 && code <= 75) { desc = "Snow"; icon = "fa-snowflake"; }
        else if (code === 77) { desc = "Snow Grains"; icon = "fa-snowflake"; }
        else if (code >= 80 && code <= 82) { desc = "Rain Showers"; icon = "fa-cloud-showers-heavy"; }
        else if (code >= 85 && code <= 86) { desc = "Snow Showers"; icon = "fa-snowflake"; }
        else if (code >= 95 && code <= 99) { desc = "Thunderstorm"; icon = "fa-cloud-bolt"; }

        safeSetText('weatherDesc', desc);

        // Update Forecast
        const forecastContainer = document.getElementById('forecastContainer');
        if (forecastContainer) {
            forecastContainer.innerHTML = '';

            const daily = data.forecast;
            if (daily && daily.time) {
                daily.time.forEach((dateStr, index) => {
                    const date = new Date(dateStr);
                    const dayName = date.toLocaleDateString('en-US', { weekday: 'short' });
                    const maxTemp = Math.round(daily.temperature_2m_max[index]);

                    const div = document.createElement('div');
                    div.className = 'w-24 flex-shrink-0 bg-white border border-gray-100 rounded-2xl p-4 text-center hover:shadow-md transition-shadow cursor-default';
                    div.innerHTML = `
                        <p class="text-sm font-bold text-gray-700 mb-2">${dayName}</p>
                        <div class="text-2xl text-blue-400 mb-2"><i class="fa-solid fa-cloud-sun"></i></div>
                        <p class="font-bold text-gray-800">${maxTemp}°</p>
                    `;
                    forecastContainer.appendChild(div);
                });
            }
        }
    }
});
