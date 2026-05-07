package com.pricewatch.pricewatch.servlets;

import com.pricewatch.pricewatch.ejb.ScraperBean;
import com.pricewatch.pricewatch.ejb.UsersBean;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.HttpConstraint;
import jakarta.servlet.annotation.ServletSecurity;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "AddProduct", value = "/AddProduct")
@ServletSecurity(@HttpConstraint(rolesAllowed = {"Admin", "Member"}))
public class AddProduct extends HttpServlet {

    @Inject
    private ScraperBean scraperBean;

    @Inject
    private UsersBean usersBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // afiseaza interfata (formularul) cand utilizatorul acceseaza /AddProduct
        request.getRequestDispatcher("/WEB-INF/pages/addProduct.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // preluam datele trimise cand utilizatorul apasa butonul de submit
        String action = request.getParameter("action");

        // aflam cine este utilizatorul care a apasat butonul
        String username = request.getRemoteUser();
        if (username == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        // extragem id ul utilizatorului
        Integer userIdInt = usersBean.findUserIdByUsername(username);

        // verificam daca id ul nu a fost gasit
        if (userIdInt == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        // transformam integer-ul primit de la tine în Long-ul necesar pentru ScraperBean
        Long userId = userIdInt.longValue();

        // redirect adminii merg pe products userii pe dashboard
        String redirectUrl = request.getContextPath() + (request.isUserInRole("Admin") ? "/Products" : "/Dashboard");

        if ("manual".equals(action)) {
            String productUrl = request.getParameter("productUrl");

            if (productUrl != null && !productUrl.isEmpty()) {
                String autoDetectedStore = detectStoreFromUrl(productUrl);

                if (autoDetectedStore != null) {
                    scraperBean.addProductFromUrl(productUrl, autoDetectedStore, userId);
                } else {
                    System.out.println("Magazin necunoscut sau nesuportat pentru url-ul: " + productUrl);
                }
            }
            response.sendRedirect(redirectUrl);

        } else if ("search".equals(action)) {
            String productName = request.getParameter("productName");

            scraperBean.searchAndAddNewProduct(productName, userId);

            response.sendRedirect(redirectUrl);

        } else {
            // daca nu este nicio actiune valida, pur si simplu reincarcam pagina de adaugare
            response.sendRedirect(request.getContextPath() + "/AddProduct");
        }
    }

    // functie ajutatoare pentru detectarea magazinului
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