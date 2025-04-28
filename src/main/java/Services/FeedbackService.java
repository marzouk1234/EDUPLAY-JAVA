package Services;

import Controllers.QRCodeGenerator;
import Models.Feedback;
import Utils.DataSource;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedbackService implements IService<Feedback> {
    private Connection conn;

    public FeedbackService() throws SQLException {
        conn = DataSource.getInstance();
    }

    @Override
    public void ajouter(Feedback f) throws SQLException {
        // Insert feedback into database
        String sql = "INSERT INTO feedback (id, id_jeux, nom, prenom, feedback, rating) VALUES (" +
                f.getId() + ", " + f.getIdJeux() + ", '" +
                f.getNom() + "', '" + f.getPrenom() + "', '" +
                f.getFeedback() + "', " + f.getRating() + ")";
        Statement st = conn.createStatement();
        st.executeUpdate(sql);

        // Generate QR code after feedback insertion
        String qrCodePath = "QRCode_" + f.getId() + ".png";
        try {
            QRCodeGenerator.generateQRCode(f.getFeedback(), qrCodePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modifier(Feedback f) throws SQLException {
        String sql = "UPDATE feedback SET id_jeux = " + f.getIdJeux() +
                ", nom = '" + f.getNom() +
                "', prenom = '" + f.getPrenom() +
                "', feedback = '" + f.getFeedback() +
                "', rating = " + f.getRating() +
                " WHERE id = " + f.getId();
        Statement st = conn.createStatement();
        st.executeUpdate(sql);
    }

    @Override
    public void supprimer(int id) throws SQLException {
        Statement st = conn.createStatement();
        st.executeUpdate("DELETE FROM feedback WHERE id = " + id);
    }

    @Override
    public List<Feedback> afficher() throws SQLException {
        List<Feedback> list = new ArrayList<>();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM feedback");
        while (rs.next()) {
            Feedback feedback = new Feedback(
                    rs.getInt("id"),
                    rs.getInt("id_jeux"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("feedback"),
                    rs.getInt("rating")
            );

            // ➡ Optional: Generate QR Code and save it as image file
            String qrText = feedback.getNom() + " " + feedback.getPrenom() + ": " + feedback.getFeedback();
            generateQRCodeToFile(qrText, "QRCode_" + feedback.getId() + ".png", 200, 200);

            list.add(feedback);
        }
        return list;
    }

    public Map<Integer, Integer> getFeedbackCountByRating() throws SQLException {
        Map<Integer, Integer> ratingCounts = new HashMap<>();
        String query = "SELECT rating, COUNT(*) as count FROM feedback GROUP BY rating";

        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(query);

        while (rs.next()) {
            ratingCounts.put(rs.getInt("rating"), rs.getInt("count"));
        }

        return ratingCounts;
    }

    // 🆕 Clean QR Code generation to file (no javafx.swing needed)
    private void generateQRCodeToFile(String data, String filePath, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height);
            Path path = FileSystems.getDefault().getPath(filePath);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
    }
}
