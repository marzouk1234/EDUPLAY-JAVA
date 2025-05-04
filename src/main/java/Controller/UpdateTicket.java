package Controller;

import Models.Ticket;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

public class UpdateTicket {
    @FXML
    private TextField eventIdField;
    @FXML
    private DatePicker purchaseDatePicker;
    @FXML
    private TextField emailField;
    @FXML
    private TextField nomField;
    @FXML
    private Button updateBtn;

    private Ticket ticket;
    private TicketController parentController;

    public void initData(Ticket ticket, TicketController controller) {
        this.ticket = ticket;
        this.parentController = controller;

        // Initialiser les champs avec les données du ticket
        eventIdField.setText(String.valueOf(ticket.getEvent_id()));
        purchaseDatePicker.setValue(LocalDate.parse(ticket.getPurchase_date()));
        emailField.setText(ticket.getEmail());
        nomField.setText(ticket.getNom());
    }

    @FXML
    public void initialize() {
        updateBtn.setOnAction(event -> handleUpdateTicket());
    }

    private void handleUpdateTicket() {
        try {
            // Validation des champs
            if (eventIdField.getText().isEmpty() || purchaseDatePicker.getValue() == null
                    || emailField.getText().isEmpty() || nomField.getText().isEmpty()) {
                showAlert("Erreur", "Champs manquants", "Veuillez remplir tous les champs", Alert.AlertType.WARNING);
                return;
            }

            // Mettre à jour l'objet ticket
            ticket.setEvent_id(Integer.parseInt(eventIdField.getText()));
            ticket.setPurchase_date(purchaseDatePicker.getValue().toString());
            ticket.setEmail(emailField.getText());
            ticket.setNom(nomField.getText());

            // Appeler la mise à jour via le parentController
            parentController.refreshTickets();

            // Fermer la fenêtre
            ((Stage) updateBtn.getScene().getWindow()).close();

            showAlert("Succès", "Ticket modifié", "Le ticket a été mis à jour avec succès", Alert.AlertType.INFORMATION);

        } catch (NumberFormatException e) {
            showAlert("Erreur", "ID Événement invalide", "L'ID de l'événement doit être un nombre", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}