package org.ecole.application_scolaire.Dashboard;

import javafx.event.ActionEvent;
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
import java.util.List;

public class DocumentController {

    @FXML
    private ComboBox<String> studentComboBox;
    @FXML
    private ComboBox<String> weekComboBox;


    public void initialize() {
        loadStudentsFromXml();
        loadWeeksFromXml();
    }

    // =================== LOAD STUDENTS =====================
    private void loadStudentsFromXml() {
        try {
            File xmlFile = new File("src/main/resources/xml/students.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            NodeList studentNodes = document.getElementsByTagName("student");
            for (int i = 0; i < studentNodes.getLength(); i++) {
                Node node = studentNodes.item(i);
                if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                    Element studentElement = (Element) node;
                    Node nameNode = studentElement.getElementsByTagName("nom").item(0);
                    Node surnameNode = studentElement.getElementsByTagName("prenom").item(0);

                    if (nameNode != null && surnameNode != null) {
                        String nom = nameNode.getTextContent();
                        String prenom = surnameNode.getTextContent();
                        String fullName = nom + " " + prenom;
                        studentComboBox.getItems().add(fullName);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadWeeksFromXml() {
        try {
            File xmlFile = new File("src/main/resources/xml/emploi.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            NodeList weekNodes = document.getElementsByTagName("week");
            for (int i = 0; i < weekNodes.getLength(); i++) {
                Node weekNode = weekNodes.item(i);
                if (weekNode != null && weekNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element weekElement = (Element) weekNode;
                    String weekNumber = weekElement.getAttribute("number");
                    if (weekNumber != null && !weekNumber.isEmpty()) {
                        weekComboBox.getItems().add("Week " + weekNumber);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getDownloadsFolderPath() {
        String userHome = System.getProperty("user.home");
        return userHome + File.separator + "Downloads";
    }

    // =================== STUDENT CARD =====================
    @FXML
    private void handleGenerateStudentCardPdf() {
        String selectedStudent = studentComboBox.getValue();
        if (selectedStudent == null) {
            showAlert("Veuillez sélectionner un étudiant avant de générer le PDF.");
            return;
        }

        try {
            String[] parts = selectedStudent.split(" ", 2);
            String nom = parts[0];
            String prenom = (parts.length > 1) ? parts[1] : "";
            String outputFileName = nom + "_" + prenom + "_student_card.pdf";

            String filteredXmlPath = filterStudentXml("src/main/resources/xml/students.xml", selectedStudent);

            generatePdf(filteredXmlPath, "src/main/resources/xml/student_card.xslt", outputFileName);

            showAlert("PDF Carte Étudiant généré avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur lors de la génération du PDF : " + e.getMessage());
        }
    }

    private String filterStudentXml(String originalXmlPath, String fullName) throws Exception {
        String tempXmlPath = getDownloadsFolderPath() + File.separator + "filtered_student.xml";

        File xmlFile = new File(originalXmlPath);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlFile);
        document.getDocumentElement().normalize();

        Document filteredDocument = builder.newDocument();
        Element rootElement = filteredDocument.createElement("students");
        filteredDocument.appendChild(rootElement);

        NodeList studentNodes = document.getElementsByTagName("student");
        for (int i = 0; i < studentNodes.getLength(); i++) {
            Node node = studentNodes.item(i);
            if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                Element studentElement = (Element) node;
                String nom = studentElement.getElementsByTagName("nom").item(0).getTextContent().trim();
                String prenom = studentElement.getElementsByTagName("prenom").item(0).getTextContent().trim();

                String xmlFullName = nom + " " + prenom; // Reconstruisez le nom complet
                if (fullName.trim().equalsIgnoreCase(xmlFullName)) { // Comparez les noms complets
                    Node importedNode = filteredDocument.importNode(studentElement, true);
                    rootElement.appendChild(importedNode);
                    break;
                }
            }
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(filteredDocument);
        StreamResult result = new StreamResult(new File(tempXmlPath));
        transformer.transform(source, result);

        return tempXmlPath;
    }

    // =================== TIMETABLE =====================
    @FXML
    private void handleGenerateTimetablePdf() {
        String selectedWeek = weekComboBox.getValue();
        if (selectedWeek == null) {
            showAlert("Veuillez sélectionner une semaine avant de générer le PDF.");
            return;
        }

        try {
            String filteredXmlPath = filterTimetableXml("src/main/resources/xml/emploi.xml", selectedWeek);
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

        String weekNumber = selectedWeek.split(" ")[1];

        File xmlFile = new File(originalXmlPath);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlFile);
        document.getDocumentElement().normalize();

        Document filteredDocument = builder.newDocument();
        Element rootElement = filteredDocument.createElement("schedule");
        filteredDocument.appendChild(rootElement);

        NodeList weekNodes = document.getElementsByTagName("week");
        for (int i = 0; i < weekNodes.getLength(); i++) {
            Node weekNode = weekNodes.item(i);
            if (weekNode != null && weekNode.getNodeType() == Node.ELEMENT_NODE) {
                Element weekElement = (Element) weekNode;
                if (weekElement.getAttribute("number").equals(weekNumber)) {
                    Node importedNode = filteredDocument.importNode(weekElement, true);
                    rootElement.appendChild(importedNode);
                    break;
                }
            }
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(filteredDocument);
        StreamResult result = new StreamResult(new File(tempXmlPath));
        transformer.transform(source, result);

        return tempXmlPath;
    }

    // =================== PDF GENERATION =====================
    private void generatePdf(String xmlPath, String xsltPath, String outputFileName) {
        try {
            String downloadsFolder = getDownloadsFolderPath();
            String outputPdfPath = downloadsFolder + File.separator + outputFileName;

            // 1) transform XML -> FO
            Source xmlSource = new StreamSource(new File(xmlPath));
            Source xsltSource = new StreamSource(new File(xsltPath));

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer(xsltSource);

            File xslFoFile = new File(downloadsFolder + File.separator + "intermediate.fo");
            try (OutputStream foOutputStream = new FileOutputStream(xslFoFile)) {
                transformer.transform(xmlSource, new StreamResult(foOutputStream));
            }

            // 2) transform FO -> PDF
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

    // =================== ALERT UTILITY =====================
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // =================== ATTESTATION =====================
    @FXML
    public void handleGenerateAttestation(ActionEvent actionEvent) {
        String selectedStudent = studentComboBox.getValue();
        if (selectedStudent == null) {
            showAlert("Veuillez sélectionner un étudiant avant de générer l'attestation.");
            return;
        }

        try {
            // 1) Filter the main students.xml to keep ONLY the chosen student
            String filteredXmlPath = filterStudentXmlForAttestation("src/main/resources/xml/students.xml", selectedStudent);

            // 2) Build a PDF filename. For example, "Attestation_ABBAD_ABDELOUAHED.pdf"
            String outputFileName = "Attestation_" + selectedStudent.replace(" ", "_") + ".pdf";

            // 3) Use generatePdf(...) with your XSL-FO
            generatePdf(filteredXmlPath, "src/main/resources/xml/attestation-students-fo.xsl", outputFileName);

            showAlert("Attestation PDF générée avec succès!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur lors de la génération de l'attestation: " + e.getMessage());
        }
    }


    private String filterStudentXmlForAttestation(String originalXmlPath, String fullName) throws Exception {
        // We'll store the filtered file in "filtered_attestation.xml"
        String tempXmlPath = getDownloadsFolderPath() + File.separator + "filtered_attestation.xml";

        // 1) load the main students.xml
        File xmlFile = new File(originalXmlPath);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlFile);
        document.getDocumentElement().normalize();

        // 2) create a new doc with <students> root
        Document filteredDoc = builder.newDocument();
        Element root = filteredDoc.createElement("students");
        filteredDoc.appendChild(root);

        // split "ABBAD ABDELOUAHED" => [ABBAD, ABDELOUAHED]
        String[] parts = fullName.split(" ", 2);
        String selectedNom = parts[0];
        String selectedPrenom = (parts.length > 1) ? parts[1] : "";

        // 3) find the matching student in original doc
        NodeList studentList = document.getElementsByTagName("student");
        for (int i = 0; i < studentList.getLength(); i++) {
            Node n = studentList.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element studentEl = (Element) n;
                String nomEl = studentEl.getElementsByTagName("nom").item(0).getTextContent();
                String prenomEl = studentEl.getElementsByTagName("prenom").item(0).getTextContent();

                if (selectedNom.equals(nomEl) && selectedPrenom.equals(prenomEl)) {
                    // Found the correct <student>, import it
                    Node imported = filteredDoc.importNode(studentEl, true);
                    root.appendChild(imported);
                    break;
                }
            }
        }

        // 4) write out to "filtered_attestation.xml"
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        DOMSource source = new DOMSource(filteredDoc);
        StreamResult result = new StreamResult(new File(tempXmlPath));
        t.transform(source, result);

        return tempXmlPath;
    }



    // =================== Releve =====================

    private String filterGradesXml(String gradesXmlPath, String codeApogee) throws Exception {
        String tempXmlPath = getDownloadsFolderPath() + File.separator + "filtered_grades.xml";

        // Charger le fichier XML des notes
        File xmlFile = new File(gradesXmlPath);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlFile);

        // Normaliser le document XML
        document.getDocumentElement().normalize();

        // Créer un nouveau document XML pour les notes filtrées
        Document filteredDocument = builder.newDocument();
        Element rootElement = filteredDocument.createElement("grades");
        filteredDocument.appendChild(rootElement);

        // Filtrer les notes de l'étudiant correspondant au code_apogee
        NodeList studentNodes = document.getElementsByTagName("student");
        for (int i = 0; i < studentNodes.getLength(); i++) {
            Node node = studentNodes.item(i);
            if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                Element studentElement = (Element) node;

                if (studentElement.getAttribute("code_apogee").equals(codeApogee)) {
                    Node importedNode = filteredDocument.importNode(studentElement, true);
                    rootElement.appendChild(importedNode);
                    break;
                }
            }
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
    private void handleGenerateGradesPdf() {
        String selectedStudent = studentComboBox.getValue();
        if (selectedStudent == null) {
            showAlert("Veuillez sélectionner un étudiant avant de générer le relevé de notes.");
            return;
        }


        try {
            // Diviser le nom complet en nom et prénom
            String[] parts = selectedStudent.split(" ", 2);
            String nom = parts[0];
            String prenom = (parts.length > 1) ? parts[1] : "";

            // Charger le fichier students.xml pour récupérer le code_apogee
            File xmlFile = new File("src/main/resources/xml/students.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            // Recherche directe du Code Apogée basé sur le nom complet
            NodeList studentNodes = document.getElementsByTagName("student");
            String codeApogee = null;
            for (int i = 0; i < studentNodes.getLength(); i++) {
                Node node = studentNodes.item(i);
                if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                    Element studentElement = (Element) node;

                    String studentNom = studentElement.getElementsByTagName("nom").item(0).getTextContent().trim();
                    String studentPrenom = studentElement.getElementsByTagName("prenom").item(0).getTextContent().trim();

                    String xmlFullName = studentNom + " " + studentPrenom;
                    if (selectedStudent.trim().equalsIgnoreCase(xmlFullName)) {
                        codeApogee = studentElement.getElementsByTagName("code_apogee").item(0).getTextContent();
                        break;
                    }
                }
            }

            if (codeApogee == null) {
                showAlert("Code Apogée introuvable pour l'étudiant sélectionné.");
                return;
            }

            // Filtrer les données des notes pour l'étudiant
            String filteredGradesPath = filterGradesXml("src/main/resources/xml/grades.xml", codeApogee);

            // Générer le PDF
            String outputFileName = nom + "_" + prenom + "_grades.pdf";
            generatePdf(filteredGradesPath, "src/main/resources/xml/grades_report.xslt", outputFileName);

            showAlert("Relevé de notes généré avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur lors de la génération du relevé de notes : " + e.getMessage());
        }
    }
}