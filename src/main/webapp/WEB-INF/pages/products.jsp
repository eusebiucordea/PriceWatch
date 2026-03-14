<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:pageTemplate pageTitle="Product Management">
    <header class="mb-8">
        <h1 class="text-3xl font-extrabold text-slate-900">Product Management</h1>
    </header>

    <div class="bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden">
        <div class="p-6 border-b border-slate-100 flex flex-col md:flex-row md:items-center justify-between ">
            <h3 class="text-lg font-bold text-slate-800">Tracked Products</h3>
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
                                <a title="View Details" class="p-1.5 text-slate-400 hover:text-blue-600 hover:bg-blue-50 rounded-md transition-all">
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
</t:pageTemplate>