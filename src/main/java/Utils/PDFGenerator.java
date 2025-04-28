package Utils;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import Models.Form_p;

public class PDFGenerator {

    /**
     * Génère un PDF simple avec le contenu fourni
     */
    public static void generatePDF(String filePath, String content) {
        try {
            // Initialiser le document PDF
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(36, 36, 36, 36);

            // Ajouter le titre
            Paragraph title = new Paragraph("Document généré")
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            // Ajouter la date de génération
            Paragraph date = new Paragraph("Généré le: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT);
            document.add(date);

            // Ajouter un séparateur
            Paragraph separator = new Paragraph(" ")
                    .setFontSize(5)
                    .setBorderBottom(new SolidBorder(1))
                    .setMarginBottom(15);
            document.add(separator);

            // Ajouter le contenu principal
            Paragraph mainContent = new Paragraph(content)
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                    .setFontSize(12);
            document.add(mainContent);

            // Fermer le document
            document.close();
            System.out.println("PDF généré avec succès: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la création du PDF: " + e.getMessage());
        }
    }

    /**
     * Génère un PDF formaté à partir d'un objet Form_p
     */
    public static void generateFormPDF(Form_p form, String filePath) {
        try {
            // Initialiser le document PDF
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(36, 36, 36, 36);

            // En-tête avec logo de l'application (si disponible)
            Paragraph header = new Paragraph("EduPlay - Formulaire")
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(15);
            document.add(header);

            // Ajouter le titre du formulaire
            Paragraph title = new Paragraph(form.getSujet())
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10);
            document.add(title);

            // Tableau des informations
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}));
            infoTable.setWidth(UnitValue.createPercentValue(100));
            infoTable.setMarginBottom(15);

            // Ajouter les informations du formulaire dans le tableau
            // Pour l'ID
            Cell idHeaderCell = new Cell();
            idHeaderCell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            idHeaderCell.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD));
            idHeaderCell.setFontSize(12);
            idHeaderCell.setPadding(5);
            idHeaderCell.add(new Paragraph("ID"));
            infoTable.addCell(idHeaderCell);

            Cell idValueCell = new Cell();
            idValueCell.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA));
            idValueCell.setFontSize(12);
            idValueCell.setPadding(5);
            idValueCell.add(new Paragraph(String.valueOf(form.getId())));
            infoTable.addCell(idValueCell);

            // Pour l'Auteur
            Cell auteurHeaderCell = new Cell();
            auteurHeaderCell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            auteurHeaderCell.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD));
            auteurHeaderCell.setFontSize(12);
            auteurHeaderCell.setPadding(5);
            auteurHeaderCell.add(new Paragraph("Auteur"));
            infoTable.addCell(auteurHeaderCell);

            Cell auteurValueCell = new Cell();
            auteurValueCell.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA));
            auteurValueCell.setFontSize(12);
            auteurValueCell.setPadding(5);
            auteurValueCell.add(new Paragraph(form.getAuteur()));
            infoTable.addCell(auteurValueCell);

            // Pour la Date
            Cell dateHeaderCell = new Cell();
            dateHeaderCell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            dateHeaderCell.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD));
            dateHeaderCell.setFontSize(12);
            dateHeaderCell.setPadding(5);
            dateHeaderCell.add(new Paragraph("Date"));
            infoTable.addCell(dateHeaderCell);

            Cell dateValueCell = new Cell();
            dateValueCell.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA));
            dateValueCell.setFontSize(12);
            dateValueCell.setPadding(5);
            dateValueCell.add(new Paragraph(form.getFormattedDate()));
            infoTable.addCell(dateValueCell);

            document.add(infoTable);

            // Ajouter le contenu du formulaire
            Paragraph contentTitle = new Paragraph("Contenu du formulaire")
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setFontSize(14)
                    .setMarginBottom(5);
            document.add(contentTitle);

            Paragraph content = new Paragraph(form.getContenu())
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                    .setFontSize(12)
                    .setMarginBottom(20);
            document.add(content);

            // Pied de page avec informations légales
            Paragraph footer = new Paragraph("Document généré le " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")) +
                    " via l'application EduPlay")
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)) // Changé de HELVETICA_ITALIC à HELVETICA
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(footer);

            // Fermer le document
            document.close();
            System.out.println("PDF du formulaire généré avec succès: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la création du PDF du formulaire: " + e.getMessage());
        }
    }
}