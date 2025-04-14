package Models;

import java.sql.Date;

public class Game {
    private int id;
    private String nom;
    private String prenom;
    private String type;
    private Date date;

    public Game(int id, String nom, String prenom, String type, Date date) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.type = type;
        this.date = date;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Date getDate() { return date; }
    public void setDate(String date) { this.date = Date.valueOf(date); }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", type='" + type + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
