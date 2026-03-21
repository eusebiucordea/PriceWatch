let priceChart = null;
let globalMinPrice = Infinity;
let globalMinStore = '';

function init() {
    processData();
    renderStorePrices();
    populateChartDropdown();
    initChart();
}

function processData() {
    storeData.forEach(store => {
        store.minPrice = store.currentPrice;
        store.history.forEach(h => {
            if (h.price < store.minPrice) store.minPrice = h.price;
        });

        if (store.minPrice < globalMinPrice && store.minPrice > 0) {
            globalMinPrice = store.minPrice;
            globalMinStore = store.name;
        }
    });

    if (globalMinPrice !== Infinity) {
        document.getElementById('global-low-price').innerText = globalMinPrice.toLocaleString('ro-RO') + ' RON';
        document.getElementById('global-low-store').innerText = globalMinStore;
    } else {
        document.getElementById('global-low-price').innerText = "N/A";
    }
}

function renderStorePrices() {
    const list = document.getElementById('stores-list');
    if (storeData.length === 0) {
        list.innerHTML = '<div class="text-center text-gray-500 py-8">Nu există date despre magazine pentru acest produs.</div>';
        return;
    }

    list.innerHTML = storeData.map(store => `
        <div class="price-card bg-white p-5 rounded-xl border flex flex-col md:flex-row md:items-center justify-between shadow-sm">
            <div class="mb-3 md:mb-0">
                <a href="${store.url}" target="_blank" class="text-lg font-bold text-blue-600 hover:underline">
                    ${store.name} <i class="fas fa-external-link-alt text-[10px] ml-1"></i>
                </a>
                <div class="text-xs text-gray-400 mt-1 font-medium">Click to visit store</div>
            </div>
            <div class="flex flex-col md:items-end">
                <div class="text-xl font-extrabold text-gray-900">${store.currentPrice.toLocaleString('ro-RO')} RON</div>
                <div class="text-xs font-medium text-gray-400">Lowest Recorded: <span class="text-green-600">${store.minPrice.toLocaleString('ro-RO')} RON</span></div>
            </div>
        </div>
    `).join('');
}

function populateChartDropdown() {
    const selector = document.getElementById('compare-selector');
    storeData.forEach(store => {
        const option = document.createElement('option');
        option.value = store.name.toLowerCase();
        option.text = store.name;
        selector.appendChild(option);
    });
}

function switchDetailTab(tab) {
    document.getElementById('view-prices').classList.toggle('hidden', tab !== 'prices');
    document.getElementById('view-charts').classList.toggle('hidden', tab !== 'charts');

    document.getElementById('tab-prices').className = tab === 'prices' ? 'px-6 py-2 text-sm font-bold rounded-lg transition-all tab-active' : 'px-6 py-2 text-sm font-bold rounded-lg text-gray-500 transition-all hover:text-gray-700';
    document.getElementById('tab-charts').className = tab === 'charts' ? 'px-6 py-2 text-sm font-bold rounded-lg transition-all tab-active' : 'px-6 py-2 text-sm font-bold rounded-lg text-gray-500 transition-all hover:text-gray-700';
}

function initChart() {
    if (storeData.length === 0) return;

    const ctx = document.getElementById('priceChart').getContext('2d');

    let allDates = new Set();
    storeData.forEach(store => store.history.forEach(h => {
        if(h.date) allDates.add(h.date.split('T')[0]);
    }));

    const labels = Array.from(allDates).sort();

    const datasets = storeData.map(store => {
        const dataPoints = labels.map(dateLabel => {
            const record = store.history.find(h => h.date && h.date.startsWith(dateLabel));
            return record ? record.price : null;
        });

        return {
            label: store.name,
            data: dataPoints,
            borderColor: store.color,
            backgroundColor: store.color + '22',
            tension: 0.3,
            fill: false,
            borderWidth: 2,
            pointRadius: 4,
            spanGaps: true
        };
    });

    priceChart = new Chart(ctx, {
        type: 'line',
        data: { labels, datasets },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: { mode: 'index', intersect: false },
            plugins: {
                legend: { position: 'bottom', labels: { boxWidth: 10, usePointStyle: true, font: { size: 11 } } }
            },
            scales: {
                y: {
                    ticks: { font: { size: 10 }, callback: v => v + ' RON' },
                    grid: { color: '#f3f4f6' }
                },
                x: { grid: { display: false }, ticks: { font: { size: 10 } } }
            }
        }
    });
}

function updateChart() {
    const filter = document.getElementById('compare-selector').value;
    priceChart.data.datasets.forEach(ds => {
        ds.hidden = filter === 'all' ? false : ds.label.toLowerCase() !== filter;
    });
    priceChart.update();
}

// Pornim totul când fereastra s-a încărcat
window.onload = init;