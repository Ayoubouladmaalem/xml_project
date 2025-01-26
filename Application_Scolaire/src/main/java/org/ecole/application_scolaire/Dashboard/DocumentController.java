package org.ecole.application_scolaire.Dashboard;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DocumentController {

    private static final List<String> ALLOWED_WEEKS = Arrays.asList(
            "Week of 30/12",
            "Week of 06/01",
            "Week of 13/01",
            "Week of 20/01"
    );

    @FXML
    private ComboBox<String> studentComboBox;
    @FXML
    private ComboBox<String> weekComboBox;

    // Initialisation du contrôleur
    public void initialize() {
        loadStudentsFromXml();
        loadWeeksFromXml();
    }

    // Charger les noms des étudiants dans le ComboBox
    private void loadStudentsFromXml() {
        try {
            // Charger le fichier XML
            File xmlFile = new File("src/main/resources/xml/students.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            // Normaliser le document XML
            document.getDocumentElement().normalize();

            // Récupérer tous les éléments <student>
            NodeList studentNodes = document.getElementsByTagName("student");

            // Parcourir les étudiants
            for (int i = 0; i < studentNodes.getLength(); i++) {
                Node node = studentNodes.item(i);

                if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                    Element studentElement = (Element) node;

                    // Vérifier si les balises <nom> et <prenom> existent
                    Node nameNode = studentElement.getElementsByTagName("nom").item(0);
                    Node surnameNode = studentElement.getElementsByTagName("prenom").item(0);

                    if (nameNode != null && surnameNode != null) {
                        String nom = nameNode.getTextContent();
                        String prenom = surnameNode.getTextContent();
                        String fullName = nom + " " + prenom;

                        // Ajouter le nom complet au ComboBox
                        studentComboBox.getItems().add(fullName);
                    } else {
                        System.err.println("Balises <nom> ou <prenom> manquantes pour un étudiant.");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Obtenir le chemin du dossier Téléchargements
    private String getDownloadsFolderPath() {
        String userHome = System.getProperty("user.home");
        return userHome + File.separator + "Downloads";
    }





    private void loadWeeksFromXml() {
        try {
            // Load the emploi.xml file
            File xmlFile = new File("src/main/resources/xml/emploi.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            // Extract all <week> elements
            NodeList weekNodes = document.getElementsByTagName("week");

            // Iterate through the weeks and add their numbers to the ComboBox
            for (int i = 0; i < weekNodes.getLength(); i++) {
                Node weekNode = weekNodes.item(i);
                if (weekNode != null && weekNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element weekElement = (Element) weekNode;
                    String weekNumber = weekElement.getAttribute("number");
                    if (weekNumber != null && !weekNumber.isEmpty()) {
                        // Add "Week <number>" to the ComboBox
                        weekComboBox.getItems().add("Week " + weekNumber);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Helper method to get the week from a date
    private String getWeekFromDate(String date) {
        // Simplistic approach: You can refine this based on your actual week logic
        return "Week of " + date;
    }







    // Gérer la génération du PDF de la carte étudiant
    @FXML
    private void handleGenerateStudentCardPdf() {
        String selectedStudent = studentComboBox.getValue();
        if (selectedStudent == null) {
            showAlert("Veuillez sélectionner un étudiant avant de générer le PDF.");
            return;
        }

        try {
            // Diviser le nom complet en nom et prénom
            String[] parts = selectedStudent.split(" ", 2);
            String nom = parts[0];
            String prenom = (parts.length > 1) ? parts[1] : "";

            // Créer un nom de fichier basé sur le nom et prénom
            String outputFileName = nom + "_" + prenom + "_student_card.pdf";

            // Filtrer les données XML pour l'étudiant sélectionné
            String filteredXmlPath = filterStudentXml("src/main/resources/xml/students.xml", selectedStudent);

            // Générer le PDF avec le nom dynamique
            generatePdf(filteredXmlPath, "src/main/resources/xml/student_card.xslt", outputFileName);

            showAlert("PDF Carte Étudiant généré avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur lors de la génération du PDF : " + e.getMessage());
        }
    }


    // Méthode pour filtrer les données XML pour l'étudiant sélectionné
    private String filterStudentXml(String originalXmlPath, String fullName) throws Exception {
        String tempXmlPath = getDownloadsFolderPath() + File.separator + "filtered_releve_note.xml";

        // Charger le fichier XML original
        File xmlFile = new File(originalXmlPath);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlFile);
        document.getDocumentElement().normalize();

        // Créer un nouveau document XML pour l'étudiant filtré
        Document filteredDocument = builder.newDocument();
        Element rootElement = filteredDocument.createElement("releveNotes");
        filteredDocument.appendChild(rootElement);

        // Diviser le nom complet en nom et prénom
        String[] parts = fullName.split(" ", 2);
        String selectedNom = parts[0].trim();
        String selectedPrenom = (parts.length > 1) ? parts[1].trim() : "";

        // Rechercher l'étudiant dans le fichier XML
        NodeList etudiantNodes = document.getElementsByTagName("etudiant");
        NodeList modulesNodes = document.getElementsByTagName("modules");
        boolean found = false;

        for (int i = 0; i < etudiantNodes.getLength(); i++) {
            Node etudiantNode = etudiantNodes.item(i);

            if (etudiantNode.getNodeType() == Node.ELEMENT_NODE) {
                Element etudiantElement = (Element) etudiantNode;

                String nom = etudiantElement.getElementsByTagName("nom").item(0).getTextContent().trim();
                String prenom = etudiantElement.getElementsByTagName("prenom").item(0).getTextContent().trim();

                if (selectedNom.equalsIgnoreCase(nom) && selectedPrenom.equalsIgnoreCase(prenom)) {
                    // Importer l'étudiant dans le document filtré
                    Node importedEtudiant = filteredDocument.importNode(etudiantElement, true);
                    rootElement.appendChild(importedEtudiant);

                    // Ajouter les modules correspondants
                    if (i < modulesNodes.getLength()) {
                        Node modulesNode = modulesNodes.item(i);
                        Node importedModules = filteredDocument.importNode(modulesNode, true);
                        rootElement.appendChild(importedModules);
                    }

                    found = true;
                    break;
                }
            }
        }

        if (!found) {
            throw new Exception("Étudiant non trouvé dans le fichier XML.");
        }

        // Écrire le nouveau document XML dans un fichier temporaire
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(filteredDocument);
        StreamResult result = new StreamResult(new File(tempXmlPath));
        transformer.transform(source, result);

        return tempXmlPath;
    }







    @FXML
    private void handleGenerateTimetablePdf() {
        String selectedWeek = weekComboBox.getValue();
        if (selectedWeek == null) {
            showAlert("Veuillez sélectionner une semaine avant de générer le PDF.");
            return;
        }

        try {
            // Filter XML for the selected week
            String filteredXmlPath = filterTimetableXml("src/main/resources/xml/emploi.xml", selectedWeek);

            // Generate PDF for the timetable
            String outputFileName = "Timetable_" + selectedWeek.replaceAll("[^a-zA-Z0-9_]", "_") + ".pdf";
            generatePdf(filteredXmlPath, "src/main/resources/xml/emploi.xslt", outputFileName);

            showAlert("PDF Emploi du Temps généré avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur lors de la génération du PDF : " + e.getMessage());
        }
    }


    private String filterTimetableXml(String originalXmlPath, String selectedWeek) throws Exception {
        String tempXmlPath = getDownloadsFolderPath() + File.separator + "filtered_timetable.xml";

        // Extract the week number from the selectedWeek string (e.g., "Week 1" -> "1")
        String weekNumber = selectedWeek.split(" ")[1];

        // Load the original XML
        File xmlFile = new File(originalXmlPath);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlFile);
        document.getDocumentElement().normalize();

        // Create a new filtered XML document
        Document filteredDocument = builder.newDocument();
        Element rootElement = filteredDocument.createElement("schedule");
        filteredDocument.appendChild(rootElement);

        // Find the matching <week> element
        NodeList weekNodes = document.getElementsByTagName("week");
        for (int i = 0; i < weekNodes.getLength(); i++) {
            Node weekNode = weekNodes.item(i);
            if (weekNode != null && weekNode.getNodeType() == Node.ELEMENT_NODE) {
                Element weekElement = (Element) weekNode;
                if (weekElement.getAttribute("number").equals(weekNumber)) {
                    // Import the matching week into the new document
                    Node importedNode = filteredDocument.importNode(weekElement, true);
                    rootElement.appendChild(importedNode);
                    break;
                }
            }
        }

        // Write the filtered document to a temporary file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(filteredDocument);
        StreamResult result = new StreamResult(new File(tempXmlPath));
        transformer.transform(source, result);

        return tempXmlPath;
    }






//    releve de note
@FXML
private void handleGenerateRelevePdf() {
    String selectedStudent = studentComboBox.getValue();
    if (selectedStudent == null) {
        showAlert("Veuillez sélectionner un étudiant avant de générer le relevé de notes.");
        return;
    }

    try {
        String filteredXmlPath = filterStudentXml("src/main/resources/xml/releve-note.xml", selectedStudent);
        String outputFileName = selectedStudent.replaceAll(" ", "_") + "_releve_notes.pdf";
        generatePdf(filteredXmlPath, "src/main/resources/xml/releve-note.xslt", outputFileName);
        showAlert("PDF Relevé de Notes généré avec succès !");
    } catch (Exception e) {
        e.printStackTrace();
        showAlert("Erreur lors de la génération du PDF : " + e.getMessage());
    }
}








    // Méthode pour générer le PDF
    private void generatePdf(String xmlPath, String xsltPath, String outputFileName) {
        try {
            // Obtenir le chemin du dossier Téléchargements
            String downloadsFolder = getDownloadsFolderPath();
            String outputPdfPath = downloadsFolder + File.separator + outputFileName;

            // Transformer XML to XSL-FO
            Source xmlSource = new StreamSource(new File(xmlPath));
            Source xsltSource = new StreamSource(new File(xsltPath));

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer(xsltSource);

            File xslFoFile = new File(downloadsFolder + File.separator + "intermediate.fo");
            try (OutputStream foOutputStream = new FileOutputStream(xslFoFile)) {
                transformer.transform(xmlSource, new StreamResult(foOutputStream));
            }

            // Render XSL-FO to PDF using Apache FOP
            File pdfFile = new File(outputPdfPath);
            try (OutputStream pdfOutputStream = new FileOutputStream(pdfFile)) {
                FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
                FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

                Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, pdfOutputStream);
                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer pdfTransformer = tf.newTransformer();

                try (InputStream foInputStream = new FileInputStream(xslFoFile)) {
                    Source foSource = new StreamSource(foInputStream);
                    pdfTransformer.transform(foSource, new SAXResult(fop.getDefaultHandler()));
                }
            }

            showAlert("PDF généré avec succès dans le dossier Téléchargements !");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur lors de la génération du PDF : " + e.getMessage());
        }
    }

    // Méthode pour afficher des alertes
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
