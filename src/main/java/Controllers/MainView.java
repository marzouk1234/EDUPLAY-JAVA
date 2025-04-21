package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainView {

    @FXML
    private void loadGameView(ActionEvent event) {
        try {
            Parent gameView = FXMLLoader.load(getClass().getResource("/GameView.fxml"));
            Scene gameScene = new Scene(gameView);

            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            window.setScene(gameScene);
            window.setTitle("Liste des jeux");
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
            // You can show an alert here if the view fails to load
        }
    }

    @FXML
    private void loadFeedbackView(ActionEvent event) {
        try {
            Parent feedbackView = FXMLLoader.load(getClass().getResource("/FeedbackView.fxml"));
            Scene feedbackScene = new Scene(feedbackView);

            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            window.setScene(feedbackScene);
            window.setTitle("Liste des feedback");
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle error, maybe show an alert to the user
        }
    }
}
