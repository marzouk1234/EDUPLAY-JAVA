package Services;

import Models.Form_p;
import Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Form_pService implements IService<Form_p> {
    private Connection con;

    public Form_pService() {
        try {
            con = DataSource.getDataSource().getConnection();
            if (con == null) {
                throw new SQLException("Impossible d'établir une connexion à la base de données");
            }
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données: " + e.getMessage());
        }
    }

    @Override
    public void add(Form_p formP) throws SQLException {
        if (con == null) {
            throw new SQLException("Pas de connexion à la base de données");
        }
        
        String query = "INSERT INTO form_p (contenu, date_pub, sujet, auteur) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, formP.getContenu());
            ps.setDate(2, Date.valueOf(formP.getDatePub()));
            ps.setString(3, formP.getSujet());
            ps.setString(4, formP.getAuteur());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La création du formulaire a échoué, aucune ligne affectée.");
            }
            
            // Récupérer l'ID généré
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    formP.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("La création du formulaire a échoué, aucun ID obtenu.");
                }
            }
        }
    }

    @Override
    public void update(Form_p formP) throws SQLException {
        if (con == null) {
            throw new SQLException("Pas de connexion à la base de données");
        }
        
        if (formP.getId() <= 0) {
            throw new SQLException("ID de formulaire invalide pour la mise à jour");
        }
        
        String query = "UPDATE form_p SET contenu = ?, date_pub = ?, sujet = ?, auteur = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, formP.getContenu());
            ps.setDate(2, Date.valueOf(formP.getDatePub()));
            ps.setString(3, formP.getSujet());
            ps.setString(4, formP.getAuteur());
            ps.setInt(5, formP.getId());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La mise à jour du formulaire a échoué, aucune ligne affectée.");
            }
        }
    }

    @Override
    public void delete(Form_p formP) throws SQLException {
        if (con == null) {
            throw new SQLException("Pas de connexion à la base de données");
        }
        
        if (formP.getId() <= 0) {
            throw new SQLException("ID de formulaire invalide pour la suppression");
        }
        
        String query = "DELETE FROM form_p WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, formP.getId());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La suppression du formulaire a échoué, aucune ligne affectée.");
            }
        }
    }

    @Override
    public List<Form_p> getAll() throws SQLException {
        if (con == null) {
            throw new SQLException("Pas de connexion à la base de données");
        }
        
        List<Form_p> formPS = new ArrayList<>();
        String query = "SELECT * FROM form_p ORDER BY id DESC";
        try (PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Form_p formP = new Form_p();
                formP.setId(rs.getInt("id"));
                formP.setContenu(rs.getString("contenu"));
                
                // Gestion des valeurs NULL dans la base de données
                Date dateDb = rs.getDate("date_pub");
                if (dateDb != null) {
                    formP.setDatePub(dateDb.toLocalDate());
                }
                
                formP.setSujet(rs.getString("sujet"));
                formP.setAuteur(rs.getString("auteur"));
                formPS.add(formP);
            }
        }
        return formPS;
    }

    // Méthode pour récupérer un formulaire par son id
    public Form_p getById(int id) throws SQLException {
        if (con == null) {
            throw new SQLException("Pas de connexion à la base de données");
        }
        
        if (id <= 0) {
            throw new SQLException("ID de formulaire invalide");
        }
        
        String query = "SELECT * FROM form_p WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Form_p formP = new Form_p();
                    formP.setId(rs.getInt("id"));
                    formP.setContenu(rs.getString("contenu"));
                    
                    // Gestion des valeurs NULL dans la base de données
                    Date dateDb = rs.getDate("date_pub");
                    if (dateDb != null) {
                        formP.setDatePub(dateDb.toLocalDate());
                    }
                    
                    formP.setSujet(rs.getString("sujet"));
                    formP.setAuteur(rs.getString("auteur"));
                    return formP;
                }
            }
        }
        return null; // Si aucun formulaire n'est trouvé
    }
    
    // Fermeture de connexion - importante pour éviter les fuites de ressources
    public void closeConnection() {
        if (con != null) {
            try {
                con.close();
                System.out.println("Connexion fermée");
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
            }
        }
    }
}
