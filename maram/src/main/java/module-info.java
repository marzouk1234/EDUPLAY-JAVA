module com.example.demo4 {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;
    requires qrgen;
    requires itextpdf;
    requires java.desktop;
    requires java.mail;
    requires org.apache.poi.poi;
    requires javafx.media;
    requires twilio;
    requires org.controlsfx.controls;

    // Ouvrir le package 'com.example.demo4.Controller' à javafx.fxml
    // pour permettre la réflexion nécessaire (injection @FXML, etc.)
    opens com.example.demo4.Controller to javafx.fxml;

    // Ouvrir le package 'com.example.demo4.entities' à javafx.base
    // si vous utilisez des entities dans des TableView ou similaires
    opens com.example.demo4.entities to javafx.base;

    // Exporter le package des contrôleurs si besoin que d’autres modules y accèdent
    exports com.example.demo4.Controller;

    opens com.example.demo4 to javafx.fxml;
}
