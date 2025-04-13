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














}
