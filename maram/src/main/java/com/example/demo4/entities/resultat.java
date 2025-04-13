package com.example.demo4.entities;

import java.time.LocalDateTime;

public class resultat {

    private int id;
    private int evaluationId;
    private double note;          // <-- Nouveau champ
    private String appreciation;
    private LocalDateTime date_creation;

    // Constructeur par défaut
    public resultat() {
    }

    // Constructeur complet
    public resultat(int id, int evaluationId, double note, String appreciation, LocalDateTime date_creation) {
        this.id = id;
        this.evaluationId = evaluationId;
        this.note = note;
        this.appreciation = appreciation;
        this.date_creation = date_creation;
    }

    // Constructeur sans 'id' (pour insertion)
    public resultat(int evaluationId, double note, String appreciation, LocalDateTime date_creation) {
        this.evaluationId = evaluationId;
        this.note = note;
        this.appreciation = appreciation;
        this.date_creation = date_creation;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEvaluationId() {
        return evaluationId;
    }

    public void setEvaluationId(int evaluationId) {
        this.evaluationId = evaluationId;
    }

    public double getNote() {
        return note;
    }

    public void setNote(double note) {
        this.note = note;
    }

    public String getAppreciation() {
        return appreciation;
    }

    public void setAppreciation(String appreciation) {
        this.appreciation = appreciation;
    }

    public LocalDateTime getDate_creation() {
        return date_creation;
    }

    public void setDate_creation(LocalDateTime date_creation) {
        this.date_creation = date_creation;
    }

    @Override
    public String toString() {
        return "resultat{" +
                "id=" + id +
                ", evaluationId=" + evaluationId +
                ", note=" + note +
                ", appreciation='" + appreciation + '\'' +
                ", date_creation=" + date_creation +
                '}';
    }
}
