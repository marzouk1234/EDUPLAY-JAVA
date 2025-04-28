package com.example.demo4.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDB {
    private static final String URL = "jdbc:mysql://localhost:3307/pidev3";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    private Connection cnx;
    private static MyDB instance;

    private MyDB() {
        establishConnection();
    }

    public static synchronized MyDB getInstance() {
        if (instance == null) {
            instance = new MyDB();
        }
        return instance;
    }

    private void establishConnection() {
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Create new connection
            cnx = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Connexion établie");
        } catch (ClassNotFoundException ex) {
            System.err.println("MySQL JDBC Driver not found!");
            ex.printStackTrace();
        } catch (SQLException ex) {
            System.err.println("Database connection failed!");
            ex.printStackTrace();
        }
    }

    public Connection getCnx() {
        try {
            // Check if connection is closed or null
            if (cnx == null || cnx.isClosed()) {
                System.out.println("Re-establishing database connection...");
                establishConnection();
            }
        } catch (SQLException ex) {
            System.err.println("Error checking connection status!");
            ex.printStackTrace();
        }
        return cnx;
    }

    public void closeConnection() {
        try {
            if (cnx != null && !cnx.isClosed()) {
                cnx.close();
                System.out.println("Connection closed");
            }
        } catch (SQLException ex) {
            System.err.println("Error closing connection!");
            ex.printStackTrace();
        }
    }
}