package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe singleton pour gérer la connexion à la base de données
 */
public class DataSource {
    private Connection connection;
    private final String url = "jdbc:mysql://localhost:3306/ps?useSSL=false&serverTimezone=UTC";
    private final String user = "root";
    private final String password = "";
    private static DataSource dataSource;

    private DataSource() {
        try {
            // Chargement explicite du driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Établissement de la connexion
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connexion à la base de données établie avec succès");
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur de chargement du driver MySQL: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static DataSource getDataSource() {
        if (dataSource == null) {
            dataSource = new DataSource();
        } else if (dataSource.getConnection() == null) {
            // Recréer l'instance si la connexion est nulle
            dataSource = new DataSource();
        }
        return dataSource;
    }

    public Connection getConnection() {
        try {
            // Vérifier si la connexion est fermée et la recréer si nécessaire
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Connexion à la base de données rétablie");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification/reconnexion à la base de données: " + e.getMessage());
        }
        return connection;
    }
    
    /**
     * Ferme la connexion à la base de données
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connexion à la base de données fermée");
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
            }
        }
    }
}
