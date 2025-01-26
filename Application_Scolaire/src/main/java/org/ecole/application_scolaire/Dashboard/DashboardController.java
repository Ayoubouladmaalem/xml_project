package org.ecole.application_scolaire.Dashboard;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class DashboardController {

    @FXML private BorderPane root;
    private StackPane contentArea;

    @FXML
    public void initialize() {
        // Initialize content area
        contentArea = new StackPane();
        root.setCenter(contentArea);
    }

    // Event handlers for each clickable item in the sidebar
    public void showConversionContent() {
        loadContent("conversion_content.fxml");
    }

    public void showAffichageContent() {
        loadContent("affichage_content.fxml");
    }

    public void showDocumentContent() {
        loadContent("document_content.fxml");
    }

    public void showListeTPSContent() {
        loadContent("liste_tps_content.fxml");
    }

    // Helper method to load FXML content into the center area
    private void loadContent(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            StackPane content = loader.load();
            contentArea.getChildren().setAll(content);
        } catch (IOException e) {
            e.printStackTrace(); // Handle loading errors
        }
    }
}
