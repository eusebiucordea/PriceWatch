package com.pricewatch.pricewatch.servlets;

import com.pricewatch.pricewatch.ejb.ScraperBean;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "AddProductAction", value = "/add-product-action")
public class AddProductAction extends HttpServlet {

    @Inject
    private ScraperBean scraperBean;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // preluam datele trimise din formular
        String action = request.getParameter("action");
        String storeName = request.getParameter("storeName");

        // verificam care din cele doua formulare a fost apasat
        if ("manual".equals(action)) {
            String productUrl = request.getParameter("productUrl");

            if (productUrl != null && !productUrl.isEmpty()) {
                scraperBean.addProductFromUrl(productUrl, storeName);
            }
            response.sendRedirect(request.getContextPath() + "/Products");
        } else if ("search".equals(action)) {
            // cautarea automata dupa nume pe viitor
            String productName = request.getParameter("productName");
            // scraperBean.searchAndAddNewProduct(productName);
            response.sendRedirect(request.getContextPath() + "/Products");
        }
    }
}