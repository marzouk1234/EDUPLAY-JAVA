package com.example.demo4.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

 
public class MyDB {

    private static MyDB instance;

	
	 private static final String URL = "jdbc:mysql://localhost:3306/pidev3";
	 private static final String USER = "root";
	 private static final String PASSWORD = "";

	 private Connection cnx;

	    private MyDB() {
	        try {
	            cnx = DriverManager.getConnection(URL, USER, PASSWORD);
	            System.out.println("Connected!");
	        } catch (SQLException ex) {
	            System.err.println(ex.getMessage());
	        }
	    }

	    public static MyDB getInstance() {
	        if (instance == null) {
	            instance = new MyDB();
	        }
	        return instance;
	    }

	    public Connection getConnection() {
	        return cnx;
	    }


	    public void closeConnection() {
	        try {
	            cnx.close();
	            System.out.println("Connection closed!");
	        } catch (SQLException ex) {
	            System.err.println(ex.getMessage());
	        }
	    }
}
 	