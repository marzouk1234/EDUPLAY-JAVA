package Controllers;

import Utils.PDFGenerator;
import Utils.EmailSender;
import Utils.QRCodeGenerator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.util.Callback;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import Models.Form_p;
import Services.Form_pService;

public class AfficherForm_pController implements Initializable {

    @FXML private AnchorPane rootPane;
    @FXML private TableView<Form_p> formTable;
    @FXML private TableColumn<Form_p, Integer> idColumn;
    @FXML private TableColumn<Form_p, String> sujetColumn;
    @FXML private TableColumn<Form_p, String> contenuColumn;
    @FXML private TableColumn<Form_p, String> dateColumn;
    @FXML private TableColumn<Form_p, String> auteurColumn;
    @FXML private TextField searchField;
    @FXML private DatePicker dateFilter;
    @FXML private Label statusLabel;
    @FXML private Label countLabel;
    @FXML private Button ajouterButton;
    @FXML private Button modifierButton;
    @FXML private Button supprimerButton;
    @FXML private Button refreshButton;
    @FXML private Button pdfButton;
    @FXML private Button mailButton;
    @FXML private Button qrButton;

    private Form_pService formService;
    private ObservableList<Form_p> formData = FXCollections.observableArrayList();
    private FilteredList<Form_p> filteredData;
    private ScheduledExecutorService autoRefreshExecutor;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            formService = new Form_pService();
            // Configuration table...
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            sujetColumn.setCellValueFactory(new PropertyValueFactory<>("sujet"));
            contenuColumn.setCellValueFactory(new PropertyValueFactory<>("contenu"));
            dateColumn.setCellValueFactory(cellData -> {
                try {
                    String formattedDate = cellData.getValue().getFormattedDate();
                    return javafx.beans.binding.Bindings.createStringBinding(() -> formattedDate);
                } catch (Exception e) {
                    return javafx.beans.binding.Bindings.createStringBinding(() -> "Date invalide");
                }
            });
            auteurColumn.setCellValueFactory(new PropertyValueFactory<>("auteur"));

            // Tooltip contenu
            contenuColumn.setCellFactory(tc -> {
                TableCell<Form_p, String> cell = new TableCell<>();
                Tooltip tooltip = new Tooltip();
                cell.textProperty().bind(cell.itemProperty());
                cell.setOnMouseEntered(event -> {
                    if (cell.getText() != null && !cell.getText().isEmpty()) {
                        tooltip.setText(cell.getText());
                        Tooltip.install(cell, tooltip);
                    }
                });
                return cell;
            });

            // Initialiser la liste filtrée
            filteredData = new FilteredList<>(formData, p -> true);
            formTable.setItems(filteredData);

