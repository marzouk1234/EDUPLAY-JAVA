package com.example.demo4.entities;

public class Etudiant {
    private int id;
    private String email;
    private String nom_et_prenom;
    private String tel;

    // Constructeurs
    public Etudiant() {}

    public Etudiant(int id, String email, String nom_et_prenom, String tel) {
        this.id = id;
        this.email = email;
        this.nom_et_prenom = nom_et_prenom;
        this.tel = tel;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getnom_et_prenom() { return nom_et_prenom; }
    public void setnom_et_prenom(String nom_et_prenom) { this.nom_et_prenom = nom_et_prenom; }

    public String getTel() { return tel; }
    public void setTel(String tel) { this.tel = tel; }
    @Override
    public String toString() {
        return nom_et_prenom; // ou ce que tu veux afficher dans le combo
    }

}
