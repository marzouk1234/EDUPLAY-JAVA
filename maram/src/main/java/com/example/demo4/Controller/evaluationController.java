package com.example.demo4.Controller;

import com.example.demo4.entities.User;
import com.example.demo4.entities.evaluation;
import com.example.demo4.entities.resultat;
import com.example.demo4.services.evaluationService;
import com.example.demo4.services.resultatService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.ResourceBundle;

public class evaluationController implements Initializable {

    private final evaluationService ab = new evaluationService();
    private final resultatService Ps = new resultatService();

    @FXML private TextField appreciationevField;
     // Champ caché dans votre FXML
    @FXML
    private TextField noteField;           // Champ note visible dans votre FXML
    @FXML private Label titreevLabel;

    @FXML private Label dateLabel;
    @FXML private Label typeLabel;

    @FXML private TextField idevF;
    @FXML private TextField iduserF;
    @FXML private ImageView imageview;
    @FXML private TextField idPartField;



    private int idev;
    private final User u = new User();
    private evaluation eve = new evaluation();
    @FXML
    private DatePicker startDatePicker;



    @Override
    public void initialize(URL url, ResourceBundle rb) {
        idevF.setVisible(false);
        iduserF.setVisible(false);
    }

    public void setevaluation(evaluation e) {
        this.eve = e;
        titreevLabel.setText(e.getTitre());


        typeLabel.setText(e.getType());
        dateLabel.setText(e.getDate().toLocalDate().toString());

        idevF.setText(String.valueOf(e.getId()));
        iduserF.setText(String.valueOf(1)); // Exemple, à remplacer avec l'id utilisateur réel

        String path = e.getImage();
        File file = new File(path);
        if (file.exists()) {
            Image img = new Image(file.toURI().toString());
            imageview.setImage(img);
        }
    }

    public void setIdev(int idev) {
        this.idev = idev;
    }

    @FXML
    private void addResultat(MouseEvent ev) {
        try {
            // 1) Vérifier si la date est bien sélectionnée
            if (startDatePicker.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Champs manquants");
                alert.setHeaderText("Dates manquantes");
                alert.setContentText("Veuillez sélectionner la date de début du résultat.");
                alert.showAndWait();
                return;
            }

            // 2) Récupérer l’ID de l’évaluation
            int evaluationId = Integer.parseInt(idevF.getText());

            // 3) Récupérer et parser la note
            //    Assurez-vous de gérer l’exception si le champ est vide ou contient un texte invalide
            double note = Double.parseDouble(noteField.getText());

            // Vérifier si la note est <= 20
            if (note > 20) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Note invalide");
                alert.setHeaderText("Valeur de note incorrecte");
                alert.setContentText("La note ne doit pas dépasser 20 !");
                alert.showAndWait();
                // On peut arrêter l’exécution ici si on ne veut pas poursuivre
                return;
            } else {
                System.out.println("Note saisie = " + note);
            }
            // 4) Déterminer l’appréciation en fonction de la note
            String appreciation;
            if (note < 10) {
                appreciation = "Faible";
            } else if (note >= 10 && note <= 13) {
                appreciation = "Passable";
            } else if (note > 13 && note <= 15) {
                appreciation = "Bien";
            } else if (note > 15 && note <= 18) {
                appreciation = "Très bien";
            } else {
                appreciation = "Excellent";
            }

            // 5) Préparer la date
            LocalDateTime start = startDatePicker.getValue().atStartOfDay();

            // 6) Construire l’objet resultat
            resultat p = new resultat(evaluationId, note, appreciation, start);

            // 7) Appel au service pour insérer en base
            Ps.ajouterresultat(p);

            // 8) MàJ éventuelle d’un champ
            idPartField.setText("27");

            // 9) Rechargement de la page (retour ou changement de vue)
            Parent loader = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("index2.fxml")));
            idevF.getScene().setRoot(loader);

        } catch (NumberFormatException e1) {
            // Gérer le cas où noteField n’est pas un nombre
            System.out.println("Erreur : La note n’est pas un nombre valide. " + e1.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText("Valeur de note incorrecte");
            alert.setContentText("Veuillez saisir un nombre pour la note.");
            alert.showAndWait();
        } catch (Exception ex) {
            System.out.println("Erreur lors de l'ajout du résultat : " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    @FXML
    private void voirResultats(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resultatsParEvaluation.fxml"));
            Parent root = loader.load();

            // Passer l’ID de l’Evaluationà l’autre contrôleur
            ResultatsParEvaluationController controller = loader.getController();
            controller.setEvaluationId(this.eve.getId());

            // Afficher la nouvelle page
            idevF.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("Erreur lors de l'ouverture de la vue des resultats : " + e.getMessage());
        }
    }
    @FXML
    private void voirResultatsFront(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resultatsParEvaluationFront.fxml"));
            Parent root = loader.load();

            // Passer l’ID de l’Evaluationà l’autre contrôleur
            ResultatsParEvaluationController controller = loader.getController();
            controller.setEvaluationId(this.eve.getId());

            // Afficher la nouvelle page
            idevF.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("Erreur lors de l'ouverture de la vue des resultats : " + e.getMessage());
        }
    }


}
