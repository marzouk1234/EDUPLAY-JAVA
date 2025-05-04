package Models;



public class Event {
    private int id;
    private String nom;
    private String description;
    private String date;
    private byte[] IMG; // 👈 Changé de String à byte[]

    public Event() {}

    // Constructeur modifié (type de IMG → byte[])
    public Event(int id, String nom, String description, String date, byte[] IMG) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.date = date;
        this.IMG = IMG;
    }

    // Getters & Setters mis à jour
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public byte[] getIMG() { return IMG; } // 👈 Retourne byte[]
    public void setIMG(byte[] IMG) { this.IMG = IMG; } // 👈 Accepte byte[]

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", IMG=" + (IMG != null ? "bytes[" + IMG.length + "]" : "null") + // Affichage simplifié
                '}';
    }

}