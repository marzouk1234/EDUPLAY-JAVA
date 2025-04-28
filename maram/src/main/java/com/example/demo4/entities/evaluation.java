package com.example.demo4.entities;

import java.time.LocalDateTime;

public class evaluation {

    private int id;
    private String titre;
    private String type;
    private LocalDateTime date;
    private String image;
    private int etudiantId; // Ajout de l'attribut etudiantId

    public evaluation() {
    }

    public evaluation(int id, String titre, String type, LocalDateTime date, String image, int etudiantId) {
        this.id = id;
        this.titre = titre;
        this.type = type;
        this.date = date;
        this.image = image;
        this.etudiantId = etudiantId;
    }

    public evaluation(String titre, String type, LocalDateTime date, String image, int etudiantId) {
        this.titre = titre;
        this.type = type;
        this.date = date;
        this.image = image;
        this.etudiantId = etudiantId;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public String getType() {
        return type;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getImage() {
        return image;
    }

    public int getEtudiantId() {
        return etudiantId;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setEtudiantId(int etudiantId) {
        this.etudiantId = etudiantId;
    }

    @Override
    public String toString() {
        return "evaluation{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", type='" + type + '\'' +
                ", date=" + date +
                ", image='" + image + '\'' +
                ", etudiantId=" + etudiantId +
                '}';
    }
}
