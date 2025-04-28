package com.example.demo4.Controller;
import java.net.URI;  // Ajoutez cette ligne en haut du fichier
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.twilio.exception.ApiException;


import com.example.demo4.entities.User;
import com.example.demo4.entities.evaluation;
import com.example.demo4.entities.resultat;
import com.example.demo4.entities.Etudiant;
import com.example.demo4.services.evaluationService;
import com.example.demo4.services.resultatService;
import com.example.demo4.services.etudiantService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import java.time.LocalDate;
import java.util.List ;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.ResourceBundle;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import java.net.URLEncoder;

// etc.

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.util.Collections;  // Ajoute cette ligne pour résoudre l'erreur
import javafx.animation.PauseTransition;
import javafx.scene.control.Alert;
import java.lang.ProcessBuilder;
import java.lang.InterruptedException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ArrayList;  // Ajoute cette ligne pour résoudre l'erreur
import java.util.List;       // Ajoute également l'importation de List si elle n'est pas déjà incluse
import javafx.animation.KeyFrame;       // Importation de KeyFrame
import javafx.animation.Timeline;       // Importation de Timeline
import javafx.util.Duration;            // Importation de Duration
import javafx.animation.KeyValue;    // Importation de KeyValue
import java.util.Map;
import java.util.HashMap;
import javafx.application.Platform;
import java.util.Comparator;
import java.nio.charset.StandardCharsets;
import java.awt.Desktop;  // Ajoutez cette importati
import com.twilio.exception.ApiException;
public class evaluationController implements Initializable {

    private final evaluationService ab = new evaluationService();
    private final etudiantService es = new etudiantService();
    private final resultatService Ps = new resultatService();

    // Twilio configuration
    private static final String ACCOUNT_SID = "AC5eb4f34d7a4d53908ee0e5113bce71ff";
    private static final String AUTH_TOKEN = "17bd80e1abb9276564f5fe5c53579216";
    private static final String TWILIO_NUMBER = "+15073534531"; // Twilio number
    private static final String WHATSAPP_SANDBOX_NUMBER = "+14155238886"; // Sandbox officiel Twilio


    @FXML
    private TextField appreciationevField;
    // Champ caché dans votre FXML
    @FXML
    private TextField noteField;           // Champ note visible dans votre FXML
    @FXML
    private Label titreevLabel;

    @FXML
    private Label dateLabel;
    @FXML
    private Label typeLabel;

    @FXML
    private TextField idevF;
    @FXML
    private TextField iduserF;
    @FXML
    private ImageView imageview;
    @FXML
    private TextField idPartField;

    @FXML
    private Label messageLabel; // Le label pour afficher les messages animés
    @FXML
    private Button genererCertificatButton;


    private int idev;
    private final User u = new User();
    private evaluation eve = new evaluation();
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private ComboBox<Etudiant> comboEtudiant; // Le ComboBox pour les étudiants


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        idevF.setVisible(false);
        iduserF.setVisible(false);

