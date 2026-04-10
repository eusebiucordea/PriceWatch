package com.pricewatch.pricewatch.ejb;

import jakarta.ejb.Stateless;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;
import java.util.logging.Logger;

@Stateless
public class EmailBean {

    private static final Logger LOG = Logger.getLogger(EmailBean.class.getName());

    private String senderEmail;
    private String senderPassword;

    // adauga asta inainte sa setezi proprietatile mailului
    private void loadCredentials() {
        try (java.io.InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) {
                Properties prop = new Properties();
                prop.load(is);
                this.senderEmail = prop.getProperty("mail.email");
                this.senderPassword = prop.getProperty("mail.password");
            } else {
                LOG.warning("Could not find config.properties");
            }
        } catch (Exception e) {
            LOG.severe("Error reading password: " + e.getMessage());
        }
    }

    // metoda care construieste si trimite emailul
    public void sendPriceAlertEmail(String recipientEmail, String productName, double oldPrice, double newPrice) {
        loadCredentials();
        LOG.info("preparing to send email to " + recipientEmail + " for product " + productName);


        // setam proprietatile pentru serverul smtp de la google folosind ssl
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");

        // folosim ssl direct
        properties.put("mail.smtp.ssl.enable", "true");
        // spunem aplicatiei java sa aiba incredere oarba in certificatul google
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.put("mail.debug", "true");

        // cream o sesiune de mail si ne autentificam
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            // cream mesajul efectiv
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));

            // setam subiectul(titlul)
            message.setSubject("Price Drop Alert " + productName);

            // construim continutul emailului folosind html pentru a arata frumos
            String emailContent = "<h3>Good news!</h3>"
                    + "<p>The product <strong>" + productName + "</strong> from your watchlist has dropped in price!</p>"
                    + "<p>Old price: <del>" + oldPrice + " RON</del></p>"
                    + "<p style='color: green; font-size: 18px; font-weight: bold;'>New price: " + newPrice + " RON</p>"
                    + "<br><p>Check your dashboard to see the product.</p>";

            // setam formatul la html
            message.setContent(emailContent, "text/html; charset=utf-8");

            // trimitem emailul catre server
            Transport.send(message);

            LOG.info("email successfully sent to " + recipientEmail);

        } catch (Exception e) {
            // prindem orice eroare si o punem in loguri
            LOG.severe("failed to send email to " + recipientEmail + " error " + e.getMessage());
        }
    }
}