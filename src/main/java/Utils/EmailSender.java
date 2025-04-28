package Utils;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import Models.Form_p;

public class EmailSender {

    public static void sendEmail(String to, String subject, String body) {
        // Configuration pour Gmail
        String host = "smtp.gmail.com";
        final String user = "chebbimaram0@gmail.com";
        final String password = "hlsbbpyoruhcweaf";
        int port = 587;

        // Propriétés pour la connexion SMTP
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");

        // Créer une session avec authentification
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        // Activer le débogage
        session.setDebug(true);

        try {
            // Valider l'adresse e-mail
            InternetAddress emailAddr = new InternetAddress(to);
            emailAddr.validate();
            System.out.println("Adresse e-mail validée : " + to);

            // Créer l'objet Message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);

            // Corps du message
            message.setText(body);

            // Envoyer l'email
            Transport.send(message);
            System.out.println("Email envoyé avec succès à : " + to);
        } catch (AddressException e) {
            System.out.println("Adresse e-mail invalide : " + to);
            e.printStackTrace();
        } catch (MessagingException e) {
            System.out.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void sendFormEmail(String to, Form_p form) {
        String subject = "Formulaire: " + form.getSujet();

        StringBuilder body = new StringBuilder()
                .append("Détails du formulaire:\n\n")
                .append("ID: ").append(form.getId()).append("\n")
                .append("Sujet: ").append(form.getSujet()).append("\n")
                .append("Auteur: ").append(form.getAuteur()).append("\n")
                .append("Date: ").append(form.getFormattedDate()).append("\n\n")
                .append("Contenu:\n").append(form.getContenu());

        sendEmail(to, subject, body.toString());
    }
}