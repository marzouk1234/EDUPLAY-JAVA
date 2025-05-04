package Services;

import Models.Event;
import Utils.DataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;

public class EventService implements IService<Event> {
    Connection con;

    public EventService() {
        con = DataSource.getDataSource().getConnection();
    }

    @Override
    public void add(Event event) throws SQLException {
        // Correction 1 : Utiliser des guillemets doubles
        String query = "INSERT INTO event (nom, description, date, IMG) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            // Correction 2 : Définir tous les paramètres
            pstmt.setString(1, event.getNom());         // Premier "?"
            pstmt.setString(2, event.getDescription()); // Deuxième "?"
            pstmt.setString(3, event.getDate());        // Troisième "?"
            pstmt.setBytes(4, event.getIMG());        // Quatrième "?"

            pstmt.executeUpdate(); // Exécuter la requête
        }
    }

    @Override
    public void update(Event event) throws SQLException {
        String query = "UPDATE event SET nom = ?, description = ?, date = ?, IMG = ? WHERE id = ?";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, event.getNom());
            pstmt.setString(2, event.getDescription());
            pstmt.setString(3, event.getDate());
            pstmt.setBytes(4, event.getIMG());
            pstmt.setInt(5, event.getId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(Event event) throws SQLException { // Changement du paramètre Person -> Event
        String query = "DELETE FROM event WHERE id = " + event.getId();
        Statement statement = con.createStatement();
        statement.executeUpdate(query);
    }

    @Override
    public List<Event> getAll() throws SQLException { // Changement du retour Person -> Event
        String query = "SELECT * FROM event";
        Statement statement = con.createStatement();
        ResultSet rs = statement.executeQuery(query);

        List<Event> events = new ArrayList<>();
        while (rs.next()) {
            Event event = new Event();
            event.setId(rs.getInt("id"));
            event.setNom(rs.getString("nom"));
            event.setDescription(rs.getString("description"));
            event.setDate(rs.getString("date"));
            event.setIMG(rs.getBytes("IMG"));
            events.add(event);
        }
        return events;
    }
}