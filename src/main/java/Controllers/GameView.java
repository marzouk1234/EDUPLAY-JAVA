package Controllers;

import Models.Feedback;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import Models.Game;
import Services.GameService1;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;

public class GameView {

    @FXML private TableView<Game> gameTable;
    @FXML private TableColumn<Game, Integer> colId;
    @FXML private TableColumn<Game, String> colNom;
    @FXML private TableColumn<Game, String> colPrenom;
    @FXML private TableColumn<Game, String> colType;
    @FXML private TableColumn<Game, Date> colDate;

    @FXML private TextField tfId;
    @FXML private TextField tfNom;
    @FXML private TextField tfPrenom;
    @FXML private ComboBox<String> cbType;
    @FXML private DatePicker tfDate;

    @FXML private Button btnAdd;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;
    @FXML private Button btnRefresh;

    private GameService1 gameService;

    public GameView() {
        // No-arg constructor required by FXMLLoader
    }

    @FXML
    public void initialize() {
        try {
            gameService = new GameService1();
            loadTable();

            //cbType.setItems(FXCollections.observableArrayList("puzzle", "snake", "chess"));
        } catch (SQLException e) {
            showError("Erreur de connexion", e.getMessage());
        }
    }

    private void loadTable() {
        try {
            ObservableList<Game> list = FXCollections.observableArrayList(gameService.afficher());
            gameTable.setItems(list);
            colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
            colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
            colType.setCellValueFactory(new PropertyValueFactory<>("type"));
            colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        } catch (SQLException e) {
            showError("Erreur lors du chargement", e.getMessage());
        }
    }

    private void clearForm() {
        tfId.clear();
        tfNom.clear();
        tfPrenom.clear();
        cbType.setValue(null);
        tfDate.setValue(null);
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

    @FXML
    void handleUpdate(ActionEvent event) {
        try {
            Game game = new Game(
                    Integer.parseInt(tfId.getText()),
                    tfNom.getText(),
                    tfPrenom.getText(),
                    cbType.getValue(),
                    Date.valueOf(tfDate.getValue())
            );
            gameService.modifier(game);
            showInfo("Modification réussie !");
            loadTable();
        } catch (Exception e) {
            showError("Erreur de modification", e.getMessage());
        }
    }

    @FXML
    void handleDelete(ActionEvent event) {
        try {
            int id = Integer.parseInt(tfId.getText());
            gameService.supprimer(id);
            showInfo("Suppression réussie !");
            loadTable();
        } catch (Exception e) {
            showError("Erreur de suppression", e.getMessage());
        }
    }

    @FXML
    void handleRefresh(ActionEvent event) {
        loadTable();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(msg);
        alert.show();
    }

    private void showError(String header, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(header);
        alert.setContentText(msg);
        alert.show();
    }
    @FXML
    void openAjouterGame() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterGame.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter Game");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showError("Error", "Failed to load Add game window: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    void openUpdateGame() {
        Game selectedGame = gameTable.getSelectionModel().getSelectedItem();
        if (selectedGame == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune sélection");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un jeu à modifier.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateGame.fxml"));
            Parent root = loader.load();

            // Passer le jeu sélectionné au contrôleur UpdateGame
            UpdateGame controller = loader.getController();
            controller.setGameToEdit(selectedGame);

            Stage stage = new Stage();
            stage.setTitle("Modifier un jeu");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void goToMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) gameTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Main View");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
