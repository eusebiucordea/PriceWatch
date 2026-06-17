<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:pageTemplate pageTitle="Dashboard">

    <header class="mb-8">
        <h1 class="text-3xl font-extrabold text-slate-900">My Dashboard</h1>
        <p class="text-slate-500 mt-2">Welcome back! Here is an overview of the products you are tracking.</p>
    </header>

    <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">

        <div class="bg-white rounded-2xl shadow-sm border border-slate-100 p-6 flex items-center">
            <div>
                <p class="text-sm font-medium text-slate-500">Tracked Products</p>
                <p class="text-2xl font-bold text-slate-900">${not empty watchlist ? watchlist.size() : 0}</p>
            </div>
        </div>

        <div class="bg-white rounded-2xl shadow-sm border border-slate-100 p-6 flex items-center">
            <div>
                <p class="text-sm font-medium text-slate-500">Recently Dropped</p>
                <p class="text-2xl font-bold text-slate-900">${not empty recentlyDroppedCount ? recentlyDroppedCount : 0}</p>
            </div>
        </div>

        <div class="bg-white rounded-2xl shadow-sm border border-slate-100 p-6 flex items-center">
            <div>
                <p class="text-sm font-medium text-slate-500">At All-Time Low</p>
                <p class="text-2xl font-bold text-slate-900">${not empty allTimeLowCount ? allTimeLowCount : 0}</p>
            </div>
        </div>

    </div>

    <div class="bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden">

        <div class="p-6 border-b border-slate-100 flex flex-col md:flex-row md:items-center justify-between">
            <h3 class="text-lg font-bold text-slate-800">Watchlist</h3>
            <a href="${pageContext.request.contextPath}/AddProduct"
               class="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium flex items-center  hover:bg-blue-700 transition-colors">
                Add Product
            </a>
        </div>

        <div class="overflow-x-auto">
            <table class="w-full text-left border-collapse">
                <thead class="bg-slate-50 text-slate-500 text-xs uppercase font-semibold">
                <tr>
                    <th class="px-6 py-4">Product Name</th>
                    <th class="px-6 py-4">Current Price</th>
                    <th class="px-6 py-4">All-Time Low</th>
                    <th class="px-6 py-4">Status</th>
                    <th class="px-6 py-4 text-right">Actions</th>
                </tr>
                </thead>
                <tbody class="divide-y divide-slate-100 text-sm">

                <c:forEach var="product" items="${watchlist}">
                    <tr class="hover:bg-slate-50 transition-colors">
                        <td class="px-6 py-4 font-semibold text-slate-800 max-w-sm truncate" title="${product.name}">
                                ${product.name}
                        </td>
                        <td class="px-6 py-4">
                            <span class="inline-flex items-center px-2 py-0.5 rounded-md text-xs font-bold bg-green-50 text-green-600">
                                ${product.current_price} RON
                            </span>
                        </td>
                        <td class="px-6 py-4">
                            <span class="inline-flex items-center px-2 py-0.5 rounded-md text-xs font-bold bg-red-50 text-red-600">
                                ${product.all_time_low} RON
                            </span>
                        </td>
                        <td class="px-6 py-4">
                            <c:choose>
                                <c:when test="${product.current_price <= product.all_time_low}">
                                    <span class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-bold bg-green-100 text-green-800">
                                        <i data-lucide="trending-down" class="w-4 h-4 mr-1"></i> Lowest!
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <span class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-bold bg-slate-100 text-slate-600">
                                        <i data-lucide="activity" class="w-4 h-4 mr-1"></i> Tracking
                                    </span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="px-6 py-4 text-right">
                            <div class="flex justify-end items-center gap-2">

                                <button onclick="openAlertModal(${product.id}, '${product.name}', ${product.current_price})"
                                        title="Price Alert"
                                        class="p-1.5 text-slate-400 hover:text-yellow-500 hover:bg-yellow-50 rounded-md transition-all">
                                    <i data-lucide="bell" class="w-5 h-5">Alert</i>
                                </button>

                                <a href="${pageContext.request.contextPath}/UpdateScraping?id=${product.id}"
                                   class="p-1.5 text-slate-400 hover:text-blue-600 hover:bg-blue-50 rounded-md transition-all">
                                    <i data-lucide="clock" class="w-5 h-5"></i>
                                </a>

                                <a href="${pageContext.request.contextPath}/ViewDetails?id=${product.id}" title="View Details"
                                   class="p-1.5 text-slate-400 hover:text-blue-600 hover:bg-blue-50 rounded-md transition-all">
                                    <i data-lucide="info" class="w-5 h-5"></i>
                                </a>

                                <button onclick="removeFromDashboard(${product.id}, '${pageContext.request.contextPath}')" title="Remove from Watchlist"
                                        class="p-1.5 text-slate-400 hover:text-red-500 hover:bg-red-50 rounded-md transition-all">
                                    <i data-lucide="trash-2" class="w-5 h-5"></i>
                                </button>

                            </div>
                        </td>
                    </tr>
                </c:forEach>

                <c:if test="${empty watchlist}">
                    <tr>
                        <td colspan="5" class="px-6 py-12 text-center text-slate-400 italic">
                            Your watchlist is currently empty. Start tracking products from the catalog!
                        </td>
                    </tr>
                </c:if>

                </tbody>
            </table>
        </div>
    </div>

    <%-- pop up pentru setare alerte --%>
    <div id="alertModal" class="fixed inset-0 bg-slate-900 bg-opacity-50 hidden flex items-center justify-center z-50 transition-opacity">
        <div class="bg-white rounded-2xl shadow-xl p-6 w-full max-w-md transform transition-all scale-95 opacity-0" id="alertModalContent">
            <h3 class="text-xl font-bold text-slate-800 mb-2">Set Price Alert</h3>
            <p class="text-sm text-slate-500 mb-4" id="modalProductName">Product Name</p>

            <div class="mb-4">
                <label class="block text-sm font-medium text-slate-700 mb-1">Notify me when the price drops by (%):</label>
                <div class="flex items-center gap-4">
                    <input type="number" id="discountPercentage" min="1" max="99" value="10"
                           class="block w-24 px-3 py-2 border border-slate-200 rounded-lg bg-slate-50 focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                           oninput="calculateTargetPrice()">
                    <span class="text-slate-500 text-sm font-medium">
                Target Price: <span id="targetPriceDisplay" class="font-bold text-green-600">0 RON</span>
            </span>
                </div>
            </div>

            <input type="hidden" id="modalProductId">
            <input type="hidden" id="modalCurrentPrice">

            <div class="flex justify-end gap-3 mt-6">
                <button onclick="closeAlertModal()" class="px-4 py-2 text-sm font-medium text-slate-600 hover:bg-slate-100 rounded-lg transition-colors">Cancel</button>
                <button onclick="savePriceAlert('${pageContext.request.contextPath}')" class="px-4 py-2 text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 rounded-lg transition-colors">Save Alert</button>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/scripts/dashboard.js"></script>

</t:pageTemplate>