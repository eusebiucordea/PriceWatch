<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:pageTemplate pageTitle="Edit Product">
    <div class="max-w-2xl mx-auto bg-white p-8 rounded-2xl shadow-sm border border-slate-100 mt-10">
        <h2 class="text-2xl font-bold mb-6 text-slate-800">Edit Product #${product.id}</h2>

        <form action="EditProduct" method="POST" class="space-y-4">
            <input type="hidden" name="id" value="${product.id}">

            <div>
                <label class="block text-sm font-medium text-slate-700 mb-1">Product Name</label>
                <textarea name="name" required rows="3"
                          class="w-full px-4 py-2 border border-slate-200 rounded-lg focus:ring-2 focus:ring-blue-600 outline-none resize-y">${product.name}</textarea>
            </div>

            <div class="grid grid-cols-2 gap-4">
                <div>
                    <label class="block text-sm font-medium text-slate-700 mb-1">Current Price (RON)</label>
                    <input type="number" step="0.01" name="current_price" value="${product.current_price}" required
                           class="w-full px-4 py-2 border border-slate-200 rounded-lg focus:ring-2 focus:ring-blue-600 outline-none">
                </div>
                <div>
                    <label class="block text-sm font-medium text-slate-700 mb-1">All-Time Low (RON)</label>
                    <input type="number" step="0.01" name="all_time_low" value="${product.all_time_low}" required
                           class="w-full px-4 py-2 border border-slate-200 rounded-lg focus:ring-2 focus:ring-blue-600 outline-none">
                </div>
            </div>

            <div class="pt-4 mt-2 border-t border-slate-100">
                <h3 class="text-sm font-semibold text-slate-800 mb-3">Scraping Settings</h3>
                <div class="grid grid-cols-2 gap-4">
                    <div>
                        <label class="block text-sm font-medium text-slate-700 mb-1">Interval (Hours)</label>
                        <input type="number" min="0" name="interval_hours" value="${not empty product.intervalHours ? product.intervalHours : 12}" required
                               class="w-full px-4 py-2 border border-slate-200 rounded-lg focus:ring-2 focus:ring-blue-600 outline-none">
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-slate-700 mb-1">Interval (Minutes)</label>
                        <input type="number" min="0" max="59" name="interval_minutes" value="${not empty product.intervalMinutes ? product.intervalMinutes : 0}" required
                               class="w-full px-4 py-2 border border-slate-200 rounded-lg focus:ring-2 focus:ring-blue-600 outline-none">
                    </div>
                </div>
                <p class="text-xs text-slate-500 mt-2">* Minim allowed is 15 minutes to prevent server overload.</p>
            </div>

            <div class="flex justify-end gap-3 mt-8">
                <a href="${pageContext.request.contextPath}/Products"
                   class="px-4 py-2 text-slate-600 bg-slate-100 hover:bg-slate-200 rounded-lg font-medium">
                    Cancel
                </a>
                <button type="submit"
                        class="px-4 py-2 bg-blue-600 text-white hover:bg-blue-700 rounded-lg font-medium">
                    Save Changes
                </button>
            </div>
        </form>
    </div>
</t:pageTemplate>