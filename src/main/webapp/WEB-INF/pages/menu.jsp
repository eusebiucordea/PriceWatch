<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script src="https://cdn.tailwindcss.com"></script>
<header>
    <nav class="fixed top-0 left-0 right-0 bg-white border-b border-slate-100 z-50 h-16 flex items-center px-6 justify-between shadow-sm">
        <div class="flex items-center gap-2 text-blue-600">
            <%--logo pricewatch--%>
            <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
                <polyline points="23 18 13.5 8.5 8.5 13.5 1 6"></polyline>
                <polyline points="17 18 23 18 23 12"></polyline>
            </svg>
            <a href="${pageContext.request.contextPath}" class="text-xl font-black tracking-tighter text-slate-800 no-underline">
                PriceWatch
            </a>
        </div>

        <div class="hidden md:flex items-center gap-8">
            <a href="${pageContext.request.contextPath}/"
               class="nav-link-custom text-sm font-semibold transition-colors ${activePage eq '' ? 'text-blue-600' : 'text-slate-500 hover:text-slate-800'} no-underline">
                Home
            </a>

            <a href="${pageContext.request.contextPath}/Dashboard"
               class="nav-link-custom text-sm font-semibold transition-colors ${activePage eq 'Dashboard' ? 'text-blue-600' : 'text-slate-500 hover:text-slate-800'} no-underline">
                Dashboard
            </a>

            <a href="${pageContext.request.contextPath}/Products"
               class="nav-link-custom text-sm font-semibold transition-colors ${activePage eq 'Products' ? 'text-blue-600' : 'text-slate-500 hover:text-slate-800'} no-underline">
                Products
            </a>

            <a href="${pageContext.request.contextPath}/about.jsp"
               class="nav-link-custom text-sm font-semibold transition-colors ${activePage eq 'About' ? 'text-blue-600' : 'text-slate-500 hover:text-slate-800'} no-underline">
                About Us
            </a>

            <a href="${pageContext.request.contextPath}/Users"
               class="nav-link-custom text-sm font-semibold transition-colors ${activePage eq 'Users' ? 'text-blue-600' : 'text-slate-500 hover:text-slate-800'} no-underline">
                Users
            </a>
        </div>

        <div class="flex items-center gap-4">
            <a href="${pageContext.request.contextPath}/Alerts" class="p-2 text-slate-400 hover:text-blue-600 relative bg-transparent border-none cursor-pointer">
                <!-- bell icon -->
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
                    <path d="M13.73 21a2 2 0 0 1-3.46 0"></path>
                </svg>
                <span class="absolute top-1 right-1 w-2.5 h-2.5 bg-red-500 border-2 border-white rounded-full"></span>
            </a>

        <c:choose>
            <c:when test="${pageContext.request.getRemoteUser() == null}">
                <a href="${pageContext.request.contextPath}/Login"
                   class="nav-link-custom text-sm font-semibold transition-colors text-slate-500 hover:text-slate-800 no-underline">
                    Login
                </a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/Logout"
                   class="nav-link-custom text-sm font-semibold transition-colors text-slate-500 hover:text-slate-800 no-underline">
                    Logout
                </a>
            </c:otherwise>
        </c:choose>
        </div>
    </nav>

</header>
<div class="h-16"></div>