package org.ecole.application_scolaire.Dashboard;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller class for ConversionView.fxml
 */
public class ConversionController {

    @FXML
    private Text contentText;

    @FXML
    private Button convertAndViewButton;

    @FXML
    private Text textBeforeUpload;

    @FXML
    private Button uploadButton;

    @FXML
    private Button viewXmlButton;

    @FXML
    private Label statusLabel;

    // The Excel file chosen by the user
    private File uploadedExcelFile;

    // We'll use one DataFormatter + FormulaEvaluator for the entire workbook
    private DataFormatter dataFormatter = new DataFormatter();
    private FormulaEvaluator formulaEvaluator;

    /**
     * A helper class to store module/submodule definitions.
     */
    private static class ModuleDefinition {
        String moduleCode;
        String moduleName;
        String[][] submodules; // Each entry: [ subCode, subName ]

        public ModuleDefinition(String code, String name, String[][] subs) {
            this.moduleCode = code;
            this.moduleName = name;
            this.submodules = subs;
        }
    }

    /**
     * A static list that defines all modules and submodules (GINF31, GINF32, etc.).
     * Adjust to match your Excel columns from index=7 onward.
     */
    private static final List<ModuleDefinition> MODULES = new ArrayList<>();

    static {
        MODULES.add(new ModuleDefinition(
                "GINF31", "Programation Orientée Objet et XML",
                new String[][]{
                        {"GINF31-S1", "Programation Orientée Objet : Java"},
                        {"GINF31-S2", "XML"}
                }
        ));
        MODULES.add(new ModuleDefinition(
                "GINF32", "Qualité et approche processus",
                new String[][]{
                        {"GINF32-S1", "Assurance contrôle qualité (ISO 9001)"},
                        {"GINF32-S2", "Cycle de Vie Logiciel et Méthodes agiles"},
                        {"GINF32-S3", "Maitrise et optimisation des processus"}
                }
        ));
        MODULES.add(new ModuleDefinition(
                "GINF33", "Modelisation orientée objet et IHM",
                new String[][]{
                        {"GINF33-S1", "Modelisation orientée objet UML"},
                        {"GINF33-S2", "IHM"},
                        {"GINF33-S3", "Cycle de Vie logiciel"}
                }
        ));
        MODULES.add(new ModuleDefinition(
                "GINF34", "Bases de données avancées 1",
                new String[][]{
                        {"GINF34-S1", "Optimisation et Qualité de Base de données"},
                        {"GINF34-S2", "Administration et Sécurité des Bases de données"},
                        {"GINF34-S3", "Base de données NoSQL"}
                }
        ));
        MODULES.add(new ModuleDefinition(
                "GINF35", "Administration et programmation système",
                new String[][]{
                        {"GINF35-S1", "Administration systèmes"},
                        {"GINF35-S2", "Programmation systèmes"}
                }
        ));
        MODULES.add(new ModuleDefinition(
                "GINF36", "Langue et communication",
                new String[][]{
                        {"GINF36-S1", "Espagnol 2 et Allemand"},
                        {"GINF36-S2", "Anglais professionnel"},
                        {"GINF36-S3", "Technique de communication"}
                }
        ));
        MODULES.add(new ModuleDefinition(
                "GINF41", "Technologies distribuées",
                new String[][]{
                        {"GINF41-S1", "Introduction à J2EE"},
                        {"GINF41-S2", "Programmation en C#"},
                        {"GINF41-S3", "Gestion des données complexes"},
                        {"GINF41-S4", "Gestion des données distribuées"}
                }
        ));
        MODULES.add(new ModuleDefinition(
                "GINF42", "Bases de données avancées 2 et Cloud",
                new String[][]{
                        {"GINF42-S1", "Gestion des données complexes"},
                        {"GINF42-S2", "Gestion des données distribuées"},
                        {"GINF42-S3", "Cloud Computing et Infogérance"}
                }
        ));
        MODULES.add(new ModuleDefinition(
                "GINF43", "Traitement de l'image",
                new String[][]{
                        {"GINF43-S1", "Traitement de l'image"},
                        {"GINF43-S2", "Vision numérique"},
                        {"GINF43-S3", "Processus stochastique"}
                }
        ));
        MODULES.add(new ModuleDefinition(
                "GINF44", "Programmat° déclarative & Techniques algo avancées",
                new String[][]{
                        {"GINF44-S1", "Programmation déclarative"},
                        {"GINF44-S2", "Technique algorithmique avancée"}
                }
        ));
        MODULES.add(new ModuleDefinition(
                "GINF45", "Sécurité et cryptographie",
                new String[][]{
                        {"GINF45-S1", "Sécurité des systèmes"},
                        {"GINF45-S2", "Cryptographie"}
                }
        ));
        MODULES.add(new ModuleDefinition(
                "GINF46", "Management de l'entreprise 2",
                new String[][]{
                        {"GINF46-S1", "Economie 2 et comptabilité 2"},
                        {"GINF46-S2", "Projet collectif et stage"},
                        {"GINF46-S3", "Management de Projet"}
                }
        ));
    }

