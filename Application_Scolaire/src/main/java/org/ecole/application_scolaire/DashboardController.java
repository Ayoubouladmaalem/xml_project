package org.ecole.application_scolaire;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

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
        contentArea.getChildren().clear();
        contentArea.getChildren().add(new Text("Excel to XML Conversion Content"));
    }

    public void showAffichageContent() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(new Text("Affichage Content"));
    }

    public void showDocumentContent() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(new Text("Document Content"));
    }

    public void showListeTPSContent() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(new Text("Liste TPS Content"));
    }
}
