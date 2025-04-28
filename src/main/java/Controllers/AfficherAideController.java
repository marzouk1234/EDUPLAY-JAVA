package Controllers;

import Models.Aide;
import Models.Form_p;
import Services.AideService;
import Services.Form_pService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.ListChangeListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class AfficherAideController implements Initializable {
    @FXML
    private TableView<Aide> aideTable;
    @FXML
    private TableColumn<Aide, Integer> idColumn;
    @FXML
    private TableColumn<Aide, String> sujetColumn;
    @FXML
    private TableColumn<Aide, String> descriptionColumn;
    @FXML
    private TableColumn<Aide, String> dateCreationColumn;
    @FXML
    private TableColumn<Aide, String> formSujetColumn;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> filterComboBox;
    @FXML
    private Label statusLabel;
    @FXML
    private Label countLabel;
    @FXML
    private Button modifierButton;
    @FXML
    private Button supprimerButton;
    @FXML
    private Button chatbotButton;
    @FXML
    private WebView chatWebView;
    @FXML
    private TitledPane chatbotPane;
    @FXML
    private TextField questionTitleField;
    @FXML
    private TextArea questionContentArea;
    @FXML
    private Button submitQuestionButton;

    private AideService aideService;
    private Form_pService formService;
    private ObservableList<Aide> aideList;
    private FilteredList<Aide> filteredData;
    private SortedList<Aide> sortedData;
    private Map<Integer, Form_p> formCache;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Process flaskProcess;
    private boolean flaskServerRunning = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Initialisation des services et du cache
            aideService = new AideService();
            formService = new Form_pService();
            aideList = FXCollections.observableArrayList();
            formCache = new HashMap<>();

            // Configuration des colonnes
            setupColumns();
            
            // Configuration du ComboBox de filtrage
            setupFilterComboBox();
            
            // Chargement des données
            loadFormData();
            loadAideData();
            
            // Configuration de la recherche et du tri
            setupSearchAndSort();
            
            // Configuration des boutons
            setupButtons();
            
            // Configuration du chatbot
            setupChatbot();
            
            statusLabel.setText("Interface initialisée avec succès");
        } catch (Exception e) {
            statusLabel.setText("Erreur d'initialisation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadFormData() {
        try {
            formCache.clear();
            for (Form_p form : formService.getAll()) {
                formCache.put(form.getId(), form);
            }
        } catch (SQLException e) {
            statusLabel.setText("Erreur lors du chargement des formulaires: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        sujetColumn.setCellValueFactory(new PropertyValueFactory<>("sujet"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        
        dateCreationColumn.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String formattedDate = cellData.getValue().getDateCreation().format(formatter);
            return javafx.beans.binding.Bindings.createStringBinding(() -> formattedDate);
        });
        
        formSujetColumn.setCellValueFactory(cellData -> {
            int formId = cellData.getValue().getFormId();
            Form_p form = formCache.get(formId);
            String sujet = form != null ? form.getSujet() : "Non associé";
            return javafx.beans.binding.Bindings.createStringBinding(() -> sujet);
        });

        // Ajouter des tooltips pour la description
        descriptionColumn.setCellFactory(tc -> {
            TableCell<Aide, String> cell = new TableCell<>() {
                private final Tooltip tooltip = new Tooltip();
                
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setTooltip(null);
                    } else {
                        setText(item);
                        tooltip.setText(item);
                        setTooltip(tooltip);
                    }
                }
            };
            return cell;
        });

        // Activer le tri sur toutes les colonnes
        aideTable.getSortOrder().addListener((ListChangeListener<TableColumn<Aide, ?>>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    // Le tri est géré automatiquement par SortedList
                }
            }
        });
    }

    private void setupFilterComboBox() {
        ObservableList<String> filterOptions = FXCollections.observableArrayList(
            "Tous",
            "Par sujet",
            "Par description",
            "Par formulaire"
        );
        filterComboBox.setItems(filterOptions);
        filterComboBox.setValue("Tous");
    }

    private void setupSearchAndSort() {
        // Initialiser la FilteredList avec la liste observable
        aideList = FXCollections.observableArrayList();
        filteredData = new FilteredList<>(aideList, p -> true);

        // Configurer le filtre
        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilter());
        filterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> applyFilter());

        // Créer et configurer le SortedList
        sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(aideTable.comparatorProperty());
        aideTable.setItems(sortedData);
    }

    private void applyFilter() {
        if (filteredData == null) return;
        
        String searchText = searchField.getText().toLowerCase();
        String filterType = filterComboBox.getValue();

        filteredData.setPredicate(aide -> {
            if (aide == null) return false;
            if (searchText == null || searchText.isEmpty()) return true;

            Form_p associatedForm = formCache.get(aide.getFormId());

            switch (filterType) {
                case "Par sujet":
                    return aide.getSujet().toLowerCase().contains(searchText);
                case "Par description":
                    return aide.getDescription().toLowerCase().contains(searchText);
                case "Par formulaire":
                    return associatedForm != null && associatedForm.getSujet().toLowerCase().contains(searchText);
                default: // "Tous"
                    boolean matchesSujet = aide.getSujet().toLowerCase().contains(searchText);
                    boolean matchesDescription = aide.getDescription().toLowerCase().contains(searchText);
                    boolean matchesForm = associatedForm != null && associatedForm.getSujet().toLowerCase().contains(searchText);
                    return matchesSujet || matchesDescription || matchesForm;
            }
        });
        updateCountLabel();
    }

    private void setupButtons() {
        modifierButton.setDisable(true);
        supprimerButton.setDisable(true);

        aideTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            modifierButton.setDisable(!hasSelection);
            supprimerButton.setDisable(!hasSelection);
        });
    }

    private void loadAideData() {
        try {
            aideList.clear();
            List<Aide> aides = aideService.getAllWithFormDetails();
            aideList.addAll(aides);
            
            // Initialiser FilteredList si nécessaire
            if (filteredData == null) {
                filteredData = new FilteredList<>(aideList, p -> true);
                sortedData = new SortedList<>(filteredData);
                sortedData.comparatorProperty().bind(aideTable.comparatorProperty());
                aideTable.setItems(sortedData);
            }
            
            // Appliquer le filtre actuel
            applyFilter();
            
            updateCountLabel();
            statusLabel.setText("Données chargées avec succès");
        } catch (SQLException e) {
            statusLabel.setText("Erreur de chargement: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateCountLabel() {
        if (filteredData == null || aideList == null) {
            countLabel.setText("0 aide(s) au total");
            return;
        }
        
        int totalCount = aideList.size();
        int filteredCount = filteredData.size();
        
        if (totalCount == filteredCount) {
            countLabel.setText(totalCount + " aide(s) au total");
        } else {
            countLabel.setText(filteredCount + " aide(s) sur " + totalCount + " au total");
        }
    }

    @FXML
    private void handleRefresh() {
        loadFormData();
        loadAideData();
    }

    @FXML
    private void handleAjouter() {
        try {
            // Charger l'interface d'ajout d'aide
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterAide.fxml"));
            Parent root = loader.load();
            
            // Créer une nouvelle fenêtre modale
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Ajouter une aide");
            stage.setScene(new Scene(root));
            
            // Afficher la fenêtre et attendre qu'elle soit fermée
            stage.showAndWait();
            
            // Rafraîchir les données après la fermeture
            loadAideData();
            
        } catch (IOException e) {
            statusLabel.setText("Erreur lors de l'ouverture du formulaire: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleModifier() {
        // Récupérer l'aide sélectionnée
        Aide selectedAide = aideTable.getSelectionModel().getSelectedItem();
        
        if (selectedAide == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", 
                    "Veuillez sélectionner une aide à modifier.");
            return;
        }
        
        try {
            // Charger l'interface de modification
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierAide.fxml"));
            Parent root = loader.load();
            
            // Récupérer le contrôleur et définir l'aide à modifier
            ModifierAideController controller = loader.getController();
            controller.setAide(selectedAide);
            
            // Créer une nouvelle fenêtre modale
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modifier une aide");
            stage.setScene(new Scene(root));
            
            // Afficher la fenêtre et attendre qu'elle soit fermée
            stage.showAndWait();
            
            // Rafraîchir les données après la fermeture
            loadAideData();
            
        } catch (IOException e) {
            statusLabel.setText("Erreur lors de l'ouverture du formulaire: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSupprimer() {
        // Récupérer l'aide sélectionnée
        Aide selectedAide = aideTable.getSelectionModel().getSelectedItem();
        
        if (selectedAide == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", 
                    "Veuillez sélectionner une aide à supprimer.");
            return;
        }
        
        // Confirmer la suppression
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette aide ?");
        
        Optional<ButtonType> result = alert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Supprimer l'aide
                aideService.delete(selectedAide);
                
                // Rafraîchir les données
                loadAideData();
                
                showAlert(Alert.AlertType.INFORMATION, "Succès", 
                        "L'aide a été supprimée avec succès.");
                
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", 
                        "Impossible de supprimer l'aide: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleSubmitQuestion() {
        String questionTitle = questionTitleField.getText().trim();
        String questionContent = questionContentArea.getText().trim();
        
        // Validation des champs
        if (questionTitle.isEmpty() || questionContent.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champs incomplets", 
                    "Veuillez remplir tous les champs pour soumettre votre question.");
            return;
        }
        
        try {
            // Créer le dossier des questions utilisateur s'il n'existe pas
            String projectPath = System.getProperty("user.dir");
            Path userQuestionsDir = Paths.get(projectPath, "user_questions");
            if (!Files.exists(userQuestionsDir)) {
                Files.createDirectories(userQuestionsDir);
            }
            
            // Créer un nom de fichier basé sur la date et l'heure
            LocalDateTime now = LocalDateTime.now();
            String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String safeTitle = questionTitle.replaceAll("[^a-zA-Z0-9\\s]", "_").replaceAll("\\s+", "_");
            String fileName = timestamp + "_" + safeTitle.substring(0, Math.min(safeTitle.length(), 30)) + ".txt";
            
            // Chemin complet du fichier
            Path questionFile = userQuestionsDir.resolve(fileName);
            
            // Écrire le contenu de la question dans le fichier
            try (BufferedWriter writer = Files.newBufferedWriter(questionFile, StandardOpenOption.CREATE)) {
                writer.write("Titre: " + questionTitle);
                writer.newLine();
                writer.write("Date: " + now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                writer.newLine();
                writer.newLine();
                writer.write(questionContent);
            }
            
            // Ajouter la question au chatbot si le serveur est en cours d'exécution
            if (flaskServerRunning) {
                // Option 1: Envoyer la question directement au serveur Flask via JavaScript
                String script = "document.getElementById('user-input').value = '" + 
                        questionTitle.replace("'", "\\'") + "'; " +
                        "document.querySelector('form').dispatchEvent(new Event('submit'));";
                chatWebView.getEngine().executeScript(script);
            }
            
            // Réinitialiser les champs
            questionTitleField.clear();
            questionContentArea.clear();
            
            // Afficher un message de succès
            showAlert(Alert.AlertType.INFORMATION, "Question soumise", 
                    "Votre question a été enregistrée avec succès!");
            
            statusLabel.setText("Question enregistrée: " + questionTitle);
            
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                    "Impossible d'enregistrer la question: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void setupChatbot() {
        // Masquer initialement le panneau du chatbot
        chatbotPane.setExpanded(false);
        
        // Démarrer le serveur Flask en arrière-plan
        startFlaskServer();
        
        // Configurer le bouton chatbot pour ajouter une action
        chatbotButton.setOnAction(event -> handleChatbot());
    }
    
    @FXML
    private void handleChatbot() {
        boolean isExpanded = chatbotPane.isExpanded();
        if (!isExpanded) {
            // Ouvrir le panneau et afficher le webview
            chatbotPane.setExpanded(true);
            
            // Ajuster la taille maximale pour utiliser plus d'espace
            chatbotPane.setMaxHeight(Double.MAX_VALUE);
            VBox.setVgrow(chatbotPane, Priority.ALWAYS);
            
            // Réduire la taille de la table pour donner plus d'espace au chatbot
            VBox.setVgrow(aideTable, Priority.SOMETIMES);
            
            // Charger l'URL du chatbot dans le WebView
            if (flaskServerRunning) {
                chatWebView.getEngine().load("http://localhost:5000/");
                statusLabel.setText("Assistant IA ouvert");
            } else {
                // Essayer de démarrer le serveur
                startFlaskServer();
                chatWebView.getEngine().loadContent("<html><body><p style='font-family: Arial; text-align: center; margin-top: 50px;'>Connexion au serveur du chatbot en cours...</p></body></html>");
                
                // Attendre un peu et essayer de charger
                new Thread(() -> {
                    try {
                        Thread.sleep(3000);
                        if (flaskServerRunning) {
                            javafx.application.Platform.runLater(() -> {
                                chatWebView.getEngine().load("http://localhost:5000/");
                            });
                        } else {
                            javafx.application.Platform.runLater(() -> {
                                chatWebView.getEngine().loadContent("<html><body><p style='font-family: Arial; text-align: center; color: red; margin-top: 50px;'>Impossible de démarrer le serveur IA. Vérifiez que Python et Flask sont installés.</p></body></html>");
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } else {
            // Fermer le panneau et restaurer la taille par défaut
            chatbotPane.setExpanded(false);
            VBox.setVgrow(aideTable, Priority.ALWAYS);
            VBox.setVgrow(chatbotPane, Priority.SOMETIMES);
        }
    }
    
    private void startFlaskServer() {
        if (flaskServerRunning) return;
        
        try {
            String projectPath = System.getProperty("user.dir");
            String flaskPath = projectPath + "/web_chatbot";
            
            // Construire la commande pour démarrer le serveur Flask
            ProcessBuilder processBuilder = new ProcessBuilder();
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                // Windows
                processBuilder.command("cmd.exe", "/c", "cd \"" + flaskPath + "\" && python app.py");
            } else {
                // Linux/Mac
                processBuilder.command("bash", "-c", "cd \"" + flaskPath + "\" && python app.py");
            }
            
            // Rediriger les erreurs vers la sortie standard
            processBuilder.redirectErrorStream(true);
            
            // Démarrer le processus
            flaskProcess = processBuilder.start();
            
            // Marquer le serveur comme démarré
            flaskServerRunning = true;
            
            // Lire la sortie de processus pour voir les erreurs
            executorService.submit(() -> {
                try (java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(flaskProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("Flask: " + line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            
            // Attendre un peu que le serveur démarre
            Thread.sleep(1000);
            
        } catch (Exception e) {
            e.printStackTrace();
            flaskServerRunning = false;
            statusLabel.setText("Erreur lors du démarrage du serveur IA: " + e.getMessage());
        }
    }
    
    private void stopFlaskServer() {
        if (flaskProcess != null && flaskProcess.isAlive()) {
            flaskProcess.destroy();
            flaskServerRunning = false;
        }
    }
    
    // Arrêter le serveur Flask lors de la fermeture
    public void shutdown() {
        stopFlaskServer();
        executorService.shutdownNow();
    }
} 