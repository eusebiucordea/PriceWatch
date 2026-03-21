package com.pricewatch.pricewatch.servlets;

import com.pricewatch.pricewatch.common.ProductDto;
import com.pricewatch.pricewatch.ejb.ProductsBean;
import jakarta.inject.Inject;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet(name = "ViewDetails", value = "/ViewDetails")
public class ViewDetails extends HttpServlet {

    @Inject
    ProductsBean productsBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. Preluăm ID-ul produsului din URL (ex: /ViewDetails?id=5)
        String idParam = request.getParameter("id");

        if (idParam != null && !idParam.isEmpty()) {
            try {
                // Convertim ID-ul din String în Long (sau Integer, depinde cum ai definit ID-ul în baza de date)
                Long productId = Long.parseLong(idParam);

                // 2. Căutăm produsul folosind EJB-ul
                // Numele metodei (ex: findById) depinde de cum ai numit-o tu în ProductsBean
                ProductDto product = productsBean.findById(productId);

                if (product != null) {
                    // 3. Trimitem obiectul "product" către pagina JSP
                    request.setAttribute("product", product);
                    request.getRequestDispatcher("/WEB-INF/pages/viewDetails.jsp").forward(request, response);
                    return; // Oprim execuția aici ca să nu treacă mai departe
                }
            } catch (NumberFormatException e) {
                // ID-ul din link nu este un număr valid (ex: /ViewDetails?id=abc)
                // Putem loga eroarea sau pur și simplu ignorăm și trecem la pasul 4
            }
        }

        // 4. Dacă nu am găsit produsul sau ID-ul lipsește, îl redirecționăm înapoi la lista de produse
        response.sendRedirect(request.getContextPath() + "/Products");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/ViewDetails");
    }
}