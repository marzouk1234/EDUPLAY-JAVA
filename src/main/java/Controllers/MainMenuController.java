package Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
import java.util.HashMap;
import java.util.Map;

public class MainMenuController {
    
    private Map<String, Stage> openWindows = new HashMap<>();
    
    @FXML
    private void handleOpenFormulaires() {
        openWindow("formulaires", "/AfficherForm_p.fxml", "Liste des Formulaires");
    }
    
    @FXML
    private void handleOpenAides() {
        openWindow("aides", "/AfficherAide.fxml", "Liste des Aides");
    }
    
    private void openWindow(String key, String fxmlPath, String title) {
        try {
            // Si la fenêtre existe déjà, la mettre au premier plan
            if (openWindows.containsKey(key)) {
                Stage existingStage = openWindows.get(key);
                if (existingStage.isShowing()) {
                    existingStage.toFront();
                    return;
                } else {
                    openWindows.remove(key);
                }
            }
            
            // Charger la nouvelle fenêtre
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.NONE); // Permet d'interagir avec d'autres fenêtres
            stage.setScene(new Scene(root));
            
            // Gérer la fermeture de la fenêtre
            stage.setOnHiding(event -> openWindows.remove(key));
            
            // Stocker la référence de la fenêtre
            openWindows.put(key, stage);
            
            // Afficher la fenêtre
            stage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 