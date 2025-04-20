package Services;

import Models.Aide;
import Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AideService implements IService<Aide> {
    private Connection con;

    public AideService() {
        con = DataSource.getDataSource().getConnection();
    }

    @Override
    public void add(Aide aide) throws SQLException {
        String query = "INSERT INTO aide (sujet, description, date_creation, form_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, aide.getSujet());
            ps.setString(2, aide.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(aide.getDateCreation()));
            ps.setInt(4, aide.getFormId());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La création de l'aide a échoué, aucune ligne affectée.");
            }
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    aide.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("La création de l'aide a échoué, aucun ID obtenu.");
                }
            }
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
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La mise à jour de l'aide a échoué, aucune ligne affectée.");
            }
        }
    }

    @Override
    public void delete(Aide aide) throws SQLException {
        String query = "DELETE FROM aide WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, aide.getId());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La suppression de l'aide a échoué, aucune ligne affectée.");
            }
        }
    }

    @Override
    public List<Aide> getAll() throws SQLException {
        String query = "SELECT * FROM aide ORDER BY date_creation DESC";
        List<Aide> aides = new ArrayList<>();
        
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                aides.add(createAideFromResultSet(rs));
            }
        }
        
        return aides;
    }

    public List<Aide> getAllWithFormDetails() throws SQLException {
        String query = "SELECT a.*, f.sujet as form_sujet, f.contenu, f.date_pub, f.auteur " +
                      "FROM aide a " +
                      "LEFT JOIN form_p f ON a.form_id = f.id " +
                      "ORDER BY a.date_creation DESC";
        List<Aide> aides = new ArrayList<>();
        
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                aides.add(createAideFromResultSet(rs));
            }
        }
        
        return aides;
    }

    public Aide getById(int id) throws SQLException {
        String query = "SELECT * FROM aide WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return createAideFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public Aide getByIdWithFormDetails(int id) throws SQLException {
        String query = "SELECT a.*, f.sujet as form_sujet, f.contenu, f.date_pub, f.auteur " +
                      "FROM aide a " +
                      "LEFT JOIN form_p f ON a.form_id = f.id " +
                      "WHERE a.id = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return createAideFromResultSet(rs);
                }
            }
        }
        return null;
    }

    private Aide createAideFromResultSet(ResultSet rs) throws SQLException {
        Aide aide = new Aide();
        aide.setId(rs.getInt("id"));
        aide.setSujet(rs.getString("sujet"));
        aide.setDescription(rs.getString("description"));
        aide.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
        aide.setFormId(rs.getInt("form_id"));
        return aide;
    }

    public List<Aide> searchByKeyword(String keyword) throws SQLException {
        String query = "SELECT * FROM aide WHERE sujet LIKE ? OR description LIKE ? ORDER BY date_creation DESC";
        List<Aide> aides = new ArrayList<>();
        
        try (PreparedStatement ps = con.prepareStatement(query)) {
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    aides.add(createAideFromResultSet(rs));
                }
            }
        }
        
        return aides;
    }
}
