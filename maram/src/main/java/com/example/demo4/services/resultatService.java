package com.example.demo4.services;

import com.example.demo4.entities.resultat;
import com.example.demo4.utils.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class resultatService {

    private final Connection cnx;
    private Statement ste;
    private PreparedStatement pst;

    public resultatService() {
        cnx = MyDB.getInstance().getCnx();
    }

    /**
     * Ajouter un nouveau résultat en base de données
     */
    public void ajouterresultat(resultat s) {
        // Ajustez la requête pour inclure la colonne "note"
        String req = "INSERT INTO resultat (evaluation_id, note, appreciation, date_creation) VALUES (?, ?, ?, ?)";
        try {
            pst = cnx.prepareStatement(req);
            pst.setInt(1, s.getEvaluationId());
            pst.setDouble(2, s.getNote());
            pst.setString(3, s.getAppreciation());
            pst.setTimestamp(4, Timestamp.valueOf(s.getDate_creation()));

            pst.executeUpdate();
            System.out.println("Resultat ajouté avec succès pour l'EvaluationID: " + s.getEvaluationId());
        } catch (SQLException ex) {
            Logger.getLogger(resultatService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Récupérer la liste de tous les résultats
     */
    public List<resultat> recupererResultats() throws SQLException {
        List<resultat> resultats = new ArrayList<>();
        String req = "SELECT * FROM resultat";
        ste = cnx.createStatement();
        ResultSet rs = ste.executeQuery(req);

        while (rs.next()) {
            resultat s = new resultat();
            s.setId(rs.getInt("id"));
            s.setEvaluationId(rs.getInt("evaluation_id"));
            s.setNote(rs.getDouble("note"));                // <-- Récupération de la note
            s.setAppreciation(rs.getString("appreciation"));
            s.setDate_creation(rs.getTimestamp("date_creation").toLocalDateTime());

            resultats.add(s);
        }
        return resultats;
    }

    /**
     * Supprimer un résultat par son id
     */
    public void supprimerresultat(int id) {
        String req = "DELETE FROM resultat WHERE id = ?";
        try {
            pst = cnx.prepareStatement(req);
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("Resultat supprimé avec ID: " + id);
        } catch (SQLException ex) {
            Logger.getLogger(resultatService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Modifier un résultat existant
     */
    public void modifierresultat(resultat s) {
        // Ajustez la requête pour inclure "note"
        String req = "UPDATE resultat SET evaluation_id = ?, note = ?, appreciation = ?, date_creation = ? WHERE id = ?";
        try {
            pst = cnx.prepareStatement(req);
            pst.setInt(1, s.getEvaluationId());
            pst.setDouble(2, s.getNote());
            pst.setString(3, s.getAppreciation());
            pst.setTimestamp(4, Timestamp.valueOf(s.getDate_creation()));
            pst.setInt(5, s.getId());

            pst.executeUpdate();
            System.out.println("Resultat modifié avec ID: " + s.getId());
        } catch (SQLException ex) {
            Logger.getLogger(resultatService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Récupérer un seul résultat à partir de son id
     */

}