        // Charger la liste des étudiants dans le ComboBox
        try {
            List<Etudiant> etudiants = es.recupereretudiant(); // Remplacer par le service pour récupérer les étudiants
            comboEtudiant.getItems().addAll(etudiants);

            // Définir l'affichage du ComboBox pour montrer le nom de l'étudiant
            comboEtudiant.setCellFactory(param -> new ListCell<Etudiant>() {
                @Override
                protected void updateItem(Etudiant item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getnom_et_prenom()); // Affiche le nom de l'étudiant
                    }
                }
            });

            comboEtudiant.setButtonCell(new ListCell<Etudiant>() {
                @Override
                protected void updateItem(Etudiant item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getnom_et_prenom()); // Affiche le nom de l'étudiant
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer l'erreur
        }
    }


    public void setevaluation(evaluation e) {
        this.eve = e;
        titreevLabel.setText(e.getTitre());


        typeLabel.setText(e.getType());
        dateLabel.setText(e.getDate().toLocalDate().toString());

        idevF.setText(String.valueOf(e.getId()));
        iduserF.setText(String.valueOf(1)); // Exemple, à remplacer avec l'id utilisateur réel

        String path = e.getImage();
        File file = new File(path);
        if (file.exists()) {
            Image img = new Image(file.toURI().toString());
            imageview.setImage(img);
        }
    }

    public void setIdev(int idev) {
        this.idev = idev;
    }

    @FXML
    private void addResultat(MouseEvent ev) {
        try {
            // 1) Validation de la date
            if (startDatePicker.getValue() == null) {
                showAlert("Champs manquants", "Dates manquantes",
                        "Veuillez sélectionner la date du résultat.",
                        Alert.AlertType.WARNING);
                return;
            }

            // 2) Vérification que la date est dans le futur (nouvelle version)
            if (startDatePicker.getValue().isBefore(LocalDate.now())) {
                showAlert("Date invalide", "Date dans le passé",
                        "La date du résultat doit être dans le futur.",
                        Alert.AlertType.ERROR);
                return;

            }

            // 3) Validation de l'étudiant sélectionné
            Etudiant etudiantSelected = comboEtudiant.getSelectionModel().getSelectedItem();
            if (etudiantSelected == null) {
                showAlert("Étudiant manquant", "Aucun étudiant sélectionné",
                        "Veuillez sélectionner un étudiant avant de continuer.",
                        Alert.AlertType.WARNING);
                return;
            }

            // 4) Validation de l'ID d'évaluation
            int evaluationId;
            try {
                evaluationId = Integer.parseInt(idevF.getText());
                if (evaluationId <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                showAlert("Erreur", "ID d'évaluation invalide",
                        "L'ID de l'évaluation doit être un nombre positif.",
                        Alert.AlertType.ERROR);
                return;
            }

            // 5) Validation de la note
            double note;
            try {
                note = Double.parseDouble(noteField.getText());
                if (note < 0 || note > 20) {
                    showAlert("Note invalide", "Valeur de note incorrecte",
                            "La note doit être comprise entre 0 et 20 !",
                            Alert.AlertType.WARNING);
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Erreur de saisie", "Valeur de note incorrecte",
                        "Veuillez saisir un nombre valide pour la note.",
                        Alert.AlertType.ERROR);
                return;
            }

            // Détermination automatique de l'appréciation
            String appreciation = determinerAppreciation(note);

            // Construction de l'objet resultat
            LocalDateTime start = startDatePicker.getValue().atStartOfDay();
            resultat p = new resultat(evaluationId, etudiantSelected.getId(), note, appreciation, start);

            // Appel au service avec gestion améliorée des erreurs
            try {
                if (Ps.ajouterresultat(p)) {
                    System.out.printf("[SUCCÈS] Résultat ajouté - Évaluation: %d, Étudiant: %d%n",
                            evaluationId, etudiantSelected.getId());

                    // Mise à jour de l'interface
                    Platform.runLater(() -> {
                        suivreProgressionEtudiant(null);
                        noteField.clear();
                        startDatePicker.setValue(null);
                    });

                    showAlert("Succès", "Opération réussie",
                            "Résultat ajouté et progression mise à jour !",
                            Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Erreur", "Échec de l'ajout",
                            "L'évaluation spécifiée n'existe pas ou n'est pas accessible.",
                            Alert.AlertType.ERROR);
                }
            } catch (SQLException e) {
                handleDatabaseError(e);
            }

        } catch (Exception ex) {
            handleUnexpectedError(ex);
        }
    }

// Méthodes utilitaires améliorées

    private String determinerAppreciation(double note) {
        if (note < 10) return "Faible";
        if (note < 13) return "Passable";
        if (note < 15) return "Bien";
        if (note < 18) return "Très bien";
        return "Excellent";
    }

    @FXML
    private void suivreProgressionEtudiant(ActionEvent event) {
        try {
            Etudiant selectedEtudiant = comboEtudiant.getSelectionModel().getSelectedItem();
            if (selectedEtudiant == null) {
                updateMessageLabel("Veuillez sélectionner un étudiant.", false);
                return;
            }

            List<resultat> resultats = Ps.recupererResultatsParEtudiantId(selectedEtudiant.getId());
            if (resultats.isEmpty()) {
                updateMessageLabel("Cet étudiant n'a aucun résultat.", false);
                return;
            }

            // Calcul de la moyenne
            double moyenne = calculerMoyenne(resultats);

            // Calcul du classement
            Map<Integer, Double> classement = calculerClassementEtudiants();
            int rang = determinerRang(classement, selectedEtudiant.getId());

            // Affichage du résultat
            String message = String.format(Locale.FRANCE,
                    "Étudiant %s - Moyenne: %.2f/20 - %s",
                    selectedEtudiant.getnom_et_prenom(),
                    moyenne,
                    getMessageRang(rang));

            updateMessageLabel(message, true);

        } catch (SQLException e) {
            updateMessageLabel("Erreur lors de l'accès aux données.", false);
            System.err.println("[ERREUR SQL] " + e.getMessage());
        }
    }

// Méthodes de calcul améliorées

    private double calculerMoyenne(List<resultat> resultats) {
        return resultats.stream()
                .mapToDouble(resultat::getNote)
                .average()
                .orElse(0.0);
    }

    private Map<Integer, Double> calculerClassementEtudiants() throws SQLException {
        Map<Integer, Double> classement = new HashMap<>();
        List<Etudiant> etudiants = Ps.recupererTousLesEtudiants();

        for (Etudiant etudiant : etudiants) {
            List<resultat> resultats = Ps.recupererResultatsParEtudiantId(etudiant.getId());
            if (!resultats.isEmpty()) {
                double moyenne = calculerMoyenne(resultats);
                classement.put(etudiant.getId(), moyenne);
            }
        }

        return classement;
    }

    private int determinerRang(Map<Integer, Double> classement, int etudiantId) {
        List<Map.Entry<Integer, Double>> sorted = new ArrayList<>(classement.entrySet());
        sorted.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        for (int i = 0; i < sorted.size(); i++) {
            if (sorted.get(i).getKey() == etudiantId) {
                return i + 1;
            }
        }
        return -1;
    }

    private String getMessageRang(int rang) {
        switch (rang) {
            case 1: return "🌟 Rang 1: Excellence !";
            case 2: return "🥈 Rang 2: Très bon travail !";
            case 3: return "🥉 Rang 3: Bon résultat !";
            case 4:
            case 5: return "🔹 Rang " + rang + ": Peut mieux faire.";
            default: return "📊 Rang " + rang + ": Nécessite plus d'efforts.";
        }
    }

// Gestion améliorée des messages

    private void updateMessageLabel(String message, boolean withAnimation) {
        messageLabel.setText(message);
        messageLabel.setVisible(true);
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(500);

        if (withAnimation) {
            animateMessageLabel();
        } else {
            messageLabel.setOpacity(1);
        }
    }

    private void animateMessageLabel() {
        // Reset animation
        messageLabel.setOpacity(0);

        // Fade in
        Timeline fadeIn = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(messageLabel.opacityProperty(), 0)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(messageLabel.opacityProperty(), 1))
        );

        // Disparition après 15 secondes
        PauseTransition delay = new PauseTransition(Duration.seconds(15));
        Timeline fadeOut = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(messageLabel.opacityProperty(), 1)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(messageLabel.opacityProperty(), 0))
        );

        fadeIn.play();
        delay.setOnFinished(e -> fadeOut.play());
        delay.play();
    }

