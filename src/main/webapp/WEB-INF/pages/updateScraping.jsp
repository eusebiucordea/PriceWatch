<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:pageTemplate pageTitle="Update">
    <div class="max-w-2xl mx-auto bg-white p-8 rounded-2xl shadow-sm border border-slate-100 mt-10">
        <h2 class="text-2xl font-bold mb-2 text-slate-800">${product.name}</h2>
        <p class="text-slate-500 mb-6">Customize how often we check this product for price drops.</p>

            <%-- verificam dacă avem parametrul de eroare in URL --%>
        <c:if test="${param.error eq 'min_limit'}">
            <div class="mb-6 p-4 bg-red-50 border-l-4 border-red-500 text-red-700 rounded-r-lg flex items-center">
                <i class="fas fa-exclamation-circle mr-3"></i>
                <div>
                    <p class="font-bold">Invalid Interval</p>
                    <p class="text-sm">To prevent server overload, the minimum interval allowed is 30 minutes.</p>
                </div>
            </div>
        </c:if>

        <form action="UpdateScraping" method="POST" class="space-y-4">
            <input type="hidden" name="id" value="${product.id}">

            <div class="pt-4 border-t border-slate-100">
                <h3 class="text-sm font-semibold text-slate-800 mb-3">Edit Settings</h3>
                <div class="grid grid-cols-2 gap-4">
                    <div>
                        <label class="block text-sm font-medium text-slate-700 mb-1">Interval (Hours)</label>
                        <input type="number" min="0" name="interval_hours"
                               value="${not empty product.intervalHours ? product.intervalHours : 12}" required
                               class="w-full px-4 py-2 border border-slate-200 rounded-lg focus:ring-2 focus:ring-blue-600 outline-none">
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-slate-700 mb-1">Interval (Minutes)</label>
                        <input type="number" min="0" max="59" name="interval_minutes"
                               value="${not empty product.intervalMinutes ? product.intervalMinutes : 0}" required
                               class="w-full px-4 py-2 border border-slate-200 rounded-lg focus:ring-2 focus:ring-blue-600 outline-none">
                    </div>
                </div>

                <div class="mt-4 p-3 bg-amber-50 rounded-lg border border-amber-100">
                    <p class="text-xs text-amber-700">
                        <i class="fas fa-exclamation-triangle mr-1"></i>
                        <strong>Note:</strong> The system always uses the shortest interval requested by all users tracking this product to ensure the best accuracy.
                        Current active interval: <strong>${(product.intervalHours != null ? product.intervalHours : 0) * 60 + (product.intervalMinutes != null ? product.intervalMinutes : 0)} minutes</strong>.
                    </p>
                </div>

                <div class="mt-4 p-3 bg-blue-50 rounded-lg">
                    <p class="text-xs text-blue-700">
                        <i class="fas fa-info-circle mr-1"></i>
                        Note: To ensure server stability, the minimum interval is 30 minutes.
                    </p>
                </div>
            </div>

            <div class="flex justify-end gap-3 mt-8">
                <a href="${pageContext.request.contextPath}/Dashboard"
                   class="px-4 py-2 text-slate-600 bg-slate-100 hover:bg-slate-200 rounded-lg font-medium">
                    Back
                </a>
                <button type="submit"
                        class="px-4 py-2 bg-blue-600 text-white hover:bg-blue-700 rounded-lg font-medium">
                    Update
                </button>
            </div>
        </form>
    </div>
</t:pageTemplate>