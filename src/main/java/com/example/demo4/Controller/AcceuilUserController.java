package com.example.demo4.Controller;

import com.example.demo4.entities.User;
import com.example.demo4.services.ServiceUser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.ResourceBundle;

public class AcceuilUserController implements Initializable {

    @FXML
    private Circle profile_image;          // Cercle d'affichage du profil

    @FXML
    private Label username;                // Label pour le prénom

    @FXML
    private Label email;                   // Label pour l'email

    // Panneau principal qui contient la zone d'update (déjà défini dans le FXML)
    @FXML
    private AnchorPane boutique_page;

    // Panneau de mise à jour (caché au démarrage)
    @FXML
    private AnchorPane modifierCard;

    // Champs du formulaire d'édition
    @FXML
    private TextField prenomField;
    @FXML
    private TextField nomField;
    @FXML
    private TextField telField;
    @FXML
    private TextField emailField;

    // L'utilisateur connecté
    private User currentUser;

    // Service d'accès aux données
    private final ServiceUser serviceUser = new ServiceUser();

    // Méthode appelée pour transmettre l'utilisateur connecté
    public void setCurrentUser(User user) {
        this.currentUser = user;
        System.out.println("Current user set: " + user);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Au démarrage, cachez le formulaire d'édition.
        modifierCard.setVisible(false);
    }

    /**
     * Actualise l'affichage des informations dans les labels et le cercle.
     */
    public void refreshData() {
        if (currentUser != null) {
            username.setText(currentUser.getPrenom());
            email.setText(currentUser.getEmail());

            // Afficher l'image de profil dans le cercle
            if (currentUser.getImage() != null) {
                try {
                    Image iconImage = new Image(
                            getClass().getResource("/com/example/demo4/" + currentUser.getImage()).toExternalForm()
                    );
                    profile_image.setFill(new ImagePattern(iconImage));
                } catch (Exception e) {
                    System.out.println("Impossible de charger l'image : " + e.getMessage());
                }
            } else {
                // Utiliser une image par défaut si aucune image n'est renseignée
                Image defaultImage = new Image(
                        getClass().getResource("/com/example/demo4/icon.png").toExternalForm()
                );
                profile_image.setFill(new ImagePattern(defaultImage));
            }
        }
    }

    /**
     * Lorsqu'on clique sur le cercle, on affiche le formulaire de mise à jour.
     */
    @FXML
    private void handleProfileImageClick(MouseEvent event) {
        if (currentUser != null) {
            // Rendre le formulaire visible
            modifierCard.setVisible(true);
            // Préremplir le formulaire avec les informations actuelles de l'utilisateur
            prenomField.setText(currentUser.getPrenom());
            nomField.setText(currentUser.getNom());
            telField.setText(currentUser.getTel());
            emailField.setText(currentUser.getEmail());
        }
    }

    /**
     * Quand on clique sur le bouton "Mettre à jour" dans le formulaire.
     */
    @FXML
    private void handleSaveClick(ActionEvent event) {
        if (currentUser != null) {
            // Récupérer les nouvelles valeurs saisies
            String newPrenom = prenomField.getText();
            String newNom = nomField.getText();
            String newTel = telField.getText();
            String newEmail = emailField.getText();

            // Mettre à jour l'objet currentUser
            currentUser.setPrenom(newPrenom);
            currentUser.setNom(newNom);
            currentUser.setTel(newTel);
            currentUser.setEmail(newEmail);

            // Appeler le service pour mettre à jour l'utilisateur dans la base de données
            serviceUser.modifierfront(currentUser, currentUser.getId());

            // Cacher le formulaire d'édition
            modifierCard.setVisible(false);

            // Actualiser l'affichage principal
            refreshData();

            // Afficher un message de confirmation
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Informations mises à jour !");
            alert.showAndWait();
        }
    }

    /**
     * Quand on clique sur "Annuler" dans le formulaire, on cache simplement le formulaire.
     */
    @FXML
    private void handleCancelClick(ActionEvent event) {
        modifierCard.setVisible(false);
    }
}
