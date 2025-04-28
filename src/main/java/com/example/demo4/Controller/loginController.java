package com.example.demo4.Controller;

import com.example.demo4.entities.User;
import com.example.demo4.services.ServiceUser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
//import com.example.demo4.auth.GoogleSignInHelper;
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;

public class loginController implements Initializable {
	private ServiceUser serviceUser;

	@FXML
	private TextField login;

	@FXML
	private PasswordField passwordField;


	@Override
	public void initialize(URL location, ResourceBundle resources) {

		serviceUser = new ServiceUser();



	}


	@FXML
	void login(ActionEvent event) {
		// 1) Vérifier si les champs sont vides
		if (login.getText().isEmpty() || passwordField.getText().isEmpty()) {
			Alert alert1 = new Alert(Alert.AlertType.WARNING);
			alert1.setTitle("Oops");
			alert1.setHeaderText(null);
			alert1.setContentText("Veuillez remplir tous les champs.");
			alert1.showAndWait();
			return;
		}

		String username = login.getText();
		String password = passwordField.getText();

		// 2) Récupérer le mot de passe haché depuis la BD
		String hashedPasswordFromDB = serviceUser.getHashedPasswordForUser(username);
		if (hashedPasswordFromDB == null) {
			Alert alert1 = new Alert(Alert.AlertType.WARNING);
			alert1.setTitle("Utilisateur non trouvé");
			alert1.setHeaderText(null);
			alert1.setContentText("Nom d'utilisateur incorrect.");
			alert1.showAndWait();
			return;
		}

		// 3) Vérifier si le mot de passe est correct
		if (BCrypt.checkpw(password, hashedPasswordFromDB)) {
			// 4) Rôles
			String roles = serviceUser.getRoles(username);
			System.out.println("Roles récupérés pour " + username + " : " + roles);

			// 5) Récupérer l'objet User complet
			User currentUser = serviceUser.findUserByUsername(username);

			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Confirmation");
			alert.setHeaderText(null);
			alert.setContentText("Login réussi.");
			alert.showAndWait();

			// 6) Charger l'interface appropriée
			try {
				// Fermer la fenêtre actuelle
				Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				stage.close();

				FXMLLoader loader = new FXMLLoader();

				// Si ROLE_ADMIN, on va vers AcceuilUser.fxml (juste pour l’exemple).
				// Sinon, on va vers ListeU.fxml
				// (Ou inverse, selon votre logique.)
				String fxmlFile;
				if (roles != null && roles.contains("ROLE_ADMIN")) {
					fxmlFile = "/com/example/demo4/ListeUsers.fxml"; // <-- Interface user
				} else {
					fxmlFile = "/com/example/demo4/AcceuilUser.fxml";      // <-- Interface admin ou autre
				}

				loader.setLocation(getClass().getResource(fxmlFile));
				Parent root = loader.load();

				// 7) Si c'est AcceuilUserController, on lui passe l'user
				Object controller = loader.getController();
				if (controller instanceof AcceuilUserController) {
					AcceuilUserController userCtrl = (AcceuilUserController) controller;
					userCtrl.setCurrentUser(currentUser);    // on transmet l'User
					userCtrl.refreshData();                  // on appelle la méthode pour afficher
				}
				// Si c'est un autre contrôleur, on pourrait y faire la même chose.

				// 8) Afficher la nouvelle scène
				Scene scene = new Scene(root);
				Stage stage2 = new Stage();
				stage2.setScene(scene);
				stage2.show();

			} catch (IOException ex) {
				ex.printStackTrace();
			}

		} else {
			Alert alert1 = new Alert(Alert.AlertType.WARNING);
			alert1.setTitle("Oops");
			alert1.setHeaderText(null);
			alert1.setContentText("Nom d'utilisateur ou mot de passe incorrect.");
			alert1.showAndWait();
		}
	}
	@FXML
	void regsitre(ActionEvent event) {
		try {
			// Fermer la fenêtre actuelle
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.close();

			// Charger le fichier FXML pour la page registreUser
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo4/registreUser.fxml"));

			// Charger la scène et l'afficher
			Parent root = loader.load();
			Scene scene = new Scene(root);
			Stage stage2 = new Stage();
			stage2.setScene(scene);
			stage2.show();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@FXML
	void handleForgotPassword(ActionEvent event) {
		String email = login.getText();

		if (email.isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Attention");
			alert.setHeaderText(null);
			alert.setContentText("Veuillez entrer votre email pour réinitialiser le mot de passe.");
			alert.showAndWait();
			return;
		}

		// Vérifie si l'utilisateur existe
		User user = serviceUser.findUserByUsername(email);
		if (user == null) {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Utilisateur introuvable");
			alert.setHeaderText(null);
			alert.setContentText("Aucun utilisateur avec cet email.");
			alert.showAndWait();
			return;
		}

		// Générer un nouveau mot de passe
		String newPassword = generateRandomPassword(10);
		String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

		// Mettre à jour le mot de passe dans la base de données
		serviceUser.updatePassword(email, hashedPassword);



		// Envoyer l'email
		boolean success = MailSender.sendMail(email, "Nouveau mot de passe", "Votre nouveau mot de passe est : " + newPassword);

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Mot de passe réinitialisé");
		alert.setHeaderText(null);
		alert.setContentText(success ?
				"Un nouveau mot de passe a été envoyé à votre adresse email." :
				"Erreur lors de l'envoi du mail.");
		alert.showAndWait();
	}
	private String generateRandomPassword(int length) {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			int index = (int) (Math.random() * chars.length());
			sb.append(chars.charAt(index));
		}
		return sb.toString();


	}
	/*
	private void handleGoogleSignIn() {
		try {
			// Simuler un navigateur qui ouvre une page Google Sign In (via OAuth Playground par exemple)
			String loginURL = "https://accounts.google.com/o/oauth2/v2/auth?"
					+ "client_id=TA_CLIENT_ID"
					+ "&redirect_uri=http://localhost:8080"
					+ "&response_type=token"
					+ "&scope=email%20profile";

			Desktop.getDesktop().browse(new URI(loginURL));

			// Ici il te faudrait une vraie solution pour capter le token depuis le navigateur ou une API Java intermédiaire
			// Pour les tests, supposons que tu reçois le token manuellement
			String idToken = "COPIE_LE_JETON_QUE_TU_REÇOIS"; // à remplacer par vrai token

			GoogleIdToken.Payload payload = GoogleSignInHelper.verifyToken(idToken);

			if (payload != null) {
				String email = payload.getEmail();
				String name = (String) payload.get("name");

				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Connexion réussie !");
				alert.setHeaderText(null);
				alert.setContentText("Bienvenue " + name + " (" + email + ")");
				alert.showAndWait();
			} else {
				showError("Jeton invalide !");
			}

		} catch (Exception e) {
			e.printStackTrace();
			showError("Erreur lors de la connexion Google");
		}
	}

	private void showError(String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Erreur");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}*/
















}
