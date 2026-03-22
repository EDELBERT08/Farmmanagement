document.addEventListener('DOMContentLoaded', () => {
    // Location Detection
    window.detectLocation = function () {
        const statusMsg = document.createElement('div');
        statusMsg.className = 'fixed top-4 right-4 bg-blue-600 text-white px-6 py-3 rounded-xl shadow-lg z-50 animate-bounce';
        statusMsg.innerText = 'Detecting location...';
        document.body.appendChild(statusMsg);

        if (!navigator.geolocation) {
            statusMsg.className = 'fixed top-4 right-4 bg-red-600 text-white px-6 py-3 rounded-xl shadow-lg z-50';
            statusMsg.innerText = 'Geolocation is not supported by your browser.';
            setTimeout(() => statusMsg.remove(), 3000);
            return;
        }

        navigator.geolocation.getCurrentPosition( // NOSONAR
            (position) => {
                const latInput = document.getElementById('latInput');
                const lonInput = document.getElementById('lonInput');
                if (latInput) latInput.value = position.coords.latitude.toFixed(6);
                if (lonInput) lonInput.value = position.coords.longitude.toFixed(6);

                statusMsg.className = 'fixed top-4 right-4 bg-green-600 text-white px-6 py-3 rounded-xl shadow-lg z-50';
                statusMsg.innerText = 'Location detected successfully!';
                setTimeout(() => statusMsg.remove(), 3000);
            },
            (error) => {
                console.error("Geolocation error:", error);
                let msg = "Error detecting location.";
                switch (error.code) {
                    case error.PERMISSION_DENIED: msg = "User denied the request for Geolocation."; break;
                    case error.POSITION_UNAVAILABLE: msg = "Location information is unavailable."; break;
                    case error.TIMEOUT: msg = "The request to get user location timed out."; break;
                }
                statusMsg.className = 'fixed top-4 right-4 bg-red-600 text-white px-6 py-3 rounded-xl shadow-lg z-50';
                statusMsg.innerText = msg;
                setTimeout(() => statusMsg.remove(), 4000);
            },
            { enableHighAccuracy: true, timeout: 10000, maximumAge: 0 }
        );
    };

    // City Search Logic
    const searchInput = document.getElementById('citySearch');
    const resultsBox = document.getElementById('searchResults');
    let timeoutId;

    if (searchInput && resultsBox) {
        searchInput.addEventListener('input', (e) => {
            clearTimeout(timeoutId);
            const query = e.target.value;

            if (query.length < 3) {
                resultsBox.classList.add('hidden');
                return;
            }

            timeoutId = setTimeout(() => {
                fetch(`https://geocoding-api.open-meteo.com/v1/search?name=${encodeURIComponent(query)}&count=5&language=en&format=json`)
                    .then(res => res.json())
                    .then(data => {
                        resultsBox.innerHTML = '';
                        if (data.results) {
                            resultsBox.classList.remove('hidden');
                            data.results.forEach(place => {
                                const div = document.createElement('div');
                                div.className = 'px-4 py-3 hover:bg-gray-50 cursor-pointer border-b border-gray-50 last:border-0 flex justify-between items-center group';
                                div.innerHTML = `
                                    <div>
                                        <span class="font-medium text-gray-800">${place.name}</span>
                                        <span class="text-xs text-gray-500 ml-2">${place.country || ''} ${place.admin1 ? ', ' + place.admin1 : ''}</span>
                                    </div>
                                    <span class="text-xs text-gray-400 group-hover:text-blue-500"><i class="fa-solid fa-arrow-right"></i></span>
                                `;
                                div.onclick = () => {
                                    const cityInput = document.getElementById('cityInput');
                                    const latInput = document.getElementById('latInput');
                                    const lonInput = document.getElementById('lonInput');

                                    if (cityInput) cityInput.value = place.name;
                                    if (latInput) latInput.value = place.latitude.toFixed(4);
                                    if (lonInput) lonInput.value = place.longitude.toFixed(4);

                                    resultsBox.classList.add('hidden');
                                    searchInput.value = ''; // Clear search
                                };
                                resultsBox.appendChild(div);
                            });
                        } else {
                            resultsBox.innerHTML = '<div class="px-4 py-3 text-sm text-gray-500">No results found</div>';
                            resultsBox.classList.remove('hidden');
                        }
                    })
                    .catch(err => console.error(err));
            }, 300); // 300ms Debounce
        });

        // Close results when clicking outside
        document.addEventListener('click', (e) => {
            if (!searchInput.contains(e.target) && !resultsBox.contains(e.target)) {
                resultsBox.classList.add('hidden');
            }
        });
    }
});