            loadFormData();
            setupSearchAndSort();
            formTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                boolean hasSelection = newSelection != null;
                modifierButton.setDisable(!hasSelection);
                supprimerButton.setDisable(!hasSelection);
                pdfButton.setDisable(!hasSelection);
                mailButton.setDisable(!hasSelection);
                qrButton.setDisable(!hasSelection);
            });

            modifierButton.setDisable(true);
            supprimerButton.setDisable(true);
            pdfButton.setDisable(true);
            mailButton.setDisable(true);
            qrButton.setDisable(true);

            startAutoRefresh();
            setupDateFilter();

            statusLabel.setText("Interface initialisée avec succès");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur d'initialisation", "Impossible d'initialiser l'écran: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Bouton pour exporter en PDF
    @FXML
    public void handleExportPdf(ActionEvent event) {
        Form_p selectedForm = formTable.getSelectionModel().getSelectedItem();
        if (selectedForm == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner un formulaire à exporter.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le formulaire en PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        File file = fileChooser.showSaveDialog(rootPane.getScene().getWindow());

        if (file != null) {
            try {
                PDFGenerator.generateFormPDF(selectedForm, file.getAbsolutePath());
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Formulaire exporté en PDF avec succès.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'exportation PDF: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Bouton pour envoyer par mail
    @FXML
    public void handleSendMail(ActionEvent event) {
        Form_p selectedForm = formTable.getSelectionModel().getSelectedItem();
        if (selectedForm == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner un formulaire à envoyer.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Envoyer un mail");
        dialog.setHeaderText("Entrer l'adresse email du destinataire:");
        dialog.setContentText("Email:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(email -> {
            try {
                EmailSender.sendFormEmail(email, selectedForm);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Formulaire envoyé par mail avec succès.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'envoi de l'email: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    // Bouton pour afficher un QR code
    @FXML
    public void handleShowQr(ActionEvent event) {
        Form_p selectedForm = formTable.getSelectionModel().getSelectedItem();
        if (selectedForm == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner un formulaire pour générer un QR code.");
            return;
        }

        try {
            // Générer le QR code avec la classe QRCodeGenerator
            BufferedImage bufferedImage = QRCodeGenerator.generateFormQRCode(selectedForm);

            // Conversion de BufferedImage à Image JavaFX
            WritableImage writableImage = new WritableImage(bufferedImage.getWidth(), bufferedImage.getHeight());
            PixelWriter pixelWriter = writableImage.getPixelWriter();

            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                for (int y = 0; y < bufferedImage.getHeight(); y++) {
                    pixelWriter.setArgb(x, y, bufferedImage.getRGB(x, y));
                }
            }

            // Afficher dans une nouvelle fenêtre
            Stage stage = new Stage();
            stage.setTitle("QR Code du Formulaire");

            ImageView imageView = new ImageView(writableImage);
            imageView.setFitHeight(300);
            imageView.setFitWidth(300);
            AnchorPane pane = new AnchorPane(imageView);
            AnchorPane.setTopAnchor(imageView, 20.0);
            AnchorPane.setLeftAnchor(imageView, 20.0);
            Scene scene = new Scene(pane, 340, 340);

            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la génération du QR code: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Méthode pour charger les données dans le tableau
    private void loadFormData() {
        try {
            // Récupérer les données depuis le service
            List<Form_p> forms = formService.getAll();

            // Mettre à jour la liste observable
            formData.clear();
            formData.addAll(forms);

            // Mettre à jour le compteur
            countLabel.setText(forms.size() + " formulaire(s)");

            statusLabel.setText("Données chargées avec succès");
        } catch (SQLException e) {
            statusLabel.setText("Erreur SQL: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            statusLabel.setText("Erreur lors du chargement des données: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Configuration de la recherche et du tri
    private void setupSearchAndSort() {
        // Recherche par texte
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(form -> {
                // Si le champ de recherche est vide, afficher tous les formulaires
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                // Vérifier si le texte correspond au sujet, contenu ou auteur
                if (form.getSujet().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (form.getContenu().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (form.getAuteur().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });

            updateFilterStatus();
        });
    }

    // Configuration du filtre par date
    private void setupDateFilter() {
        dateFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(form -> {
                // Si aucune date n'est sélectionnée, afficher tous les formulaires
                if (newValue == null) {
                    return true;
                }

                // Vérifier si la date du formulaire correspond à la date sélectionnée
                try {
                    LocalDate formDate = form.getDatePub(); // Utilisez la méthode getDatePub() de votre modèle
                    return formDate.equals(newValue);
                } catch (Exception e) {
                    return false;
                }
            });

            updateFilterStatus();
        });
    }

    // Mise à jour du statut des filtres
    private void updateFilterStatus() {
        int totalCount = formData.size();
        int filteredCount = filteredData.size();

        if (totalCount == filteredCount) {
            countLabel.setText(totalCount + " formulaire(s)");
        } else {
            countLabel.setText(filteredCount + " formulaire(s) sur " + totalCount);
        }
    }

    // Configuration de l'actualisation automatique
    private void startAutoRefresh() {
        if (autoRefreshExecutor != null && !autoRefreshExecutor.isShutdown()) {
            autoRefreshExecutor.shutdown();
        }

        // Actualiser toutes les 5 minutes
        autoRefreshExecutor = Executors.newSingleThreadScheduledExecutor();
        autoRefreshExecutor.scheduleAtFixedRate(() -> {
            javafx.application.Platform.runLater(this::loadFormData);
        }, 5, 5, TimeUnit.MINUTES);
    }

    // Arrêter l'actualisation automatique à la fermeture de la fenêtre
    public void shutdown() {
        if (autoRefreshExecutor != null && !autoRefreshExecutor.isShutdown()) {
            autoRefreshExecutor.shutdown();
        }
    }

    // Afficher une alerte
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Bouton Ajouter
    @FXML
    public void handleAjouter(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterForm_p.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter un formulaire");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(rootPane.getScene().getWindow());

            stage.setScene(new Scene(root));

            // Attendre la fermeture de la fenêtre et recharger les données
            stage.setOnHidden(e -> loadFormData());

            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre d'ajout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Bouton Modifier
    @FXML
    public void handleModifier(ActionEvent event) {
        Form_p selectedForm = formTable.getSelectionModel().getSelectedItem();
        if (selectedForm == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner un formulaire à modifier.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierForm_p.fxml"));
            Parent root = loader.load();

            // Vérifiez si le contrôleur existe et s'il a une méthode initData
            Object controller = loader.getController();
            if (controller != null && controller instanceof Initializable) {
                // Si le contrôleur n'a pas de méthode initData spécifique, vous pouvez
                // passer les données via une autre approche (par exemple, singleton, service, etc.)
                try {
                    // Tentative d'appeler initData si elle existe
                    java.lang.reflect.Method initDataMethod = controller.getClass().getMethod("initData", Form_p.class);
                    initDataMethod.invoke(controller, selectedForm);
                } catch (NoSuchMethodException e) {
                    // La méthode n'existe pas, gérer autrement si nécessaire
                    System.out.println("La méthode initData n'existe pas dans le contrôleur ModifierForm_p");
                }
            }

            Stage stage = new Stage();
            stage.setTitle("Modifier un formulaire");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(rootPane.getScene().getWindow());

            stage.setScene(new Scene(root));

            // Attendre la fermeture de la fenêtre et recharger les données
            stage.setOnHidden(e -> loadFormData());

            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre de modification: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur inattendue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Bouton Supprimer
    @FXML
    public void handleSupprimer(ActionEvent event) {
        Form_p selectedForm = formTable.getSelectionModel().getSelectedItem();
        if (selectedForm == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner un formulaire à supprimer.");
            return;
        }

        // Demander confirmation avant suppression
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le formulaire");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce formulaire?\n" +
                "Sujet: " + selectedForm.getSujet() + "\n" +
                "Auteur: " + selectedForm.getAuteur());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Supprimer le formulaire en utilisant la méthode delete du service
                formService.delete(selectedForm);

                // Recharger les données
                loadFormData();

                statusLabel.setText("Formulaire supprimé avec succès");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Erreur lors de la suppression: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur inattendue: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Bouton Actualiser
    @FXML
    public void handleRefresh(ActionEvent event) {
        loadFormData();
        statusLabel.setText("Données actualisées");
    }
}