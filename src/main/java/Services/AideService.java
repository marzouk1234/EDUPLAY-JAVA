package Services;

import Models.Aide;
import Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AideService implements IService<Aide> {
    Connection con;

    public AideService() {
        con = DataSource.getDataSource().getConnection();
    }

    @Override
    public void add(Aide aide) throws SQLException {
        String query = "INSERT INTO aide (sujet, description, date_creation, form_id) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, aide.getSujet());
        ps.setString(2, aide.getDescription());
        ps.setTimestamp(3, Timestamp.valueOf(aide.getDateCreation()));
        ps.setInt(4, aide.getFormId());
        ps.executeUpdate();
    }

    @Override
    public void update(Aide aide) throws SQLException {
        String query = "UPDATE aide SET sujet = ?, description = ?, date_creation = ?, form_id = ? WHERE id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, aide.getSujet());
        ps.setString(2, aide.getDescription());
        ps.setTimestamp(3, Timestamp.valueOf(aide.getDateCreation()));
        ps.setInt(4, aide.getFormId());
        ps.setInt(5, aide.getId());
        ps.executeUpdate();
    }

    @Override
    public void delete(Aide aide) throws SQLException {
        String query = "DELETE FROM aide WHERE id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, aide.getId());
        ps.executeUpdate();
    }

    @Override
    public List<Aide> getAll() throws SQLException {
        String query = "SELECT * FROM aide";
        Statement statement = con.createStatement();
        ResultSet rs = statement.executeQuery(query);
        List<Aide> aides = new ArrayList<>();

        while (rs.next()) {
            Aide aide = new Aide();
            aide.setId(rs.getInt("id"));
            aide.setSujet(rs.getString("sujet"));
            aide.setDescription(rs.getString("description"));
            aide.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
            aide.setFormId(rs.getInt("form_id"));
            aides.add(aide);
        }

        return aides;
    }

    public List<Aide> getAllWithFormDetails() throws SQLException {
        String query = "SELECT a.*, f.sujet as form_sujet, f.contenu, f.date_pub, f.auteur " +
                      "FROM aide a " +
                      "LEFT JOIN form_p f ON a.form_id = f.id";
        Statement statement = con.createStatement();
        ResultSet rs = statement.executeQuery(query);
        List<Aide> aides = new ArrayList<>();

        while (rs.next()) {
            Aide aide = new Aide();
            aide.setId(rs.getInt("id"));
            aide.setSujet(rs.getString("sujet"));
            aide.setDescription(rs.getString("description"));
            aide.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
            aide.setFormId(rs.getInt("form_id"));
            
            aides.add(aide);
        }

        return aides;
    }

    public Aide getByIdWithFormDetails(int aideId) throws SQLException {
        String query = "SELECT a.*, f.sujet as form_sujet, f.contenu, f.date_pub, f.auteur " +
                      "FROM aide a " +
                      "LEFT JOIN form_p f ON a.form_id = f.id " +
                      "WHERE a.id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, aideId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Aide aide = new Aide();
            aide.setId(rs.getInt("id"));
            aide.setSujet(rs.getString("sujet"));
            aide.setDescription(rs.getString("description"));
            aide.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
            aide.setFormId(rs.getInt("form_id"));
            return aide;
        }
        return null;
    }
}
