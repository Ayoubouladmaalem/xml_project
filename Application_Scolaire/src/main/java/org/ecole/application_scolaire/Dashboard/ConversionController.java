package org.ecole.application_scolaire.Dashboard;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;

public class ConversionController {

    @FXML private Text contentText;
    @FXML private Button uploadButton;
    @FXML private Button viewXmlButton; // Button to view XML file
    @FXML private Label statusLabel;
    @FXML
    private Button convertAndViewButton;

    private File xmlFile; // Store reference to the generated XML file

    @FXML
    public void initialize() {
        contentText.setText("Convertir le fichier excel a Students_GInf2.xml");
        viewXmlButton.setDisable(true); // Disable button until an XML file is generated
    }

    @FXML
    private void handleUploadButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Excel File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls"));

        // Open FileChooser dialog
        Stage stage = (Stage) uploadButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                xmlFile = new File(selectedFile.getParent(), "output.xml");
                convertExcelToXml(selectedFile, xmlFile);
                statusLabel.setText("Conversion Successful! XML saved to: " + xmlFile.getAbsolutePath());
                viewXmlButton.setDisable(false); // Enable the button to view the XML file
            } catch (Exception e) {
                statusLabel.setText("Conversion Failed: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            statusLabel.setText("No file selected.");
        }
    }

    @FXML
    private void handleViewXmlButton() {
        try {
            if (xmlFile != null && xmlFile.exists()) {
                Desktop.getDesktop().browse(xmlFile.toURI()); // Open the XML file in the default web browser
            } else {
                statusLabel.setText("XML file not found!");
            }
        } catch (Exception e) {
            statusLabel.setText("Failed to open XML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void convertExcelToXml(File excelFile, File xmlOutput) throws Exception {
        // Load Excel file
        try (FileInputStream fis = new FileInputStream(excelFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Use the first sheet
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator(); // Create formula evaluator

            // Create XML document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document doc = builder.newDocument();

            // Create root element
            org.w3c.dom.Element rootElement = doc.createElement("Workbook");
            doc.appendChild(rootElement);

            // Iterate through rows and cells
            for (Row row : sheet) {
                org.w3c.dom.Element rowElement = doc.createElement("Row");
                rootElement.appendChild(rowElement);

                row.forEach(cell -> {
                    org.w3c.dom.Element cellElement = doc.createElement("Cell");
                    cellElement.setAttribute("Column", String.valueOf(cell.getColumnIndex() + 1));

                    // Handle different cell types, including formulas
                    switch (cell.getCellType()) {
                        case STRING:
                            cellElement.setTextContent(cell.getStringCellValue());
                            break;
                        case NUMERIC:
                            cellElement.setTextContent(String.valueOf(cell.getNumericCellValue()));
                            break;
                        case BOOLEAN:
                            cellElement.setTextContent(String.valueOf(cell.getBooleanCellValue()));
                            break;
                        case FORMULA:
                            // Evaluate the formula and get its value
                            cellElement.setTextContent(
                                    String.valueOf(evaluator.evaluate(cell).getNumberValue()));
                            break;
                        default:
                            cellElement.setTextContent("");
                            break;
                    }
                    rowElement.appendChild(cellElement);
                });
            }

            // Save XML to file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            DOMSource source = new DOMSource(doc);
            try (FileOutputStream fos = new FileOutputStream(xmlOutput)) {
                StreamResult result = new StreamResult(fos);
                transformer.transform(source, result);
            }
        }
    }
    @FXML
    private void handleConvertAndViewButton() {
        try {
            // Path to the Excel file embedded in your project resources
            File excelFile = new File(getClass().getResource("/GINF2.xlsx").toURI());

            // Path to save the converted XML file
            File xmlFile = new File(System.getProperty("java.io.tmpdir"), "Students_GInf2.xml");

            // Perform conversion
            convertExcelToXml(excelFile, xmlFile);

            // Update the status label and open the XML file
            statusLabel.setText("XML file generated: " + xmlFile.getAbsolutePath());
            Desktop.getDesktop().browse(xmlFile.toURI());
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
