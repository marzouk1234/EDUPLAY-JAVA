package Controllers;

import Models.Feedback;
import Models.Game;
import Services.FeedbackService1;
import Services.GameService1;
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

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class FeedbackView {

    @FXML private TextField tfId;
    @FXML private TextField tfIdJeux;
    @FXML private TextField tfNom;
    @FXML private TextField tfPrenom;
    @FXML private TextField tfFeedback;
    @FXML private TextField tfRating;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> fieldComboBox;

    @FXML private TableColumn<Feedback, Void> colViewQR;
    @FXML private TableView<Feedback> feedbackTable;
    @FXML private TableColumn<Feedback, Integer> colId;
    @FXML private TableColumn<Feedback, Integer> colIdJeux;
    @FXML private TableColumn<Feedback, String> colNom;
    @FXML private TableColumn<Feedback, String> colPrenom;
    @FXML private TableColumn<Feedback, String> colFeedback;
    @FXML private TableColumn<Feedback, Integer> colRating;

    private FeedbackService1 feedbackService;
    private FilteredList<Feedback> filteredList;
    private ObservableList<Feedback> fullList;

    private int currentPage = 1;
    private int pageSize = 10; // 10 feedbacks per page

    @FXML
    public void initialize() throws SQLException {
        feedbackService = new FeedbackService1();
        fullList = FXCollections.observableArrayList(feedbackService.afficher());

        filteredList = new FilteredList<>(fullList, p -> true);


        feedbackTable.setItems(FXCollections.observableArrayList(getCurrentPageItems()));

        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colFeedback.setCellValueFactory(new PropertyValueFactory<>("feedback"));
        colRating.setCellValueFactory(new PropertyValueFactory<>("rating"));
        fieldComboBox.setItems(FXCollections.observableArrayList("Nom", "Prenom", "Rating"));
        fieldComboBox.getSelectionModel().selectFirst();
        addViewQRButtonToTable();
    }

    private ObservableList<Feedback> getCurrentPageItems() {
        int fromIndex = (currentPage - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, filteredList.size());
        return FXCollections.observableArrayList(filteredList.subList(fromIndex, toIndex));
    }

    @FXML
    void handleNextPage(ActionEvent event) {
        if ((currentPage * pageSize) < filteredList.size()) {
            currentPage++;
            feedbackTable.setItems(FXCollections.observableArrayList(getCurrentPageItems()));
        }
    }

    @FXML
    void handlePreviousPage(ActionEvent event) {
        if (currentPage > 1) {
            currentPage--;
            feedbackTable.setItems(FXCollections.observableArrayList(getCurrentPageItems()));
        }
    }

    @FXML
    void handleSearch() {
        String selectedField = fieldComboBox.getValue();
        String searchText = searchField.getText().toLowerCase().trim();

        filteredList.setPredicate(feedback -> {
            if (searchText.isEmpty()) {
                return true;
            }

            switch (selectedField) {
                case "Nom":
                    return feedback.getNom() != null && feedback.getNom().toLowerCase().contains(searchText);
                case "Prenom":
                    return feedback.getPrenom() != null && feedback.getPrenom().toLowerCase().contains(searchText);
                case "Rating":
                    String rating = feedback.getRating() != null ? feedback.getRating().toString().toLowerCase() : "";
                    return rating.contains(searchText);
                default:
                    return true;
            }
        });

        currentPage = 1; // Reset to first page after search
        feedbackTable.setItems(FXCollections.observableArrayList(getCurrentPageItems()));
    }

    @FXML
    void handleDelete() {
        try {
            Feedback selectedFeedback = feedbackTable.getSelectionModel().getSelectedItem();
            if (selectedFeedback == null) {
                showError("Delete Error", "Please select a feedback to delete.");
                return;
            }

            feedbackService.supprimer(selectedFeedback.getId());

            showInfo("Feedback deleted successfully!");
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
            fullList = FXCollections.observableArrayList(feedbackService.afficher());
            filteredList = new FilteredList<>(fullList, p -> true);
            currentPage = 1;
            feedbackTable.setItems(FXCollections.observableArrayList(getCurrentPageItems()));
        } catch (SQLException e) {
            showError("Load Error", e.getMessage());
        }
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
    void openAjouterFeed(ActionEvent event) {
        try {
            Parent gameView = FXMLLoader.load(getClass().getResource("/AjouterFeed.fxml"));
            Scene gameScene = new Scene(gameView);

            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            window.setScene(gameScene);
            window.setTitle("Ajouter Feedback");
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openUpdateFeed() {
        Feedback selectedFeedback = feedbackTable.getSelectionModel().getSelectedItem();
        if (selectedFeedback == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a feedback to edit.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateFeed.fxml"));
            Parent root = loader.load();

            UpdateFeed controller = loader.getController();
            controller.setFeedbackToEdit(selectedFeedback);

            Stage stage = new Stage();
            stage.setTitle("Update Feedback");
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

            Stage stage = (Stage) feedbackTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Main View");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void openFeedbackStatistics() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FeedbackStatistics.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) feedbackTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Feedback Statistics");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void addViewQRButtonToTable() {
        colViewQR.setCellFactory(param -> new TableCell<Feedback, Void>() {
            private final Button btn = new Button("View QR");

            {
                btn.setOnAction((ActionEvent event) -> {
                    Feedback feedback = getTableView().getItems().get(getIndex());
                    openQRImage(feedback.getId());
                });
                btn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
    }

    private void openQRImage(int feedbackId) {
        String filePath = "QRCode_" + feedbackId + ".png";  // Assuming QR codes are stored in the project root directory.
        File file = new File(filePath);

        if (file.exists()) {
            try {
                java.awt.Desktop.getDesktop().open(file);
            } catch (IOException e) {
                showError("Error Opening QR Code", "Failed to open QR code image for Feedback ID: " + feedbackId);
                e.printStackTrace();
            }
        } else {
            showError("QR Code Not Found", "QR code image not found for Feedback ID: " + feedbackId + ". Please generate the QR code first.");
        }
    }

}
