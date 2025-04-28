package Models;

import javafx.scene.image.ImageView; // import for ImageView

public class Feedback {
    private int id;
    private int id_jeux;
    private String nom;
    private String prenom;
    private String feedback;
    private int rating;

    private ImageView qrCode; // 🆕 Add QR code image view

    public Feedback(int id, int id_jeux, String nom, String prenom, String feedback, int rating) {
        this.id = id;
        this.id_jeux = id_jeux;
        this.nom = nom;
        this.prenom = prenom;
        this.feedback = feedback;
        this.rating = rating;
    }

    // 🆕 New constructor (with QR code)
    public Feedback(int id, int id_jeux, String nom, String prenom, String feedback, int rating, ImageView qrCode) {
        this.id = id;
        this.id_jeux = id_jeux;
        this.nom = nom;
        this.prenom = prenom;
        this.feedback = feedback;
        this.rating = rating;
        this.qrCode = qrCode;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdJeux() { return id_jeux; }
    public void setIdJeux(int idJeux) { this.id_jeux = idJeux; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public void setid_jeux(int i) { this.id_jeux = i; }
    public int getid_jeux() { return id_jeux; }

    // 🆕 QR code getter/setter
    public ImageView getQrCode() {
        return qrCode;
    }

    public void setQrCode(ImageView qrCode) {
        this.qrCode = qrCode;
    }
}
