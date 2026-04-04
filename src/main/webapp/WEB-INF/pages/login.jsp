<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:pageTemplate pageTitle="Login">
    <style>
        .login-card {
            background: #ffffff;
            border: 1px solid #e2e8f0;
            border-radius: 1.25rem;
        }

        .pw-input {
            transition: all 0.2s ease;
            border: 1px solid #e2e8f0;
        }

        .pw-input:focus {
            border-color: #2563eb;
            box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.1);
            outline: none;
        }

        .btn-primary {
            background-color: #2563eb;
            transition: all 0.2s ease;
        }

        .btn-primary:hover {
            background-color: #1d4ed8;
            transform: translateY(-1px);
        }

        .logo-text {
            color: #1e293b;
            font-weight: 800;
        }

        .fade-in {
            animation: fadeIn 0.5s ease-in-out;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(10px); }
            to { opacity: 1; transform: translateY(0); }
        }
    </style>

    <div class="min-h-[80vh] flex flex-col">
        <!-- Main Content -->
        <main class="flex-grow flex items-center justify-center px-4">
            <div class="login-card w-full max-w-md p-10 shadow-sm fade-in">
                <div class="text-center mb-10">
                    <h1 class="text-2xl font-bold text-slate-900">Welcome to PriceWatch</h1>
                    <p class="text-slate-500 mt-2 text-sm">Please enter your details to access your dashboard.</p>
                </div>

                <c:if test="${not empty message}">
                    <div class="bg-red-50 border border-red-200 text-red-600 text-sm p-3 rounded-lg text-center mb-6 font-semibold">
                            ${message}
                    </div>
                </c:if>

                <!-- Action targets your login servlet or controller -->
                <form action="j_security_check" method="POST" class="space-y-6">
                    <div>
                        <label for="j_username" class="block text-sm font-semibold text-slate-700 mb-2">Username</label>
                        <div class="relative">
                            <span class="absolute inset-y-0 left-0 pl-4 flex items-center text-slate-400">
                                <i class="far fa-envelope"></i>
                            </span>
                            <input type="text" id="j_username" name="j_username" required
                                   class="pw-input w-full pl-11 pr-4 py-3 rounded-xl text-sm"
                                   placeholder="username">
                        </div>
                    </div>

                    <div>
                        <div class="flex justify-between mb-2">
                            <label for="j_password" class="text-sm font-semibold text-slate-700">Password</label>
                            <a href="#" class="text-xs font-medium text-blue-600 hover:text-blue-700">Forgot password?</a>
                        </div>
                        <div class="relative">
                            <span class="absolute inset-y-0 left-0 pl-4 flex items-center text-slate-400">
                                <i class="fas fa-lock"></i>
                            </span>
                            <input type="password" id="j_password" name="j_password" required
                                   class="pw-input w-full pl-11 pr-12 py-3 rounded-xl text-sm"
                                   placeholder="••••••••">
                            <button type="button" onclick="const p = document.getElementById('j_password'); p.type = p.type === 'password' ? 'text' : 'password';"
                                    class="absolute inset-y-0 right-0 pr-4 flex items-center text-slate-400 hover:text-slate-600">
                                <i class="far fa-eye"></i>
                            </button>
                        </div>
                    </div>

                    <div class="flex items-center">
                        <input type="checkbox" id="remember" name="remember" class="w-4 h-4 text-blue-600 border-slate-300 rounded focus:ring-blue-500">
                        <label for="remember" class="ml-2 block text-sm text-slate-600">Remember me</label>
                    </div>

                    <button type="submit" class="btn-primary w-full text-white py-3.5 rounded-xl font-bold text-sm shadow-lg shadow-blue-200">
                        Sign In
                    </button>
                </form>

                <div class="mt-8 text-center">
                    <p class="text-sm text-slate-500">
                        Don't have an account?
                        <a href="${pageContext.request.contextPath}/register.jsp" class="text-blue-600 font-bold hover:underline">Sign up now</a>
                    </p>
                </div>
            </div>
        </main>
    </div>
</t:pageTemplate>