package com.example.demo4.Controller;

import com.example.demo4.entities.evaluation;
import com.example.demo4.services.evaluationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class AfficherevaluationController implements Initializable {

    @FXML
    private GridPane gridev;

    @FXML
    private TextField chercherevField;

    private final evaluationService ab = new evaluationService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        afficherevaluation();
    }

    public void afficherevaluation() {
        try {
            List<evaluation> evaluation = ab.recupererevaluation();
            gridev.getChildren().clear();
            int row = 0;
            int column = 0;
            for (int i = 0; i < evaluation.size(); i++) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("evaluation.fxml"));
                AnchorPane pane = loader.load();

                evaluationController controller = loader.getController();
                controller.setevaluation(evaluation.get(i));
                controller.setIdev(evaluation.get(i).getId());

                gridev.add(pane, column, row);
                column++;
                if (column > 2) {
                    column = 0;
                    row++;
                }
            }
        } catch (SQLException | IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    @FXML
    private void retour(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("dashboard.fxml")));
            gridev.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("Erreur lors du retour à la page d'accueil : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void rechercherevaluation(KeyEvent ev) {
        try {
            List<evaluation> evaluation = ab.chercherev(chercherevField.getText());
            gridev.getChildren().clear();
            int row = 0;
            int column = 0;
            for (int i = 0; i < evaluation.size(); i++) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("evaluation.fxml"));
                AnchorPane pane = loader.load();

                evaluationController controller = loader.getController();
                controller.setevaluation(evaluation.get(i));
                controller.setIdev(evaluation.get(i).getId());

                gridev.add(pane, column, row);
                column++;
                if (column > 1) {
                    column = 0;
                    row++;
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    private void trierevaluation(ActionEvent ev) throws SQLException {
        try {
            List<evaluation> evaluation = ab.trierev();
            gridev.getChildren().clear();
            int row = 0;
            int column = 0;
            for (int i = 0; i < evaluation.size(); i++) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("evaluation.fxml"));
                AnchorPane pane = loader.load();

                evaluationController controller = loader.getController();
                controller.setevaluation(evaluation.get(i));
                controller.setIdev(evaluation.get(i).getId());

                gridev.add(pane, column, row);
                column++;
                if (column > 1) {
                    column = 0;
                    row++;
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
