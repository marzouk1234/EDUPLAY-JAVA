package Services;

import Models.Aide;
import Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AideService1 implements IService<Aide> {

    private Connection con;

    public AideService1() {
        con = DataSource.getDataSource().getConnection();
    }

    @Override
    public void add(Aide aide) throws SQLException {
        String query = "INSERT INTO aide (sujet, description, date_creation, form_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, aide.getSujet());
            ps.setString(2, aide.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(aide.getDateCreation()));
            ps.setInt(4, aide.getFormId());
            ps.executeUpdate();
        }
    }

    @Override
    public void update(Aide aide) throws SQLException {
        String query = "UPDATE aide SET sujet = ?, description = ?, date_creation = ?, form_id = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, aide.getSujet());
            ps.setString(2, aide.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(aide.getDateCreation()));
            ps.setInt(4, aide.getFormId());
            ps.setInt(5, aide.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(Aide aide) throws SQLException {
        String query = "DELETE FROM aide WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, aide.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public List<Aide> getAll() throws SQLException {
        List<Aide> aides = new ArrayList<>();
        String query = "SELECT * FROM aide";
        try (PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Aide aide = new Aide();
                aide.setId(rs.getInt("id"));
                aide.setSujet(rs.getString("sujet"));
                aide.setDescription(rs.getString("description"));
                aide.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
                aide.setFormId(rs.getInt("form_id"));
                aides.add(aide);
            }
        }
        return aides;
    }
}
