package com.pricewatch.pricewatch.servlets;

import com.pricewatch.pricewatch.ejb.ScraperBean;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "AddProductAction", value = "/AddProductAction")
public class AddProductAction extends HttpServlet {

    @Inject
    private ScraperBean scraperBean;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // preluam datele trimise din formular
        String action = request.getParameter("action");

        if ("manual".equals(action)) {
            String productUrl = request.getParameter("productUrl");

            if (productUrl != null && !productUrl.isEmpty()) {
                // detectarea magazin automat
                String autoDetectedStore = detectStoreFromUrl(productUrl);

                if (autoDetectedStore != null) {
                    scraperBean.addProductFromUrl(productUrl, autoDetectedStore);
                } else {
                    System.out.println("Magazin necunoscut sau nesuportat pentru URL-ul: " + productUrl);
                }
            }
            response.sendRedirect(request.getContextPath() + "/Products");

        } else if ("search".equals(action)) {
            String productName = request.getParameter("productName");
            scraperBean.searchAndAddNewProduct(productName);
            response.sendRedirect(request.getContextPath() + "/Products");
        }
    }

    // functie pentru detectarea magazinului
    private String detectStoreFromUrl(String url) {
        if (url == null) return null;

        String lowerUrl = url.toLowerCase();

        if (lowerUrl.contains("emag.ro")) return "emag";
        if (lowerUrl.contains("altex.ro")) return "altex";
        if (lowerUrl.contains("pcgarage.ro")) return "pc garage";
        if (lowerUrl.contains("flanco.ro")) return "flanco";
        if (lowerUrl.contains("mediagalaxy.ro")) return "mediagalaxy";

        return null;
    }
}