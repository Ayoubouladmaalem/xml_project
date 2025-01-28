package org.ecole.application_scolaire.Dashboard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.OutputStream;

public class ListeTpController {

    @FXML
    private Label contentText; // Linked to <Label fx:id="contentText"> in the FXML

    /**
     * Handler for the "GenererTpListe" button.
     */
    @FXML
    public void GenerateTpListe(ActionEvent actionEvent) {
        try {
            // Update these paths as needed for your environment
            String xmlFile = "src/main/resources/xml/students.xml";
            String xsltFile = "src/main/resources/xml/students-to-fo.xsl";
            String outputPdf = getDownloadsFolderPath() + File.separator + "TPGroups.pdf";

            // 1) Initialize FOP factory
            FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
            OutputStream out = new java.io.FileOutputStream(outputPdf);

            // 2) Create FOP instance for PDF
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

            // 3) Set up Transformer for XSLT-FO
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer(new StreamSource(new File(xsltFile)));

            // 4) Source XML and apply XSL-FO -> PDF
            StreamSource xmlSource = new StreamSource(new File(xmlFile));
            SAXResult result = new SAXResult(fop.getDefaultHandler());
            transformer.transform(xmlSource, result);

            // 5) Cleanup
            out.close();

            // Show success message in the Label
            contentText.setText("PDF Generated Successfully: " + outputPdf);
        } catch (Exception e) {
            e.printStackTrace();
            contentText.setText("Error Generating PDF: " + e.getMessage());
        }
    }

    /**
     * Get the Downloads folder path for the current user.
     */
    private String getDownloadsFolderPath() {
        String userHome = System.getProperty("user.home");
        return userHome + File.separator + "Downloads";
    }
}
