package Models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Represents a form/post entity in the system.
 * Contains information about the content, publication date, subject, and author.
 */
public class Form_p {
    private int id;
    private String contenu;
    private LocalDate datePub;
    private String sujet;
    private String auteur;

    /**
     * Default constructor
     */
    public Form_p() {
    }

    /**
     * Constructor for creating a new form without ID
     */
    public Form_p(String contenu, LocalDate datePub, String sujet, String auteur) {
        setContenu(contenu);
        setDatePub(datePub);
        setSujet(sujet);
        setAuteur(auteur);
    }

    /**
     * Constructor for creating a form with an existing ID
     */
    public Form_p(int id, String contenu, LocalDate datePub, String sujet, String auteur) {
        this(contenu, datePub, sujet, auteur);
        setId(id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        if (contenu == null || contenu.trim().isEmpty()) {
            throw new IllegalArgumentException("Le contenu ne peut pas être vide");
        }
        this.contenu = contenu;
    }

    public LocalDate getDatePub() {
        return datePub;
    }
    
    /**
     * Get formatted publication date as string
     */
    public String getFormattedDate() {
        if (datePub == null) return "";
        return datePub.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public void setDatePub(LocalDate datePub) {
        if (datePub == null) {
            throw new IllegalArgumentException("La date de publication ne peut pas être nulle");
        }
        this.datePub = datePub;
    }

    public String getSujet() {
        return sujet;
    }

    public void setSujet(String sujet) {
        if (sujet == null || sujet.trim().isEmpty()) {
            throw new IllegalArgumentException("Le sujet ne peut pas être vide");
        }
        this.sujet = sujet;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        if (auteur == null || auteur.trim().isEmpty()) {
            throw new IllegalArgumentException("L'auteur ne peut pas être vide");
        }
        this.auteur = auteur;
    }

    @Override
    public String toString() {
        return "Form{" +
                "id=" + id +
                ", contenu='" + contenu + '\'' +
                ", datePub=" + datePub +
                ", sujet='" + sujet + '\'' +
                ", auteur='" + auteur + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Form_p form_p = (Form_p) o;
        return id == form_p.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}