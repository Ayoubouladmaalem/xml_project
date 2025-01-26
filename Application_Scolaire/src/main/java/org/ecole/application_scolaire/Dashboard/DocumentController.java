package org.ecole.application_scolaire.Dashboard;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;

public class DocumentController {

    // Generate "Carte Étudiant" PDF
    @FXML
    public void handleGeneratePdf() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            try {
                // Parse XML File
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(selectedFile);

                // Extract data
                Element root = doc.getDocumentElement();
                String nameUae = root.getElementsByTagName("nameUae").item(0).getTextContent();
                String nameSchool = root.getElementsByTagName("nameSchool").item(0).getTextContent();
                String villeSchool = root.getElementsByTagName("villeSchool").item(0).getTextContent();
                String lastName = root.getElementsByTagName("lastName").item(0).getTextContent();
                String firstName = root.getElementsByTagName("firstName").item(0).getTextContent();
                String codeApoge = root.getElementsByTagName("codeApoge").item(0).getTextContent();
                String footer = root.getElementsByTagName("footer").item(0).getTextContent();

                // Generate PDF
                generateStudentCardPdf(nameUae, nameSchool, villeSchool, lastName, firstName, codeApoge, footer);

            } catch (Exception e) {
                e.printStackTrace();
                showError("Erreur", "Une erreur est survenue lors de la génération du PDF : " + e.getMessage());
            }
        }
    }

    private void generateStudentCardPdf(String nameUae, String nameSchool, String villeSchool, String lastName,
                                        String firstName, String codeApoge, String footer) throws IOException {
        String userHome = System.getProperty("user.home");
        String downloadPath = userHome + "/Downloads/Carte_Etudiant.pdf";

        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
            contentStream.beginText();
            contentStream.setLeading(20f);
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("CARTE D'ETUDIANT");
            contentStream.newLine();

            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.showText("Université: " + nameUae);
            contentStream.newLine();
            contentStream.showText("Ecole: " + nameSchool);
            contentStream.newLine();
            contentStream.showText("Ville: " + villeSchool);
            contentStream.newLine();
            contentStream.showText("Nom: " + lastName);
            contentStream.newLine();
            contentStream.showText("Prénom: " + firstName);
            contentStream.newLine();
            contentStream.showText("Code Apogée: " + codeApoge);
            contentStream.newLine();
            contentStream.showText("Note: " + footer);
            contentStream.endText();
        }

        document.save(downloadPath);
        document.close();
        showInfo("Succès", "PDF généré avec succès dans :  Téléchargement de votre pc  ");
    }

    // Generate "Emploi du Temps" PDF
    @FXML
    public void handleGenerateTimetablePdf() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            try {
                // Parse XML File
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(selectedFile);

                // Generate the timetable PDF
                generateTimetablePdf(doc);

            } catch (Exception e) {
                e.printStackTrace();
                showError("Erreur", "Une erreur est survenue lors de la génération du PDF : " + e.getMessage());
            }
        }
    }

    private void generateTimetablePdf(Document doc) throws IOException {
        String userHome = System.getProperty("user.home");
        String downloadPath = userHome + "/Downloads/Emploi_du_Temps.pdf";

        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        float margin = 50;
        float startY = 700;
        float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
        float cellHeight = 20;
        float rowHeight = 20;
        int columns = 5;
        float cellWidth = tableWidth / columns;

        String[] headers = {"Jour", "Heure", "Matière", "Enseignant", "Salle"};

        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        try {
            // Title
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
            contentStream.beginText();
            contentStream.newLineAtOffset(220, 750);
            contentStream.showText("Emploi du Temps");
            contentStream.endText();

            // Draw header row
            drawRow(contentStream, headers, margin, startY, cellWidth, cellHeight, true);

            // Parse XML data
            NodeList days = doc.getElementsByTagName("day");
            contentStream.setFont(PDType1Font.HELVETICA, 10);

            float currentY = startY - rowHeight;

            for (int i = 0; i < days.getLength(); i++) {
                Element day = (Element) days.item(i);
                String dayName = day.getAttribute("name");
                NodeList slots = day.getElementsByTagName("slot");

                for (int j = 0; j < slots.getLength(); j++) {
                    Element slot = (Element) slots.item(j);

                    String time = slot.getAttribute("time");
                    String subject = slot.getElementsByTagName("subject").item(0).getTextContent();
                    String teacher = slot.getElementsByTagName("teacher").item(0).getTextContent();
                    String room = slot.getElementsByTagName("room").item(0).getTextContent();

                    String[] rowData = {dayName, time, subject, teacher, room};

                    // Draw the row
                    drawRow(contentStream, rowData, margin, currentY, cellWidth, cellHeight, false);
                    currentY -= rowHeight;

                    // Check if we need a new page
                    if (currentY < 50) {
                        contentStream.close(); // Close the current content stream
                        page = new PDPage();
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page); // Open a new content stream
                        currentY = 700; // Reset starting position
                        drawRow(contentStream, headers, margin, currentY, cellWidth, cellHeight, true); // Redraw headers
                        currentY -= rowHeight;
                    }

                    // Clear day name for subsequent rows on the same day
                    if (j > 0) {
                        dayName = "";
                    }
                }
            }
        } finally {
            contentStream.close(); // Ensure the content stream is closed
        }

        document.save(downloadPath);
        document.close();
        showInfo("Succès", "PDF généré avec succès dans :  Téléchargement de votre pc  ");
    }

    // Helper method to draw a row
    private void drawRow(PDPageContentStream contentStream, String[] rowData, float startX, float startY, float cellWidth, float cellHeight, boolean isHeader) throws IOException {
        // Draw the row borders
        float nextX = startX;
        for (int i = 0; i < rowData.length; i++) {
            contentStream.addRect(nextX, startY - cellHeight, cellWidth, cellHeight); // Draw the cell border
            contentStream.stroke(); // Apply the border stroke
            nextX += cellWidth;
        }

        // Write the text inside the cells
        float textX = startX + 5; // Padding for text
        float textY = startY - 15;

        for (int i = 0; i < rowData.length; i++) {
            contentStream.beginText(); // Begin text block
            if (isHeader) {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12); // Bold for header
            } else {
                contentStream.setFont(PDType1Font.HELVETICA, 10); // Regular font for rows
            }
            contentStream.newLineAtOffset(textX, textY); // Move to cell position
            contentStream.showText(rowData[i]); // Write cell text
            contentStream.endText(); // End text block
            textX += cellWidth; // Move to the next cell
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    public void handleGenerateRelevePdf() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            try {
                // Parse XML File
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(selectedFile);

                // Generate the Relevé PDF
                generateRelevePdf(doc);

            } catch (Exception e) {
                e.printStackTrace();
                showError("Erreur", "Une erreur est survenue lors de la génération du PDF : " + e.getMessage());
            }
        }
    }

    private void generateRelevePdf(Document doc) throws IOException {
        String userHome = System.getProperty("user.home");
        String downloadPath = userHome + "/Downloads/Releve_Notes.pdf";

        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        float margin = 50;
        float startY = 700;
        float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
        float cellHeight = 20;
        float rowHeight = 20;
        int columns = 4;
        float cellWidth = tableWidth / columns;

        String[] headers = {"MODULE", "MOYENNE", "RESULTAT", "SESSION"};
        float currentY = startY;

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            // Title
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("UNIVERSITE ABDELMALEK ESSAADI");
            contentStream.newLine();
            contentStream.showText("Ecole Nationale des Sciences Appliquées de Tanger");
            contentStream.newLine();
            contentStream.showText("Année universitaire : " + doc.getElementsByTagName("anneeUniversitaire").item(0).getTextContent());
            contentStream.newLine();
            contentStream.endText();

            // Draw the header row
            drawRow(contentStream, headers, margin, currentY, cellWidth, cellHeight, true);
            currentY -= rowHeight;
        }

        NodeList modules = doc.getElementsByTagName("module");

        for (int i = 0; i < modules.getLength(); i++) {
            if (currentY < 50) {
                // Create a new page and reset currentY
                page = new PDPage();
                document.addPage(page);
                currentY = startY;

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    // Redraw the header row on the new page
                    drawRow(contentStream, headers, margin, currentY, cellWidth, cellHeight, true);
                    currentY -= rowHeight;
                }
            }

            Element module = (Element) modules.item(i);
            String moduleName = module.getElementsByTagName("nom").item(0).getTextContent();
            String moyenne = module.getElementsByTagName("moyenne").item(0).getTextContent();
            String resultat = module.getElementsByTagName("resultat").item(0).getTextContent();
            String session = module.getElementsByTagName("session").item(0).getTextContent();

            String[] rowData = {moduleName, moyenne, resultat, session};

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Draw the row
                drawRow(contentStream, rowData, margin, currentY, cellWidth, cellHeight, false);
                currentY -= rowHeight;
            }
        }

        document.save(downloadPath);
        document.close();
        showInfo("Succès", "PDF généré avec succès dans : " + downloadPath);
    }

}
