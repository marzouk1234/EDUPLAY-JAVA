package Controller;

import Models.Event;
import Services.EventService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Comparator;
import java.sql.SQLException;
import java.util.List;

public class EventController {

    @FXML private ListView<Event> eventsListView;
    @FXML private Button supp;
    @FXML private Button udp;
    private  Event selectedEvent;
    private static EventController instance;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> searchAttribute;
    @FXML private Label statusLabel;
    private String selectedAttribute = "Nom";


    private final EventService eventService = new EventService();
    private final ObservableList<Event> events = FXCollections.observableArrayList();
    private final FilteredList<Event> filteredEvents = new FilteredList<>(events, p -> true);


    private void loadView(String fxmlPath, String title) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.show();
    }
    @FXML
    public void initialize() {
       
        setupEventHandlers();
        searchAttribute.getItems().clear();
        searchAttribute.getItems().addAll("Nom", "Description", "Date");
        searchAttribute.setValue("Nom"); // Valeur par défaut
        searchAttribute.valueProperty().addListener((obs, oldVal, newVal) -> {
            selectedAttribute = newVal;
            updateFilter();
        });
        searchField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());

        loadEvents();
        configureListView();
    }
    private void updateFilter() {
        filteredEvents.setPredicate(event -> {
            String searchText = searchField.getText().toLowerCase();
            if (searchText.isEmpty()) return true; // Afficher tout si le champ est vide


            String attributeValue = switch (selectedAttribute) {
                case "Nom" -> event.getNom() != null ? event.getNom().toLowerCase() : "";
                case "Description" -> event.getDescription() != null ? event.getDescription().toLowerCase() : "";
                case "Date" -> event.getDate() != null ? event.getDate().toLowerCase() : "";
                default -> "";
            };
            return attributeValue.contains(searchText);
        });
    }


    public  Event getSelectedEvent() {
        return selectedEvent;
    }
    public static EventController getInstance() {
        if (instance == null) {
            instance = new EventController();
        }
        return instance;
    }

    private void setupEventHandlers() {
        supp.setOnAction(this::handleDeleteEvent);
        udp.setOnAction(this::handleModifyEvent);
    }

    private void loadEvents() {
        try {
            List<Event> eventsFromDB = eventService.getAll();
            System.out.println("Événements récupérés : " + eventsFromDB); // Ajoutez ceci
            events.setAll(eventsFromDB);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void configureListView() {
        eventsListView.setItems(filteredEvents); // Utiliser la liste filtrée
        eventsListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Event event, boolean empty) {
                super.updateItem(event, empty);
                setText(empty || event == null ? null : formatEventText(event));
            }
        });
    }

    private String formatEventText(Event event) {
        return String.format(
                "Nom: %s\nDescription: %s\nDate: %s\nImage: %s",
                event.getNom(),
                event.getDescription(),
                event.getDate(),
                event.getIMG()
        );
    }

    @FXML
    private void handleModifyEvent(ActionEvent event) {
        selectedEvent = eventsListView.getSelectionModel().getSelectedItem();
        if (selectedEvent != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateEvent.fxml"));
                Parent root = loader.load();
                UpdateEvent controller = loader.getController();
                controller.initData(selectedEvent, this); // <-- "this" suffit
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                showAlert("Erreur", "Impossible d'ouvrir l'éditeur", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner un événement", Alert.AlertType.WARNING);
        }

    }

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

    public void refreshEvents() {
        loadEvents();
        eventsListView.refresh();
    }
    @FXML
    private void sortByDate(ActionEvent event) {

        events.sort(Comparator.comparing(Event::getDate));

        eventsListView.setItems(events);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}