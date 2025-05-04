package Controller;

import Models.Event;
import Services.EventService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javafx.scene.control.Control;
import java.sql.SQLException;
import java.time.LocalDate;

public class CreateEvent {
    @FXML
    private TextField nom;
    @FXML
    private TextField desc;
    @FXML
    private DatePicker date;
    @FXML
    private ImageView img;
    @FXML
    private Button par;
    @FXML
    private Button cr;

    private String imagePath;
    private final EventService eventService = new EventService(); // Champ final

    @FXML
    public void initialize() {
        par.setOnAction(e -> handleBrowseImage());
        cr.setOnAction(e -> handleCreateEvent());
    }

    private void handleBrowseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            try {
                imagePath = selectedFile.getAbsolutePath();
                img.setImage(new Image(selectedFile.toURI().toString())); // Gestion d'erreur d'image
            } catch (IllegalArgumentException e) {
                showAlert("Erreur", "Image invalide", "Le fichier sélectionné n'est pas une image valide", Alert.AlertType.ERROR);
            }
        }
    }

    private void handleCreateEvent() {
        // Validation du nom
        if (nom.getText().trim().isEmpty() || nom.getText().length() > 50) {
            showError(nom, "Nom invalide", "Le nom doit contenir entre 1 et 50 caractères");
            return;
        }

        // Validation de la description
        if (desc.getText().length() > 255) {
            showError(desc, "Description invalide", "La description ne doit pas dépasser 255 caractères");
            return;
        }

        // Validation de la date
        if (date.getValue() == null || date.getValue().isBefore(LocalDate.now())) {
            showError(date, "Date invalide", "La date doit être aujourd'hui ou ultérieure");
            return;
        }

        // Validation de l'image
        if (imagePath == null || imagePath.isEmpty()) {
            showAlert("Erreur", "Image manquante", "Veuillez sélectionner une image", Alert.AlertType.ERROR);
            return;
        }

        try {
            // Conversion de l'image en byte[]
            File imageFile = new File(imagePath);
            byte[] imageData = Files.readAllBytes(imageFile.toPath());

            // Création de l'événement
            Event event = new Event();
            event.setNom(nom.getText().trim());
            event.setDescription(desc.getText().trim());
            event.setDate(date.getValue().toString());
            event.setIMG(imageData);

            eventService.add(event); // Appel au service
            showAlert("Succès", "Événement créé", "L'événement a été créé avec succès", Alert.AlertType.INFORMATION);
            resetForm();

        } catch (IOException | SQLException e) {
            showAlert("Erreur", "Erreur système", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Méthodes utilitaires
    private void showError(Control control, String header, String message) { // 👈 Paramètre Control
        control.setStyle("-fx-border-color: red;");
        showAlert("Erreur", header, message, Alert.AlertType.ERROR);
    }

    private void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void resetForm() {
        nom.clear();
        desc.clear();
        date.setValue(null);
        img.setImage(null);
        imagePath = null;
        nom.setStyle("");
        desc.setStyle("");
        date.setStyle("");
    }
}