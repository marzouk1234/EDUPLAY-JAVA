package com.example.demo4.Controller;

import com.example.demo4.entities.evaluation;
import com.example.demo4.services.evaluationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.Parent;
import javafx.scene.control.*;
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

import com.example.demo4.services.etudiantService;
import com.example.demo4.entities.Etudiant;

// Twilio
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

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

    @FXML private ComboBox<Etudiant> comboEtudiant;

    private final evaluationService Ev = new evaluationService();

    // Twilio configuration
    private static final String ACCOUNT_SID = "AC5eb4f34d7a4d53908ee0e5113bce71ff";
    private static final String AUTH_TOKEN = "17bd80e1abb9276564f5fe5c53579216";
    private static final String TWILIO_NUMBER = "+15073534531"; // Twilio number

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        typeCombo.setItems(FXCollections.observableArrayList("Exam", "Quiz", "Projet", "DS"));
        getevs();

        etudiantService es = new etudiantService();
        try {
            List<Etudiant> etudiants = es.recupereretudiant();
            comboEtudiant.setItems(FXCollections.observableArrayList(etudiants));
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        if (titreField.getText().isEmpty() ||
                typeCombo.getValue() == null ||
                dateField.getValue() == null ||
                imageField.getText().isEmpty() ||
                comboEtudiant.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Champs manquants", "Veuillez remplir tous les champs.");
            return;
        }

        if (titreField.getText().length() < 6) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Titre trop court", "Le titre doit contenir au moins 6 caractères.");
            return;
        }

        if (dateField.getValue().isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Date invalide", "La date ne peut pas être dans le passé.");
            return;
        }

        try {
            Etudiant etu = comboEtudiant.getValue();
            int etudiantId = etu.getId();

            evaluation e = new evaluation(
                    titreField.getText(),
                    typeCombo.getValue(),
                    dateField.getValue().atStartOfDay(),
                    imageField.getText(),
                    etudiantId  // Ajout de l'ID de l'étudiant
            );

            Ev.ajouterevaluation(e); // on utilise la version qui lit etudiant_id de l'objet

            // Envoi du SMS après ajout
            sendSmsToStudent(etu, e);

            showAlert(Alert.AlertType.INFORMATION, "Succès", null, "Évaluation ajoutée et SMS envoyé avec succès !");
            reset();
            getevs();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void sendSmsToStudent(Etudiant etudiant, evaluation e) {
        try {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

            String messageText = "Bonjour " + etudiant.getnom_et_prenom() +
                    ", vous avez une évaluation le " + e.getDate().toLocalDate() +
                    " de type " + e.getType() +
                    " avec comme titre : " + e.getTitre() + ". Bonne chance !";

            String numero = etudiant.getTel(); // Exemple : "9399XXXX"
            if (!numero.startsWith("+216")) {
                numero = "+216" + numero;
            }

            Message.creator(
                    new PhoneNumber(numero),         // Numéro de l'étudiant formaté
                    new PhoneNumber(TWILIO_NUMBER),  // Ton numéro Twilio
                    messageText
            ).create();

        } catch (Exception ex) {
            System.out.println("Erreur d'envoi SMS : " + ex.getMessage());
        }
    }

    @FXML
    private void rechercherev(KeyEvent ev) {
        ObservableList<evaluation> filtered = Ev.chercherev(rechercher.getText());
        evaluationTv.setItems(filtered);
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
    private void uploadImage(ActionEvent ev) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload Image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
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
            // 1. Récupération des données depuis le service
            List<evaluation> evaluations = Ev.recupererevaluation();

            // 2. Conversion en ObservableList pour JavaFX
            ObservableList<evaluation> data = FXCollections.observableArrayList(evaluations);

            // 3. Liaison des données avec la TableView
            evaluationTv.setItems(data);

            // 4. Configuration des colonnes (uniquement si non déjà fait)
            if (titreCol.getCellValueFactory() == null) {
                titreCol.setCellValueFactory(new PropertyValueFactory<>("titre"));
                typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
                dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
                imageCol.setCellValueFactory(new PropertyValueFactory<>("image"));
            }

        } catch (SQLException ex) {
            // Gestion d'erreur améliorée
            Logger.getLogger(AjouterevaluationController.class.getName()).log(Level.SEVERE, "Erreur lors du chargement des évaluations", ex);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Échec du chargement", "Impossible de charger les évaluations depuis la base de données.");
        }
    }

    @FXML
    private void retour(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("index.fxml")));
            dateField.getScene().setRoot(root);
        } catch (IOException e) {
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

    @FXML
    private void modifierevaluation(ActionEvent ev) {
        // Validation des champs
        if (titreField.getText().isEmpty() ||
                typeCombo.getValue() == null ||
                dateField.getValue() == null ||
                imageField.getText().isEmpty() ||
                comboEtudiant.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Champs manquants", "Veuillez remplir tous les champs.");
            return;
        }

        // Vérification du titre
        if (titreField.getText().length() < 6) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Titre trop court", "Le titre doit contenir au moins 6 caractères.");
            return;
        }

        // Vérification de la date
        if (dateField.getValue().isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Date invalide", "La date ne peut pas être dans le passé.");
            return;
        }

        try {
            // Récupérer l'étudiant sélectionné dans le ComboBox
            Etudiant etu = comboEtudiant.getValue();
            int etudiantId = etu.getId();  // Récupérer l'ID de l'étudiant

            evaluation e = new evaluation(
                    titreField.getText(),
                    typeCombo.getValue(),
                    dateField.getValue().atStartOfDay(),
                    imageField.getText(),
                    etudiantId  // Ajout de l'ID de l'étudiant
            );

            // Modifier l'évaluation existante dans la base de données
            Ev.modifierevaluation(e, etudiantId);  // Appeler la méthode modifierevaluation avec l'ID de l'étudiant

            // Afficher l'alerte de succès et réinitialiser le formulaire
            showAlert(Alert.AlertType.INFORMATION, "Succès", null, "Évaluation modifiée avec succès !");
            reset();
            getevs();  // Recharger les évaluations dans la table

        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", null, "Une erreur est survenue lors de la modification de l'évaluation.");
        }
    }

    @FXML
    private void supprimerevaluation(ActionEvent ev) {
        evaluation e = evaluationTv.getSelectionModel().getSelectedItem();
        if (e != null) {
            // Créer une alerte de confirmation
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmation de suppression");
            confirmationAlert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette évaluation ?");
            confirmationAlert.setContentText("Cette action est irréversible.");

            // Charger l'icône depuis les ressources
            URL imageUrl = getClass().getResource("/com/example/demo4/images/warning.png");
            if (imageUrl != null) {
                Image icon = new Image(imageUrl.toExternalForm());
                Stage dialogStage = (Stage) confirmationAlert.getDialogPane().getScene().getWindow();
                dialogStage.getIcons().add(icon);
            } else {
                System.out.println("⚠️ Image introuvable !");
            }

            // Attendre la réponse de l'utilisateur
            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    Ev.supprimerevaluation(e);
                    showAlert(Alert.AlertType.INFORMATION, "Succès", null, "Évaluation supprimée avec succès");
                    getevs();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur", null, "Une erreur est survenue lors de la suppression.");
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Avertissement", null, "Veuillez sélectionner une évaluation à supprimer.");
        }
    }

}
