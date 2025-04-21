package Services;
import Models.Game;
import Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static Utils.DataSource.connection;

public class GameService1 implements IService<Game> {
    private Connection conn;

    public GameService1() throws SQLException {
        conn = DataSource.getInstance();
    }

    @Override
    public void ajouter(Game g) throws SQLException {
        String req = "INSERT INTO game (id, nom, prenom, type, date) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(req);
        ps.setInt(1, g.getId());
        ps.setString(2, g.getNom());
        ps.setString(3, g.getPrenom());
        ps.setString(4, g.getType());
        ps.setDate(5, g.getDate());
        ps.executeUpdate();
        System.out.println("Game ajouté avec PreparedStatement !");
    }

    @Override
    public void modifier(Game g) throws SQLException {
        String req = "UPDATE game SET nom = ?, prenom = ?, type = ?, date = ? WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(req);
        ps.setString(1, g.getNom());
        ps.setString(2, g.getPrenom());
        ps.setString(3, g.getType());
        ps.setDate(4, g.getDate());
        ps.setInt(5, g.getId());
        ps.executeUpdate();
        System.out.println("Game modifié avec PreparedStatement !");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM game WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(req);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("Game supprimé avec PreparedStatement !");
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
    public Game getGameById(int id) {
        Game game = null;
        String query = "SELECT * FROM game WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                game = new Game(
                        rs.getInt("id"),
                        rs.getString("nom")
                        // Add other fields if needed
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return game;  // Will return null if no game is found
    }

}
