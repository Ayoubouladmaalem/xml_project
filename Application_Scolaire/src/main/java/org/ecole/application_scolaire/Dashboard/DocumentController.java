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
            File xmlFile = new File("src/main/resources/xml/emploi/emploi.xml");
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
            // Normaliser le nom pour éviter les erreurs de comparaison
            String fullName = selectedStudent.replaceAll("\\s+", " ").trim();
            String outputFileName = fullName.replaceAll("\\s+", "_") + "_student_card.pdf";

            // 1) Filtrer le XML pour ne garder que l'étudiant sélectionné
            String filteredXmlPath = filterStudentXml("src/main/resources/xml/students.xml", fullName);

            // 2) Générer le PDF avec XSLT-FO
            generatePdf(filteredXmlPath, "src/main/resources/xml/student_card/student_card.xslt", outputFileName);

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
        fullName = fullName.replaceAll("\\s+", " ").trim(); // Normaliser les espaces

        for (int i = 0; i < studentNodes.getLength(); i++) {
            Node node = studentNodes.item(i);
            if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                Element studentElement = (Element) node;
                String nom = studentElement.getElementsByTagName("nom").item(0).getTextContent().trim();
                String prenom = studentElement.getElementsByTagName("prenom").item(0).getTextContent().trim();

                String xmlFullName = (nom + " " + prenom).replaceAll("\\s+", " ").trim(); // Normaliser les espaces
                if (fullName.equalsIgnoreCase(xmlFullName)) {
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
            String filteredXmlPath = filterTimetableXml("src/main/resources/xml/emploi/emploi.xml", selectedWeek);
            String outputFileName = "Timetable_" + selectedWeek.replaceAll("[^a-zA-Z0-9_]", "_") + ".pdf";
            generatePdf(filteredXmlPath, "src/main/resources/xml/emploi/emploi.xslt", outputFileName);

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
            // Normaliser les espaces dans le nom complet
            String fullName = selectedStudent.replaceAll("\\s+", " ").trim();
            String outputFileName = "Attestation_" + fullName.replaceAll("\\s+", "_") + ".pdf";

            // 1) Filtrer le XML pour ne garder que l'étudiant sélectionné
            String filteredXmlPath = filterStudentXmlForAttestation("src/main/resources/xml/students.xml", fullName);

            // 2) Générer l'attestation en PDF
            generatePdf(filteredXmlPath, "src/main/resources/xml/attestation/attestation-students-fo.xsl", outputFileName);

            showAlert("Attestation PDF générée avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur lors de la génération de l'attestation : " + e.getMessage());
        }
    }


    private String filterStudentXmlForAttestation(String originalXmlPath, String fullName) throws Exception {
        String tempXmlPath = getDownloadsFolderPath() + File.separator + "filtered_attestation.xml";

        File xmlFile = new File(originalXmlPath);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlFile);
        document.getDocumentElement().normalize();

        Document filteredDoc = builder.newDocument();
        Element root = filteredDoc.createElement("students");
        filteredDoc.appendChild(root);

        fullName = fullName.replaceAll("\\s+", " ").trim(); // Normaliser les espaces

        NodeList studentList = document.getElementsByTagName("student");
        for (int i = 0; i < studentList.getLength(); i++) {
            Node n = studentList.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element studentEl = (Element) n;
                String nomEl = studentEl.getElementsByTagName("nom").item(0).getTextContent().trim();
                String prenomEl = studentEl.getElementsByTagName("prenom").item(0).getTextContent().trim();

                String xmlFullName = (nomEl + " " + prenomEl).replaceAll("\\s+", " ").trim();
                if (fullName.equalsIgnoreCase(xmlFullName)) {
                    Node imported = filteredDoc.importNode(studentEl, true);
                    root.appendChild(imported);
                    break;
                }
            }
        }

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

        File xmlFile = new File("src/main/resources/" + gradesXmlPath);
        if (!xmlFile.exists()) {
            showAlert("Erreur : Le fichier grades.xml est introuvable !");
            return null;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlFile);
        document.getDocumentElement().normalize();

        Document filteredDocument = builder.newDocument();
        Element rootElement = filteredDocument.createElement("grades");
        filteredDocument.appendChild(rootElement);

        codeApogee = codeApogee.replaceAll("\\s+", "").trim();

        NodeList studentNodes = document.getElementsByTagName("student");
        for (int i = 0; i < studentNodes.getLength(); i++) {
            Node node = studentNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element studentElement = (Element) node;

                String studentCodeApogee = studentElement.getAttribute("code_apogee").trim().replaceAll("\\s+", "");
                if (studentCodeApogee.equals(codeApogee)) {
                    Node importedNode = filteredDocument.importNode(studentElement, true);
                    rootElement.appendChild(importedNode);
                    break;
                }
            }
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
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
            File xmlFile = new File("src/main/resources/xml/students.xml");
            if (!xmlFile.exists()) {
                showAlert("Erreur : Le fichier students.xml est introuvable !");
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            String selectedFullName = selectedStudent.replaceAll("\\s+", " ").trim();
            NodeList studentNodes = document.getElementsByTagName("student");
            String codeApogee = null;

            for (int i = 0; i < studentNodes.getLength(); i++) {
                Node node = studentNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element studentElement = (Element) node;

                    String studentNom = studentElement.getElementsByTagName("nom").item(0).getTextContent().trim();
                    String studentPrenom = studentElement.getElementsByTagName("prenom").item(0).getTextContent().trim();
                    String xmlFullName = (studentNom + " " + studentPrenom).replaceAll("\\s+", " ").trim();

                    if (selectedFullName.equalsIgnoreCase(xmlFullName)) {
                        codeApogee = studentElement.getElementsByTagName("code_apogee").item(0).getTextContent().trim();
                        break;
                    }
                }
            }

            if (codeApogee == null) {
                showAlert("Code Apogée introuvable.");
                return;
            }

            String filteredGradesPath = filterGradesXml("xml/grades/grades.xml", codeApogee);
            generatePdf(filteredGradesPath, "src/main/resources/xml/grades/grades_report.xslt", selectedFullName.replaceAll("\\s+", "_") + "_grades.pdf");

            showAlert("Relevé de notes généré avec succès !");
        } catch (Exception e) {
            showAlert("Erreur : " + e.getMessage());
        }
    }
}