package Main;

import Models.Game;
import Services.GameService1;

import java.sql.Date;

public class Main {
    public static void main(String[] args) {
        try {
            GameService1 service = new GameService1();

            Date gameDate = Date.valueOf("2025-04-13"); // yyyy-MM-dd
            Game g1 = new Game(1, "Chess", "Board", "puzzle", gameDate);
            service.ajouter(g1);

            g1.setNom("Updated Chess");
            service.modifier(g1);

            service.afficher().forEach(System.out::println);

            service.supprimer(60);

        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
}
