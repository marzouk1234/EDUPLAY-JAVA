package com.example.demo4.Controller;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

public class MailSender {

    public static boolean sendMail(String to, String subject, String content) {
        final String fromEmail = "marzouk.mohamed27082003@gmail.com"; // 🔐 met un mail valide
        final String password = "xori rpus wpvq wsva"; // 🔐 mot de passe d’application

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(content);

            Transport.send(message);
            System.out.println("Email envoyé à : " + to);
            return true; // ✅ succès
        } catch (MessagingException e) {
            e.printStackTrace();
            return false; // ❌ échec
        }
    }
}
