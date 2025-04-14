package Controller;

import Models.Event;
import Services.EventService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.sql.SQLException;

public class create {
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

    private EventService eventService = new EventService();

    @FXML
    public void initialize() {
        // Action pour le bouton Parcourir
        par.setOnAction(e -> handleBrowseImage());
        // Action pour le bouton Create
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
            imagePath = selectedFile.getAbsolutePath();
            img.setImage(new Image(selectedFile.toURI().toString()));
        }
    }

    private void handleCreateEvent() {
        try {
            Event event = new Event();
            event.setNom(nom.getText());
            event.setDescription(desc.getText());
            event.setDate(date.getValue().toString());
            event.setIMG(imagePath);

            eventService.add(event);

            // Réinitialiser les champs après création
            nom.clear();
            desc.clear();
            date.setValue(null);
            img.setImage(null);
            imagePath = null;

            System.out.println("Événement créé avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la création : " + e.getMessage());
        }
    }
}