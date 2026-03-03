<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:pageTemplate pageTitle="About Us">
    <div class="max-w-4xl mx-auto space-y-12 py-8">
        <div class="text-center space-y-4">
            <h2 class="text-4xl font-extrabold text-slate-900">About PriceWatch</h2>
            <p class="text-lg text-slate-600">Your trusted partner for intelligent online price monitoring.</p>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
            <div class="bg-blue-600 p-8 rounded-3xl text-white shadow-lg shadow-blue-200">
                <h4 class="text-xl font-bold mb-4">Our Mission</h4>
                <p class="opacity-90 leading-relaxed">
                    We empower consumers and businesses to make informed purchasing decisions, saving time and money through advanced tracking algorithms and real-time alerts.
                </p>
            </div>

            <div class="bg-white p-8 rounded-3xl border border-slate-100 shadow-sm">
                <h4 class="text-xl font-bold text-slate-800 mb-4">How It Works</h4>
                <ul class="space-y-3 text-slate-600">
                    <li class="flex items-center gap-2">
                        <div class="w-2 h-2 bg-blue-500 rounded-full"></div>
                        24/7 Automatic scanning
                    </li>
                    <li class="flex items-center gap-2">
                        <div class="w-2 h-2 bg-blue-500 rounded-full"></div>
                        Full price history
                    </li>
                    <li class="flex items-center gap-2">
                        <div class="w-2 h-2 bg-blue-500 rounded-full"></div>
                        Email alerts
                    </li>
                </ul>
            </div>
        </div>
    </div>
</t:pageTemplate>