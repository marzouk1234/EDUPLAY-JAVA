package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import Services.FeedbackService;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Map;
import java.util.ResourceBundle;

public class FeedbackStatistics implements Initializable {  // 👈 ADD implements Initializable

    @FXML
    private PieChart pieChart;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        FeedbackService fs = null;
        try {
            fs = new FeedbackService();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            Map<Integer, Integer> stats = fs.getFeedbackCountByRating();
            for (Map.Entry<Integer, Integer> entry : stats.entrySet()) {
                pieChart.getData().add(new PieChart.Data(entry.getKey() + " Stars", entry.getValue()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
