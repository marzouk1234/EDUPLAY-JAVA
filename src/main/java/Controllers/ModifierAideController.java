package Controllers;

import Models.Aide;
import Models.Form_p;
import Services.AideService;
import Services.Form_pService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class ModifierAideController {
    @FXML
    private Label idLabel;
    @FXML
    private TextField sujetField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private ComboBox<Form_p> formComboBox;
    @FXML
    private DatePicker dateCreationPicker;
    @FXML
    private Label messageLabel;

    private AideService aideService;
    private Form_pService formService;
    private Aide aide;

    @FXML
    public void initialize() {
        aideService = new AideService();
        formService = new Form_pService();
        loadForms();

        // Validation en temps réel et mise en évidence des erreurs de saisie avec un contour rouge
        sujetField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.trim().length() < 3) {
                sujetField.setStyle("-fx-border-color: red;");
                messageLabel.setText("Le sujet doit contenir au moins 3 caractères");
            } else {
                sujetField.setStyle("");  // Réinitialiser le style
                messageLabel.setText("");
            }
        });

        descriptionArea.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.trim().length() < 10) {
                descriptionArea.setStyle("-fx-border-color: red;");
                messageLabel.setText("La description doit contenir au moins 10 caractères");
            } else {
                descriptionArea.setStyle("");  // Réinitialiser le style
                messageLabel.setText("");
            }
        });
    }

    public void setAide(Aide aide) {
        this.aide = aide;
        populateFields();
    }

    private void loadForms() {
        try {
            List<Form_p> forms = formService.getAll();
            formComboBox.getItems().clear();
            formComboBox.getItems().addAll(forms);

            // Affichage personnalisé dans la ComboBox
            formComboBox.setCellFactory(param -> new ListCell<Form_p>() {
                @Override
                protected void updateItem(Form_p item, boolean empty) {
                    super.updateItem(item, empty);
                    setText((empty || item == null) ? null : item.getSujet());
                }
            });

            formComboBox.setButtonCell(new ListCell<Form_p>() {
                @Override
                protected void updateItem(Form_p item, boolean empty) {
                    super.updateItem(item, empty);
                    setText((empty || item == null) ? null : item.getSujet());
                }
            });
        } catch (SQLException e) {
            messageLabel.setText("Erreur lors du chargement des formulaires: " + e.getMessage());
        }
    }

    private void populateFields() {
        if (aide != null) {
            idLabel.setText(String.valueOf(aide.getId()));
            sujetField.setText(aide.getSujet());
            descriptionArea.setText(aide.getDescription());
            dateCreationPicker.setValue(aide.getDateCreation().toLocalDate());

            for (Form_p form : formComboBox.getItems()) {
                if (form.getId() == aide.getFormId()) {
                    formComboBox.setValue(form);
                    break;
                }
            }
        }
    }

    @FXML
    private void handleEnregistrer() {
        if (!validateInputs()) {
            return;
        }

        try {
            aide.setSujet(sujetField.getText().trim());
            aide.setDescription(descriptionArea.getText().trim());
            aide.setDateCreation(LocalDateTime.of(
                    dateCreationPicker.getValue(),
                    aide.getDateCreation().toLocalTime()
            ));

            Form_p selectedForm = formComboBox.getValue();
            if (selectedForm != null) {
                aide.setFormId(selectedForm.getId());
            }

            aideService.update(aide);
            messageLabel.setText("Aide modifiée avec succès");

            // Fermeture automatique après 1 seconde
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    javafx.application.Platform.runLater(() -> getStage().close());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();

        } catch (SQLException e) {
            messageLabel.setText("Erreur lors de la modification: " + e.getMessage());
        }
    }

    @FXML
    private void handleAnnuler() {
        getStage().close();
    }

    private boolean validateInputs() {
        String sujet = sujetField.getText().trim();
        String description = descriptionArea.getText().trim();
        LocalDateTime now = LocalDateTime.now();

        boolean valid = true;

        if (sujet.isEmpty()) {
            sujetField.setStyle("-fx-border-color: red;");
            messageLabel.setText("Le sujet est obligatoire");
            valid = false;
        } else if (sujet.length() < 3) {
            sujetField.setStyle("-fx-border-color: red;");
            messageLabel.setText("Le sujet doit contenir au moins 3 caractères");
            valid = false;
        } else {
            sujetField.setStyle(""); // Réinitialiser le style si valide
        }

        if (description.isEmpty()) {
            descriptionArea.setStyle("-fx-border-color: red;");
            messageLabel.setText("La description est obligatoire");
            valid = false;
        } else if (description.length() < 10) {
            descriptionArea.setStyle("-fx-border-color: red;");
            messageLabel.setText("La description doit contenir au moins 10 caractères");
            valid = false;
        } else {
            descriptionArea.setStyle(""); // Réinitialiser le style si valide
        }

        if (formComboBox.getValue() == null) {
            messageLabel.setText("Veuillez sélectionner un formulaire");
            valid = false;
        }

        if (dateCreationPicker.getValue() == null) {
            messageLabel.setText("La date de création est obligatoire");
            valid = false;
        } else if (dateCreationPicker.getValue().isAfter(now.toLocalDate())) {
            messageLabel.setText("La date de création ne peut pas être dans le futur");
            valid = false;
        }

        return valid;
    }

    private Stage getStage() {
        return (Stage) sujetField.getScene().getWindow();
    }
}
