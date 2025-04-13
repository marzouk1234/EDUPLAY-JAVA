package com.example.demo4.entities;

import java.time.LocalDateTime;

public class evaluation {

    private int id;
    private String titre;

    private String type;
    private LocalDateTime date;
    private String image;


    public evaluation() {
    }

    public evaluation(int id, String titre, String type, LocalDateTime date,
                       String image) {
        this.id = id;
        this.titre = titre;
        this.type = type;
        this.date = date;
        this.image = image;

    }

    public evaluation(String titre, String type, LocalDateTime date,
                       String image) {
        this.titre = titre;
        this.type = type;
        this.date = date;
        this.image = image;

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



    @Override
    public String toString() {
        return "evaluation{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", type='" + type + '\'' +
                ", date=" + date +

                ", image='" + image + '\'' +
                '}';
    }
}
