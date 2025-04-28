package com.example.demo4.services;
import com.example.demo4.utils.MyDB;


import java.sql.*;
import java.time.LocalDate;
import java.util.Properties;
import jakarta.mail.*;

import com.example.demo4.entities.User;

import jakarta.mail.Authenticator;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ServiceUser {

    private Connection connection = MyDB.getInstance().getConnection();;

    public ServiceUser() {
        // TODO Auto-generated constructor stub
    }




    public User findUserByUsername(String prenom) {
        User user = null;
        String query = "SELECT * FROM user WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, prenom);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String email = rs.getString("email");
                    String roles = rs.getString("roles");
                    String password = rs.getString("password");
                    String firstname = rs.getString("prenom");
                    String lastname = rs.getString("nom");
                    String phonenumber = rs.getString("tel");
                    String image = rs.getString("image");
                    Date sqlDate = rs.getDate("date_naissance");
                    LocalDate dateNaissance = (sqlDate != null) ? sqlDate.toLocalDate() : null;

                    user = new User(id, email, roles, password,
                            firstname, lastname,
                            phonenumber, image,
                            dateNaissance);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
    public void modifierfront(User user, int id) {
        String query = "UPDATE user SET prenom = ?, nom = ?, tel = ?, email = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getPrenom());
            statement.setString(2, user.getNom());
            statement.setString(3, user.getTel());
            statement.setString(4, user.getEmail());
            statement.setInt(5, id);

            statement.executeUpdate();
            System.out.println("User mis à jour (front) avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors du modifierfront : " + e.getMessage());
        }
    }


    public void ajouter(User user, String role) {
        String query = "INSERT INTO user (email, roles, password, is_verified, prenom, nom, tel, image, date_naissance) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getEmail());
            // Ici on transforme "ROLE_ENS" en un tableau JSON comme ["ROLE_ENS"]
            statement.setString(2, "[\"" + role + "\"]");

            statement.setString(3, user.getPassword());
            statement.setInt(4, 0);
            statement.setString(5, user.getPrenom());
            statement.setString(6, user.getNom());
            statement.setString(7, user.getTel());
            statement.setString(8, user.getImage());

            if (user.getDateNaissance() != null) {
                statement.setDate(9, Date.valueOf(user.getDateNaissance()));
            } else {
                statement.setNull(9, Types.DATE);
            }

            statement.executeUpdate();
            System.out.println("Utilisateur ajouté avec succès.");
        } catch (SQLException ex) {
            System.out.println("Erreur SQL : " + ex.getMessage());
        }
    }


    private void sendEmail(String to, String subject, String body) {
        final String username = "";
        final String password = "";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Email envoyé avec succès à " + to);
        } catch (MessagingException e) {
            System.err.println("Erreur d'envoi d'email : " + e.getMessage());
        }
    }





    public void supprimer(int id) {
        String query = "DELETE FROM user WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            System.out.println("ok");
        } catch (SQLException e) {
            System.err.println("Error executing SQL query: " + e.getMessage());
        }
    }


    public void Update(User user, int id) {
        String query = "UPDATE user SET prenom = ? ,nom=?,tel=? WHERE id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1,user.getPrenom());
            statement.setString(2,user.getNom());
            statement.setString(3,user.getTel());
            statement.setInt(4, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error executing SQL query: " + e.getMessage());
        }
    }

    public ObservableList<User> afficherTous() {

        ObservableList<User> users = FXCollections.observableArrayList();

        try   { String query = "SELECT * FROM user";
            Statement statement = connection.createStatement();


            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String email = resultSet.getString("email");
                String roles = resultSet.getString("roles");
                String password = resultSet.getString("password");
                String firstname = resultSet.getString("prenom");
                String lastname = resultSet.getString("nom");
                String phonenumber = resultSet.getString("tel");
                String image = resultSet.getString("image");
                Date sqlDate = resultSet.getDate("date_naissance");
                LocalDate dateNaissance = null;
                if (sqlDate != null) {
                    dateNaissance = sqlDate.toLocalDate();
                }
                User user = new User(id, email, roles, password, firstname, lastname, phonenumber, image, dateNaissance);
                users.add(user);
                System.out.println(user.toString());
            }
        } catch (SQLException e) {
            System.err.println("Error executing SQL query: " + e.getMessage());
        }
        return users;
    }


    public String getHashedPasswordForUser(String username) {
        String hashedPassword = null;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT password FROM user WHERE email = ?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                hashedPassword = rs.getString("password");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hashedPassword;
    }


    public String getRoles(String username) {
        // TODO Auto-generated method stub
        String query="SELECT roles FROM user WHERE email = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("roles");
            }
            System.out.println("bloquerr");
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return null;


    }

    public boolean emailExiste(String email) {
        try {
            String query = "SELECT COUNT(*) FROM user WHERE email = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void updatePassword(String email, String hashedPassword) {
        try {
            String req = "UPDATE user SET password=? WHERE email=?";
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setString(1, hashedPassword);
            ps.setString(2, email);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
