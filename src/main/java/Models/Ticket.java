package Models;



public class Ticket {
    private int id;
    private int event_id;
    private String purchase_date;
    private String email;
    private String nom;

    public Ticket() {
    }

    public Ticket(int id, int event_id, String purchase_date, String email, String nom) {
        this.id = id;
        this.event_id = event_id;
        this.purchase_date = purchase_date;
        this.email = email;
        this.nom = nom;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEvent_id() {
        return event_id;
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }

    public String getPurchase_date() {
        return purchase_date;
    }

    public void setPurchase_date(String purchase_date) {
        this.purchase_date = purchase_date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", event_id=" + event_id +
                ", purchase_date=" + purchase_date +
                ", email='" + email + '\'' +
                ", nom='" + nom + '\'' +
                '}';
    }
}