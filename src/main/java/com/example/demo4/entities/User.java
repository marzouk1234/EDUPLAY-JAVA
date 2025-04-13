package com.example.demo4.entities;

import java.time.LocalDate; // Vous pouvez utiliser un autre type si nécessaire

public class User {

    private int id;
    private String email;
    private String roles;
    private String password;
    private String prenom;
    private String nom;
    private String tel;
    private String image;

    // Nouveau champ date de naissance
    private LocalDate dateNaissance;

    public User() {
        // Constructeur par défaut
    }

    // Constructeur partiel, si vous en avez besoin
    public User(String prenom, String nom, String tel) {
        super();
        this.prenom = prenom;
        this.nom = nom;
        this.tel = tel;
    }

    // Constructeur complet (avec dateNaissance en plus si nécessaire)
    public User(int id, String email, String roles, String password,
                String prenom, String nom, String tel,
                String image, LocalDate dateNaissance) {
        this.id = id;
        this.email = email;
        this.roles = roles;
        this.password = password;
        this.prenom = prenom;
        this.nom = nom;
        this.tel = tel;
        this.image = image;
        this.dateNaissance = dateNaissance;
    }

    @Override
    public String toString() {
        return "User [id=" + id
                + ", email=" + email
                + ", roles=" + roles
                + ", password=" + password
                + ", prenom=" + prenom
                + ", nom=" + nom
                + ", tel=" + tel
                + ", image=" + image
                + ", dateNaissance=" + dateNaissance
                + "]";
    }

    // Getters / Setters pour chaque champ
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }
}
