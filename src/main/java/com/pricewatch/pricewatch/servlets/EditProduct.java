package com.pricewatch.pricewatch.servlets;

import com.pricewatch.pricewatch.common.ProductDto;
import com.pricewatch.pricewatch.ejb.ProductsBean;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "EditProduct", value = "/EditProduct")
public class EditProduct extends HttpServlet {

    @Inject
    private ProductsBean productsBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");

        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/Products");
            return;
        }
        try {
            Long id = Long.parseLong(idParam);
            ProductDto product = productsBean.findById(id);

            if (product != null) {
                request.setAttribute("product", product);
                request.getRequestDispatcher("/WEB-INF/pages/editProduct.jsp").forward(request, response);
            } else {
                // produsul nu exista in baza de date
                response.sendRedirect(request.getContextPath() + "/Products");
            }
        } catch (NumberFormatException e) {
            // // caz particular daca scrie abc in url
            response.sendRedirect(request.getContextPath() + "/Products");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Long id = Long.parseLong(request.getParameter("id"));
            String name = request.getParameter("name");
            Double currentPrice = Double.parseDouble(request.getParameter("current_price"));
            Double allTimeLow = Double.parseDouble(request.getParameter("all_time_low"));

            // preluam intervalele noi introduse de admin
            int hours = Integer.parseInt(request.getParameter("interval_hours"));
            int minutes = Integer.parseInt(request.getParameter("interval_minutes"));

            // calculam totalul in minute
            int totalMinutes = (hours * 60) + minutes;

            // REGULA DE SIGURANTA nu permitem verificAri mai dese de 15 minute
            if (totalMinutes < 15) {
                totalMinutes = 15;
            }

            // trimitem totalMinutes catre bean-ul care actualizeaza baza de date
            productsBean.updateProduct(id, name, currentPrice, allTimeLow, totalMinutes);

            response.sendRedirect(request.getContextPath() + "/Products");
        } catch (Exception e) {
            // in caz de eroare
            response.sendRedirect(request.getContextPath() + "/Products");
        }
    }
}