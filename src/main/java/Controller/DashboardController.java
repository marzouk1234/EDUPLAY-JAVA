package Controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;


public class DashboardController {

    @FXML
    private AnchorPane contentPane;

    @FXML
    private void goToCreateEvent() {
        loadUI("CreateEvent");
    }

    @FXML
    private void goToUpdateEvent() {loadUI("UpdateEvent");}



    @FXML
    private void goToEventList() {
        loadUI("EventList");
    }

    @FXML
    private void goToCreateTicket() {
        loadUI("CreateTicket");
    }


    private void loadUI(String fxml) {
        String resourcePath = "/" + fxml + ".fxml";
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcePath));
            Parent root = loader.load();

            // Initialiser le contrôleur si c'est EventList
            if (fxml.equals("EventList")) {
                EventController controller = loader.getController();
                controller.initialize(); // Force le chargement des données
            }

            contentPane.getChildren().setAll(root);
        } catch (IOException e) {
            System.err.println("Erreur: Impossible de charger " + resourcePath + ". Cause: " + e.getMessage());
        }
    }


}