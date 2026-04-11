package com.pricewatch.pricewatch.servlets;

import com.pricewatch.pricewatch.ejb.UsersBean;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.HttpConstraint;
import jakarta.servlet.annotation.ServletSecurity;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "DeleteUser", value = "/DeleteUser")
@ServletSecurity(@HttpConstraint(rolesAllowed = {"Admin"}))
public class DeleteUser extends HttpServlet {

    @Inject
    private UsersBean usersBean;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");

        if (idParam != null && !idParam.isEmpty()) {
            try {
                Long id = Long.parseLong(idParam);
                usersBean.deleteUser(id);
            } catch (NumberFormatException e) {
                // ignoram erorile daca id ul nu e numar valid
            }
        }

        response.sendRedirect(request.getContextPath() + "/Users");
    }
}