package Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import Models.Game;
import Services.GameService1;

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
    @FXML private TextField tfType;
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
        } catch (SQLException e) {
            showError("Erreur de connexion", e.getMessage());
        }
    }

    private void loadTable() {
        try {
            ObservableList<Game> list = FXCollections.observableArrayList(gameService.afficher());
            gameTable.setItems(list);
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
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
        tfType.clear();
        tfDate.setValue(null);
    }


    @FXML
    void handleAdd(ActionEvent event) {
        try {
            // Check if fields are empty
            if (tfId.getText().isEmpty() ||
                    tfNom.getText().isEmpty() ||
                    tfPrenom.getText().isEmpty() ||
                    tfType.getText().isEmpty() ||
                    tfDate.getValue() == null) {
                showError("Champs obligatoires", "Tous les champs doivent être remplis.");
                return;
            }

            int id = Integer.parseInt(tfId.getText());

            // Check if id is positive
            if (id <= 0) {
                showError("ID invalide", "L'ID doit être un nombre positif.");
                return;
            }

            Game game = new Game(
                    id,
                    tfNom.getText().trim(),
                    tfPrenom.getText().trim(),
                    tfType.getText().trim(),
                    Date.valueOf(tfDate.getValue())
            );

            gameService.ajouter(game);
            showInfo("Ajout réussi !");
            loadTable();

            // Optionally clear form
            clearForm();

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
                    tfType.getText(),
                    Date.valueOf(tfDate.getValue().toString())
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
}
