package Services;

import Models.Feedback;
import Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackService implements IService<Feedback> {
    private Connection conn;

    public FeedbackService() throws SQLException {
        conn = DataSource.getInstance();
    }

    @Override
    public void ajouter(Feedback f) throws SQLException {
        String sql = "INSERT INTO feedback (id, id_jeux, nom, prenom, feedback, rating) VALUES (" +
                f.getId() + ", " + f.getIdJeux() + ", '" +
                f.getNom() + "', '" + f.getPrenom() + "', '" +
                f.getFeedback() + "', " + f.getRating() + ")";
        Statement st = conn.createStatement();
        st.executeUpdate(sql);
    }

    @Override
    public void modifier(Feedback f) throws SQLException {
        String sql = "UPDATE feedback SET id_jeux = " + f.getIdJeux() +
                ", nom = '" + f.getNom() +
                "', prenom = '" + f.getPrenom() +
                "', feedback = '" + f.getFeedback() +
                "', rating = " + f.getRating() +
                " WHERE id = " + f.getId();
        Statement st = conn.createStatement();
        st.executeUpdate(sql);
    }

    @Override
    public void supprimer(int id) throws SQLException {
        Statement st = conn.createStatement();
        st.executeUpdate("DELETE FROM feedback WHERE id = " + id);
    }

    @Override
    public List<Feedback> afficher() throws SQLException {
        List<Feedback> list = new ArrayList<>();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM feedback");
        while (rs.next()) {
            list.add(new Feedback(
                    rs.getInt("id"),
                    rs.getInt("id_jeux"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("feedback"),
                    rs.getInt("rating")
            ));
        }
        return list;
    }
}
