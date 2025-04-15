package Controllers;

import Models.Form_p;
import Services.Form_pService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AfficherForm_pController implements Initializable {

    @FXML
    private TableView<Form_p> formTable;

    @FXML
    private TableColumn<Form_p, Integer> idColumn;

    @FXML
    private TableColumn<Form_p, String> sujetColumn;

    @FXML
    private TableColumn<Form_p, String> contenuColumn;

    @FXML
    private TableColumn<Form_p, String> dateColumn;

    @FXML
    private TableColumn<Form_p, String> auteurColumn;

    @FXML
    private Button ajouterButton;

    @FXML
    private Button modifierButton;

    @FXML
    private Button supprimerButton;

    @FXML
    private AnchorPane rootPane;

    private Form_pService formService;
    private ObservableList<Form_p> formList;
    private ScheduledExecutorService scheduler;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Initialize service
            formService = new Form_pService();

            // Configure table columns
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            sujetColumn.setCellValueFactory(new PropertyValueFactory<>("sujet"));
            contenuColumn.setCellValueFactory(new PropertyValueFactory<>("contenu"));
            dateColumn.setCellValueFactory(cellData -> {
                try {
                    String formattedDate = cellData.getValue().getFormattedDate();
                    return javafx.beans.binding.Bindings.createStringBinding(() -> formattedDate);
                } catch (Exception e) {
                    return javafx.beans.binding.Bindings.createStringBinding(() -> "Date invalide");
                }
            });
            auteurColumn.setCellValueFactory(new PropertyValueFactory<>("auteur"));

            // Ajouter un tooltip aux cellules du contenu (qui peut être long)
            contenuColumn.setCellFactory(tc -> {
                TableCell<Form_p, String> cell = new TableCell<>();
                Tooltip tooltip = new Tooltip();
                
                cell.textProperty().bind(cell.itemProperty());
                cell.setOnMouseEntered(event -> {
                    if (cell.getText() != null && !cell.getText().isEmpty()) {
                        tooltip.setText(cell.getText());
                        Tooltip.install(cell, tooltip);
                    }
                });
                return cell;
            });

            // Load data
            loadFormData();

            // Add listener for row selection
            formTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                boolean hasSelection = newSelection != null;
                modifierButton.setDisable(!hasSelection);
                supprimerButton.setDisable(!hasSelection);
            });

            // Initially disable buttons
            modifierButton.setDisable(true);
            supprimerButton.setDisable(true);
            
            // Configurer le rafraîchissement automatique toutes les 5 secondes (plus court pour voir les changements plus rapidement)
            startAutoRefresh();
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur d'initialisation", 
                    "Impossible d'initialiser l'écran: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void startAutoRefresh() {
        // Arrêter le scheduler existant s'il existe
        stopAutoRefresh();
        
        // Créer un nouveau scheduler
        scheduler = Executors.newSingleThreadScheduledExecutor();
        
        // Programmer le rafraîchissement périodique
        scheduler.scheduleAtFixedRate(() -> {
            // Exécution sur le thread JavaFX
            Platform.runLater(this::loadFormData);
        }, 5, 5, TimeUnit.SECONDS); // Réduit à 5 secondes pour plus de réactivité
    }
    
    private void stopAutoRefresh() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                // Attendre la fin des tâches en cours
                if (!scheduler.awaitTermination(2, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
        }
    }

    private void loadFormData() {
        try {
            // Sauvegarder l'élément sélectionné pour le restaurer après
            Form_p selectedForm = formTable.getSelectionModel().getSelectedItem();
            Integer selectedId = selectedForm != null ? selectedForm.getId() : null;
            
            // Get all forms from the database
            List<Form_p> forms = formService.getAll();
            
            // Convert to observable list
            formList = FXCollections.observableArrayList(forms);
            
            // Set items in table
            formTable.setItems(formList);
            
            // Restaurer la sélection si possible
            if (selectedId != null) {
                formList.stream()
                    .filter(form -> form.getId() == selectedId)
                    .findFirst()
                    .ifPresent(form -> {
                        formTable.getSelectionModel().select(form);
                        formTable.scrollTo(form);
                    });
            }
            
            // Informer si aucun formulaire n'est trouvé
            if (forms.isEmpty() && formTable.getItems().isEmpty()) {
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.INFORMATION, "Aucune donnée", 
                            "Aucun formulaire trouvé dans la base de données.");
                });
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement", 
                    "Impossible de charger les données: " + e.getMessage());
            // Initialiser une liste vide en cas d'erreur
            formList = FXCollections.observableArrayList(Collections.emptyList());
            formTable.setItems(formList);
            e.printStackTrace();
        }
    }

    @FXML
    public void handleAjouter(ActionEvent event) {
        try {
            // Load the add form UI
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterForm_p.fxml"));
            Parent root = loader.load();
            
            // Create new stage
            Stage stage = new Stage();
            stage.setTitle("Ajouter un formulaire");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(rootPane.getScene().getWindow());
            
            // Show stage and wait until closed
            stage.showAndWait();
            
            // Refresh table immediately after closing
            loadFormData();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                    "Impossible d'ouvrir le formulaire d'ajout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleModifier(ActionEvent event) {
        Form_p selectedForm = formTable.getSelectionModel().getSelectedItem();
        
        if (selectedForm == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", 
                    "Veuillez sélectionner un formulaire à modifier.");
            return;
        }
        
        try {
            // Load the modify form UI
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierForm_p.fxml"));
            Parent root = loader.load();
            
            // Get the controller and set the form data
            ModifierForm_pController controller = loader.getController();
            controller.setFormData(selectedForm);
            
            // Create new stage
            Stage stage = new Stage();
            stage.setTitle("Modifier le formulaire");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(rootPane.getScene().getWindow());
            
            // Show stage and wait until closed
            stage.showAndWait();
            
            // Refresh table immediately after closing
            loadFormData();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                    "Impossible d'ouvrir le formulaire de modification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSupprimer(ActionEvent event) {
        Form_p selectedForm = formTable.getSelectionModel().getSelectedItem();
        
        if (selectedForm == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", 
                    "Veuillez sélectionner un formulaire à supprimer.");
            return;
        }
        
        // Confirm deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation de suppression");
        confirmAlert.setHeaderText("Supprimer le formulaire");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer ce formulaire?");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Delete from database
                formService.delete(selectedForm);
                
                // Remove from table
                formList.remove(selectedForm);
                
                showAlert(Alert.AlertType.INFORMATION, "Suppression réussie", 
                        "Le formulaire a été supprimé avec succès.");
                
                // Reload data in case there are other changes
                loadFormData();
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de suppression", 
                        "Impossible de supprimer le formulaire: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
    
    // Méthode appelée lorsque la fenêtre est fermée
    public void cleanup() {
        stopAutoRefresh();
        if (formService != null) {
            formService.closeConnection();
        }
    }
} 