    /**
     * Called by the FXML loader after the components have been injected.
     */
    @FXML
    public void initialize() {
        statusLabel.setText("Status: En attente pour upload...");
    }

    /**
     * The "Upload" button action: open a FileChooser to select an Excel file.
     */
    @FXML
    public void handleUploadButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner un fichier Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx"));

        uploadedExcelFile = fileChooser.showOpenDialog(new Stage());
        if (uploadedExcelFile != null) {
            statusLabel.setText("Status: Fichier Excel téléchargé: " + uploadedExcelFile.getName());
        } else {
            statusLabel.setText("Status: Aucun fichier sélectionné.");
        }
    }

    /**
     * The "Convert & View" button action: parse the uploaded Excel and generate the 3 XML files.
     */
    @FXML
    public void handleConvertAndViewButton() {
        if (uploadedExcelFile == null) {
            statusLabel.setText("Status: Veuillez d'abord uploader un fichier Excel.");
            return;
        }

        try (FileInputStream fis = new FileInputStream(uploadedExcelFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            // We will use one formula evaluator for the entire workbook
            formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

            // We assume the data we need is on the first sheet (index 0)
            // If your data is on a different sheet, adjust accordingly:
            Sheet sheet = workbook.getSheetAt(0);

            // Create the "xml" directory inside "resources" if it doesn't exist
            File resourcesDir = new File("src/main/resources/xml");
            if (!resourcesDir.exists()) {
                resourcesDir.mkdirs(); // Create the directory and any necessary parent directories
            }

            // 1) Generate students.xml
            generateStudentsXml(sheet);

            // 2) Generate module.xml
            generateModulesXml();

            // 3) Generate grades.xml
            generateGradesXml(sheet);

            statusLabel.setText("Status: Conversion réussie! 3 fichiers XML générés dans le dossier 'resources/xml'.");

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Status: Erreur lors de la conversion: " + e.getMessage());
        }
    }

    /**
     * The "View XML" button action (placeholder).
     * You might open the generated XML in a browser or a text editor.
     */
    @FXML
    public void handleViewXmlButton() {
        // Check if the XML files exist in the resources/xml folder
        File studentsXml = new File("src/main/resources/xml/students.xml");
        File modulesXml = new File("src/main/resources/xml/module/module.xml");
        File gradesXml = new File("src/main/resources/xml/grades/grades.xml");

        if (!studentsXml.exists() || !modulesXml.exists() || !gradesXml.exists()) {
            statusLabel.setText("Status: Les fichiers XML n'existent pas. Veuillez d'abord convertir un fichier Excel.");
            return;
        }

        // Open the XML files in the default web browser or text editor
        try {
            // Open students.xml
            openFileInDefaultApplication(studentsXml);

            // Open module.xml
            openFileInDefaultApplication(modulesXml);

            // Open grades.xml
            openFileInDefaultApplication(gradesXml);

            statusLabel.setText("Status: Les fichiers XML ont été ouverts dans l'application par défaut.");
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Status: Erreur lors de l'ouverture des fichiers XML: " + e.getMessage());
        }
    }

    /**
     * Helper method to open a file in the default application (e.g., web browser or text editor).
     */
    private void openFileInDefaultApplication(File file) throws Exception {
        if (java.awt.Desktop.isDesktopSupported()) {
            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
            if (file.exists()) {
                desktop.open(file);
            } else {
                throw new Exception("Le fichier " + file.getName() + " n'existe pas.");
            }
        } else {
            throw new Exception("Le bureau n'est pas supporté sur cette plateforme.");
        }
    }

    // ----------------------------------------------------------------------
    // 1) Generate students.xml
    //    Reads columns 0..6 from each row:
    //      0: code_apogee
    //      1: CIN
    //      2: CNE
    //      3: Nom
    //      4: Prénom
    //      5: Lieu_Naissance
    //      6: Date_Naissance
    // ----------------------------------------------------------------------
    private void generateStudentsXml(Sheet sheet) throws Exception {
        Document doc = createDocument();
        Element root = doc.createElement("students");
        doc.appendChild(root);

        boolean skipHeader = true;
        for (Row row : sheet) {
            if (row == null) continue;
            if (skipHeader) {
                skipHeader = false; // skip the very first row (header)
                continue;
            }

            String codeApogee = getCellValueAsString(row.getCell(0));
            String cin        = getCellValueAsString(row.getCell(1));
            String cne        = getCellValueAsString(row.getCell(2));
            String nom        = getCellValueAsString(row.getCell(3));
            String prenom     = getCellValueAsString(row.getCell(4));
            String lieuNais   = getCellValueAsString(row.getCell(5));
            String dateNais   = getCellValueAsString(row.getCell(6));

            if (codeApogee.trim().isEmpty() && cin.trim().isEmpty() && cne.trim().isEmpty()) {
                // Probably an empty row
                continue;
            }

            Element studentElt = doc.createElement("student");
            root.appendChild(studentElt);

            studentElt.appendChild(createElement(doc, "code_apogee", codeApogee));
            studentElt.appendChild(createElement(doc, "CIN", cin));
            studentElt.appendChild(createElement(doc, "CNE", cne));
            studentElt.appendChild(createElement(doc, "nom", nom));
            studentElt.appendChild(createElement(doc, "prenom", prenom));
            studentElt.appendChild(createElement(doc, "lieu_naissance", lieuNais));
            studentElt.appendChild(createElement(doc, "date_naissance", dateNais));
        }

        // Write to disk in the "resources/xml" folder
        writeXmlToFile(doc, "src/main/resources/xml/students.xml");
    }

    // ----------------------------------------------------------------------
    // 2) Generate module.xml
    //    Uses the static MODULES array above
    // ----------------------------------------------------------------------
    private void generateModulesXml() throws Exception {
        Document doc = createDocument();
        Element root = doc.createElement("modules");
        doc.appendChild(root);

        for (ModuleDefinition mdef : MODULES) {
            Element moduleElt = doc.createElement("module");
            moduleElt.setAttribute("code", mdef.moduleCode);
            moduleElt.setAttribute("intitule", mdef.moduleName);
            root.appendChild(moduleElt);

            for (String[] sub : mdef.submodules) {
                String subCode = sub[0];
                String subName = sub[1];

                Element subElt = doc.createElement("submodule");
                subElt.setAttribute("code", subCode);
                subElt.setAttribute("intitule", subName);
                subElt.setAttribute("coefficient", "1"); // or set your real coefficient
                moduleElt.appendChild(subElt);
            }
        }

        writeXmlToFile(doc, "src/main/resources/xml/module/module.xml");
    }

    // ----------------------------------------------------------------------
    // 3) Generate grades.xml
    //    For each student row, read submodule columns from index=7 onward.
    //    The order must match the order in MODULES array.
    // ----------------------------------------------------------------------
    private void generateGradesXml(Sheet sheet) throws Exception {
        Document doc = createDocument();
        Element root = doc.createElement("grades");
        doc.appendChild(root);

        boolean skipHeader = true;
        for (Row row : sheet) {
            if (row == null) continue;
            if (skipHeader) {
                skipHeader = false;
                continue;
            }

            // Student's code_apogee
            String codeApogee = getCellValueAsString(row.getCell(0));
            if (codeApogee.trim().isEmpty()) {
                // empty row, skip
                continue;
            }

            // Create <student code_apogee="...">
            Element studentElt = doc.createElement("student");
            studentElt.setAttribute("code_apogee", codeApogee);
            root.appendChild(studentElt);

            int colIndex = 7; // first submodule grade is at column 7
            for (ModuleDefinition mdef : MODULES) {
                for (String[] sub : mdef.submodules) {
                    String subCode = sub[0];
                    Cell gradeCell = row.getCell(colIndex);
                    String gradeValue = getCellValueAsString(gradeCell);
                    if (gradeValue.isEmpty()) {
                        gradeValue = "10"; // ou toute autre valeur par défaut
                    }
                    // <grade submodule="XXX">gradeValue</grade>
                    Element gradeElt = doc.createElement("grade");
                    gradeElt.setAttribute("submodule", subCode);
                    gradeElt.setTextContent(gradeValue);

                    studentElt.appendChild(gradeElt);
                    colIndex++;
                }
            }
        }

        writeXmlToFile(doc, "src/main/resources/xml/grades/grades.xml");
    }

    // ----------------------------------------------------------------------
    // Utility methods
    // ----------------------------------------------------------------------
    /**
     * Create a new empty DOM Document.
     */
    private Document createDocument() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.newDocument();
    }

    /**
     * Write the DOM Document to an XML file with pretty-print.
     */
    private void writeXmlToFile(Document doc, String fileName) throws TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        // Pretty print
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(fileName));
        transformer.transform(source, result);
    }

    /**
     * Create an element with text content.
     */
    private Element createElement(Document doc, String name, String value) {
        Element elt = doc.createElement(name);
        elt.appendChild(doc.createTextNode(value));
        return elt;
    }

    /**
     * Safely get the cell's value as a String, evaluating formulas.
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "12"; // Indiquer une valeur par défaut pour repérer les erreurs
        }
        return dataFormatter.formatCellValue(cell, formulaEvaluator).trim();
    }
}