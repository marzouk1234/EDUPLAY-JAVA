package Controllers;

import Models.Feedback;
import Models.Game;
import Services.FeedbackService1;
import Services.GameService1;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.collections.transformation.FilteredList;

import java.io.IOException;
import java.sql.SQLException;

import static sun.net.www.MimeTable.loadTable;

public class FeedbackView {

    @FXML private TextField tfId;
    @FXML private TextField tfIdJeux;
    @FXML private TextField tfNom;
    @FXML private TextField tfPrenom;
    @FXML private TextField tfFeedback;
    @FXML private TextField tfRating;
    @FXML private TextField searchField;

    @FXML private TableView<Feedback> feedbackTable;
    @FXML private TableColumn<Feedback, Integer> colId;
    @FXML private TableColumn<Feedback, Integer> colIdJeux;
    @FXML private TableColumn<Feedback, String> colNom;
    @FXML private TableColumn<Feedback, String> colPrenom;
    @FXML private TableColumn<Feedback, String> colFeedback;
    @FXML private TableColumn<Feedback, Integer> colRating;

    private FeedbackService1 feedbackService;
    private FilteredList<Feedback> filteredList;

    @FXML
    public void initialize() throws SQLException {
        feedbackService = new FeedbackService1();
        ObservableList<Feedback> list = FXCollections.observableArrayList(feedbackService.afficher());

        // Create a filtered list
        filteredList = new FilteredList<>(list, p -> true);

        feedbackTable.setItems(filteredList);

        // Setting up the table columns
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colFeedback.setCellValueFactory(new PropertyValueFactory<>("feedback"));
        colRating.setCellValueFactory(new PropertyValueFactory<>("rating"));
    }

    @FXML
    void handleSearch() {
        String searchText = searchField.getText().toLowerCase();

        filteredList.setPredicate(feedback -> {
            if (searchText == null || searchText.isEmpty()) {
                return true; // No filtering
            }

            // Filter by 'nom' field (case-insensitive)
            return feedback.getNom().toLowerCase().contains(searchText);
        });
    }

    @FXML
    void handleAdd() {
        try {
            String idJeuxText = tfIdJeux.getText();
            if (idJeuxText == null || idJeuxText.isEmpty()) {
                showError("Validation Error", "Please enter a game ID.");
                return;
            }

            int idJeux = Integer.parseInt(idJeuxText);

            // Check if the game with the entered id_jeux exists
            Game game = new GameService1().getGameById(idJeux);
            if (game == null) {
                showError("Validation Error", "The entered game ID does not exist.");
                return;
            }

            // Create the Feedback object with the entered game ID (id_jeux)
            Feedback fb = new Feedback(
                    Integer.parseInt(tfId.getText()),  // Assuming tfId is the ID for feedback
                    Integer.parseInt(tfIdJeux.getText()),  // Use entered ID here
                    tfNom.getText(),
                    tfPrenom.getText(),
                    tfFeedback.getText(),
                    Integer.parseInt(tfRating.getText())
            );

            // Call the service to add feedback to the database
            feedbackService.ajouter(fb);

            showInfo("Feedback added successfully!");
            loadTable();  // Refresh the table
        } catch (Exception e) {
            showError("Add Error", e.getMessage());
        }
    }

    @FXML
    void handleUpdate() {
        try {
            Feedback selectedFeedback = feedbackTable.getSelectionModel().getSelectedItem();
            if (selectedFeedback == null) {
                showError("Update Error", "Please select a feedback to update.");
                return;
            }

            // Get values from the text fields to update the selected feedback
            selectedFeedback.setNom(tfNom.getText());
            selectedFeedback.setPrenom(tfPrenom.getText());
            selectedFeedback.setFeedback(tfFeedback.getText());
            selectedFeedback.setRating(Integer.parseInt(tfRating.getText()));

            // Update the feedback in the database
            feedbackService.modifier(selectedFeedback);

            showInfo("Feedback updated successfully!");
            loadTable();  // Refresh the table
        } catch (Exception e) {
            showError("Update Error", e.getMessage());
        }
    }

    @FXML
    void handleDelete() {
        try {
            Feedback selectedFeedback = feedbackTable.getSelectionModel().getSelectedItem();
            if (selectedFeedback == null) {
                showError("Delete Error", "Please select a feedback to delete.");
                return;
            }

            // Delete the selected feedback from the database
            feedbackService.supprimer(selectedFeedback.getId());

            showInfo("Feedback deleted successfully!");
            loadTable();  // Refresh the table
        } catch (Exception e) {
            showError("Delete Error", e.getMessage());
        }
    }

    @FXML
    void handleRefresh() {
        loadTable();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setContentText(msg);
        alert.show();
    }

    private void showError(String header, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(msg);
        alert.show();
    }

    @FXML
    void openAjouterFeed() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterFeed.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter Feedback");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showError("Error", "Failed to load Add Feedback window: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void openUpdateFeed() {
        Feedback selectedFeedback = feedbackTable.getSelectionModel().getSelectedItem();
        if (selectedFeedback == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a feedback to edit.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateFeed.fxml"));
            Parent root = loader.load();

            // Pass the selected feedback to UpdateFeed controller
            UpdateFeed controller = loader.getController();
            controller.setFeedbackToEdit(selectedFeedback);

            Stage stage = new Stage();
            stage.setTitle("Update Feedback");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void goToMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) feedbackTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Main View");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
