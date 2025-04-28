package com.example.demo4.Controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import com.example.demo4.entities.User;
import com.example.demo4.services.ServiceUser;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;

import org.mindrot.jbcrypt.BCrypt;

public class RegistreUserController implements Initializable {

	@FXML private TextField urlTF;
	@FXML private DatePicker dateNaissancePicker;
	@FXML private ComboBox<String> roleCombo;
	@FXML private TextField txtemail;
	@FXML private TextField txtnom1; // prénom
	@FXML private TextField txtnom2; // nom
	@FXML private PasswordField passwordField;
	@FXML private PasswordField passwordField2;
	@FXML private TextField txtnumT;
	@FXML private Stage stage;

	private File file;

	private boolean isValidEmail(String email) {
		String EMAIL_PATTERN = "^[\\w.-]+@[\\w.-]+\\.\\w{2,}$";
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	private boolean isValidPhone(String num) {
		return num.matches("^[2|5|9][0-9]{7}$");
	}

	private boolean isAlpha(String input) {
		return input.matches("^[a-zA-Z]+$");
	}

	private boolean isStrongPassword(String password) {
		return password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
	}

	private void showAlert(String title, String message) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	@FXML
	void ajouter(ActionEvent event) {
		String prenom = txtnom1.getText();
		String nom = txtnom2.getText();
		String email = txtemail.getText();
		String num_t = txtnumT.getText();
		String pass = passwordField.getText();
		String pass2 = passwordField2.getText();
		LocalDate dateNaissance = dateNaissancePicker.getValue();
		String selectedRole = roleCombo.getValue();


// Vérification de l'âge
		int age = LocalDate.now().getYear() - dateNaissance.getYear();
		if (age < 13 || age > 60) {
			showAlert("Âge invalide", "L'âge doit être compris entre 13 et 60 ans.");
			return;
		}


		if (prenom.isEmpty() || nom.isEmpty() || email.isEmpty() || num_t.isEmpty() ||
				pass.isEmpty() || pass2.isEmpty() || urlTF.getText().isEmpty() ||
				dateNaissance == null || selectedRole == null || file == null) {
			showAlert("Champs manquants", "Veuillez remplir tous les champs.");
			return;
		}

		if (!isAlpha(prenom) || !isAlpha(nom)) {
			showAlert("Nom invalide", "Le nom et le prénom doivent contenir uniquement des lettres.");
			return;
		}

		if (!isValidEmail(email)) {
			showAlert("Email invalide", "Veuillez saisir une adresse email valide.");
			return;
		}

		ServiceUser serviceUser = new ServiceUser();
		if (serviceUser.emailExiste(email)) {
			showAlert("Email existant", "Cette adresse email est déjà utilisée.");
			return;
		}

		if (!isValidPhone(num_t)) {
			showAlert("Téléphone invalide", "Le numéro doit contenir 8 chiffres et commencer par 2, 5 ou 9.");
			return;
		}

		if (!isStrongPassword(pass)) {
			showAlert("Mot de passe faible", "Le mot de passe doit contenir au moins 8 caractères, dont des lettres et des chiffres.");
			return;
		}

		if (!pass.equals(pass2)) {
			showAlert("Mot de passe", "Les deux mots de passe ne correspondent pas.");
			return;
		}

		User nouvelleUser = new User();
		nouvelleUser.setPrenom(prenom);
		nouvelleUser.setNom(nom);
		nouvelleUser.setEmail(email);
		nouvelleUser.setTel(num_t);
		nouvelleUser.setDateNaissance(dateNaissance);
		nouvelleUser.setImage(file.getAbsolutePath());

		String hashedPassword = BCrypt.hashpw(pass, BCrypt.gensalt());
		nouvelleUser.setPassword(hashedPassword);

		serviceUser.ajouter(nouvelleUser, selectedRole);

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Succès");
		alert.setHeaderText(null);
		alert.setContentText("Utilisateur enregistré avec succès !");
		alert.showAndWait();

		try {
			Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			currentStage.close();

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo4/login.fxml"));
			Parent root = loader.load();
			Stage stage2 = new Stage();
			stage2.setScene(new Scene(root));
			stage2.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void importer(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Sélectionnez une image");
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg")
		);
		File fichierSelectionne = fileChooser.showOpenDialog(stage);
		if (fichierSelectionne != null) {
			urlTF.setText(fichierSelectionne.getName());
			file = fichierSelectionne;
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		roleCombo.setItems(FXCollections.observableArrayList("ROLE_ENS", "ROLE_ETU"));
	}
}
