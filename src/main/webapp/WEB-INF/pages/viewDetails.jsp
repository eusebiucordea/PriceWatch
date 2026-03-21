<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:pageTemplate pageTitle="View Details">

    <style>
        .glass-card { background: rgba(255, 255, 255, 0.9); backdrop-filter: blur(10px); border: 1px solid rgba(229, 231, 235, 0.5); border-radius: 1rem; }
        .tab-active { color: #2563eb; border-bottom: 2px solid #2563eb; }
        .price-card { transition: transform 0.2s; }
        .price-card:hover { transform: translateY(-2px); }
        .fade-in { animation: fadeIn 0.3s ease-out; }
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(10px); }
            to { opacity: 1; transform: translateY(0); }
        }
    </style>

    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">

    <main class="max-w-6xl mx-auto px-4 mt-8 pb-12">

        <a href="${pageContext.request.contextPath}/Products" class="mb-6 inline-flex items-center text-sm font-medium text-gray-500 hover:text-blue-600 transition-colors">
            <i class="fas fa-arrow-left mr-2"></i> Back to list
        </a>

        <div id="main-view">
            <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">

                <div class="lg:col-span-1 space-y-6">
                    <div class="glass-card p-6 shadow-sm flex flex-col items-center">
                        <div class="w-full aspect-square bg-white rounded-xl mb-6 flex items-center justify-center p-4 border overflow-hidden">
                            <img src="${not empty product.image_url ? product.image_url : 'https://via.placeholder.com/400x400?text=No+Image'}" alt="${product.name}" class="max-w-full h-auto object-contain">
                        </div>
                        <h1 class="text-2xl font-bold text-center mb-2">${product.name}</h1>
                        <p class="text-sm text-gray-500 font-medium uppercase tracking-wider">Tracked Product</p>
                    </div>

                    <div class="glass-card p-6 shadow-sm">
                        <h3 class="font-bold text-gray-800 mb-4 flex items-center">
                            <i class="fas fa-chart-line mr-2 text-green-500"></i> Price Records
                        </h3>
                        <div class="space-y-4">
                            <div class="flex justify-between items-center">
                                <span class="text-sm text-gray-500">All-Time Global Low</span>
                                <span class="text-lg font-bold text-green-600" id="global-low-price">Calculating...</span>
                            </div>
                            <div class="flex justify-between items-center">
                                <span class="text-sm text-gray-500">Best Store</span>
                                <span class="text-sm font-bold text-blue-600" id="global-low-store">-</span>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="lg:col-span-2 space-y-8">

                    <div class="bg-white p-1 rounded-xl shadow-sm inline-flex mb-2">
                        <button onclick="switchDetailTab('prices')" id="tab-prices" class="px-6 py-2 text-sm font-bold rounded-lg transition-all tab-active">Compare Prices</button>
                        <button onclick="switchDetailTab('charts')" id="tab-charts" class="px-6 py-2 text-sm font-bold rounded-lg text-gray-500 transition-all hover:text-gray-700">Price Evolution</button>
                    </div>

                    <div id="view-prices" class="fade-in space-y-4">
                        <div class="grid grid-cols-1 gap-3" id="stores-list">
                        </div>
                    </div>

                    <div id="view-charts" class="hidden fade-in space-y-6">
                        <div class="glass-card p-6 shadow-sm">
                            <div class="flex justify-between items-center mb-6">
                                <h4 class="font-bold text-gray-800">Price History</h4>
                                <div class="flex space-x-2">
                                    <select id="compare-selector" class="text-xs border rounded-lg px-2 py-1 outline-none focus:ring-1 focus:ring-blue-500" onchange="updateChart()">
                                        <option value="all">All stores</option>
                                    </select>
                                </div>
                            </div>
                            <div class="h-[350px] w-full">
                                <canvas id="priceChart"></canvas>
                            </div>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    </main>

    <script>
        const storeColors = {
            'emag': '#2563eb',
            'altex': '#ef4444',
            'mediagalaxy': '#ea580c',
            'pc garage': '#16a34a',
            'flanco': '#ca8a04',
            'default': '#6b7280'
        };

        const productName = "${product.name}";
        const storeData = [
            <c:forEach items="${product.links}" var="link" varStatus="status">
            {
                name: '${link.storeName}',
                url: '${link.url}',
                currentPrice: ${link.lastPrice},
                color: storeColors['${link.storeName}'.toLowerCase()] || storeColors['default'],
                history: [
                    <c:forEach items="${link.priceHistories}" var="hist" varStatus="hStatus">
                    { date: '${hist.recordedAt}', price: ${hist.price} }${!hStatus.last ? ',' : ''}
                    </c:forEach>
                ]
            }${!status.last ? ',' : ''}
            </c:forEach>
        ];
    </script>

    <script src="${pageContext.request.contextPath}/scripts/view-details.js"></script>

</t:pageTemplate>