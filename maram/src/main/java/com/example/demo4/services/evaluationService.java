package com.example.demo4.services;

import com.example.demo4.entities.evaluation;
import com.example.demo4.utils.MyDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class evaluationService implements IevaluationService<evaluation> {

    private final Connection cnx;
    private PreparedStatement pst;

    public evaluationService() {
        cnx = MyDB.getInstance().getCnx();
    }


    @Override
    public void ajouterevaluation(evaluation e) throws SQLException {
        String requete = "INSERT INTO evaluation (titre, type, date, image) VALUES (  ?, ?, ?, ?)";
        pst = cnx.prepareStatement(requete);
        pst.setString(1, e.getTitre());
        pst.setString(2, e.getType());
        pst.setTimestamp(3, Timestamp.valueOf(e.getDate()));
        pst.setString(4, e.getImage());

        pst.executeUpdate();
        System.out.println("evaluation ajouté avec succès");
    }

    @Override
    public void modifierevaluation(evaluation e) throws SQLException {
        String req = "UPDATE evaluation SET titre = ?, type = ?, date = ?, image = ? WHERE id = ?";
        pst = cnx.prepareStatement(req);
        pst.setString(1, e.getTitre());
        pst.setString(2, e.getType());
        pst.setTimestamp(3, Timestamp.valueOf(e.getDate()));
        pst.setString(4, e.getImage());

        pst.setInt(5, e.getId());
        pst.executeUpdate();
        System.out.println("evaluation modifié avec succès");
    }

    @Override
    public void supprimerevaluation(evaluation e) throws SQLException {
        String req = "DELETE FROM evaluation WHERE id = ?";
        pst = cnx.prepareStatement(req);
        pst.setInt(1, e.getId());
        pst.executeUpdate();
        System.out.println("evaluation supprimé avec succès");
    }

    @Override
    public List<evaluation> recupererevaluation() throws SQLException {
        List<evaluation> liste = new ArrayList<>();
        String s = "SELECT * FROM evaluation";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(s);
        while (rs.next()) {
            evaluation e = new evaluation(
                    rs.getInt("id"),
                    rs.getString("titre"),
                    rs.getString("type"),
                    rs.getTimestamp("date").toLocalDateTime(),
                    rs.getString("image")
            );
            liste.add(e);
        }
        return liste;
    }

    public ObservableList<evaluation> chercherev(String chaine) {
        ObservableList<evaluation> myList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM evaluation WHERE titre LIKE ? ORDER BY titre";
        try {
            pst = cnx.prepareStatement(sql);
            pst.setString(1, "%" + chaine + "%");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                evaluation e = new evaluation(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("type"),
                        rs.getTimestamp("date").toLocalDateTime(),
                        rs.getString("image")

                );
                myList.add(e);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return myList;
    }

    public List<evaluation> trierev() throws SQLException {
        List<evaluation> liste = new ArrayList<>();
        String s = "SELECT * FROM evaluation ORDER BY titre";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(s);
        while (rs.next()) {
            evaluation e = new evaluation(
                    rs.getInt("id"),
                    rs.getString("titre"),
                    rs.getString("type"),
                    rs.getTimestamp("date").toLocalDateTime(),
                    rs.getString("image")
            );
            liste.add(e);
        }
        return liste;
    }




}
