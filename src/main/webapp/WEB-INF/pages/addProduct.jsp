<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:pageTemplate pageTitle="Add Product">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600&display=swap" rel="stylesheet">

        <style>
            :root {
                --primary: #2563eb;
                --primary-hover: #1d4ed8;
                --bg: #f8fafc;
                --card-bg: #ffffff;
                --text-main: #1e293b;
                --text-muted: #64748b;
                --border: #e2e8f0;
            }

            /* Container centering */
            .wrapper {
                font-family: 'Inter', sans-serif;
                background-color: var(--bg);
                color: var(--text-main);
                min-height: 80vh;
                display: flex;
                align-items: center;
                justify-content: center;
                padding: 20px;
            }

            .container {
                max-width: 800px;
                width: 100%;
            }

            header {
                text-align: center;
                margin-bottom: 40px;
            }

            header h1 {
                font-weight: 600;
                letter-spacing: -0.025em;
                margin-bottom: 8px;
                font-size: 2.25rem;
            }

            header p {
                color: var(--text-muted);
                font-size: 1.1rem;
            }

            .grid {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 24px;
            }

            @media (max-width: 640px) {
                .grid { grid-template-columns: 1fr; }
            }

            .card {
                background: var(--card-bg);
                border: 1px solid var(--border);
                padding: 32px;
                border-radius: 16px;
                box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
                transition: all 0.2s ease-in-out;
                text-align: center; /* Centering text inside cards */
            }

            .card:hover {
                transform: translateY(-4px);
                box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
            }

            h3 {
                margin-top: 0;
                font-size: 1.25rem;
                color: var(--text-main);
                margin-bottom: 16px;
            }

            .form-group {
                margin-bottom: 20px;
                text-align: left; /* Keep labels aligned with inputs */
            }

            label {
                display: block;
                font-size: 0.85rem;
                font-weight: 600;
                margin-bottom: 8px;
                color: var(--text-muted);
                text-transform: uppercase;
                letter-spacing: 0.05em;
            }

            input[type="text"], select {
                width: 100%;
                padding: 12px 16px;
                border: 1px solid var(--border);
                border-radius: 8px;
                font-size: 1rem;
                box-sizing: border-box;
                outline: none;
                transition: all 0.2s;
                background-color: #f9fafb;
            }

            input[type="text"]:focus {
                border-color: var(--primary);
                background-color: #fff;
                ring: 2px var(--primary);
            }

            button {
                width: 100%;
                background-color: var(--primary);
                color: white;
                padding: 12px;
                border: none;
                border-radius: 8px;
                font-weight: 600;
                font-size: 1rem;
                cursor: pointer;
                transition: background 0.2s;
                margin-top: 10px;
            }

            button:hover {
                background-color: var(--primary-hover);
            }

            .icon {
                font-size: 2rem;
                margin-bottom: 16px;
                display: inline-block;
            }
        </style>
    </head>

    <div class="wrapper">
        <div class="container">
            <header>
                <h1>Add New Product</h1>
                <p>Track prices in real-time and shop smarter.</p>
            </header>

            <div class="grid">
                <div class="card">
                    <h3>Automatic Search</h3>
                    <form action="AddProduct" method="POST">
                        <input type="hidden" name="action" value="search">
                        <div class="form-group">
                            <label>Product Name</label>
                            <input type="text" name="productName" placeholder="e.g. iPhone 17 Pro Max" required>
                        </div>
                        <button type="submit">Search Stores</button>
                    </form>
                </div>

                <div class="card">
                    <h3>Direct Link</h3>
                    <form action="AddProduct" method="POST">
                        <input type="hidden" name="action" value="manual">
                        <div class="form-group">
                            <label>Product URL</label>
                            <input type="text" name="productUrl" placeholder="https://www.emag.ro/..." required>
                        </div>
                        <button type="submit">Start Tracking</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</t:pageTemplate>