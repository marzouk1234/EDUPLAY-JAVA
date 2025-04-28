package Utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import Models.Form_p;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class QRCodeGenerator {

    /**
     * Génère un QR Code à partir d'une chaîne de données et le sauvegarde dans un fichier
     */
    public static void generateQRCode(String data, String filePath) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 2);
            
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 250, 250, hints);

            // Sauver l'image du QR Code dans un fichier
            File file = new File(filePath);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", file.toPath());
            System.out.println("QR Code généré avec succès: " + filePath);
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la création du QR Code: " + e.getMessage());
        }
    }

    /**
     * Génère un QR Code avec les informations du formulaire et renvoie une image BufferedImage
     */
    public static BufferedImage generateFormQRCode(Form_p form) throws WriterException {
        // Créer le contenu du QR code avec les infos du formulaire au format texte structuré
        StringBuilder qrContent = new StringBuilder();
        qrContent.append("ID: ").append(form.getId()).append("\n");
        qrContent.append("Sujet: ").append(form.getSujet()).append("\n");
        qrContent.append("Auteur: ").append(form.getAuteur()).append("\n");
        qrContent.append("Date: ").append(form.getFormattedDate()).append("\n");
        qrContent.append("\nEduPlay - Formulaire");
        
        // Configurer les options du QR code
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 1);
        
        // Générer la matrice de bits du QR code
        BitMatrix bitMatrix = qrCodeWriter.encode(qrContent.toString(), BarcodeFormat.QR_CODE, 300, 300, hints);
        
        // Convertir en image avec des couleurs personnalisées
        return createQRImage(bitMatrix);
    }
    
    /**
     * Crée une image du QR code avec des couleurs personnalisées
     */
    private static BufferedImage createQRImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        
        // Couleurs personnalisées pour le QR code
        Color darkColor = new Color(0, 102, 204); // Bleu EduPlay
        Color lightColor = Color.WHITE;
        
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        
        // Remplir l'arrière-plan
        graphics.setColor(lightColor);
        graphics.fillRect(0, 0, width, height);
        
        // Dessiner les points du QR code
        graphics.setColor(darkColor);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (matrix.get(x, y)) {
                    graphics.fillRect(x, y, 1, 1);
                }
            }
        }
        
        graphics.dispose();
        return image;
    }
    
    /**
     * Sauvegarde une image BufferedImage dans un fichier
     */
    public static void saveQRCodeImage(BufferedImage image, String filePath) {
        try {
            File file = new File(filePath);
            ImageIO.write(image, "PNG", file);
            System.out.println("Image QR Code enregistrée avec succès: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'enregistrement de l'image: " + e.getMessage());
        }
    }
}
