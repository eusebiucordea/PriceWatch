package com.pricewatch.pricewatch.servlets;

import com.pricewatch.pricewatch.ejb.WatchlistBean;
import jakarta.ejb.EJB;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet(name = "SetAlert", value = "/SetAlert")
public class SetAlert extends HttpServlet {

    @EJB
    private WatchlistBean watchlistBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws
            ServletException, IOException {
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws
            ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // preluam sesiunea curenta
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        // oprim executia si returnam eroare daca utilizatorul nu este logat
        if (userId == null) {
            response.getWriter().write("{\"status\":\"error\", \"message\":\"You must be logged in\"}");
            return;
        }

        try {
            // extragem id ul produsului si procentul din cerere
            int productId = Integer.parseInt(request.getParameter("productId"));
            int percentage = Integer.parseInt(request.getParameter("percentage"));

            // apelam metoda din ejb pentru a actualiza baza de date
            watchlistBean.setPriceAlert(userId, productId, percentage);

            // trimitem un raspuns de succes catre frontend
            response.getWriter().write("{\"status\":\"success\"}");
        } catch (Exception e) {
            // trimitem un mesaj de eroare daca procesarea a esuat
            response.getWriter().write("{\"status\":\"error\", \"message\":\"An internal error occurred\"}");
        }
    }
}