// Gestion des erreurs améliorée

    private void handleDatabaseError(SQLException e) {
        String errorMessage;
        if (e.getMessage().contains("foreign key constraint")) {
            errorMessage = "Erreur de référence : l'évaluation ou l'étudiant n'existe pas.";
        } else {
            errorMessage = "Erreur de base de données : " + e.getMessage();
        }

        showAlert("Erreur technique", "Problème base de données", errorMessage, Alert.AlertType.ERROR);
        System.err.println("[ERREUR DB] " + e.getMessage());
    }


    private void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    private void voirResultats(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resultatsParEvaluation.fxml"));
            Parent root = loader.load();

            // Passer l’ID de l’Evaluationà l’autre contrôleur
            ResultatsParEvaluationController controller = loader.getController();
            controller.setEvaluationId(this.eve.getId());

            // Afficher la nouvelle page
            idevF.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("Erreur lors de l'ouverture de la vue des resultats : " + e.getMessage());
        }
    }

    @FXML
    private void voirResultatsFront(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resultatsParEvaluationFront.fxml"));
            Parent root = loader.load();

            // Passer l’ID de l’Evaluationà l’autre contrôleur
            ResultatsParEvaluationController controller = loader.getController();
            controller.setEvaluationId(this.eve.getId());

            // Afficher la nouvelle page
            idevF.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("Erreur lors de l'ouverture de la vue des resultats : " + e.getMessage());
        }
    }

    @FXML
    private void onGenererCertificat(ActionEvent event) {
        System.out.println("[DEBUG] Début du processus de génération de certificat");

        try {
            // 1. Validation de la sélection
            Etudiant selectedEtudiant = comboEtudiant.getSelectionModel().getSelectedItem();
            if (selectedEtudiant == null) {
                showAlert("Sélection requise",
                        "Veuillez sélectionner un étudiant",
                        Alert.AlertType.WARNING);
                return;
            }

            // 2. Vérification des données
            if (!verifyStudentResults(selectedEtudiant.getId())) {
                return;
            }

            // 3. Génération du certificat
            generateCertificate(selectedEtudiant.getId());

        } catch (Exception e) {
            handleUnexpectedError(e);
        }
    }

