package Controllers;

import Utils.PDFGenerator;
import Utils.EmailSender;
import Utils.QRCodeGenerator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
<<<<<<< HEAD
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
=======
import javafx.collections.transformation.SortedList;
>>>>>>> 84436b55f0f56204e6f48c9d38756d282329aa78
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
<<<<<<< HEAD
=======
import java.time.format.DateTimeFormatter;
import java.util.Collections;
>>>>>>> 84436b55f0f56204e6f48c9d38756d282329aa78
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

<<<<<<< HEAD
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
=======
    @FXML
    private TableView<Form_p> formTable;

    @FXML
    private TableColumn<Form_p, Integer> idColumn;

    @FXML
    private TableColumn<Form_p, String> sujetColumn;

    @FXML
    private TableColumn<Form_p, String> contenuColumn;

    @FXML
    private TableColumn<Form_p, String> dateColumn;

    @FXML
    private TableColumn<Form_p, String> auteurColumn;

    @FXML
    private Button ajouterButton;

    @FXML
    private Button modifierButton;

    @FXML
    private Button supprimerButton;
    
    @FXML
    private Button refreshButton;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private Label countLabel;

    @FXML
    private AnchorPane rootPane;
>>>>>>> 84436b55f0f56204e6f48c9d38756d282329aa78

    @FXML
    private DatePicker dateFilter;

    private Form_pService formService;
<<<<<<< HEAD
    private ObservableList<Form_p> formData = FXCollections.observableArrayList();
    private FilteredList<Form_p> filteredData;
    private ScheduledExecutorService autoRefreshExecutor;
=======
    private ObservableList<Form_p> formList;
    private FilteredList<Form_p> filteredData;
    private SortedList<Form_p> sortedData;
    private ScheduledExecutorService scheduler;
>>>>>>> 84436b55f0f56204e6f48c9d38756d282329aa78

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

<<<<<<< HEAD
            // Tooltip contenu
=======
            // Configurer le tooltip pour le contenu
>>>>>>> 84436b55f0f56204e6f48c9d38756d282329aa78
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

<<<<<<< HEAD
            // Initialiser la liste filtrée
            filteredData = new FilteredList<>(formData, p -> true);
            formTable.setItems(filteredData);
=======
            // Load data
            loadFormData();
            
            // Configurer la recherche
            setupSearchAndSort();
>>>>>>> 84436b55f0f56204e6f48c9d38756d282329aa78

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
<<<<<<< HEAD
            pdfButton.setDisable(true);
            mailButton.setDisable(true);
            qrButton.setDisable(true);

            startAutoRefresh();
            setupDateFilter();

            statusLabel.setText("Interface initialisée avec succès");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur d'initialisation", "Impossible d'initialiser l'écran: " + e.getMessage());
