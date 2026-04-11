package com.pricewatch.pricewatch.servlets;

import com.pricewatch.pricewatch.common.AlertDto;
import com.pricewatch.pricewatch.ejb.UsersBean;
import com.pricewatch.pricewatch.ejb.WatchlistBean;
import jakarta.inject.Inject;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "Alerts", value = "/Alerts")
@ServletSecurity(@HttpConstraint(rolesAllowed = {"Admin", "Member"}))
public class Alerts extends HttpServlet {

    @Inject
    private UsersBean usersBean;

    @Inject
    private WatchlistBean watchlistBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String username = request.getRemoteUser();

        if (username != null && session.getAttribute("userId") == null) {
            // daca suntem logati dar id-ul nu e in sesiune
             int userId = usersBean.findUserIdByUsername(username);
             session.setAttribute("userId", userId);
        }

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            List<AlertDto> alertsList = watchlistBean.getActiveAlerts(userId);
            request.setAttribute("alertsList", alertsList);
        }

        request.getRequestDispatcher("/WEB-INF/pages/alerts.jsp").forward(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws
            ServletException, IOException {
    }
}
