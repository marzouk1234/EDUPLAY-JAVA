package Services;

import Models.Ticket;
import Utils.DataSource;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class TicketService implements IService<Ticket> {
    Connection con;
    private static final String SMS_API_URL = "https://51zkyy.api.infobip.com/sms/2/text/advanced";
    private static final String API_KEY = "3bcee6fa3de16b93a0a51a423b31b1a5-ed3576b5-2f41-4c56-ab12-d15c60a95e90";
    private static final String SENDER_ID = "447491163443";
    private static final String RECIPIENT_NUMBER = "21692299012";
    public TicketService() throws SQLException { // 👈 Déclarez explicitement l'exception
        con = DataSource.getDataSource().getConnection();
    }


    private void sendSmsNotification(Ticket ticket) {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            String jsonPayload = String.format(
                    "{\"messages\":[{\"destinations\":[{\"to\":\"%s\"}],\"from\":\"%s\",\"text\":\"You have created ticket with success for %s\"}]}",
                    RECIPIENT_NUMBER, SENDER_ID, ticket.getNom());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SMS_API_URL))
                    .header("Authorization", "App " + API_KEY)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // You can log the response if needed
            System.out.println("SMS API Response Code: " + response.statusCode());
            System.out.println("SMS API Response Body: " + response.body());
        } catch (Exception e) {
            // Log the error but don't stop the application flow
            System.err.println("Failed to send SMS notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @Override
    public void add(Ticket ticket) throws SQLException {
        String query = "INSERT INTO ticket (event_id, puchase_date, email, nom) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, ticket.getEvent_id());
            pstmt.setString(2, ticket.getPurchase_date());
            pstmt.setString(3, ticket.getEmail());
            pstmt.setString(4, ticket.getNom());
            sendSmsNotification(ticket);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void update(Ticket ticket) throws SQLException {
        String query = "UPDATE ticket SET event_id = ?, purchase_date = ?, email = ?, nom = ? WHERE id = ?";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, ticket.getEvent_id());
            pstmt.setDate(2, Date.valueOf(ticket.getPurchase_date()));
            pstmt.setString(3, ticket.getEmail());
            pstmt.setString(4, ticket.getNom());
            pstmt.setInt(5, ticket.getId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(Ticket ticket) throws SQLException {
        String query = "DELETE FROM ticket WHERE id = " + ticket.getId();
        Statement statement = con.createStatement();
        statement.executeUpdate(query);
    }

    @Override
    public List<Ticket> getAll() throws SQLException {
        String query = "SELECT * FROM ticket";
        Statement statement = con.createStatement();
        ResultSet rs = statement.executeQuery(query);

        List<Ticket> tickets = new ArrayList<>();
        while (rs.next()) {
            Ticket ticket = new Ticket();
            ticket.setId(rs.getInt("id"));
            ticket.setEvent_id(rs.getInt("event_id"));
            ticket.setPurchase_date(rs.getString("purchase_date"));
            ticket.setEmail(rs.getString("email"));
            ticket.setNom(rs.getString("nom"));

            tickets.add(ticket);
        }
        return tickets;
    }

    // Méthode supplémentaire pour récupérer les tickets d'un événement spécifique
    public List<Ticket> getTicketsByEvent(int eventId) throws SQLException {
        String query = "SELECT * FROM ticket WHERE event_id = ?";
        List<Ticket> tickets = new ArrayList<>();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Ticket ticket = new Ticket();
                ticket.setId(rs.getInt("id"));
                ticket.setEvent_id(rs.getInt("event_id"));
                ticket.setPurchase_date(rs.getString("purchase_date"));
                ticket.setEmail(rs.getString("email"));
                ticket.setNom(rs.getString("nom"));

                tickets.add(ticket);
            }
        }
        return tickets;
    }
}