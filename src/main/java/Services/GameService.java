package Services;
import Models.Game;
import Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameService implements IService<Game> {
    private Connection conn;

    public GameService() throws SQLException {
        conn = DataSource.getInstance();
    }

    @Override
    public void ajouter(Game g) throws SQLException {
        String req = "INSERT INTO game (id, nom, prenom, type, date) VALUES (" +
                g.getId() + ", '" +
                g.getNom() + "', '" +
                g.getPrenom() + "', '" +
                g.getType() + "', '" +
                g.getDate() + "')";
        Statement st = conn.createStatement();
        st.executeUpdate(req);
        System.out.println("Game ajouté avec Statement !");
    }

    @Override
    public void modifier(Game g) throws SQLException {
        String req = "UPDATE game SET nom = '" + g.getNom() +
                "', prenom = '" + g.getPrenom() +
                "', type = '" + g.getType() +
                "', date = '" + g.getDate() +
                "' WHERE id = " + g.getId();
        Statement st = conn.createStatement();
        st.executeUpdate(req);
        System.out.println("Game modifié avec Statement !");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM game WHERE id = " + id;
        Statement st = conn.createStatement();
        st.executeUpdate(req);
        System.out.println("Game supprimé avec Statement !");
    }

    @Override
    public List<Game> afficher() throws SQLException {
        List<Game> list = new ArrayList<>();
        String req = "SELECT * FROM game";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            Game g = new Game(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("type"),
                    rs.getDate("date")
            );
            list.add(g);
        }
        return list;
    }
}
