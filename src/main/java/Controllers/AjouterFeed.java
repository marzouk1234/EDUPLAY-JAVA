package Controllers;

import Models.Feedback;
import Models.Game;
import Services.GameService1;
import Services.FeedbackService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;

public class AjouterFeed {

    @FXML
    private TextField tfId;
    @FXML
    private TextField tfIdJeux;
    @FXML
    private TextField tfNom;
    @FXML
    private TextField tfPrenom;
    @FXML
    private TextField tfFeedback;
    @FXML
    private TextField tfRating;

    private FeedbackService feedbackService = new FeedbackService();

    public AjouterFeed() throws SQLException {
    }

    @FXML
    void handleAdd() {
        try {
            // Validate and retrieve the Game ID (id_jeux)
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

            // Validate other fields
            String feedbackText = tfFeedback.getText();
            if (feedbackText == null || feedbackText.isEmpty()) {
                showError("Validation Error", "Please provide feedback.");
                return;
            }

            String ratingText = tfRating.getText();
            int rating;
            try {
                rating = Integer.parseInt(ratingText);
            } catch (NumberFormatException e) {
                showError("Validation Error", "Rating must be a valid number.");
                return;
            }

            // Create the Feedback object
            Feedback fb = new Feedback(
                    Integer.parseInt(tfId.getText()),  // Assuming tfId is the ID for feedback
                    idJeux,                             // Game ID
                    tfNom.getText(),
                    tfPrenom.getText(),
                    feedbackText,
                    rating
            );

            // Call the service to add feedback to the database
            feedbackService.ajouter(fb);

            showInfo("Feedback added successfully!");
            loadTable();  // Refresh the table
        } catch (Exception e) {
            showError("Add Error", e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadTable() {
        // Implement table reloading logic here
        System.out.println("Reload the feedback table.");
    }
}
