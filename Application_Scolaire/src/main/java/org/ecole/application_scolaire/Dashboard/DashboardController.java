package org.ecole.application_scolaire.Dashboard;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.io.IOException;

public class DashboardController {

    @FXML
    private BorderPane root;
    private StackPane contentArea;

    @FXML
    public void initialize() {
        // Initialize content area
        contentArea = new StackPane();
        root.setCenter(contentArea);
    }

    // Event handlers for each clickable item in the sidebar
    public void showConversionContent() {
        loadContent("conversion-view.fxml");
    }

    public void showAffichageContent() {
        loadContent("affichage-view.fxml");
    }

    public void showDocumentContent() {
        loadContent("document-view.fxml");
    }

    public void showListeTPSContent() {
        loadContent("listeTp-view.fxml");
    }

    // Helper method to load FXML content into the center area
    private void loadContent(String fxmlFile) {
        String resourcePath = "/org/ecole/application_scolaire/DashboardContent/" + fxmlFile;

        // Check if the resource exists
        if (!resourceExists(resourcePath)) {
            showErrorPlaceholder("Error: FXML file not found: " + fxmlFile);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcePath));
            Pane content = loader.load();
            contentArea.getChildren().setAll(content);
        } catch (IOException e) {
            // Log the error with details and show an alert
            logError("Error loading FXML file: " + fxmlFile, e);
            showErrorAlert("Error Loading View", "Unable to load " + fxmlFile, e.getMessage());
        }
    }

    // Utility method to check if a resource exists
    private boolean resourceExists(String resourcePath) {
        return getClass().getResource(resourcePath) != null;
    }

    // Method to display an error alert
    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Method to display an error message in the content area
    private void showErrorPlaceholder(String errorMessage) {
        Label errorLabel = new Label(errorMessage);
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px; -fx-alignment: center;");
        contentArea.getChildren().setAll(errorLabel);
    }

    // Method to log the error with message and stack trace
    private void logError(String message, Exception e) {
        System.err.println(message);
        e.printStackTrace();
    }
}
