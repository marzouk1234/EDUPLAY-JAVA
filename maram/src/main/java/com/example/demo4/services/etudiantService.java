package com.example.demo4.services;

import com.example.demo4.entities.Etudiant;
import com.example.demo4.utils.MyDB;
import com.example.demo4.utils.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class etudiantService {

    private final Connection cnx;

    public etudiantService() {
        cnx = MyDB.getInstance().getCnx() ;
    }

    // ✅ Ajouter un étudiant
    public void ajouterEtudiant(Etudiant e) throws SQLException {
        String req = "INSERT INTO etudiant (nom_et_prenom, email, tel) VALUES (?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setString(1, e.getnom_et_prenom());
        ps.setString(2, e.getEmail());
        ps.setString(3, e.getTel());
        ps.executeUpdate();
    }

    // ✅ Modifier un étudiant
    public void modifierEtudiant(Etudiant e) throws SQLException {
        String req = "UPDATE etudiant SET nom_et_prenom=?, email=?, tel=? WHERE id=?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setString(1, e.getnom_et_prenom());
        ps.setString(2, e.getEmail());
        ps.setString(3, e.getTel());
        ps.setInt(4, e.getId());
        ps.executeUpdate();
    }

    // ✅ Supprimer un étudiant
    public void supprimerEtudiant(int id) throws SQLException {
        String req = "DELETE FROM etudiant WHERE id=?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    // ✅ Récupérer tous les étudiants (pour ComboBox ou tableau)
    public List<Etudiant> recupereretudiant() throws SQLException {
        List<Etudiant> list = new ArrayList<>();
        String req = "SELECT * FROM etudiant";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            Etudiant e = new Etudiant();
            e.setId(rs.getInt("id"));
            e.setnom_et_prenom(rs.getString("nom_et_prenom"));
            e.setEmail(rs.getString("email"));
            e.setTel(rs.getString("tel"));

            list.add(e);
        }

        return list;
    }

    // ✅ Optionnel : chercher un étudiant par id
    public Etudiant getEtudiantById(int id) throws SQLException {
        String req = "SELECT * FROM etudiant WHERE id=?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new Etudiant(
                    rs.getInt("id"),
                    rs.getString("email"),
                    rs.getString("nom_et_prenom"),
                    rs.getString("tel")
            );
        }
        return null;
    }
}
