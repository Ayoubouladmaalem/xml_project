package org.ecole.application_scolaire.Dashboard;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

import static org.apache.xmlbeans.impl.common.DocumentHelper.createDocument;

public class AffichageController {

    @FXML
    private ComboBox<String> moduleComboBox;

    @FXML
    private TableView<StudentRow> resultTableView;

    @FXML
    private TableColumn<StudentRow, String> codeApogeeColumn;
    @FXML
    private TableColumn<StudentRow, String> studentNameColumn;

    @FXML
    private Label statusLabel;

    private final Map<String, String> studentsMap = new HashMap<>();
    private final Map<String, List<SubmoduleDefinition>> modulesMap = new HashMap<>();
    private final Map<String, Map<String, String>> gradesMap = new HashMap<>();
    private String lastSelectedModule = null;
    private List<SubmoduleDefinition> lastSelectedSubDefs = null;

    static class SubmoduleDefinition {
        private final String code;
        private final String intitule;

        public SubmoduleDefinition(String code, String intitule) {
            this.code = code;
            this.intitule = intitule;
        }

        public String getCode() {
            return code;
        }

        public String getIntitule() {
            return intitule;
        }
    }

    public static class StudentRow {
        private final String codeApogee;
        private final String studentName;
        private final Map<String, String> submoduleGrades = new HashMap<>();

        public StudentRow(String codeApogee, String studentName) {
            this.codeApogee = codeApogee;
            this.studentName = studentName;
        }

        public String getCodeApogee() {
            return codeApogee;
        }

        public String getStudentName() {
            return studentName;
        }

        public Map<String, String> getSubmoduleGrades() {
            return submoduleGrades;
        }

        public double computeAverage(List<SubmoduleDefinition> subDefs) {
            double sum = 0;
            int count = 0;
            for (SubmoduleDefinition sd : subDefs) {
                String gradeStr = submoduleGrades.get(sd.getCode());
                if (gradeStr == null || gradeStr.isEmpty()) continue;
                try {
                    double g = Double.parseDouble(gradeStr);
                    sum += g;
                    count++;
                } catch (NumberFormatException ignored) {
                }
            }
            return (count > 0) ? sum / count : 0.0;
        }
    }

