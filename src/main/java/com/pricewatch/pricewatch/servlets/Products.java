package com.pricewatch.pricewatch.servlets;

import com.pricewatch.pricewatch.common.ProductDto;
import com.pricewatch.pricewatch.ejb.ProductsBean;
import jakarta.inject.Inject;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "Products", value = "/Products")
@ServletSecurity(@HttpConstraint(rolesAllowed = {"Admin"}))
public class Products extends HttpServlet {

    @Inject
    ProductsBean productsBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws
            ServletException, IOException {
        List<ProductDto> products = productsBean.findAllProducts();
        request.setAttribute("products", products);
        request.getRequestDispatcher("/WEB-INF/pages/products.jsp").forward(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws
            ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/Products");
    }
}