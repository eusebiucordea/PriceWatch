<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:pageTemplate pageTitle="Register">
    <style>
        .register-card {
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
            <div class="register-card w-full max-w-md p-10 shadow-sm fade-in">
                <div class="text-center mb-10">
                    <h1 class="text-2xl font-bold text-slate-900">Create your account</h1>
                    <p class="text-slate-500 mt-2 text-sm">Join PriceWatch today and start tracking prices.</p>
                </div>

                <!-- Action targets your registration servlet -->
                <form action="${pageContext.request.contextPath}/Register" method="POST" class="space-y-6">

                    <!-- Username -->
                    <div>
                        <label for="username" class="block text-sm font-semibold text-slate-700 mb-2">Username</label>
                        <div class="relative">
                            <span class="absolute inset-y-0 left-0 pl-4 flex items-center text-slate-400">
                                <i class="far fa-user"></i>
                            </span>
                            <input type="text" id="username" name="username" required
                                   class="pw-input w-full pl-11 pr-4 py-3 rounded-xl text-sm"
                                   placeholder="alex123">
                        </div>
                    </div>

                    <!-- Email -->
                    <div>
                        <label for="email" class="block text-sm font-semibold text-slate-700 mb-2">Email Address</label>
                        <div class="relative">
                            <span class="absolute inset-y-0 left-0 pl-4 flex items-center text-slate-400">
                                <i class="far fa-envelope"></i>
                            </span>
                            <input type="email" id="email" name="email" required
                                   class="pw-input w-full pl-11 pr-4 py-3 rounded-xl text-sm"
                                   placeholder="example@mail.com">
                        </div>
                    </div>

                    <!-- Password -->
                    <div>
                        <label for="password" class="block text-sm font-semibold text-slate-700 mb-2">Password</label>
                        <div class="relative">
                            <span class="absolute inset-y-0 left-0 pl-4 flex items-center text-slate-400">
                                <i class="fas fa-lock"></i>
                            </span>
                            <input type="password" id="password" name="password" required
                                   class="pw-input w-full pl-11 pr-12 py-3 rounded-xl text-sm"
                                   placeholder="••••••••">
                            <button type="button" onclick="const p = document.getElementById('password'); p.type = p.type === 'password' ? 'text' : 'password';"
                                    class="absolute inset-y-0 right-0 pr-4 flex items-center text-slate-400 hover:text-slate-600">
                                <i class="far fa-eye"></i>
                            </button>
                        </div>
                        <p class="mt-2 text-[11px] text-slate-400 font-medium italic">Must be at least 8 characters long.</p>
                    </div>

                    <!-- Terms checkbox -->
                    <div class="flex items-start">
                        <input type="checkbox" id="terms" name="terms" required class="mt-1 w-4 h-4 text-blue-600 border-slate-300 rounded focus:ring-blue-500">
                        <label for="terms" class="ml-2 block text-sm text-slate-600 leading-tight">
                            I agree to the <a href="#" class="text-blue-600 hover:underline">Terms of Service</a> and <a href="#" class="text-blue-600 hover:underline">Privacy Policy</a>.
                        </label>
                    </div>

                    <!-- Register Button -->
                    <button type="submit" class="btn-primary w-full text-white py-3.5 rounded-xl font-bold text-sm shadow-lg shadow-blue-200">
                        Create Account
                    </button>
                </form>

                <div class="mt-8 text-center">
                    <p class="text-sm text-slate-500">
                        Already have an account?
                        <a href="${pageContext.request.contextPath}/Login" class="text-blue-600 font-bold hover:underline">Sign in</a>
                    </p>
                </div>
            </div>
        </main>
    </div>
</t:pageTemplate>