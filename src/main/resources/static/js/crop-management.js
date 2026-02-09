document.addEventListener('DOMContentLoaded', () => {
    // Data Container
    const cropData = document.getElementById('crop-data');
    if (!cropData) return;

    const harvestStats = JSON.parse(cropData.dataset.harvestStats || '{}');
    const growthData = JSON.parse(cropData.dataset.growthData || '[]');
    const fieldMetrics = JSON.parse(cropData.dataset.fieldMetrics || '{}');

    // --- Harvest Summary Chart ---
    const harvestCanvas = document.getElementById('harvestSummaryChart');
    if (harvestCanvas) {
        new Chart(harvestCanvas.getContext('2d'), {
            type: 'doughnut',
            data: {
                labels: ['Wasted', 'Planted', 'Collected'],
                datasets: [{
                    data: [harvestStats.Wasted || 30, harvestStats.Planted || 200, harvestStats.Collected || 170],
                    backgroundColor: ['#3b82f6', '#22c55e', '#fbbf24'], // Blue, Green, Yellow
                    borderWidth: 0,
                    hoverOffset: 4
                }]
            },
            options: {
                cutout: '65%',
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'right',
                        labels: { usePointStyle: true, font: { family: 'Inter', size: 12 } }
                    }
                }
            }
        });
    }

    // --- Growth Chart ---
    const growthCanvas = document.getElementById('growthChart');
    if (growthCanvas) {
        new Chart(growthCanvas.getContext('2d'), {
            type: 'line',
            data: {
                labels: ['1day', '2day', '5day', '6day', '7day', '8day', '9day', '10day'],
                datasets: [{
                    label: 'Height (cm)',
                    data: growthData.length > 0 ? growthData : [1, 2, 2.5, 3, 3.5, 4, 4.5, 5],
                    borderColor: '#22c55e',
                    backgroundColor: (context) => {
                        const ctx = context.chart.ctx;
                        const gradient = ctx.createLinearGradient(0, 0, 0, 200);
                        gradient.addColorStop(0, 'rgba(34, 197, 94, 0.2)');
                        gradient.addColorStop(1, 'rgba(34, 197, 94, 0)');
                        return gradient;
                    },
                    fill: true,
                    tension: 0.4,
                    pointBackgroundColor: '#fff',
                    pointBorderColor: '#22c55e',
                    pointRadius: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: { beginAtZero: true, grid: { borderDash: [2, 4] } },
                    x: { grid: { display: false } }
                },
                plugins: { legend: { display: false } }
            }
        });
    }

    // --- Field Metrics Chart (Gauge/Doughnut) ---
    const fieldCanvas = document.getElementById('fieldMetricsChart');
    if (fieldCanvas) {
        // Mock proportions if not fully available, purely visualization based on metric presence?
        // Actually, fieldMetrics contains specific values like PH, Temp, WaterLevel.
        // The original chart had hardcoded mock data [60, 25, 15]. 
        // We will keep it visual for now as the values have different units.

        new Chart(fieldCanvas.getContext('2d'), {
            type: 'doughnut',
            data: {
                labels: ['Water', 'PH', 'Temp'],
                datasets: [{
                    data: [60, 25, 15], // Mock proportions for visualization
                    backgroundColor: ['#3b82f6', '#22c55e', '#e5e7eb'],
                    circumference: 180,
                    rotation: 270,
                    borderWidth: 0,
                    borderRadius: 10
                }]
            },
            options: {
                cutout: '80%',
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { display: false }, tooltip: { enabled: false } }
            }
        });
    }
});
