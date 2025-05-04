package Services;

import Models.Ticket;
import Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketService implements IService<Ticket> {
    Connection con;

    public TicketService() throws SQLException { // 👈 Déclarez explicitement l'exception
        con = DataSource.getDataSource().getConnection();
    }

    @Override
    public void add(Ticket ticket) throws SQLException {
        String query = "INSERT INTO ticket (event_id, purchase_date, email, nom) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, ticket.getEvent_id());
            pstmt.setDate(2, Date.valueOf(ticket.getPurchase_date()));
            pstmt.setString(3, ticket.getEmail());
            pstmt.setString(4, ticket.getNom());

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