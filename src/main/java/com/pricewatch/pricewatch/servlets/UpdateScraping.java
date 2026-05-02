package com.pricewatch.pricewatch.servlets;

import com.pricewatch.pricewatch.common.ProductDto;
import com.pricewatch.pricewatch.ejb.ProductsBean;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.HttpConstraint;
import jakarta.servlet.annotation.ServletSecurity;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "UpdateScraping", value = "/UpdateScraping")
@ServletSecurity(@HttpConstraint(rolesAllowed = {"Admin", "Member"}))
public class UpdateScraping extends HttpServlet {

    @Inject
    private ProductsBean productsBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");

        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/Dashboard");
            return;
        }
        try {
            Long id = Long.parseLong(idParam);
            ProductDto product = productsBean.findById(id);

            if (product != null) {
                request.setAttribute("product", product);
                request.getRequestDispatcher("/WEB-INF/pages/updateScraping.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/Dashboard");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/Dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Long id = Long.parseLong(request.getParameter("id"));

            int reqHours = Integer.parseInt(request.getParameter("interval_hours"));
            int reqMinutes = Integer.parseInt(request.getParameter("interval_minutes"));
            int requestedTotalMinutes = (reqHours * 60) + reqMinutes;

            // VERIFICARE si EROARE
            if (requestedTotalMinutes < 30) {
                // trimitem utilizatorul inapoi la formular si ii dam un parametru de eroare in url
                response.sendRedirect(request.getContextPath() + "/UpdateScraping?id=" + id + "&error=min_limit");
                return; // Oprim execuția aici! Nu mai salvăm nimic în DB.
            }

            ProductDto product = productsBean.findById(id);
            int currentHoursInDb = (product.getIntervalHours() != null) ? product.getIntervalHours() : 0;
            int currentMinutesInDb = (product.getIntervalMinutes() != null) ? product.getIntervalMinutes() : 0;
            int currentTotalMinutesInDb = (currentHoursInDb * 60) + currentMinutesInDb;

            int finalInterval;
            if (currentTotalMinutesInDb <= 0) {
                finalInterval = requestedTotalMinutes;
            } else {
                finalInterval = Math.min(requestedTotalMinutes, currentTotalMinutesInDb);
            }

            productsBean.updateProduct(id, product.getName(), product.getCurrent_price(), product.getAll_time_low(), finalInterval);

            response.sendRedirect(request.getContextPath() + "/Dashboard?success=true");
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/Dashboard");
        }
    }
}