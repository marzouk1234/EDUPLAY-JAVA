package Models;

import java.time.LocalDateTime;

public class Aide {
    private int id;
    private String sujet;
    private String description;
    private LocalDateTime dateCreation;
    private int formId;

    public Aide() {
    }

    public Aide(String sujet, String description, LocalDateTime dateCreation, int formId) {
        this.sujet = sujet;
        this.description = description;
        this.dateCreation = dateCreation;
        this.formId = formId;
    }

    public Aide(int id, String sujet, String description, LocalDateTime dateCreation, int formId) {
        this.id = id;
        this.sujet = sujet;
        this.description = description;
        this.dateCreation = dateCreation;
        this.formId = formId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSujet() {
        return sujet;
    }

    public void setSujet(String sujet) {
        this.sujet = sujet;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

    @Override
    public String toString() {
        return "Aide{" +
                "id=" + id +
                ", sujet='" + sujet + '\'' +
                ", description='" + description + '\'' +
                ", dateCreation=" + dateCreation +
                ", formId=" + formId +
                '}';
    }
}
