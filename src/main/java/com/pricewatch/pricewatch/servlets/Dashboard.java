package com.pricewatch.pricewatch.servlets;

import com.pricewatch.pricewatch.common.ProductDto;
import com.pricewatch.pricewatch.ejb.ScraperBean;
import com.pricewatch.pricewatch.ejb.WatchlistBean;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "Dashboard", value = "/Dashboard")
public class Dashboard extends HttpServlet {

    @Inject
    private ScraperBean scraperBean;

    @Inject
    private WatchlistBean watchlistBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // preia sesiunea curenta
        HttpSession session = request.getSession();

        // login temporar pentru testare
        if (session.getAttribute("userId") == null) {
            session.setAttribute("userId", 2);
        }
        Integer userId = (Integer) session.getAttribute("userId");

        // redirectioneaza catre login daca utilizatorul nu este autentificat
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        // extrage lista de favorite a utilizatorului si o ataseaza la cerere
        List<ProductDto> myWatchlist = watchlistBean.getUserWatchlist(userId);
        request.setAttribute("watchlist", myWatchlist);

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

            // login temporar pentru testare
            if (session.getAttribute("userId") == null) {
                session.setAttribute("userId", 2);
            }
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