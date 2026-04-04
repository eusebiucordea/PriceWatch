<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:pageTemplate pageTitle="Products">
    <header class="mb-8">
        <h1 class="text-3xl font-extrabold text-slate-900">All Tracked Products</h1>
    </header>

    <div class="bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden">
        <div class="p-6 border-b border-slate-100 flex flex-col md:flex-row md:items-center justify-between ">
            <h3 class="text-lg font-bold text-slate-800">Products</h3>

            <div class="relative flex-1 max-w-md w-full">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <i data-lucide="search" class="w-5 h-5 text-slate-400"></i>
                </div>
                <input type="text" id="searchInput" onkeyup="filterProducts()"
                       class="block w-full pl-10 pr-3 py-2 border border-slate-200 rounded-lg leading-5 bg-slate-50 placeholder-slate-400 focus:outline-none focus:bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 sm:text-sm transition-colors"
                       placeholder="Caută produs după nume...">
            </div>

            <a href="${pageContext.request.contextPath}/AddProduct"
               class="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium flex items-center  hover:bg-blue-700 transition-colors">
                 Add Product
            </a>
        </div>

        <div class="overflow-x-auto">
            <table class="w-full text-left border-collapse">
                <thead class="bg-slate-50 text-slate-500 text-xs uppercase font-semibold">
                <tr>
                    <th class="px-6 py-4">ID</th>
<%--                    <th class="px-6 py-4">Image</th>--%>
                    <th class="px-6 py-4">Product Name</th>
                    <th class="px-6 py-4">Current Price</th>
                    <th class="px-6 py-4">All-Time Low</th>
                    <th class="px-6 py-4 text-right">Actions</th>
                </tr>
                </thead>
                <tbody class="divide-y divide-slate-100">
                <c:forEach var="product" items="${products}">
                    <tr class="hover:bg-slate-50 transition-colors">
                        <td class="px-6 py-4 text-sm text-slate-500 font-mono">
                            #${product.id}
                        </td>
<%--                        <td class="px-6 py-4 text-sm text-slate-500 font-mono">--%>
<%--                                ${product.image_url}--%>
<%--                        </td>--%>
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

                        <td class="px-6 py-4 text-right">
                            <div class="flex justify-end items-center">
                                <button onclick="toggleWatchlist(${product.id}, this, '${pageContext.request.contextPath}')"
                                        title="Add to Watchlist"
                                        class="p-1.5 transition-all inline-block m-0">

                                    <c:choose>
                                        <c:when test="${savedProductIds.contains(product.id)}">
                                            <i data-lucide="heart" class="w-5 h-5 text-red-500" fill="currentColor"></i>
                                        </c:when>
                                        <c:otherwise>
                                            <i data-lucide="heart" class="w-5 h-5 text-slate-400" fill="none"></i>
                                        </c:otherwise>
                                    </c:choose>

                                </button>
                                <a href="${pageContext.request.contextPath}/ViewDetails?id=${product.id}" title="View Details" class="p-1.5 text-slate-400 hover:text-blue-600 hover:bg-blue-50 rounded-md transition-all">
                                    <i data-lucide="info" class="w-5 h-5">View Details</i>
                                </a>
                                <a href="${pageContext.request.contextPath}/EditProduct?id=${product.id}" title="Edit" class="p-1.5 text-slate-400 hover:text-blue-600 hover:bg-blue-50 rounded-md transition-all inline-block">
                                    <i data-lucide="edit" class="w-5 h-5">Edit</i>
                                </a>
                                <form action="${pageContext.request.contextPath}/DeleteProduct" method="POST" class="inline-block m-0" onsubmit="return confirm('Esti sigur ca vrei sa stergi acest produs? Tot istoricul de preturi va fi pierdut.');">
                                    <input type="hidden" name="id" value="${product.id}">
                                    <button type="submit" title="Delete Product" class="p-1.5 text-slate-400 hover:text-red-500 hover:bg-red-50 rounded-md transition-all">
                                        <i data-lucide="trash-2" class="w-5 h-5">Delete</i>
                                    </button>
                                </form>
                            </div>
                        </td>
                    </tr>
                </c:forEach>

                <c:if test="${empty products}">
                    <tr>
                        <td colspan="4" class="px-6 py-12 text-center text-slate-400 italic">
                            No products found in the database.
                        </td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>


    <script>
        //searchbar
        function filterProducts() {
            // preluam valoarea din input si o transformam in litere mici
            const input = document.getElementById("searchInput");
            const filter = input.value.toLowerCase();

            // selectam toate randurile din corpul tabelului
            const tbody = document.querySelector("table tbody");
            const rows = tbody.getElementsByTagName("tr");

            // trecem prin fiecare rand pentru a verifica numele produsului
            for (let i = 0; i < rows.length; i++) {
                // coloana cu numele produsului este a doua coloana index 1
                const nameColumn = rows[i].getElementsByTagName("td")[1];

                if (nameColumn) {
                    const productName = nameColumn.textContent || nameColumn.innerText;

                    // verificam daca numele contine textul cautat
                    if (productName.toLowerCase().indexOf(filter) > -1) {
                        rows[i].style.display = ""; // afiseaza randul
                    } else {
                        rows[i].style.display = "none"; // ascunde randul
                    }
                }
            }
        }
    </script>
    <script src="${pageContext.request.contextPath}/scripts/dashboard.js"></script>
</t:pageTemplate>