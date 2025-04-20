package Controllers;

import Models.Aide;
import Models.Form_p;
import Services.AideService;
import Services.Form_pService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class AjouterAideController {

    @FXML
    private TextField sujetField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private ComboBox<Form_p> formComboBox;

    @FXML
    private Label messageLabel;

    @FXML
    private AnchorPane rootPane;

    private AideService aideService;
    private Form_pService formService;

    @FXML
    public void initialize() {
        aideService = new AideService();
        formService = new Form_pService();
        loadForms();
        setupLiveValidation();
        System.out.println("Initialization done");
    }

    private void loadForms() {
        try {
            List<Form_p> forms = formService.getAll();
            System.out.println("Forms loaded: " + forms.size());  // Check if forms are loaded correctly
            formComboBox.getItems().clear();
            formComboBox.getItems().addAll(forms);

            formComboBox.setCellFactory(param -> new ListCell<Form_p>() {
                @Override
                protected void updateItem(Form_p item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getSujet());
                    }
                }
            });

            formComboBox.setButtonCell(new ListCell<Form_p>() {
                @Override
                protected void updateItem(Form_p item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getSujet());
                    }
                }
            });

        } catch (SQLException e) {
            messageLabel.setText("Erreur lors du chargement des formulaires: " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void handleEnregistrer() {
        System.out.println("Handle Enregistrer clicked");
        if (!validateInputs()) {
            return;
        }

        try {
            Aide aide = new Aide();
            aide.setSujet(sujetField.getText().trim());
            aide.setDescription(descriptionArea.getText().trim());
            aide.setDateCreation(LocalDateTime.now());

            Form_p selectedForm = formComboBox.getValue();
            if (selectedForm != null) {
                aide.setFormId(selectedForm.getId());
            }

            aideService.add(aide);
            messageLabel.setText("Aide ajoutée avec succès");
            messageLabel.setStyle("-fx-text-fill: green;");

            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    Platform.runLater(this::closeWindow);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();

        } catch (SQLException e) {
            messageLabel.setText("Erreur lors de l'ajout: " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Validation du sujet
        if (sujetField.getText().trim().isEmpty()) {
            sujetField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            isValid = false;
        } else {
            sujetField.setStyle(null); // Réinitialiser le style
        }

        // Validation de la description
        if (descriptionArea.getText().trim().isEmpty()) {
            descriptionArea.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            isValid = false;
        } else {
            descriptionArea.setStyle(null); // Réinitialiser le style
        }

        // Validation du formulaire sélectionné
        if (formComboBox.getValue() == null) {
            formComboBox.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            isValid = false;
        } else {
            formComboBox.setStyle(null); // Réinitialiser le style
        }

        if (!isValid) {
            messageLabel.setText("Veuillez corriger les champs en rouge.");
            messageLabel.setStyle("-fx-text-fill: red;");
        }

        return isValid;
    }

    private void setupLiveValidation() {
        // Validation en temps réel du sujet
        sujetField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.trim().isEmpty()) {
                sujetField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            } else {
                sujetField.setStyle(null);
            }
        });

        // Validation en temps réel de la description
        descriptionArea.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.trim().isEmpty()) {
                descriptionArea.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            } else {
                descriptionArea.setStyle(null);
            }
        });

        // Validation en temps réel du formulaire sélectionné
        formComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                formComboBox.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            } else {
                formComboBox.setStyle(null);
            }
        });
    }

    @FXML
    private void handleAnnuler() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }
}
 