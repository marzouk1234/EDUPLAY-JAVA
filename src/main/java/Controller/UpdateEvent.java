package Controller;

import Models.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;

public class UpdateEvent {

    @FXML
    private TextField nom;

    @FXML
    private TextField desc;

    @FXML
    private DatePicker date;

    @FXML
    private ImageView IMG;

    @FXML
    private Button upd;

    @FXML
    private Button chimg;

    private Event eventToUpdate;
    private EventController previousController;

    private String imagePath; // pour stocker le chemin de la nouvelle image choisie

    public void initData(Event event, EventController controller) {
        this.eventToUpdate = event;
        this.previousController = controller;
        fillForm(event);


        upd.setOnAction(e -> handleUpdate());
        chimg.setOnAction(e -> handleChangeImage());
    }

    private void fillForm(Event event) {
        nom.setText(event.getNom());
        desc.setText(event.getDescription());
        LocalDate localDate = LocalDate.parse(event.getDate());
        date.setValue(localDate);
        if (event.getIMG() != null && !event.getIMG().isEmpty()) {
            Image image = new Image(event.getIMG());
            IMG.setImage(image);
            imagePath = event.getIMG();
        }
    }

    private void handleUpdate() {

        eventToUpdate.setNom(nom.getText());
        eventToUpdate.setDescription(desc.getText());
        eventToUpdate.setDate(date.getValue().toString());
        eventToUpdate.setIMG(imagePath);






        Stage stage = (Stage) upd.getScene().getWindow();
        stage.close();
    }

    private void handleChangeImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(upd.getScene().getWindow());
        if (selectedFile != null) {
            imagePath = selectedFile.toURI().toString();
            IMG.setImage(new Image(imagePath));
        }
    }
}
