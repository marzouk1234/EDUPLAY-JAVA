package Main;

import Models.Aide;
import Services.AideService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestAide {
    public static void main(String[] args) {
        AideService ps = new AideService();
        try {
            // Exemple de String pour la date
            String dateString = "2025-04-06 10:30:00";

            // Conversion de String à LocalDateTime
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateCreation = LocalDateTime.parse(dateString, formatter);

            // Création de l'objet Aide avec la date convertie et un formId
            Aide aide = new Aide("Sujet Test", "Description Test", dateCreation, 1); // formId = 1
            ps.add(aide);
            System.out.println(ps.getAll());

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
