package Controller;

import Models.Ticket;
import Services.TicketService;
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

public class TicketController {

    @FXML private ListView<Ticket> ticketsListView;
    @FXML private Button deleteBtn;
    @FXML private Button modifyBtn;
    @FXML private Button eventBtn;

    private TicketService ticketService;
    private final ObservableList<Ticket> tickets = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            ticketService = new TicketService();
            setupEventHandlers();
            loadTickets();
            configureListView();
        } catch (SQLException e) {
            showAlert(
                    "Erreur SQL",
                    "Connexion échouée",
                    "Impossible de se connecter à la base de données : " + e.getMessage(),
                    Alert.AlertType.ERROR
            );
        }
    }

    private void setupEventHandlers() {
        deleteBtn.setOnAction(this::handleDeleteTicket);
        modifyBtn.setOnAction(this::handleModifyTicket);
        eventBtn.setOnAction(this::handleEventButton);
    }

    private void loadTickets() {
        try {
            List<Ticket> ticketsFromDB = ticketService.getAll();
            tickets.setAll(ticketsFromDB);
        } catch (SQLException e) {
            showAlert(
                    "Erreur SQL",
                    "Échec du chargement",
                    "Échec du chargement des tickets : " + e.getMessage(),
                    Alert.AlertType.ERROR
            );
        }
    }

    private void configureListView() {
        ticketsListView.setItems(tickets);
        ticketsListView.setCellFactory(lv -> new javafx.scene.control.ListCell<Ticket>() {
            @Override
            protected void updateItem(Ticket ticket, boolean empty) {
                super.updateItem(ticket, empty);
                setText(empty || ticket == null ? null : formatTicketText(ticket));
            }
        });
    }

    private String formatTicketText(Ticket ticket) {
        return String.format(
                "Événement ID: %d\nDate d'achat: %s\nEmail: %s\nNom: %s",
                ticket.getEvent_id(),
                ticket.getPurchase_date(),
                ticket.getEmail(),
                ticket.getNom()
        );
    }

    @FXML
    private void handleModifyTicket(ActionEvent event) {
        Ticket selectedTicket = ticketsListView.getSelectionModel().getSelectedItem();

        if (selectedTicket != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateTicket.fxml"));
                Parent root = loader.load();

                UpdateTicket controller = loader.getController();
                controller.initData(selectedTicket, this);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Modifier le ticket");
                stage.show();

            } catch (IOException e) {
                showAlert(
                        "Erreur d'interface",
                        "Ouverture impossible",
                        "Impossible d'ouvrir l'éditeur de tickets",
                        Alert.AlertType.ERROR
                );
            }
        } else {
            showAlert(
                    "Aucune sélection",
                    "Sélection requise",
                    "Veuillez sélectionner un ticket à modifier",
                    Alert.AlertType.WARNING
            );
        }
    }

    @FXML
    private void handleDeleteTicket(ActionEvent event) {
        Ticket selectedTicket = ticketsListView.getSelectionModel().getSelectedItem();

        if (selectedTicket != null) {
            try {
                ticketService.delete(selectedTicket);
                tickets.remove(selectedTicket);
                showAlert(
                        "Succès",
                        "Suppression réussie",
                        "Ticket supprimé",
                        Alert.AlertType.INFORMATION
                );
            } catch (SQLException e) {
                showAlert(
                        "Erreur SQL",
                        "Échec de suppression",
                        "Échec de la suppression : " + e.getMessage(),
                        Alert.AlertType.ERROR
                );
            }
        } else {
            showAlert(
                    "Aucune sélection",
                    "Sélection requise",
                    "Veuillez sélectionner un ticket",
                    Alert.AlertType.WARNING
            );
        }
    }

    public void refreshTickets() {
        loadTickets();
        ticketsListView.refresh();
    }

    private void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleEventButton(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/EventView.fxml"));
            Stage stage = (Stage) eventBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(
                    "Erreur",
                    "Chargement impossible",
                    "Impossible de charger la vue des événements",
                    Alert.AlertType.ERROR
            );
        }
    }
}