=======
            
            // Configurer le rafraîchissement automatique toutes les 5 secondes
            startAutoRefresh();
            
            // Configuration du filtre par date
            setupDateFilter();
            
            statusLabel.setText("Interface initialisée avec succès");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur d'initialisation", 
                    "Impossible d'initialiser l'écran: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void setupSearchAndSort() {
        // Configurer le filtre
        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilter());
        dateFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilter());
    }
    
    private void applyFilter() {
        if (filteredData == null) return;
        
        String searchText = searchField.getText().toLowerCase();
        LocalDate selectedDate = dateFilter.getValue();

        filteredData.setPredicate(form -> {
            if (form == null) return false;

            boolean matchesSearch = searchText == null || searchText.isEmpty() ||
                    form.getSujet().toLowerCase().contains(searchText) ||
                    form.getContenu().toLowerCase().contains(searchText) ||
                    form.getAuteur().toLowerCase().contains(searchText);

            boolean matchesDate = selectedDate == null ||
                    form.getDatePub().equals(selectedDate);

            return matchesSearch && matchesDate;
        });
        
        updateCountLabel();
    }
    
    private void updateCountLabel() {
        if (filteredData == null) {
            filteredData = new FilteredList<>(formList, p -> true);
        }
        int totalCount = formList.size();
        int filteredCount = filteredData.size();
        
        if (totalCount == filteredCount) {
            countLabel.setText(totalCount + " formulaire(s) au total");
        } else {
            countLabel.setText(filteredCount + " formulaire(s) sur " + totalCount + " au total");
        }
    }
    
    private void startAutoRefresh() {
        // Arrêter le scheduler existant s'il existe
        stopAutoRefresh();
        
        // Créer un nouveau scheduler
        scheduler = Executors.newSingleThreadScheduledExecutor();
        
        // Programmer le rafraîchissement périodique
        scheduler.scheduleAtFixedRate(() -> {
            // Exécution sur le thread JavaFX
            Platform.runLater(this::loadFormData);
        }, 5, 5, TimeUnit.SECONDS);
    }
    
    private void stopAutoRefresh() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                // Attendre la fin des tâches en cours
                if (!scheduler.awaitTermination(2, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
        }
    }

    private void loadFormData() {
        try {
            // Sauvegarder l'élément sélectionné pour le restaurer après
            Form_p selectedForm = formTable.getSelectionModel().getSelectedItem();
            Integer selectedId = selectedForm != null ? selectedForm.getId() : null;
            
            // Get all forms from the database
            List<Form_p> forms = formService.getAll();
            
            // Convert to observable list
            formList = FXCollections.observableArrayList(forms);
            
            // Configurer le FilteredList si c'est la première fois
            if (filteredData == null) {
                filteredData = new FilteredList<>(formList, p -> true);
                // Créer et configurer le SortedList
                sortedData = new SortedList<>(filteredData);
                sortedData.comparatorProperty().bind(formTable.comparatorProperty());
                formTable.setItems(sortedData);
            }
            
            // Appliquer le filtre actuel
            applyFilter();
            
            // Restaurer la sélection si possible
            if (selectedId != null) {
                formList.stream()
                    .filter(form -> form.getId() == selectedId)
                    .findFirst()
                    .ifPresent(form -> {
                        formTable.getSelectionModel().select(form);
                        formTable.scrollTo(form);
                    });
            }
            
            // Mettre à jour le compteur
            updateCountLabel();
            
            // Informer si aucun formulaire n'est trouvé
            if (forms.isEmpty()) {
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.INFORMATION, "Aucune donnée", 
                            "Aucun formulaire trouvé dans la base de données.");
                });
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement", 
                    "Impossible de charger les données: " + e.getMessage());
            // Initialiser une liste vide en cas d'erreur
            formList = FXCollections.observableArrayList(Collections.emptyList());
            formTable.setItems(formList);
>>>>>>> 84436b55f0f56204e6f48c9d38756d282329aa78
            e.printStackTrace();
        }
    }

    // Bouton pour exporter en PDF
    @FXML
<<<<<<< HEAD
    public void handleExportPdf(ActionEvent event) {
=======
    public void handleAjouter(ActionEvent event) {
        try {
            // Load the add form UI
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterForm_p.fxml"));
            Parent root = loader.load();
            
            // Create a new stage for the add form
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Ajouter un formulaire");
            stage.setScene(new Scene(root));
            
            // Show the stage and wait for it to close
            stage.showAndWait();
            
            // Refresh the data after the form is closed
            loadFormData();
            
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                    "Impossible d'ouvrir le formulaire d'ajout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleModifier(ActionEvent event) {
        // Get the selected form
>>>>>>> 84436b55f0f56204e6f48c9d38756d282329aa78
        Form_p selectedForm = formTable.getSelectionModel().getSelectedItem();
        if (selectedForm == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner un formulaire à exporter.");
            return;
        }
<<<<<<< HEAD

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
=======
        
        try {
            // Load the modify form UI
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierForm_p.fxml"));
            Parent root = loader.load();
            
            // Get the controller and set the form to modify
            ModifierForm_pController controller = loader.getController();
            controller.setFormData(selectedForm);
            
            // Create a new stage for the modify form
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modifier un formulaire");
            stage.setScene(new Scene(root));
            
            // Show the stage and wait for it to close
            stage.showAndWait();
            
            // Refresh the data after the form is closed
            loadFormData();
            
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                    "Impossible d'ouvrir le formulaire de modification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSupprimer(ActionEvent event) {
        // Get the selected form
        Form_p selectedForm = formTable.getSelectionModel().getSelectedItem();
        
        if (selectedForm == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", 
                    "Veuillez sélectionner un formulaire à supprimer.");
            return;
        }
        
        // Confirm deletion
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce formulaire ?");
        
        Optional<ButtonType> result = alert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Delete the form
                formService.delete(selectedForm);
                
                // Refresh the data
                loadFormData();
                
                showAlert(Alert.AlertType.INFORMATION, "Succès", 
                        "Le formulaire a été supprimé avec succès.");
                
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", 
                        "Impossible de supprimer le formulaire: " + e.getMessage());
>>>>>>> 84436b55f0f56204e6f48c9d38756d282329aa78
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    public void handleRefresh(ActionEvent event) {
        loadFormData();
    }

<<<<<<< HEAD
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
=======
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void cleanup() {
        stopAutoRefresh();
    }

    private void setupDateFilter() {
        dateFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilter();
        });
>>>>>>> 84436b55f0f56204e6f48c9d38756d282329aa78
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