package Models;

public class Feedback {
    private int id;
    private int id_jeux;
    private String nom;
    private String prenom;
    private String feedback;
    private int rating;

    public Feedback(int id, int id_jeux, String nom, String prenom, String feedback, int rating) {
        this.id = id;
        this.id_jeux = id_jeux;
        this.nom = nom;
        this.prenom = prenom;
        this.feedback = feedback;
        this.rating = rating;
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

    public void setid_jeux(int i) {
    }

    public int getid_jeux() {
        return id_jeux;
    }
}
