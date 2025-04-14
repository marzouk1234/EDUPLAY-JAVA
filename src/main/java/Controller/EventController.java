package Controller;

import Models.Event;
import Services.EventService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class EventController {

    @FXML private ListView<Event> eventsListView;
    @FXML private Button supp;
    @FXML private Button mod;
    @FXML private Button tk;

    private final EventService eventService = new EventService();
    private final ObservableList<Event> events = FXCollections.observableArrayList();

    // Initialisation principale
    @FXML
    public void initialize() {
        setupEventHandlers();
        loadEvents();
        configureListView();
    }

    // Configuration des gestionnaires d'événements
    private void setupEventHandlers() {
        supp.setOnAction(this::handleDeleteEvent);
        mod.setOnAction(this::handleModifyEvent);
    }

    // Chargement des événements depuis la base de données
    private void loadEvents() {
        try {
            List<Event> eventsFromDB = eventService.getAll();
            events.setAll(eventsFromDB);
        } catch (SQLException e) {
            showAlert("Erreur SQL", "Échec du chargement : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Configuration de l'affichage de la ListView
    private void configureListView() {
        eventsListView.setItems(events);
        eventsListView.setCellFactory(lv -> new javafx.scene.control.ListCell<Event>() {
            @Override
            protected void updateItem(Event event, boolean empty) {
                super.updateItem(event, empty);
                setText(empty || event == null ? null : formatEventText(event));
            }
        });
    }

    // Formatage du texte pour chaque événement
    private String formatEventText(Event event) {
        return String.format(
                "Nom: %s\nDescription: %s\nDate: %s\nImage: %s",
                event.getNom(),
                event.getDescription(),
                event.getDate(),
                event.getIMG()
        );
    }

    // Gestion de la modification d'événement
    @FXML
    private void handleModifyEvent(ActionEvent event) {
        Event selectedEvent = eventsListView.getSelectionModel().getSelectedItem();

        if (selectedEvent != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateEvent.fxml"));
                Parent root = loader.load();

                UpdateEvent controller = loader.getController();
                controller.initData(selectedEvent, this);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Modifier l'événement");
                stage.show();

            } catch (IOException e) {
                showAlert("Erreur d'interface", "Impossible d'ouvrir l'éditeur", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner un événement à modifier", Alert.AlertType.WARNING);
        }
    }

    // Gestion de la suppression d'événement
    @FXML
    private void handleDeleteEvent(ActionEvent event) {
        Event selectedEvent = eventsListView.getSelectionModel().getSelectedItem();

        if (selectedEvent != null) {
            try {
                eventService.delete(selectedEvent);
                events.remove(selectedEvent);
                showAlert("Succès", "Événement supprimé", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Erreur SQL", "Échec de la suppression : " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner un événement", Alert.AlertType.WARNING);
        }
    }

    // Méthode pour rafraîchir la liste
    public void refreshEvents() {
        loadEvents();
        eventsListView.refresh();
    }

    // Affichage des alertes standardisées
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}