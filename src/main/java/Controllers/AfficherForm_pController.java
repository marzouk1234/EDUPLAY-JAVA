package Controllers;

import Models.Form_p;
import Services.Form_pService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private Button refreshButton;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private Label countLabel;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private DatePicker dateFilter;

    private Form_pService formService;
    private ObservableList<Form_p> formList;
    private FilteredList<Form_p> filteredData;
    private SortedList<Form_p> sortedData;
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

            // Configurer le tooltip pour le contenu
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
            
            // Configurer la recherche
            setupSearchAndSort();

            // Add listener for row selection
            formTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                boolean hasSelection = newSelection != null;
                modifierButton.setDisable(!hasSelection);
                supprimerButton.setDisable(!hasSelection);
            });

            // Initially disable buttons
            modifierButton.setDisable(true);
            supprimerButton.setDisable(true);
            
            // Configurer le rafraîchissement automatique toutes les 5 secondes
            startAutoRefresh();
            
            // Configuration du filtre par date
            setupDateFilter();
            
            statusLabel.setText("Interface initialisée avec succès");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur d'initialisation", 
                    "Impossible d'initialiser l'écran: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void setupSearchAndSort() {
        // Configurer le filtre
        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilter());
        dateFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilter());
    }
    
    private void applyFilter() {
        if (filteredData == null) return;
        
        String searchText = searchField.getText().toLowerCase();
        LocalDate selectedDate = dateFilter.getValue();

        filteredData.setPredicate(form -> {
            if (form == null) return false;

            boolean matchesSearch = searchText == null || searchText.isEmpty() ||
                    form.getSujet().toLowerCase().contains(searchText) ||
                    form.getContenu().toLowerCase().contains(searchText) ||
                    form.getAuteur().toLowerCase().contains(searchText);

            boolean matchesDate = selectedDate == null ||
                    form.getDatePub().equals(selectedDate);

            return matchesSearch && matchesDate;
        });
        
        updateCountLabel();
    }
    
    private void updateCountLabel() {
        if (filteredData == null) {
            filteredData = new FilteredList<>(formList, p -> true);
        }
        int totalCount = formList.size();
        int filteredCount = filteredData.size();
        
        if (totalCount == filteredCount) {
            countLabel.setText(totalCount + " formulaire(s) au total");
        } else {
            countLabel.setText(filteredCount + " formulaire(s) sur " + totalCount + " au total");
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
        }, 5, 5, TimeUnit.SECONDS);
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
            
            // Configurer le FilteredList si c'est la première fois
            if (filteredData == null) {
                filteredData = new FilteredList<>(formList, p -> true);
                // Créer et configurer le SortedList
                sortedData = new SortedList<>(filteredData);
                sortedData.comparatorProperty().bind(formTable.comparatorProperty());
                formTable.setItems(sortedData);
            }
            
            // Appliquer le filtre actuel
            applyFilter();
            
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
            
            // Mettre à jour le compteur
            updateCountLabel();
            
            // Informer si aucun formulaire n'est trouvé
            if (forms.isEmpty()) {
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
            
            // Create a new stage for the add form
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Ajouter un formulaire");
            stage.setScene(new Scene(root));
            
            // Show the stage and wait for it to close
            stage.showAndWait();
            
            // Refresh the data after the form is closed
            loadFormData();
            
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                    "Impossible d'ouvrir le formulaire d'ajout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleModifier(ActionEvent event) {
        // Get the selected form
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
            
            // Get the controller and set the form to modify
            ModifierForm_pController controller = loader.getController();
            controller.setFormData(selectedForm);
            
            // Create a new stage for the modify form
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modifier un formulaire");
            stage.setScene(new Scene(root));
            
            // Show the stage and wait for it to close
            stage.showAndWait();
            
            // Refresh the data after the form is closed
            loadFormData();
            
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                    "Impossible d'ouvrir le formulaire de modification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSupprimer(ActionEvent event) {
        // Get the selected form
        Form_p selectedForm = formTable.getSelectionModel().getSelectedItem();
        
        if (selectedForm == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", 
                    "Veuillez sélectionner un formulaire à supprimer.");
            return;
        }
        
        // Confirm deletion
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce formulaire ?");
        
        Optional<ButtonType> result = alert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Delete the form
                formService.delete(selectedForm);
                
                // Refresh the data
                loadFormData();
                
                showAlert(Alert.AlertType.INFORMATION, "Succès", 
                        "Le formulaire a été supprimé avec succès.");
                
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", 
                        "Impossible de supprimer le formulaire: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    public void handleRefresh(ActionEvent event) {
        loadFormData();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void cleanup() {
        stopAutoRefresh();
    }

    private void setupDateFilter() {
        dateFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilter();
        });
    }
} 