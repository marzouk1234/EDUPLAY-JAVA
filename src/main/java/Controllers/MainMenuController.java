package Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
<<<<<<< HEAD
import javafx.scene.control.Alert;
import java.io.IOException;
=======
>>>>>>> 84436b55f0f56204e6f48c9d38756d282329aa78
import java.util.HashMap;
import java.util.Map;

public class MainMenuController {
<<<<<<< HEAD

    private Map<String, Stage> openWindows = new HashMap<>();

=======
    
    private Map<String, Stage> openWindows = new HashMap<>();
    
>>>>>>> 84436b55f0f56204e6f48c9d38756d282329aa78
    @FXML
    private void handleOpenFormulaires() {
        openWindow("formulaires", "/AfficherForm_p.fxml", "Liste des Formulaires");
    }

    @FXML
    private void handleOpenAides() {
        openWindow("aides", "/AfficherAide.fxml", "Liste des Aides");
    }
<<<<<<< HEAD

=======
    
>>>>>>> 84436b55f0f56204e6f48c9d38756d282329aa78
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
<<<<<<< HEAD

            System.out.println("Tentative de chargement: " + fxmlPath);

            // Vérifier si la ressource existe
            if (getClass().getResource(fxmlPath) == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Fichier FXML introuvable: " + fxmlPath);
                return;
            }

            // Charger la nouvelle fenêtre
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

=======
            
            // Charger la nouvelle fenêtre
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
>>>>>>> 84436b55f0f56204e6f48c9d38756d282329aa78
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.NONE); // Permet d'interagir avec d'autres fenêtres
            stage.setScene(new Scene(root));
<<<<<<< HEAD

            // Gérer la fermeture de la fenêtre
            stage.setOnHiding(event -> openWindows.remove(key));

            // Stocker la référence de la fenêtre
            openWindows.put(key, stage);

            // Afficher la fenêtre
            stage.show();

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de " + fxmlPath);
=======
            
            // Gérer la fermeture de la fenêtre
            stage.setOnHiding(event -> openWindows.remove(key));
            
            // Stocker la référence de la fenêtre
            openWindows.put(key, stage);
            
            // Afficher la fenêtre
            stage.show();
            
        } catch (Exception e) {
>>>>>>> 84436b55f0f56204e6f48c9d38756d282329aa78
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement",
                    "Impossible de charger la fenêtre: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur inattendue: " + e.getClass().getName());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur inattendue", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}