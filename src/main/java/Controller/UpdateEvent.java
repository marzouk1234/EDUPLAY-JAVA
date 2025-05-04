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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UpdateEvent {

    @FXML private TextField nom;
    @FXML private TextField desc;
    @FXML private DatePicker date;
    @FXML private ImageView IMG;
    @FXML private Button upd;
    @FXML private Button chimg;

    private Event currentEvent;
    private EventController parentController;
    private byte[] newImageData;
    private final EventService eventService = new EventService();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void initData(Event event, EventController parentController) {
        System.out.println("Initialisation des données pour : " + event.getNom());
        this.currentEvent = event;
        this.parentController = parentController;
        populateFields();
    }

    private void populateFields() {
        nom.setText(currentEvent.getNom());
        desc.setText(currentEvent.getDescription());

        LocalDate localDate = LocalDate.parse(currentEvent.getDate(), formatter);
        date.setValue(localDate); // Définir directement la LocalDate dans le DatePicker

        // Affichage de l'image
        if (currentEvent.getIMG() != null && currentEvent.getIMG().length > 0) {
            IMG.setImage(new Image(new ByteArrayInputStream(currentEvent.getIMG())));
        }
    }

    @FXML
    public void initialize() {
        chimg.setOnAction(e -> handleImageChange());
        upd.setOnAction(e -> handleUpdate());
    }

    private void handleImageChange() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une nouvelle image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            try {
                newImageData = Files.readAllBytes(selectedFile.toPath());
                IMG.setImage(new Image(selectedFile.toURI().toString()));
            } catch (IOException e) {
                showAlert("Erreur", "Impossible de charger l'image : " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void handleUpdate() {
        try {
            updateEventFromFields();
            eventService.update(currentEvent);
            parentController.refreshEvents(); // Rafraîchir la liste
            closeWindow();
        } catch (SQLException e) {
            showAlert("Erreur", "Échec de la mise à jour : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateEventFromFields() {
        currentEvent.setNom(nom.getText().trim());
        currentEvent.setDescription(desc.getText().trim());

        // Conversion de LocalDate en LocalDateTime
        LocalDate selectedDate = date.getValue();
        currentEvent.setDate(selectedDate.format(formatter));

        if (newImageData != null) {
            currentEvent.setIMG(newImageData);
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) upd.getScene().getWindow();
        stage.close();
    }
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


