package Services;

import Models.Form_p;
import Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Form_pService1 implements IService<Form_p> {
    Connection con;

    public Form_pService1() {
        con = DataSource.getDataSource().getConnection();
    }

    @Override
    public void add(Form_p formP) throws SQLException {
        String query = "INSERT INTO form (contenu, date_pub, sujet, auteur) VALUES ('" + formP.getContenu() + "', '" + formP.getDatePub() + "', '" + formP.getSujet() + "', '" + formP.getAuteur() + "')";
        Statement stmt = con.createStatement();
        stmt.executeUpdate(query);
    }

    @Override
    public void update(Form_p formP) throws SQLException {
        String query = "UPDATE form SET contenu = '" + formP.getContenu() + "', date_pub = '" + formP.getDatePub() + "', sujet = '" + formP.getSujet() + "', auteur = '" + formP.getAuteur() + "' WHERE id = '" + formP.getId() + "'";
        Statement stmt = con.createStatement();
        stmt.executeUpdate(query);
    }

    @Override
    public void delete(Form_p formP) throws SQLException {
        String query = "DELETE FROM form WHERE id = '" + formP.getId() + "'";
        Statement stmt = con.createStatement();
        stmt.executeUpdate(query);
    }

    @Override
    public List<Form_p> getAll() throws SQLException {
        String query = "SELECT * FROM form";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        List<Form_p> formPS = new ArrayList<>();
        while (rs.next()) {
            Form_p formP = new Form_p();
            formP.setId(rs.getInt(1));
            formP.setContenu(rs.getString("contenu"));
            formP.setDatePub(rs.getDate("date_pub").toLocalDate());
            formP.setSujet(rs.getString("sujet"));
            formP.setAuteur(rs.getString("auteur"));
            formPS.add(formP);
        }

        return formPS;
    }
}
