package com.pricewatch.pricewatch.servlets;

import com.pricewatch.pricewatch.ejb.WatchlistBean;
import jakarta.inject.Inject;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet(name = "RemoveAlerts", value = "/RemoveAlerts")
public class RemoveAlerts extends HttpServlet {

     @Inject
     private WatchlistBean watchlistBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productIdStr = request.getParameter("productId");
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("userId") != null && productIdStr != null) {
            int userId = (Integer) session.getAttribute("userId");
            Long productId = Long.parseLong(productIdStr);

            watchlistBean.removeAlert(userId, productId);

            response.setStatus(200); // succes
            response.getWriter().write("Succes");
        } else {
            response.setStatus(400); // eroare
        }
    }
}