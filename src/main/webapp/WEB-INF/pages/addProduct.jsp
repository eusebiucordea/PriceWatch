<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:pageTemplate pageTitle="Add Product">


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

        /* Am redenumit .container in .add-product-container */
        .add-product-container {
            max-width: 800px;
            width: 100%;
        }

        /* Am transformat tag-ul header intr-o clasa specifica */
        .add-product-header {
            text-align: center;
            margin-bottom: 40px;
        }

        .add-product-header h1 {
            font-weight: 600;
            letter-spacing: -0.025em;
            margin-bottom: 8px;
            font-size: 2.25rem;
        }

        .add-product-header p {
            color: var(--text-muted);
            font-size: 1.1rem;
        }

        .add-product-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 24px;
        }

        @media (max-width: 640px) {
            .add-product-grid { grid-template-columns: 1fr; }
        }

        .add-product-card {
            background: var(--card-bg);
            border: 1px solid var(--border);
            padding: 32px;
            border-radius: 16px;
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
            transition: all 0.2s ease-in-out;
            text-align: center;
        }

        .add-product-card:hover {
            transform: translateY(-4px);
            box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
        }

        .add-product-card h3 {
            margin-top: 0;
            font-size: 1.25rem;
            color: var(--text-main);
            margin-bottom: 16px;
        }

        .form-group {
            margin-bottom: 20px;
            text-align: left;
        }

        .form-group label {
            display: block;
            font-size: 0.85rem;
            font-weight: 600;
            margin-bottom: 8px;
            color: var(--text-muted);
            text-transform: uppercase;
            letter-spacing: 0.05em;
        }

        .add-product-card input[type="text"] {
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

        .add-product-card input[type="text"]:focus {
            border-color: var(--primary);
            background-color: #fff;
            box-shadow: 0 0 0 2px var(--primary); /* corectat 'ring' in 'box-shadow' */
        }

        .add-product-card button {
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

        .add-product-card button:hover {
            background-color: var(--primary-hover);
        }
    </style>

    <div class="wrapper">
        <div class="add-product-container">
            <div class="add-product-header">
                <h1>Add New Product</h1>
                <p>Track prices in real-time and shop smarter.</p>
            </div>

            <div class="add-product-grid">
                <div class="add-product-card">
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

                <div class="add-product-card">
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