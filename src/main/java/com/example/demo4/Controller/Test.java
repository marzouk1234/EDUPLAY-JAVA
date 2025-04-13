package com.example.demo4.Controller;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Test extends Application{
	public static Stage stg;
    public void start(Stage primaryStage) throws IOException  
    {
	            Test.stg = primaryStage;

		URL fxmlLocation = getClass().getResource("/com/example/demo4/login.fxml");
		if (fxmlLocation == null) {
			System.out.println("Fichier login.fxml introuvable !");
		} else {
			System.out.println("Fichier trouvé : " + fxmlLocation);
		}
		FXMLLoader loader = new FXMLLoader(fxmlLocation);



		Parent root= loader.load();
	            Scene scene= new Scene(root);
	            primaryStage.setTitle("Bievennue");
	            primaryStage.setScene(scene);
	            primaryStage.show();
	        
	        
	    }
	    

	 
	    
	   public static void main(String[] args) {
	           launch(args);
	         
	    }}
 
