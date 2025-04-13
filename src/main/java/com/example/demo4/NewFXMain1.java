/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this temanimauxe file, choose Tools | Temanimauxes
 * and open the temanimauxe in the editor.
 */
package com.example.demo4;
import java.io.IOException;
import java.util.Objects;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author asus
 */
public class NewFXMain1 extends Application {
   
    @Override
    public void start(Stage primaryStage) throws IOException {
               Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("ajouterdocument.fxml")));
        Scene scene = new Scene(root,950,650);
        primaryStage.setTitle("Gérer animauxs");
        //primaryStage.setIconified(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
