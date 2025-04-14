package Controllers;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Home extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/GameView.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Game Manager");
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