// Méthodes auxiliaires

    private boolean verifyStudentResults(int etudiantId) throws SQLException {
        try {
            if (!Ps.etudiantHasResultats(etudiantId)) {
                showAlert("Données manquantes",
                        "Aucun résultat trouvé pour cet étudiant",
                        Alert.AlertType.WARNING);
                return false;
            }
            return true;
        } catch (SQLException e) {
            System.err.println("[ERREUR SQL] " + e.getMessage());
            showAlert("Erreur technique",
                    "Impossible d'accéder aux données étudiant",
                    Alert.AlertType.ERROR);
            throw e;
        }
    }

    private void generateCertificate(int etudiantId) {
        try {
            resultat res = Ps.recupererResultatParEtudiantId(etudiantId);
            if (res == null) {
                showAlert("Erreur", "Aucun résultat trouvé pour l'étudiant", Alert.AlertType.ERROR);
                return;
            }

            // 1. Chemin absolu spécifique vers le script Python
            String pythonPath = "python"; // ou "python3" sur Linux/Mac
            String scriptPath = "C:\\Users\\KB\\Downloads\\code.py";

            // Vérification que le fichier existe
            File scriptFile = new File(scriptPath);
            if (!scriptFile.exists()) {
                throw new CertificateGenerationException(
                        "Fichier Python introuvable à: " + scriptPath +
                                "\nVeuillez vérifier que le fichier existe à cet emplacement."
                );
            }

            // 2. Préparation des arguments avec ProcessBuilder
            ProcessBuilder pb = new ProcessBuilder(
                    pythonPath,
                    scriptPath,
                    String.valueOf(etudiantId),
                    String.format(Locale.US, "%.2f", res.getNote()), // Format US pour les décimaux
                    res.getAppreciation() // Pas besoin de guillemets
            );

            // 3. Exécution et capture de la sortie
            pb.redirectErrorStream(true);
            Process process = pb.start();

            String output = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new CertificateGenerationException(
                        "Erreur Python (code " + exitCode + "):\n" + output
                );
            }

            showAlert("Succès", "Certificat généré avec succès", Alert.AlertType.INFORMATION);

            // ==== AJOUT POUR UPLOAD SUR IMGBB ====
            String apiKey = "b8a6cc627764eceb8833cc3a5b33b3db"; // <-- Mets ta clé API ici
            String certificatPath = "C:\\Users\\KB\\Documents\\maram_crud_finale\\certificats\\certificat_1_266.png";

            String uploadedUrl = uploadImage(certificatPath, apiKey);
            System.out.println("Lien du certificat : " + uploadedUrl);
            // Tu peux ici envoyer uploadedUrl par WhatsApp après si tu veux.
            // =====================================

        } catch (Exception e) {
            handleError("Erreur de génération", e.getMessage(), e);
        }
    }



    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Supprimez une des deux déclarations de handleUnexpectedError et gardez celle-ci :
    private void handleUnexpectedError(Exception e) {
        showAlert("Erreur système",
                "Une erreur inattendue est survenue: " + e.getMessage(),
                Alert.AlertType.ERROR);
        e.printStackTrace();
    }

    // Supprimez également la méthode dupliquée handleError() et utilisez handleUnexpectedError() à la place
