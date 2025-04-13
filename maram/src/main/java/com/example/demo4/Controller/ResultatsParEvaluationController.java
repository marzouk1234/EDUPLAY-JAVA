package com.example.demo4.Controller;

import com.example.demo4.entities.resultat;
import com.example.demo4.services.resultatService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class ResultatsParEvaluationController {

    @FXML
    private VBox resultatContainer;

    @FXML
    private Label titreLabel;

    private final resultatService service = new resultatService();

    public void setEvaluationId(int evaluationId) {
        titreLabel.setText("Resultats de l'Evaluation" + evaluationId);
        try {
            List<resultat> resultats = service.recupererResultats();
            for (resultat s : resultats) {
                if (s.getEvaluationId() == evaluationId) {
                    Text resultatText = new Text(
                            "ID: " + s.getId() +
                                    "\nAppreciation: " + s.getAppreciation() +
                                    "\nDébut: " + s.getDate_creation()
                    );
                    resultatText.setStyle("-fx-background-color: #f2f2f2; -fx-padding: 10;");
                    resultatContainer.getChildren().add(resultatText);
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement des resultats : " + e.getMessage());
        }
    }
    @FXML
    private void retour(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("index.fxml")));
            resultatContainer.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("Erreur lors du retour à la page d'accueil : " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    private void retourFront(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("front.fxml")));
            resultatContainer.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("Erreur lors du retour à la page d'accueil : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
