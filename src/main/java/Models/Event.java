package Models;

public class Event {
     private int id;
     private String nom;
     private String description;
     private String date;
     private String IMG;
     public Event(){

     }
     public Event(int id, String nom, String description, String date, String IMG) {
         this.id = id;
         this.nom = nom;
         this.description = description;
         this.date = date;
         this.IMG = IMG;
     }
     public int getId() {
         return id;
     }
    public void setId(int id) {
        this.id = id;
    }
    public String getNom() {
         return nom;
    }
    public void setNom(String nom) {
         this.nom = nom;
    }
    public String getDescription() {
         return description;
    }
    public void setDescription(String description) {
         this.description = description;
    }
    public String getDate() {
         return date;
    }
    public void setDate(String date) {
         this.date = date;
    }
    public String getIMG() {
         return IMG;
    }
    public void setIMG(String IMG) {
         this.IMG = IMG;
    }
    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", IMG='" + IMG + '\'' +
                '}';


    }

}
