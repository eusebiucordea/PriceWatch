package com.pricewatch.pricewatch.servlets;

import com.pricewatch.pricewatch.common.ProductDto;
import com.pricewatch.pricewatch.ejb.ScraperBean;
import com.pricewatch.pricewatch.ejb.UsersBean;
import com.pricewatch.pricewatch.ejb.WatchlistBean;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "Dashboard", value = "/Dashboard")
@ServletSecurity(@HttpConstraint(rolesAllowed = {"Admin", "Member"}))
public class Dashboard extends HttpServlet {

    @EJB
    private ScraperBean scraperBean;

    @EJB
    private WatchlistBean watchlistBean;

    @EJB
    private UsersBean usersBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String username = request.getRemoteUser();

        if (username == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        // verifica daca userid e null
        if (userId == null) {
            userId = usersBean.findUserIdByUsername(username);

            if (userId != null) {
                session.setAttribute("userId", userId);
            } else {
                // Ca masura de siguranta, daca ceva merge prost
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "User ID not found in database");
                return;
            }
        }

        // redirectioneaza catre login daca utilizatorul nu este autentificat
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        // extrage lista de favorite a utilizatorului si o ataseaza la cerere
        List<ProductDto> myWatchlist = watchlistBean.getUserWatchlist(userId);

        int allTimeLowCount = 0;
        int recentlyDroppedCount = 0;

        if (myWatchlist != null) {
            for (ProductDto product : myWatchlist) {

                Double current = product.getCurrent_price();
                Double allTimeLow = product.getAll_time_low();
                Double old = product.getOld_price();

                // Regula de aur: Verificăm ( != null ) înainte să folosim operatorii matematici!
                if (current != null && allTimeLow != null && current <= allTimeLow) {
                    allTimeLowCount++;
                }

                // Aici apărea eroarea ta. Acum, dacă old este null, codul se oprește
                // la "old != null" și nu mai încearcă să facă unboxing pentru "current < old"
                if (current != null && old != null && current < old) {
                    recentlyDroppedCount++;
                }
            }
        }

        // Atașăm lista și numerele la request
        request.setAttribute("watchlist", myWatchlist);
        request.setAttribute("allTimeLowCount", allTimeLowCount);
        request.setAttribute("recentlyDroppedCount", recentlyDroppedCount);

        // trimite cererea catre pagina jsp dashboard
        request.getRequestDispatcher("/WEB-INF/pages/dashboard.jsp").forward(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // seteaza tipul raspunsului ca json pentru cererile ajax
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            HttpSession session = request.getSession();
            Integer userId = (Integer) session.getAttribute("userId");

            // returneaza eroare de neautorizare daca utilizatorul nu este logat
            if (userId == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"status\":\"error\", \"message\":\"You must be logged in!\"}");
                return;
            }

            // extrage parametrii din cerere
            String action = request.getParameter("action");
            int productId = Integer.parseInt(request.getParameter("productId"));

            // executa actiunea in functie de parametrul primit
            if ("add".equals(action)) {
                watchlistBean.addToWatchlist(userId, productId);
                response.getWriter().write("{\"status\":\"success\", \"message\":\"Product added\"}");
            }
            else if ("remove".equals(action)) {
                watchlistBean.removeFromWatchlist(userId, productId);
                response.getWriter().write("{\"status\":\"success\", \"message\":\"Product removed\"}");
            }
            else {
                // gestioneaza actiunile necunoscute
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"status\":\"error\", \"message\":\"Invalid action!\"}");
            }

        } catch (NumberFormatException e) {
            // gestioneaza cazul in care id ul produsului nu este valid
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\":\"error\", \"message\":\"Invalid product ID!\"}");
        } catch (Exception e) {
            // gestioneaza exceptiile neasteptate de pe server
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\":\"error\", \"message\":\"Internal server error\"}");
        }
    }
}