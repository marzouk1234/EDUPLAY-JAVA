package Controllers;

import Models.Feedback;
import Services.FeedbackService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class UpdateFeed {

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

    private final FeedbackService feedbackService = new FeedbackService();
    private Feedback selectedFeedbackToEdit;

    public UpdateFeed() throws SQLException {
    }

    // This method is called from FeedbackView to send the feedback to update
    public void setFeedbackToEdit(Feedback feedback) {
        this.selectedFeedbackToEdit = feedback;

        // Pre-fill form
        tfId.setText(String.valueOf(feedback.getId()));
        tfIdJeux.setText(String.valueOf(feedback.getid_jeux()));
        tfNom.setText(feedback.getNom());
        tfPrenom.setText(feedback.getPrenom());
        tfFeedback.setText(feedback.getFeedback());
        tfRating.setText(String.valueOf(feedback.getRating()));
    }

    @FXML
    void handleUpdate() {
        try {
            if (selectedFeedbackToEdit == null) {
                showError("Update Error", "No feedback was provided to update.");
                return;
            }

            // Update values from form
            selectedFeedbackToEdit.setId(Integer.parseInt(tfId.getText()));
            selectedFeedbackToEdit.setid_jeux(Integer.parseInt(tfIdJeux.getText()));
            selectedFeedbackToEdit.setNom(tfNom.getText());
            selectedFeedbackToEdit.setPrenom(tfPrenom.getText());
            selectedFeedbackToEdit.setFeedback(tfFeedback.getText());
            selectedFeedbackToEdit.setRating(Integer.parseInt(tfRating.getText()));

            feedbackService.modifier(selectedFeedbackToEdit);

            showInfo("Feedback updated successfully!");
        } catch (Exception e) {
            showError("Update Error", e.getMessage());
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
}
