package com.example.demo4.services;

import com.example.demo4.entities.resultat;
import com.example.demo4.utils.MyDB;
import com.example.demo4.entities.Etudiant;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class resultatService {

    private static final Logger logger = Logger.getLogger(resultatService.class.getName());

    public resultatService() {
        // Initialisation sans garder de connexion persistante
    }

    /**
     * Ajouter un nouveau résultat en base de données
     */
    public boolean evaluationExiste(int evaluationId) throws SQLException {
        String sql = "SELECT 1 FROM evaluation WHERE id = ?";
        try (Connection conn = MyDB.getInstance().getCnx();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, evaluationId);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        }
    }
    public boolean ajouterresultat(resultat s) throws SQLException {
        if (!evaluationExiste(s.getEvaluationId())) {
            throw new SQLException("L'évaluation " + s.getEvaluationId() + " n'existe pas");
        }

        String sql = "INSERT INTO resultat (evaluation_id, note, appreciation, date_creation) VALUES (?, ?, ?, ?)";
        try (Connection conn = MyDB.getInstance().getCnx();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, s.getEvaluationId());
            pst.setDouble(2, s.getNote());
            pst.setString(3, s.getAppreciation());
            pst.setTimestamp(4, Timestamp.valueOf(s.getDate_creation()));

            return pst.executeUpdate() > 0;
        }
    }
    /**
     * Récupérer la liste de tous les résultats
     */
    public List<resultat> recupererResultats() throws SQLException {
        List<resultat> resultats = new ArrayList<>();
        String sql = "SELECT * FROM resultat";

        try (Connection conn = MyDB.getInstance().getCnx();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                resultat s = new resultat();
                s.setId(rs.getInt("id"));
                s.setEvaluationId(rs.getInt("evaluation_id"));
                s.setNote(rs.getDouble("note"));
                s.setAppreciation(rs.getString("appreciation"));
                s.setDate_creation(rs.getTimestamp("date_creation").toLocalDateTime());
                resultats.add(s);
            }
        }
        return resultats;
    }

    /**
     * Récupérer l'ID de l'étudiant à partir d'un résultat
     */
    public int recupererEtudiantIdParResultat(int resultatId) throws SQLException {
        String sql = "SELECT e.etudiant_id FROM resultat r " +
                "JOIN evaluation e ON r.evaluation_id = e.id " +
                "WHERE r.id = ?";

        try (Connection conn = MyDB.getInstance().getCnx();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, resultatId);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() ? rs.getInt("etudiant_id") : -1;
            }
        }
    }

    /**
     * Supprimer un résultat par son ID
     */
    public boolean supprimerresultat(int id) throws SQLException {
        String sql = "DELETE FROM resultat WHERE id = ?";

        try (Connection conn = MyDB.getInstance().getCnx();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        }
    }

    /**
     * Modifier un résultat existant
     */
    public boolean modifierresultat(resultat s) throws SQLException {
        String sql = "UPDATE resultat SET evaluation_id = ?, note = ?, appreciation = ?, date_creation = ? WHERE id = ?";

        try (Connection conn = MyDB.getInstance().getCnx();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, s.getEvaluationId());
            pst.setDouble(2, s.getNote());
            pst.setString(3, s.getAppreciation());
            pst.setTimestamp(4, Timestamp.valueOf(s.getDate_creation()));
            pst.setInt(5, s.getId());

            return pst.executeUpdate() > 0;
        }
    }

    /**
     * Vérifie si un étudiant possède des résultats en base de données
     */
    public boolean etudiantHasResultats(int etudiantId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM resultat r " +
                "JOIN evaluation e ON r.evaluation_id = e.id " +
                "WHERE e.etudiant_id = ?";

        try (Connection conn = MyDB.getInstance().getCnx();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, etudiantId);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public resultat recupererResultatParEtudiantId(int etudiantId) throws SQLException {
        String sql = "SELECT r.* FROM resultat r " +
                "JOIN evaluation e ON r.evaluation_id = e.id " +
                "WHERE e.etudiant_id = ? LIMIT 1";

        try (Connection conn = MyDB.getInstance().getCnx();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, etudiantId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    resultat res = new resultat();
                    res.setId(rs.getInt("id"));
                    res.setEvaluationId(rs.getInt("evaluation_id"));
                    res.setNote(rs.getDouble("note"));
                    res.setAppreciation(rs.getString("appreciation"));
                    res.setDate_creation(rs.getTimestamp("date_creation").toLocalDateTime());
                    return res;
                }
            }
        }
        return null;
    }

    public List<resultat> recupererResultatsParEtudiantId(int etudiantId) throws SQLException {
        List<resultat> resultats = new ArrayList<>();
        String sql = "SELECT r.* FROM resultat r " +
                "JOIN evaluation e ON r.evaluation_id = e.id " +
                "WHERE e.etudiant_id = ?";

        try (Connection conn = MyDB.getInstance().getCnx();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, etudiantId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    resultat res = new resultat();
                    res.setId(rs.getInt("id"));
                    res.setEvaluationId(rs.getInt("evaluation_id"));
                    res.setNote(rs.getDouble("note"));
                    res.setAppreciation(rs.getString("appreciation"));
                    res.setDate_creation(rs.getTimestamp("date_creation").toLocalDateTime());
                    resultats.add(res);
                }
            }
        }
        return resultats;
    }

    public List<Etudiant> recupererTousLesEtudiants() throws SQLException {
        List<Etudiant> etudiants = new ArrayList<>();
        String sql = "SELECT * FROM etudiant";

        try (Connection conn = MyDB.getInstance().getCnx();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Etudiant etudiant = new Etudiant();
                etudiant.setId(rs.getInt("id"));
                etudiant.setEmail(rs.getString("email"));
                etudiant.setnom_et_prenom(rs.getString("nom_et_prenom"));
                etudiant.setTel(rs.getString("tel"));
                etudiants.add(etudiant);
            }
        }
        return etudiants;
    }
}