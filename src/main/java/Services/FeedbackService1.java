package Services;

import Models.Feedback;
import Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackService1 implements IService<Feedback> {
    private Connection conn;

    public FeedbackService1() throws SQLException {
        conn = DataSource.getInstance();
    }

    @Override
    public void ajouter(Feedback f) throws SQLException {
        String sql = "INSERT INTO feedback (id, id_jeux, nom, prenom, feedback, rating) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, f.getId());
        ps.setInt(2, f.getIdJeux());
        ps.setString(3, f.getNom());
        ps.setString(4, f.getPrenom());
        ps.setString(5, f.getFeedback());
        if (f.getRating() != null) {
            ps.setInt(6, f.getRating());
        } else {
            ps.setNull(6, Types.INTEGER);
        }
        ps.executeUpdate();
        System.out.println("Feedback ajouté avec PreparedStatement !");
    }

    @Override
    public void modifier(Feedback f) throws SQLException {
        String sql = "UPDATE feedback SET id_jeux = ?, nom = ?, prenom = ?, feedback = ?, rating = ? WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, f.getIdJeux());
        ps.setString(2, f.getNom());
        ps.setString(3, f.getPrenom());
        ps.setString(4, f.getFeedback());
        if (f.getRating() != null) {
            ps.setInt(5, f.getRating());
        } else {
            ps.setNull(5, Types.INTEGER);
        }
        ps.setInt(6, f.getId());
        ps.executeUpdate();
        System.out.println("Feedback modifié avec PreparedStatement !");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM feedback WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("Feedback supprimé avec PreparedStatement !");
    }

    @Override
    public List<Feedback> afficher() throws SQLException {
        List<Feedback> list = new ArrayList<>();
        String sql = "SELECT * FROM feedback";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Feedback f = new Feedback(
                    rs.getInt("id"),
                    rs.getInt("id_jeux"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("feedback"),
                    rs.getInt("rating")
            );
            list.add(f);
        }

        return list;
    }
}
