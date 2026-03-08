<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:pageTemplate pageTitle="Users">
    <header class="mb-8">
        <h1 class="text-3xl font-extrabold text-slate-900">Users</h1>
    </header>

    <div class="bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden">
        <div class="p-6 border-b border-slate-100 flex flex-col md:flex-row md:items-center justify-between">
            <h3 class="text-lg font-bold text-slate-800">User Directory</h3>
        </div>

        <div class="overflow-x-auto">
            <table class="w-full text-left">
                <thead class="bg-slate-50 text-slate-500 text-xs uppercase font-semibold">
                <tr>
                    <th class="px-6 py-4">ID</th>
                    <th class="px-6 py-4">Username</th>
                    <th class="px-6 py-4">Email Address</th>
                    <th class="px-6 py-4">Role</th>
                    <th class="px-6 py-4 text-right">Actions</th>
                </tr>
                </thead>
                <tbody class="divide-y divide-slate-100">
                <c:forEach var="user" items="${users}">
                    <tr class="hover:bg-slate-50 transition-colors">
                        <td class="px-6 py-4 text-sm text-slate-400 font-mono">
                            #${user.id}
                        </td>
                        <td class="px-6 py-4">
                            <span class="font-medium text-slate-800">${user.username}</span>
                        </td>
                        <td class="px-6 py-4 text-slate-600 text-sm">
                                ${user.email}
                        </td>

                        <td class="px-6 py-4">
                            <span class="px-2 py-1 rounded text-xs font-bold ${user.role == 'Admin' ? 'bg-orange-100 text-orange-700' : 'bg-blue-100 text-blue-700'}">
                                    ${user.role}
                            </span>
                        </td>

                        <td class="px-6 py-4 text-right">
                            <div class="flex justify-end">
                                <c:if test="${user.role != 'Admin'}">
                                    <button title="Delete User" class="p-2 text-slate-400 hover:text-red-500 hover:bg-red-50 rounded-lg transition-all">
                                        <i data-lucide="trash-2" class="w-5 h-5">Delete</i>
                                    </button>
                                </c:if>
                            </div>
                        </td>
                    </tr>
                </c:forEach>

                <c:if test="${empty users}">
                    <tr>
                        <td colspan="5" class="px-6 py-12 text-center text-slate-400 italic">
                            No registered users found.
                        </td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>
</t:pageTemplate>