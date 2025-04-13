/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this temevaluatione file, choose Tools | Temevaluationes
 * and open the temevaluatione in the editor.
 */
package com.example.demo4.Controller;

import com.example.demo4.entities.resultat;

import java.io.IOException;

import com.example.demo4.services.resultatService;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;


/**
 * FXML Controller class
 *
 * @author asus
 */
public class AfficherresultatController implements Initializable {
    @FXML private TableView<resultat> tableresultat;
    @FXML private TableColumn<resultat, String> appreciationevTv;
    @FXML private TableColumn<resultat, String> startTv;

    @FXML private TableColumn<resultat, Integer> eventTv;
    @FXML private TableColumn<resultat, Double> noteTv;

    @FXML private TextField appreciationevField;
    @FXML private DatePicker startDatePicker;
    @FXML private TextField noteField;

    @FXML private TextField idevField;



    resultatService Ps = new resultatService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        getResultats();
    }
    @FXML
    private void retour(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("index.fxml")));
            startDatePicker.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("Erreur lors du retour à la page d'accueil : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void getResultats() {
        try {
            List<resultat> list = Ps.recupererResultats();
            ObservableList<resultat> data = FXCollections.observableArrayList(list);
            tableresultat.setItems(data);

            appreciationevTv.setCellValueFactory(new PropertyValueFactory<>("appreciation"));
            startTv.setCellValueFactory(new PropertyValueFactory<>("date_creation"));
            noteTv.setCellValueFactory(new PropertyValueFactory<>("note"));

            eventTv.setCellValueFactory(new PropertyValueFactory<>("evaluationId"));
        } catch (SQLException ex) {
            System.out.println("Erreur : " + ex.getMessage());
        }
    }

    @FXML
    private void choisirresultat(MouseEvent event) {
        resultat s = tableresultat.getSelectionModel().getSelectedItem();
        if (s != null) {
            appreciationevField.setText(s.getAppreciation());
            startDatePicker.setValue(s.getDate_creation().toLocalDate());
            noteField.setText(String.valueOf(s.getNote()));

            idevField.setText(String.valueOf(s.getEvaluationId()));
        }
    }

    @FXML
    private void modifierresultat(ActionEvent event) {
        resultat s = tableresultat.getSelectionModel().getSelectedItem();
        if (s != null) {
            s.setAppreciation(appreciationevField.getText());
            s.setDate_creation(startDatePicker.getValue().atStartOfDay());

            s.setEvaluationId(Integer.parseInt(idevField.getText()));

            try {
                double noteValue = Double.parseDouble(noteField.getText());
                s.setNote(noteValue);
            } catch (NumberFormatException e) {
                System.out.println("Note invalide : " + e.getMessage());
                // Vous pouvez ajouter un Alert pour prévenir l'utilisateur
                return;
            }
            Ps.modifierresultat(s);
            getResultats();
        }
    }

    @FXML
    private void supprimerresultat(ActionEvent event) {
        resultat s = tableresultat.getSelectionModel().getSelectedItem();
        if (s != null) {
            Ps.supprimerresultat(s.getId());
            getResultats();
        }
    }



}


