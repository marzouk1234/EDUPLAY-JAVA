package Controllers;

import Models.Game;
import Services.GameService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Date;
import java.sql.SQLException;

public class AjouterGame {

    @FXML
    private TextField tfId, tfNom, tfPrenom;

    @FXML
    private ComboBox<String> cbType;

    @FXML
    private DatePicker tfDate;

    private final GameService gameService = new GameService();

    public AjouterGame() throws SQLException {
        // Constructeur par défaut requis si GameService lance SQLException
    }

    @FXML
    public void initialize() {
        cbType.getItems().addAll("puzzle", "snake", "chess");
    }

    @FXML
    void handleAdd(ActionEvent event) {
        try {
            if (tfId.getText().isEmpty() ||
                    tfNom.getText().isEmpty() ||
                    tfPrenom.getText().isEmpty() ||
                    cbType.getValue() == null ||
                    tfDate.getValue() == null) {
                showError("Champs obligatoires", "Tous les champs doivent être remplis.");
                return;
            }

            int id = Integer.parseInt(tfId.getText());

            if (id <= 0) {
                showError("ID invalide", "L'ID doit être un nombre positif.");
                return;
            }

            Game game = new Game(
                    id,
                    tfNom.getText().trim(),
                    tfPrenom.getText().trim(),
                    cbType.getValue(),
                    Date.valueOf(tfDate.getValue())
            );

            gameService.ajouter(game);
            showInfo("Ajout réussi !");
            clearForm();
            loadTable();

        } catch (NumberFormatException e) {
            showError("ID invalide", "L'ID doit être un nombre entier.");
        } catch (Exception e) {
            showError("Erreur d'ajout", e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur - " + titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearForm() {
        tfId.clear();
        tfNom.clear();
        tfPrenom.clear();
        cbType.getSelectionModel().clearSelection();
        tfDate.setValue(null);
    }

    private void loadTable() {
        // Placeholder : à remplacer si tu as une TableView à recharger
        System.out.println("Table des jeux rechargée (placeholder).");
    }
}
