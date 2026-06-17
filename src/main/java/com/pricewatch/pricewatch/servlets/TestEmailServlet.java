package com.pricewatch.pricewatch.servlets;

import com.pricewatch.pricewatch.ejb.EmailBean;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "TestEmail", value = "/test-email")
public class TestEmailServlet extends HttpServlet {

    @Inject
    private EmailBean emailBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // foloseste adresa ta personala pentru a verifica primirea
        String testEmail = "eusebiucordea@gmail.com";

        try {
            // apelam metoda de trimitere cu date de proba
            emailBean.sendPriceAlertEmail(testEmail, "Produs Test", 1000.0, 850.0);

            response.getWriter().write("Email request sent Check your inbox and spam folder");
        } catch (Exception e) {
            // afisam eroarea in browser daca ceva nu merge
            response.getWriter().write("Error sending email " + e.getMessage());
        }
    }
}