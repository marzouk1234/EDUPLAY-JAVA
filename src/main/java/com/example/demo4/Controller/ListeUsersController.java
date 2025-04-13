package com.example.demo4.Controller;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

import com.example.demo4.entities.User;
import com.example.demo4.utils.MyDB;
import com.example.demo4.services.ServiceUser;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

public class ListeUsersController implements Initializable {

    private Connection connection = MyDB.getInstance().getConnection();;


    @FXML
    private TableColumn<User, String> id;

    @FXML
    private TableColumn<User, String> nom;
    boolean ok;
    @FXML
    private TableColumn<User, String> prenom;
    @FXML
    private TableColumn<User, String> email;

    @FXML
    private TableColumn<User, String> phone;
    @FXML
    private TableColumn<User, LocalDate> colDateNaissance;


    @FXML
    private TableView<User> users;


    @FXML
    private TextField filtre ;



    ObservableList<User> listeB = FXCollections.observableArrayList();
    @FXML
    public void show(){
        ServiceUser bs=new ServiceUser();
        listeB=bs.afficherTous();
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        nom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        prenom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        email.setCellValueFactory(new PropertyValueFactory<>("email"));
        phone.setCellValueFactory(new PropertyValueFactory<>("tel"));
        colDateNaissance.setCellValueFactory(new PropertyValueFactory<>("dateNaissance"));


        users.setItems(listeB);

    }



    @FXML
    public void handleSearch(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            String searchText = filtre.getText().trim();
            if (searchText.isEmpty()) {
                users.setItems(listeB);
            } else {
                ObservableList<User> filteredList = FXCollections.observableArrayList();
                boolean productFound = false;
                for (User b : listeB) {
                    // search for name or description
                    if ((b.getPrenom().toLowerCase().contains(searchText.toLowerCase()))
                            || (b.getNom().toLowerCase().contains(searchText.toLowerCase()))) {
                        filteredList.add(b);
                        productFound = true;
                    }
                }
                if (!productFound) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle(" document non trouv�");
                    alert.setHeaderText("Aucun document ne correspond � votre recherche");
                    alert.setContentText("Veuillez essayer une autre recherche.");
                    alert.showAndWait();
                }
                users.setItems(filteredList);
            }
        }
    }

    @FXML
    void supp(ActionEvent event) {
        User selectedLN =  users.getSelectionModel().getSelectedItem();
        if (selectedLN == null) {
            // Afficher un message d'erreur
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible de supprimer la User ");
            alert.setContentText("Veuillez sélectionner une User à supprimer !");
            alert.showAndWait();}
        else {

            Alert confirmation = new Alert(AlertType.CONFIRMATION, "�tes-vous s�r de vouloir supprimer cet utilisateur ?", ButtonType.YES, ButtonType.NO);
            confirmation.setHeaderText(null);
            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    // Si l'utilisateur confirme, appeler la fonction de suppression
                    ServiceUser sc =new ServiceUser();
                    sc.supprimer(selectedLN.getId());

                    // Afficher une alerte de r�ussite apr�s la suppression
                    Alert success = new Alert(AlertType.INFORMATION, "L'utilisateur a �t� supprim� avec succ�s !");
                    success.setHeaderText(null);
                    success.showAndWait();
                    show();
                    users.refresh();
                }
            });
        }

    }





    @FXML
    void Modifier(ActionEvent event) {

        User selectedUser = users.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            // Afficher un message d'erreur
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible de modifier");
            alert.setContentText("Veuillez s�lectionner un utilisateur pour modifier !");
            alert.showAndWait();
        } else {
            // Show an input dialog to get the new user details.
            Dialog<User> dialog = new Dialog<>();
            dialog.setTitle("Modifier un utilisateur");
            dialog.setHeaderText("Modifier les champs");

            // Set the default value of the input fields to the current user details.
            TextField prenomField = new TextField(selectedUser.getPrenom());
            TextField nomField = new TextField(selectedUser.getNom());
            TextField telField = new TextField(selectedUser.getTel());
            DatePicker datePicker = new DatePicker(selectedUser.getDateNaissance());

            // Add the input fields to the dialog pane.
            GridPane grid = new GridPane();
            grid.add(new Label("Nom:"), 1, 1);
            grid.add(prenomField, 2, 1);
            grid.add(new Label("Nom de famille:"), 1, 2);
            grid.add(nomField, 2, 2);
            grid.add(new Label("Num�ro de t�l�phone:"), 1, 3);
            grid.add(telField, 2, 3);
            grid.add(new Label("Date de naissance:"), 1, 4);
            grid.add(datePicker, 2, 4);
            dialog.getDialogPane().setContent(grid);

            // Add buttons to the dialog pane.
            ButtonType modifierButtonType = new ButtonType("Modifier", ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(modifierButtonType, ButtonType.CANCEL);

            // Convert the result to a user object when the modify button is clicked.
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == modifierButtonType) {
                    return new User(
                            prenomField.getText(),
                            nomField.getText(),
                            telField.getText()
                    );
                }
                return null;
            });

            Optional<User> result = dialog.showAndWait();
            if (result.isPresent()) {
                // Update the selected user with the new values.
                selectedUser.setPrenom(result.get().getPrenom());
                selectedUser.setNom(result.get().getNom());
                selectedUser.setTel(result.get().getTel());

                // Call the service method to update the user in the database.
                ServiceUser bs = new ServiceUser();
                bs.Update(selectedUser, selectedUser.getId());

                // Show a confirmation alert.
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Succ�s");
                alert.setHeaderText("Utilisateur a �t� modifi� avec succ�s");
                alert.setContentText("Les modifications ont �t� enregistr�es.");
                alert.showAndWait();
            }

        }
        users.refresh();

    }




    public void initialize(URL location, ResourceBundle resources) {
        users.getStyleClass().add("my-tableview");
        users.setRowFactory(tv -> new TableRow<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    pseudoClassStateChanged(BLOQUE_PSEUDO_CLASS, false);
                    getStyleClass().remove("bloque"); // remove bloque style class
                } else {
                    try {
                        PreparedStatement ps = connection.prepareStatement("SELECT bloque FROM user WHERE id = ?");
                        ps.setInt(1, item.getId());
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            int x = rs.getInt("bloque");
                            if (x == 1) {
                                pseudoClassStateChanged(BLOQUE_PSEUDO_CLASS, true);
                                getStyleClass().add("bloque"); // add bloque style class
                            } else {
                                pseudoClassStateChanged(BLOQUE_PSEUDO_CLASS, false);
                                getStyleClass().remove("bloque"); // remove bloque style class
                            }
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        show();
    }
    private static final PseudoClass BLOQUE_PSEUDO_CLASS = PseudoClass.getPseudoClass("bloque");

}

