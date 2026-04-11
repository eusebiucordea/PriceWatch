<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:pageTemplate pageTitle="My Alerts">

    <header class="mb-8">
        <h1 class="text-3xl font-extrabold text-slate-900">My Alerts</h1>
        <p class="text-slate-500 mt-2">Manage the products you want to receive email notifications for.</p>
    </header>

    <div class="bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden">
        <div class="p-6 border-b border-slate-100">
            <h3 class="text-lg font-bold text-slate-800">Active Notifications</h3>
        </div>

        <div class="overflow-x-auto">
            <table class="w-full text-left border-collapse">
                <thead class="bg-slate-50 text-slate-500 text-xs uppercase font-semibold">
                <tr>
                    <th class="px-6 py-4">Product Name</th>
                    <th class="px-6 py-4">Current Price</th>
                    <th class="px-6 py-4">Alert Threshold</th>
                    <th class="px-6 py-4 text-right">Actions</th>
                </tr>
                </thead>

                <tbody class="divide-y divide-slate-100 text-sm">

                    <%-- Iteram prin lista 'alertsList' trimisa din Servlet --%>
                <c:forEach var="item" items="${alertsList}">
                    <tr class="hover:bg-slate-50 transition-colors" id="alert-row-${item.productId}">
                        <td class="px-6 py-4 font-semibold text-slate-800 max-w-sm truncate" title="${item.productName}">
                                ${item.productName}
                        </td>
                        <td class="px-6 py-4">
                                <span class="inline-flex items-center px-2 py-0.5 rounded-md text-xs font-bold bg-green-50 text-green-600">
                                    ${item.currentPrice} RON
                                </span>
                        </td>
                        <td class="px-6 py-4">
                                <span class="inline-flex items-center px-2 py-0.5 rounded-md text-xs font-bold bg-blue-50 text-blue-600">
                                    Drops by ${item.targetDiscount}%
                                </span>
                        </td>
                        <td class="px-6 py-4 text-right">
                            <div class="flex justify-end items-center gap-2">
                                <button onclick="removeAlert(${item.productId}, '${pageContext.request.contextPath}')"
                                        title="Cancel Notification"
                                        class="px-3 py-1.5 text-xs font-bold text-red-500 bg-red-50 hover:bg-red-100 rounded-md transition-all flex items-center gap-1 border-none cursor-pointer">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="w-4 h-4"><path d="M18 6 6 18"/><path d="m6 6 12 12"/></svg>
                                    Remove Alert
                                </button>
                            </div>
                        </td>
                    </tr>
                </c:forEach>

                <c:if test="${empty alertsList}">
                    <tr>
                        <td colspan="4" class="px-6 py-12 text-center text-slate-400 italic">
                            You have no active price alerts. Go to your Dashboard to set them!
                        </td>
                    </tr>
                </c:if>

                </tbody>
            </table>
        </div>
    </div>

    <%-- scriptul pentru stergerea asincrona a alertei --%>
    <script>
        function removeAlert(productId, contextPath) {
            if(confirm('Are you sure you want to stop receiving emails for this product?')) {
                // facem un request POST catre servlet-ul creat anterior
                fetch(contextPath + '/RemoveAlerts', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: 'productId=' + productId
                })
                    .then(response => {
                        if (response.ok) {
                            // daca stergerea a avut succes, eliminam randul din tabel cu o tranzitie fina
                            const row = document.getElementById('alert-row-' + productId);
                            if (row) {
                                row.style.opacity = '0';
                                setTimeout(() => {
                                    row.remove();
                                    // putem da refresh la pagina
                                    // window.location.reload();
                                }, 300);
                            }
                        } else {
                            alert('Could not remove alert. Please try again.');
                        }
                    })
                    .catch(error => console.error('Error:', error));
            }
        }
    </script>

</t:pageTemplate>