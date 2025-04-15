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
            
            // Configuration de l'affichage des items dans la ComboBox
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
            
            // Configuration de l'affichage de l'item sélectionné
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
        }
    }

    private void populateFields() {
        if (aide != null) {
            idLabel.setText(String.valueOf(aide.getId()));
            sujetField.setText(aide.getSujet());
            descriptionArea.setText(aide.getDescription());
            dateCreationPicker.setValue(aide.getDateCreation().toLocalDate());
            
            // Sélectionner le formulaire associé
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
            
            // Fermer la fenêtre après 1 seconde
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    javafx.application.Platform.runLater(() -> {
                        getStage().close();
                    });
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
        if (sujetField.getText().trim().isEmpty()) {
            messageLabel.setText("Le sujet est obligatoire");
            return false;
        }
        if (descriptionArea.getText().trim().isEmpty()) {
            messageLabel.setText("La description est obligatoire");
            return false;
        }
        if (formComboBox.getValue() == null) {
            messageLabel.setText("Veuillez sélectionner un formulaire");
            return false;
        }
        if (dateCreationPicker.getValue() == null) {
            messageLabel.setText("La date de création est obligatoire");
            return false;
        }
        return true;
    }

    private Stage getStage() {
        return (Stage) sujetField.getScene().getWindow();
    }
} 