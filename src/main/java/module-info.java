module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;
    requires qrgen;
    requires itextpdf;
    requires java.desktop;

    requires org.apache.poi.poi;
    requires javafx.media;
    requires javafx.web;
    requires twilio;
    requires org.controlsfx.controls;
    requires stripe.java;
    requires fontawesomefx;
    requires jakarta.mail;
    requires jbcrypt;
    requires opencv;

    opens com.example.demo4.Controller to javafx.fxml;
exports com.example.demo4.Controller;
    opens com.example.demo4.entities to javafx.base;  // Permet l'accès via réflexion
    exports com.example.demo4;
}


