package Controllers;

import Models.Game;
import Services.GameService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.Date;
import java.sql.SQLException;

public class UpdateGame {

    @FXML
    private TextField tfId;
    @FXML
    private TextField tfNom;
    @FXML
    private TextField tfPrenom;
    @FXML
    private ComboBox<String> cbType;
    @FXML
    private DatePicker tfDate;

    private final GameService gameService = new GameService();
    private Game selectedGameToEdit;

    public UpdateGame() throws SQLException {
    }

    // This method is called from GameView to send the game to update
    public void setGameToEdit(Game game) {
        this.selectedGameToEdit = game;

        // Pre-fill form
        tfId.setText(String.valueOf(game.getId()));
        tfNom.setText(game.getNom());
        tfPrenom.setText(game.getPrenom());
        cbType.getItems().addAll("puzzle", "snake", "chess");
        cbType.setValue(game.getType());
        tfDate.setValue(game.getDate().toLocalDate());
    }

    @FXML
    void handleUpdate() {
        try {
            if (selectedGameToEdit == null) {
                showError("Erreur de mise à jour", "Aucun jeu sélectionné pour la mise à jour.");
                return;
            }

            // Update values from form
            selectedGameToEdit.setId(Integer.parseInt(tfId.getText()));
            selectedGameToEdit.setNom(tfNom.getText());
            selectedGameToEdit.setPrenom(tfPrenom.getText());
            selectedGameToEdit.setType(cbType.getValue());
            selectedGameToEdit.setDate(String.valueOf(Date.valueOf(tfDate.getValue())));

            gameService.modifier(selectedGameToEdit);

            showInfo("Jeu mis à jour avec succès !");
        } catch (Exception e) {
            showError("Erreur de mise à jour", e.getMessage());
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
