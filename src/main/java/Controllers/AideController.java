package Controllers;

import Models.Aide;
import Models.Form_p;
import Services.AideService;
import Services.Form_pService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AideController {
    @FXML
    private TableView<Aide> aideTable;
    @FXML
    private TableColumn<Aide, Integer> idColumn;
    @FXML
    private TableColumn<Aide, String> sujetColumn;
    @FXML
    private TableColumn<Aide, String> descriptionColumn;
    @FXML
    private TableColumn<Aide, String> dateCreationColumn;
    @FXML
    private TableColumn<Aide, String> formSujetColumn;
    @FXML
    private TextField searchField;
    @FXML
    private Label statusLabel;

    private AideService aideService;
    private Form_pService formService;
    private ObservableList<Aide> aideList;
    private Map<Integer, String> formSujets;

    @FXML
    public void initialize() {
        aideService = new AideService();
        formService = new Form_pService();
        aideList = FXCollections.observableArrayList();
        formSujets = new HashMap<>();

        // Configuration des colonnes
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        sujetColumn.setCellValueFactory(new PropertyValueFactory<>("sujet"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        dateCreationColumn.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String formattedDate = cellData.getValue().getDateCreation().format(formatter);
            return new SimpleStringProperty(formattedDate);
        });
        formSujetColumn.setCellValueFactory(cellData -> {
            int formId = cellData.getValue().getFormId();
            String sujet = formSujets.getOrDefault(formId, "Non associé");
            return new SimpleStringProperty(sujet);
        });

        // Chargement des données
        loadAides();
        loadFormSujets();

        // Configuration de la recherche
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterAides(newValue);
        });
    }

    private void loadFormSujets() {
        try {
            List<Form_p> forms = formService.getAll();
            for (Form_p form : forms) {
                formSujets.put(form.getId(), form.getSujet());
            }
        } catch (SQLException e) {
            statusLabel.setText("Erreur lors du chargement des formulaires: " + e.getMessage());
        }
    }

    private void loadAides() {
        try {
            aideList.clear();
            aideList.addAll(aideService.getAllWithFormDetails());
            aideTable.setItems(aideList);
            statusLabel.setText("Données chargées avec succès");
        } catch (SQLException e) {
            statusLabel.setText("Erreur lors du chargement des données: " + e.getMessage());
        }
    }

    private void filterAides(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            aideTable.setItems(aideList);
            return;
        }

        ObservableList<Aide> filteredList = FXCollections.observableArrayList();
        for (Aide aide : aideList) {
            if (aide.getSujet().toLowerCase().contains(searchText.toLowerCase()) ||
                aide.getDescription().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(aide);
            }
        }
        aideTable.setItems(filteredList);
    }

    @FXML
    private void handleAjouterAide() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterAide.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Ajouter une aide");
            stage.setScene(new Scene(root));
            
            stage.showAndWait();
            loadAides(); // Recharger la liste après l'ajout
        } catch (IOException e) {
            statusLabel.setText("Erreur lors de l'ouverture du formulaire d'ajout: " + e.getMessage());
        }
    }

    @FXML
    private void handleModifierAide() {
        Aide selectedAide = aideTable.getSelectionModel().getSelectedItem();
        if (selectedAide == null) {
            statusLabel.setText("Veuillez sélectionner une aide à modifier");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierAide.fxml"));
            Parent root = loader.load();
            
            ModifierAideController controller = loader.getController();
            controller.setAide(selectedAide);
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modifier une aide");
            stage.setScene(new Scene(root));
            
            stage.showAndWait();
            loadAides(); // Recharger la liste après la modification
        } catch (IOException e) {
            statusLabel.setText("Erreur lors de l'ouverture du formulaire de modification: " + e.getMessage());
        }
    }

    @FXML
    private void handleSupprimerAide() {
        Aide selectedAide = aideTable.getSelectionModel().getSelectedItem();
        if (selectedAide == null) {
            statusLabel.setText("Veuillez sélectionner une aide à supprimer");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette aide ?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                aideService.delete(selectedAide);
                loadAides(); // Recharger la liste après la suppression
                statusLabel.setText("Aide supprimée avec succès");
            } catch (SQLException e) {
                statusLabel.setText("Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleRefresh() {
        loadAides();
        loadFormSujets();
    }
} 