// Partout où vous aviez handleError(), remplacez par handleUnexpectedError()
    // Classe interne pour gérer les erreurs de génération de certificat
    private static class CertificateGenerationException extends RuntimeException {
        public CertificateGenerationException(String message) {
            super(message);
        }
    }

    private void handleError(String title, String message, Exception e) {
        System.err.println("ERREUR: " + title + " - " + message);
        e.printStackTrace();

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
    private String uploadImage(String filePath, String apiKey) throws IOException {
        File file = new File(filePath);
        byte[] imageBytes;

        // Vérification que le fichier existe
        if (!file.exists()) {
            throw new FileNotFoundException("Fichier image introuvable: " + filePath);
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            imageBytes = fis.readAllBytes();
        }

        String encodedImage = Base64.getEncoder().encodeToString(imageBytes);
        String urlParameters = "key=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8) +
                "&image=" + URLEncoder.encode(encodedImage, StandardCharsets.UTF_8);
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

        URL url = new URL("https://api.imgbb.com/1/upload");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
            wr.write(postData);
        }

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Erreur HTTP: " + responseCode);
        }

        // Lecture de la réponse sans utiliser JSONObject
        try (BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
            }

            // Extraction simple de l'URL (méthode alternative sans JSON)
            String jsonResponse = response.toString();
            int urlStart = jsonResponse.indexOf("\"url\":\"") + 7;
            if (urlStart > 7) { // Si on a trouvé le motif
                int urlEnd = jsonResponse.indexOf("\"", urlStart);
                return jsonResponse.substring(urlStart, urlEnd);
            }
            throw new IOException("Format de réponse inattendu de l'API");
        }
    }

    private String uploadImageToImgBB(String filePath, String apiKey) throws IOException {
        // Vérification du fichier
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("Fichier image introuvable: " + filePath);
        }

        // Lecture et encodage de l'image
        byte[] imageBytes;
        try (InputStream is = new FileInputStream(file)) {
            imageBytes = is.readAllBytes();
        }
        String encodedImage = Base64.getEncoder().encodeToString(imageBytes);

        // Préparation de la requête
        String requestBody = "key=" + URLEncoder.encode(apiKey, "UTF-8") +
                "&image=" + URLEncoder.encode(encodedImage, "UTF-8");

        // Configuration de la connexion
        URL url = new URL("https://api.imgbb.com/1/upload");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        // Envoi des données
        try (OutputStream os = connection.getOutputStream()) {
            os.write(requestBody.getBytes(StandardCharsets.UTF_8));
        }

        // Vérification de la réponse
        if (connection.getResponseCode() != 200) {
            throw new IOException("Erreur lors de l'upload: " + connection.getResponseMessage());
        }

        // Lecture de la réponse
        String response;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()))) {
            response = reader.lines().collect(Collectors.joining());
        }

        // Extraction de l'URL (approche basique et fragile)
        int dataStart = response.indexOf("\"data\":") + 7;
        int urlStart = response.indexOf("\"url\":\"", dataStart) + 7;
        int urlEnd = response.indexOf("\"", urlStart);
        return response.substring(urlStart, urlEnd);
    }



    // 2. Modifiez votre méthode comme suit :
    public void sendWhatsAppCertificate(Etudiant etudiant, String certificateUrl) {
        try {
            // Vérification des entrées
            if (etudiant == null || etudiant.getTel() == null || certificateUrl == null) {
                throw new IllegalArgumentException("Paramètres manquants");
            }

            // Formatage strict du numéro tunisien
            String numeroNetoye = etudiant.getTel().replaceAll("[^0-9]", "");
            if (numeroNetoye.length() < 8) {
                throw new IllegalArgumentException("Numéro trop court");
            }

            String numeroWhatsApp = "+216" + numeroNetoye.substring(0, 8);
            System.out.println("[DEBUG] Envoi à: whatsapp:" + numeroWhatsApp);

            // Initialisation Twilio
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

            // Envoi via le sandbox
            Message message = Message.creator(
                    new PhoneNumber("whatsapp:" + numeroWhatsApp),
                    new PhoneNumber("whatsapp:" + WHATSAPP_SANDBOX_NUMBER), // Format sandbox
                    "📄 Votre certificat est disponible : " + certificateUrl
            ).create();

            System.out.println("[SUCCÈS] Message ID: " + message.getSid());

        } catch (ApiException e) {
            System.err.println("[ERREUR TWILIO] Code: " + e.getCode() + " | " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[ERREUR] " + e.getMessage());
        }
    }
}