    @FXML
    public void initialize() {
        ObservableList<String> moduleTitles = FXCollections.observableArrayList();
        try {
            File modulesFile = new File("src/main/resources/xml/module/module.xml");
            if (!modulesFile.exists()) {
                statusLabel.setText("module.xml introuvable");
                return;
            }

            Document doc = parseXml(modulesFile);
            NodeList mList = doc.getElementsByTagName("module");
            for (int i = 0; i < mList.getLength(); i++) {
                Element moduleElement = (Element) mList.item(i);
                String moduleTitle = moduleElement.getAttribute("intitule");
                NodeList subList = moduleElement.getElementsByTagName("submodule");

                List<SubmoduleDefinition> subDefs = new ArrayList<>();
                for (int j = 0; j < subList.getLength(); j++) {
                    Element sEl = (Element) subList.item(j);
                    String subCode = sEl.getAttribute("code");
                    String subIntitule = sEl.getAttribute("intitule");
                    subDefs.add(new SubmoduleDefinition(subCode, subIntitule));
                }
                modulesMap.put(moduleTitle, subDefs);
                moduleTitles.add(moduleTitle);
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Erreur lors du chargement des modules : " + e.getMessage());
            return;
        }

        moduleComboBox.setItems(moduleTitles);

        codeApogeeColumn.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getCodeApogee())
        );
        studentNameColumn.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getStudentName())
        );

        loadStudents();
        loadGrades();

        statusLabel.setText("Données chargées avec succès.");
    }

    private void loadStudents() {
        try {
            File studentsFile = new File("src/main/resources/xml/students.xml");
            if (!studentsFile.exists()) {
                statusLabel.setText("students.xml introuvable");
                return;
            }
            Document doc = parseXml(studentsFile);
            NodeList sList = doc.getElementsByTagName("student");
            for (int i = 0; i < sList.getLength(); i++) {
                Element e = (Element) sList.item(i);
                String apogee = e.getElementsByTagName("code_apogee").item(0).getTextContent();
                String nom = e.getElementsByTagName("nom").item(0).getTextContent();
                String prenom = e.getElementsByTagName("prenom").item(0).getTextContent();
                studentsMap.put(apogee, nom + " " + prenom);
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Erreur lors du chargement des étudiants : " + e.getMessage());
        }
    }

    private void loadGrades() {
        try {
            File gradesFile = new File("src/main/resources/xml/grades/grades.xml");
            if (!gradesFile.exists()) {
                statusLabel.setText("grades.xml introuvable");
                return;
            }
            Document doc = parseXml(gradesFile);
            NodeList gradeStudList = doc.getElementsByTagName("student");
            for (int i = 0; i < gradeStudList.getLength(); i++) {
                Element stEl = (Element) gradeStudList.item(i);
                String apog = stEl.getAttribute("code_apogee");
                Map<String, String> subGrMap = new HashMap<>();
                NodeList gList = stEl.getElementsByTagName("grade");
                for (int g = 0; g < gList.getLength(); g++) {
                    Element ge = (Element) gList.item(g);
                    String subcode = ge.getAttribute("submodule");
                    String gradeVal = ge.getTextContent();
                    subGrMap.put(subcode, gradeVal);
                }
                gradesMap.put(apog, subGrMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Erreur lors du chargement des notes : " + e.getMessage());
        }
    }

    @FXML
    public void handleGenerateButton() {
        String selModuleTitle = moduleComboBox.getValue();
        if (selModuleTitle == null) {
            statusLabel.setText("Veuillez sélectionner un module.");
            return;
        }

        List<SubmoduleDefinition> subDefs = modulesMap.get(selModuleTitle);
        if (subDefs == null || subDefs.isEmpty()) {
            statusLabel.setText("Aucune donnée pour le module : " + selModuleTitle);
            return;
        }

        clearDynamicColumns();

        for (SubmoduleDefinition sd : subDefs) {
            TableColumn<StudentRow, String> col = new TableColumn<>(sd.getIntitule());
            col.setPrefWidth(150);
            col.setCellValueFactory(data -> {
                StudentRow row = data.getValue();
                String grade = row.getSubmoduleGrades().getOrDefault(sd.getCode(), "N/A");
                return new ReadOnlyStringWrapper(grade);
            });
            resultTableView.getColumns().add(col);
        }

        TableColumn<StudentRow, String> resultCol = new TableColumn<>("Résultat");
        resultCol.setPrefWidth(80);
        resultCol.setCellValueFactory(data -> {
            StudentRow row = data.getValue();
            double avg = row.computeAverage(subDefs);
            return new ReadOnlyStringWrapper(String.format("%.2f", avg));
        });
        resultTableView.getColumns().add(resultCol);

        ObservableList<StudentRow> rows = buildStudentRows(subDefs);
        resultTableView.setItems(rows);

        lastSelectedModule = selModuleTitle;
        lastSelectedSubDefs = subDefs;

        statusLabel.setText("Affichage généré pour : " + selModuleTitle);
    }

    private void clearDynamicColumns() {
        while (resultTableView.getColumns().size() > 2) {
            resultTableView.getColumns().remove(2);
        }
    }

    private ObservableList<StudentRow> buildStudentRows(List<SubmoduleDefinition> subDefs) {
        ObservableList<StudentRow> list = FXCollections.observableArrayList();

        for (String codeApogee : gradesMap.keySet()) {
            Map<String, String> studGrades = gradesMap.get(codeApogee);

            boolean hasSomething = false;
            for (SubmoduleDefinition sd : subDefs) {
                if (studGrades.containsKey(sd.getCode())) {
                    hasSomething = true;
                    break;
                }
            }
            if (!hasSomething) continue;

            String fullName = studentsMap.getOrDefault(codeApogee, "Inconnu");
            StudentRow row = new StudentRow(codeApogee, fullName);

            for (SubmoduleDefinition sd : subDefs) {
                String gradeVal = studGrades.getOrDefault(sd.getCode(), "N/A");
                row.getSubmoduleGrades().put(sd.getCode(), gradeVal);
            }
            list.add(row);
        }

        return list;
    }

    private Document parseXml(File f) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(f);
    }

    @FXML
    public void handleExportHtmlButton() {
        // must have a selected module
        if (lastSelectedModule == null || lastSelectedSubDefs == null) {
            statusLabel.setText("Veuillez d'abord sélectionner un module et cliquer Générer.");
            return;
        }
        // get the table data
        ObservableList<StudentRow> rows = resultTableView.getItems();
        if (rows == null || rows.isEmpty()) {
            statusLabel.setText("Table vide, rien à exporter.");
            return;
        }

        try {
            // Step 1) build module.xml
            Document doc = createDocument();
            Element root = doc.createElement("moduleResult");
            root.setAttribute("moduleCode", lastSelectedModule);

            // Add module name
            String moduleName = lastSelectedModule; // You can retrieve a more detailed name if needed
            root.setAttribute("moduleName", moduleName); // Add an attribute for the module name
            doc.appendChild(root);

            // Fill from the table data
            for (StudentRow row : rows) {
                Element stuElt = doc.createElement("student");
                stuElt.setAttribute("codeApogee", row.getCodeApogee());

                String[] parts = row.getStudentName().split(" ", 2);
                String nomStr = (parts.length > 0) ? parts[0] : "";
                String prenomStr = (parts.length > 1) ? parts[1] : "";
                Element nomElt = doc.createElement("nom");
                nomElt.setTextContent(nomStr);
                stuElt.appendChild(nomElt);
                Element prenomElt = doc.createElement("prenom");
                prenomElt.setTextContent(prenomStr);
                stuElt.appendChild(prenomElt);

                Element naisElt = doc.createElement("naissance");
                naisElt.setTextContent("??/??/????");
                stuElt.appendChild(naisElt);

                for (SubmoduleDefinition sd : lastSelectedSubDefs) {
                    String gradeVal = row.getSubmoduleGrades().get(sd.getCode());
                    if ("N/A".equals(gradeVal)) continue;
                    Element subm = doc.createElement("submodule");
                    subm.setAttribute("code", sd.getCode());
                    subm.setAttribute("intitule", sd.getIntitule());
                    subm.setAttribute("grade", gradeVal);
                    subm.setAttribute("bareme", "20");
                    stuElt.appendChild(subm);
                }
                root.appendChild(stuElt);
            }

            // Write the doc to "module.xml"
            File xmlOut = new File("src/main/resources/xml/module/module.xml");
            writeXml(doc, xmlOut);

            // Step 2) transform with moduleResult.xslt => moduleResult.html
            File xsltFile = new File("src/main/resources/xml/module/moduleResult.xslt");
            if (!xsltFile.exists()) {
                statusLabel.setText("XSLT introuvable: " + xsltFile.getAbsolutePath());
                return;
            }

            // Determine the user's Downloads directory
            String userHome = System.getProperty("user.home");
            File downloadsDir = new File(userHome, "Downloads");
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs(); // Create the Downloads directory if it doesn't exist
            }

            // Create the HTML file with a name based on the selected module
            String htmlFileName = moduleName + ".html";
            File htmlOut = new File(downloadsDir, htmlFileName);

            // Transform XML to HTML
            transformXmlToHtml(xmlOut, xsltFile, htmlOut);

            statusLabel.setText("Export HTML réussi : " + htmlOut.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Erreur export : " + e.getMessage());
        }
    }

    private void writeXml(Document doc, File f) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource src = new DOMSource(doc);
        StreamResult res = new StreamResult(new FileOutputStream(f));
        t.transform(src, res);
    }

    private void transformXmlToHtml(File xmlFile, File xslFile, File htmlFile) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer(new StreamSource(xslFile));
        transformer.transform(new StreamSource(xmlFile), new StreamResult(htmlFile));
    }
}
