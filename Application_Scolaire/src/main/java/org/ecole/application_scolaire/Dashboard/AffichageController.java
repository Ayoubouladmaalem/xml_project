package org.ecole.application_scolaire.Dashboard;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.net.URL;

public class AffichageController {

    @FXML private WebView webViewNormal;
    @FXML private WebView webViewRattrapage;

    @FXML
    public void initialize() {
        webViewNormal.getEngine().loadContent("<p>Veuillez uploader un fichier XML pour voir l'affichage normal.</p>");
        webViewRattrapage.getEngine().loadContent("<p>Veuillez uploader un fichier XML pour voir l'affichage après rattrapage.</p>");
    }

    @FXML
    public void handleFileUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            try {
                // Load the XML file
                FileInputStream xmlStream = new FileInputStream(selectedFile);

                // Transform for normal view
                String normalHtml = transformXmlWithXslt(xmlStream, "/students.xsl");
                webViewNormal.getEngine().loadContent(normalHtml, "text/html");

                // Transform for rattrapage view
                xmlStream = new FileInputStream(selectedFile); // Reload the stream for the second transformation
                String rattrapageHtml = transformXmlWithXslt(xmlStream, "/rattrapage.xsl");
                webViewRattrapage.getEngine().loadContent(rattrapageHtml, "text/html");

            } catch (Exception e) {
                e.printStackTrace();
                webViewNormal.getEngine().loadContent("<p style='color:red;'>Une erreur est survenue : " + e.getMessage() + "</p>");
                webViewRattrapage.getEngine().loadContent("<p style='color:red;'>Une erreur est survenue : " + e.getMessage() + "</p>");
            }
        }
    }

    private String transformXmlWithXslt(FileInputStream xmlStream, String xsltPath) throws Exception {
        // Load the XSLT file
        URL xsltUrl = getClass().getResource(xsltPath);
        if (xsltUrl == null) {
            throw new Exception("XSLT file not found: " + xsltPath);
        }

        File xsltFile = new File(xsltUrl.toURI());
        StreamSource xslt = new StreamSource(xsltFile);

        // Apply XSLT transformation
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(xslt);

        StreamSource xmlSource = new StreamSource(xmlStream);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);

        transformer.transform(xmlSource, result);

        return writer.toString();
    }
}
