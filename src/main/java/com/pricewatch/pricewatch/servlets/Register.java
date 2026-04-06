package com.pricewatch.pricewatch.servlets;

import com.pricewatch.pricewatch.ejb.UsersBean;
import jakarta.ejb.EJB;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.Arrays;

@WebServlet(name = "Register", value = "/Register")
public class Register extends HttpServlet {

    @EJB
    UsersBean usersBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // preluam rolurile si reparam numele variabilei
        String[] roles = request.getParameterValues("user_groups");

        // setam un rol default daca utilizatorul nu a bifat nimic
        if (roles == null || roles.length == 0) {
            roles = new String[]{"Member"};
        }

        // trimitem datele catre bean pentru a salva utilizatorul in baza de date
        usersBean.createUser(username, email, password, Arrays.asList(roles));

        // facem doar redirect catre login ca sa nu avem eroare de server
        response.sendRedirect(request.getContextPath() + "/Login");
    }
}