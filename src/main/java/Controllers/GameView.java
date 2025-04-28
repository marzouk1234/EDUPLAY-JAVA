package Controllers;

import Models.Game;
import Services.GameService1;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class GameView {

    @FXML private TextField tfId;
    @FXML private TextField tfNom;
    @FXML private TextField tfPrenom;
    @FXML private ComboBox<String> cbType;
    @FXML private DatePicker tfDate;
    @FXML private TextField searchField;
    @FXML private TableColumn<Game, Void> colAction;
    @FXML private ComboBox<String> fieldComboBox;

    @FXML private TableView<Game> gameTable;
    @FXML private TableColumn<Game, Integer> colId;
    @FXML private TableColumn<Game, String> colNom;
    @FXML private TableColumn<Game, String> colPrenom;
    @FXML private TableColumn<Game, String> colType;
    @FXML private TableColumn<Game, java.sql.Date> colDate;

    private GameService1 gameService;
    private FilteredList<Game> filteredList;
    private ObservableList<Game> fullList;

    private int currentPage = 1;
    private int pageSize = 8;

    @FXML
    public void initialize() throws SQLException {
        gameService = new GameService1();
        addButtonToTable();
        fullList = FXCollections.observableArrayList(gameService.afficher());

        filteredList = new FilteredList<>(fullList, p -> true);

        gameTable.setItems(FXCollections.observableArrayList(getCurrentPageItems()));

        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        fieldComboBox.setItems(FXCollections.observableArrayList("Nom", "Prenom", "Type"));
        fieldComboBox.getSelectionModel().selectFirst();
    }

    private ObservableList<Game> getCurrentPageItems() {
        int fromIndex = (currentPage - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, filteredList.size());
        return FXCollections.observableArrayList(filteredList.subList(fromIndex, toIndex));
    }

    @FXML
    void handleNextPage(ActionEvent event) {
        if ((currentPage * pageSize) < filteredList.size()) {
            currentPage++;
            gameTable.setItems(FXCollections.observableArrayList(getCurrentPageItems()));
        }
    }

    @FXML
    void handlePreviousPage(ActionEvent event) {
        if (currentPage > 1) {
            currentPage--;
            gameTable.setItems(FXCollections.observableArrayList(getCurrentPageItems()));
        }
    }

    @FXML
    void handleSearch() {
        String selectedField = fieldComboBox.getValue();
        String searchText = searchField.getText().toLowerCase().trim();

        filteredList.setPredicate(game -> {
            if (searchText.isEmpty()) {
                return true;
            }

            switch (selectedField) {
                case "Nom":
                    return game.getNom() != null && game.getNom().toLowerCase().contains(searchText);
                case "Prenom":
                    return game.getPrenom() != null && game.getPrenom().toLowerCase().contains(searchText);
                case "Type":
                    return game.getType() != null && game.getType().toLowerCase().contains(searchText);
                default:
                    return true;
            }
        });

        currentPage = 1;
        gameTable.setItems(FXCollections.observableArrayList(getCurrentPageItems()));
    }

    @FXML
    void handleDelete() {
        try {
            Game selectedGame = gameTable.getSelectionModel().getSelectedItem();
            if (selectedGame == null) {
                showError("Delete Error", "Please select a game to delete.");
                return;
            }

            gameService.supprimer(selectedGame.getId());

            showInfo("Game deleted successfully!");
            loadTable();
        } catch (Exception e) {
            showError("Delete Error", e.getMessage());
        }
    }

    @FXML
    void handleRefresh() {
        loadTable();
    }

    private void loadTable() {
        try {
            fullList = FXCollections.observableArrayList(gameService.afficher());
            filteredList = new FilteredList<>(fullList, p -> true);
            currentPage = 1;
            gameTable.setItems(FXCollections.observableArrayList(getCurrentPageItems()));
        } catch (SQLException e) {
            showError("Load Error", e.getMessage());
        }
    }

    // Create export method per game
    private void exportGameToPDF(Game game) {
        Document document = new Document();
        try {
            String fileName = "Game_" + game.getId() + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(fileName));

            document.open();
            document.add(new Paragraph("Détails du Jeu"));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(2);

            table.addCell("ID");
            table.addCell(String.valueOf(game.getId()));

            table.addCell("Nom");
            table.addCell(game.getNom());

            table.addCell("Prénom");
            table.addCell(game.getPrenom());

            table.addCell("Type");
            table.addCell(game.getType());

            table.addCell("Date");
            table.addCell(game.getDate().toString());

            document.add(table);
            document.close();

            showInfo("PDF généré pour : " + game.getNom());

        } catch (Exception e) {
            showError("Erreur PDF", e.getMessage());
            e.printStackTrace();
        }
    }

    private void addButtonToTable() {
        Callback<TableColumn<Game, Void>, TableCell<Game, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Game, Void> call(final TableColumn<Game, Void> param) {
                final TableCell<Game, Void> cell = new TableCell<>() {

                    private final Button btn = new Button("Export PDF");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Game game = getTableView().getItems().get(getIndex());
                            exportGameToPDF(game);
                        });
                        btn.getStyleClass().add("action-button"); // Optional style
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };

        colAction.setCellFactory(cellFactory);
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setContentText(msg);
        alert.show();
    }

    private void showError(String header, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(msg);
        alert.show();
    }

    @FXML
    void openAjouterGame(ActionEvent event) {
        try {
            Parent gameView = FXMLLoader.load(getClass().getResource("/AjouterGame.fxml"));
            Scene gameScene = new Scene(gameView);

            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            window.setScene(gameScene);
            window.setTitle("Ajouter Jeu");
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openUpdateGame() {
        Game selectedGame = gameTable.getSelectionModel().getSelectedItem();
        if (selectedGame == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a game to edit.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateGame.fxml"));
            Parent root = loader.load();

            UpdateGame controller = loader.getController();
            controller.setGameToEdit(selectedGame);

            Stage stage = new Stage();
            stage.setTitle("Update Game");
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
