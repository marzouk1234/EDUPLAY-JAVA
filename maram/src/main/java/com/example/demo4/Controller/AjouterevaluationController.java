package com.example.demo4.Controller;

import com.example.demo4.entities.evaluation;
import com.example.demo4.services.evaluationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AjouterevaluationController implements Initializable {

    @FXML private TextField idField;
    @FXML private TextField titreField;
    @FXML private DatePicker dateField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField imageField;
    @FXML private ImageView imageview;
    @FXML private TextField rechercher;

    @FXML private TableView<evaluation> evaluationTv;
    @FXML private TableColumn<evaluation, String> titreCol;
    @FXML private TableColumn<evaluation, String> typeCol;
    @FXML private TableColumn<evaluation, LocalDate> dateCol;
    @FXML private TableColumn<evaluation, String> imageCol;

    private final evaluationService Ev = new evaluationService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        typeCombo.setItems(FXCollections.observableArrayList("Exam", "Quiz", "Projet", "Autre"));

        getevs();
    }

    private void reset() {
        idField.clear();
        titreField.clear();
        typeCombo.setValue(null);
        dateField.setValue(null);

        imageField.clear();
        imageview.setImage(null);
    }

    @FXML
    private void ajouterevaluation(ActionEvent ev) {
        if (titreField.getText().isEmpty()  ||
                typeCombo.getValue() == null || dateField.getValue() == null  || imageField.getText().isEmpty()) {

            showAlert(Alert.AlertType.ERROR, "Erreur", "Champs manquants", "Veuillez remplir tous les champs.");
            return;
        }
        if (titreField.getText().length() < 6) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Titre trop court", "Le titre doit contenir au moins 6 caractères.");
            return;
        }
        try {
            evaluation e = new evaluation(
                    titreField.getText(),
                    typeCombo.getValue(), // Récupération de la valeur du comboBox
                    dateField.getValue().atStartOfDay(),
                    imageField.getText()
            );
            Ev.ajouterevaluation(e);
            showAlert(Alert.AlertType.INFORMATION, "Succès", null, "Evaluationajouté avec succès");
            reset();
            getevs();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void modifierevaluation(ActionEvent ev) {
        try {
            evaluation e = new evaluation(
                    Integer.parseInt(idField.getText()),
                    titreField.getText(),
                    typeCombo.getValue(), // Ici aussi
                    dateField.getValue().atStartOfDay(),
                    imageField.getText()
            );
            Ev.modifierevaluation(e);
            showAlert(Alert.AlertType.INFORMATION, "Succès", null, "Evaluationmodifié avec succès");
            reset();
            getevs();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void supprimerevaluation(ActionEvent ev) {
        evaluation e = evaluationTv.getSelectionModel().getSelectedItem();
        if (e != null) {
            try {
                Ev.supprimerevaluation(e);
                showAlert(Alert.AlertType.INFORMATION, "Succès", null, "Evaluationsupprimé avec succès");
                getevs();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    private void choisirev(MouseEvent ev) {
        evaluation e = evaluationTv.getSelectionModel().getSelectedItem();
        if (e != null) {
            idField.setText(String.valueOf(e.getId()));
            titreField.setText(e.getTitre());
            typeCombo.setValue(e.getType());
            dateField.setValue(e.getDate().toLocalDate());
            imageField.setText(e.getImage());


            File imgFile = new File(e.getImage());
            if (imgFile.exists()) imageview.setImage(new Image(imgFile.toURI().toString()));


        }
    }

    @FXML
    private void rechercherev(KeyEvent ev) {
        ObservableList<evaluation> filtered = Ev.chercherev(rechercher.getText());
        evaluationTv.setItems(filtered);
    }

    @FXML
    private void uploadImage(ActionEvent ev) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload Image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            String dest = "C:\\xampp\\htdocs\\imageP\\" + UUID.randomUUID() + ".jpg";
            try (InputStream in = new FileInputStream(file); OutputStream out = new FileOutputStream(dest)) {
                in.transferTo(out);
                imageview.setImage(new Image(file.toURI().toString()));
                imageField.setText(dest);
            }
        }
    }

    private void getevs() {
        try {
            ObservableList<evaluation> data = FXCollections.observableArrayList(Ev.recupererevaluation());
            evaluationTv.setItems(data);
            titreCol.setCellValueFactory(new PropertyValueFactory<>("titre"));
            typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
            dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
            imageCol.setCellValueFactory(new PropertyValueFactory<>("image"));
        } catch (SQLException ex) {
            Logger.getLogger(AjouterevaluationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @FXML
    private void retour(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("index.fxml")));
            dateField.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("Erreur lors du retour à la page d'accueil : " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
