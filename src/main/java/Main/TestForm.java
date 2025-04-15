package Main;

import Models.Form_p;
import Services.Form_pService;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TestForm {
    public static void main(String[] args) {
        try {
            Form_pService formPService = new Form_pService();

            // Example date string
            String dateString = "2025-04-07";

            // Convert String to LocalDate
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate datePub = LocalDate.parse(dateString, formatter);

            // Create Form object with converted date
            Form_p formP = new Form_p("Contenu de test", datePub, "Sujet Test", "Auteur Test");

            // Test CRUD operations
            formPService.add(formP);
            System.out.println("After adding:");
            System.out.println(formPService.getAll());

            // Update test
            formP.setContenu("Contenu modifié");
            formPService.update(formP);
            System.out.println("\nAfter updating:");
            System.out.println(formPService.getAll());

            // Get by ID test
            System.out.println("\nGetting by ID:");
            Form_p retrievedFormP = formPService.getById(formP.getId());
            System.out.println(retrievedFormP);

            // Delete test
            formPService.delete(formP);
            System.out.println("\nAfter deleting:");
            System.out.println(formPService.getAll());

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
