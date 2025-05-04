package Controller;

import Models.Ticket;
import Models.Event;
import Services.TicketService;
import Services.EventService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class CreateTicket {

    @FXML private ComboBox<Integer> eventIdComboBox;
    @FXML
    private DatePicker purchaseDatePicker;
    @FXML
    private TextField emailField;
    @FXML
    private TextField nomField;
    @FXML
    private Button createBtn;

    private TicketService ticketService; // 👈 Supprimez 'final'

    private EventService eventService;

    @FXML
    public void initialize() {
        try {
            // Initialisation sécurisée des services
            ticketService = new TicketService();
            eventService = new EventService();
            loadEventIds();
        } catch (SQLException e) {
            showAlert(
                    "Erreur SQL",
                    "Connexion échouée",
                    "Impossible de se connecter à la base de données : " + e.getMessage(),
                    Alert.AlertType.ERROR
            );
        }
        createBtn.setOnAction(e -> handleCreateTicket());
    }

    private void loadEventIds() {
        try {
            List<Event> events = eventService.getAll();
            ObservableList<Integer> eventIds = FXCollections.observableArrayList(
                    events.stream()
                            .map(Event::getId)
                            .collect(Collectors.toList())
            );
            eventIdComboBox.setItems(eventIds);

            if (eventIds.isEmpty()) {
                showAlert(
                        "Aucun événement",
                        "Information",
                        "Créez un événement avant de créer un ticket",
                        Alert.AlertType.WARNING
                );
            }
        } catch (SQLException e) {
            showAlert(
                    "Erreur",
                    "Problème de base de données",
                    "Impossible de charger les événements : " + e.getMessage(),
                    Alert.AlertType.ERROR
            );
        }
    }

    private void handleCreateTicket() {
        try {
            if (eventIdComboBox.getValue() == null
                    || purchaseDatePicker.getValue() == null
                    || emailField.getText().isEmpty()
                    || nomField.getText().isEmpty()) {

                showAlert("Erreur", "Validation", "Tous les champs sont obligatoires", Alert.AlertType.WARNING);
                return;
            }

            Ticket ticket = new Ticket();
            ticket.setEvent_id(eventIdComboBox.getValue());
            ticket.setPurchase_date(purchaseDatePicker.getValue().toString());
            ticket.setEmail(emailField.getText());
            ticket.setNom(nomField.getText());

            ticketService.add(ticket); // <-- Assurez-vous que cette méthode peut lancer SQLException
            clearFields();
            showAlert("Succès", "Confirmation", "Ticket créé avec succès", Alert.AlertType.INFORMATION);

        } catch (SQLException e) {
            showAlert(
                    "Erreur SQL",
                    "Échec de création",
                    "Erreur lors de la création : " + e.getMessage(),
                    Alert.AlertType.ERROR
            );
        }
    }

    private void clearFields() {
        eventIdComboBox.setValue(null);
        purchaseDatePicker.setValue(null);
        emailField.clear();
        nomField.clear();
    }

    private void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
