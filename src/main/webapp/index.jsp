<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:pageTemplate pageTitle="PriceWatch">

    <style>
        .welcome-wrapper {
            max-width: 1200px;
            margin: 0 auto;
            padding: 60px 20px;
            font-family: 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
        }

        .hero-section {
            text-align: center;
            margin-bottom: 60px;
        }

        .hero-title {
            color: #0f172a; /* Dark color from logo */
            font-size: 2.5rem;
            font-weight: 800;
            margin-bottom: 16px;
        }

        .hero-subtitle {
            color: #64748b;
            font-size: 1.1rem;
            max-width: 650px;
            margin: 0 auto;
            line-height: 1.6;
        }

        .features-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
            gap: 30px;
        }

        .feature-card {
            background-color: #ffffff;
            border: 1px solid #e2e8f0;
            border-radius: 12px;
            padding: 32px 24px;
            transition: transform 0.2s ease, box-shadow 0.2s ease;
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05), 0 2px 4px -1px rgba(0, 0, 0, 0.03);
        }

        .feature-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
        }

        .feature-icon {
            font-size: 2rem;
            margin-bottom: 20px;
            color: #2563eb; /* PriceWatch specific blue */
        }

        .feature-title {
            color: #1e293b;
            font-size: 1.25rem;
            font-weight: 700;
            margin-bottom: 12px;
        }

        .feature-text {
            color: #475569;
            font-size: 0.95rem;
            line-height: 1.6;
        }
    </style>

    <div class="welcome-wrapper">
        <div class="hero-section">
            <h1 class="hero-title">Welcome to PriceWatch</h1>
            <p class="hero-subtitle">
                Your personal assistant for smart shopping. Discover the best time and the ideal place to purchase the products you desire.
            </p>
        </div>

        <div class="features-grid">

            <div class="feature-card">
                <div class="feature-icon">🛍️</div>
                <h3 class="feature-title">Great Deals</h3>
                <p class="feature-text">
                    We expand the online search scope to offer you the most advantageous price when purchasing products from electronics, fashion, beauty stores, and more.
                </p>
            </div>

            <div class="feature-card">
                <div class="feature-icon">🛡️</div>
                <h3 class="feature-title">Avoid Overpricing</h3>
                <p class="feature-text">
                    Don't pay extra just for the brand name! We help you avoid the retailers' practice of unjustified price hikes for popular brands by showing you the true value of the product.
                </p>
            </div>

            <div class="feature-card">
                <div class="feature-icon">📊</div>
                <h3 class="feature-title">Simplified Comparison</h3>
                <p class="feature-text">
                    We save you the effort of checking dozens of websites. We provide a detailed list with a variety of online stores and their prices, making comparison extremely easy.
                </p>
            </div>

            <div class="feature-card">
                <div class="feature-icon">🔔</div>
                <h3 class="feature-title">Custom Alerts</h3>
                <p class="feature-text">
                    Set a price threshold and you will be automatically notified by email when your desired product drops below that amount. No need to constantly check the market.
                </p>
            </div>

        </div>
    </div>

</t:pageTemplate>