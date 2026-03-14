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
        Long id = Long.parseLong(request.getParameter("id"));
        String name = request.getParameter("name");
        Double currentPrice = Double.parseDouble(request.getParameter("current_price"));
        Double allTimeLow = Double.parseDouble(request.getParameter("all_time_low"));

        productsBean.updateProduct(id, name, currentPrice, allTimeLow);
        response.sendRedirect(request.getContextPath() + "/Products");
    }
}