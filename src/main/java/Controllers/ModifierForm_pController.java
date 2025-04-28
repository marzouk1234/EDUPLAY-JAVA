package Controllers;

import Models.Form_p;
import Services.Form_pService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ModifierForm_pController implements Initializable {

    @FXML
    private TextField idField;

    @FXML
    private TextField contenuField;

    @FXML
    private TextField sujetField;

    @FXML
    private TextField auteurField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button submitButton;

    @FXML
    private AnchorPane rootPane;

    private Form_pService formService;
    private Form_p currentForm;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        formService = new Form_pService();
        addTextLimiter(contenuField, 500);
        addTextLimiter(sujetField, 100);
        addTextLimiter(auteurField, 50);
        setupLiveValidation();
    }

    public void setFormData(Form_p form) {
        this.currentForm = form;
        idField.setText(String.valueOf(form.getId()));
        contenuField.setText(form.getContenu());
        sujetField.setText(form.getSujet());
        auteurField.setText(form.getAuteur());
        datePicker.setValue(form.getDatePub());
    }

    @FXML
    public void handleUpdateSubmission(ActionEvent event) {
        try {
            if (!validateFields()) return;

            int id = Integer.parseInt(idField.getText().trim());
            String contenu = contenuField.getText().trim();
            String sujet = sujetField.getText().trim();
            String auteur = auteurField.getText().trim();
            LocalDate datePub = datePicker.getValue();

            currentForm.setContenu(contenu);
            currentForm.setDatePub(datePub);
            currentForm.setSujet(sujet);
            currentForm.setAuteur(auteur);

            formService.update(currentForm);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Formulaire mis à jour avec succès");
            closeWindow();

        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", e.getMessage());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de base de données",
                    "Impossible de mettre à jour le formulaire: " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Une erreur est survenue: " + e.getMessage());
        }
    }

    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();

        String contenu = contenuField.getText().trim();
        String sujet = sujetField.getText().trim();
        String auteur = auteurField.getText().trim();
        LocalDate date = datePicker.getValue();

        if (contenu.isEmpty() || contenu.length() < 10) {
            errors.append("Le contenu doit contenir au moins 10 caractères.\n");
        }

        if (sujet.isEmpty() || sujet.length() < 5) {
            errors.append("Le sujet doit contenir au moins 5 caractères.\n");
        }

        if (!auteur.matches("[a-zA-ZÀ-ÿ\\s'-]{2,}")) {
            errors.append("Le nom de l'auteur est invalide (lettres uniquement, au moins 2 caractères).\n");
        }

        if (date == null) {
            errors.append("La date de publication doit être spécifiée.\n");
        } else if (date.isAfter(LocalDate.now())) {
            errors.append("La date ne peut pas être dans le futur.\n");
        }

        if (errors.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", errors.toString());
            return false;
        }

        return true;
    }

    private void addTextLimiter(TextField textField, int maxLength) {
        textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.length() > maxLength) {
                textField.setText(oldValue);
            }
        });
    }

    private void setupLiveValidation() {
        contenuField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.trim().length() < 10) {
                contenuField.setStyle("-fx-border-color: red;");
            } else {
                contenuField.setStyle(null);
            }
        });

        sujetField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.trim().length() < 5) {
                sujetField.setStyle("-fx-border-color: red;");
            } else {
                sujetField.setStyle(null);
            }
        });

        auteurField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("[a-zA-ZÀ-ÿ\\s'-]{2,}")) {
                auteurField.setStyle("-fx-border-color: red;");
            } else {
                auteurField.setStyle(null);
            }
        });

        datePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null && newDate.isAfter(LocalDate.now())) {
                datePicker.setStyle("-fx-border-color: red;");
            } else {
                datePicker.setStyle(null);
            }
        });
    }

    @FXML
    private void handleCancelButton(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}