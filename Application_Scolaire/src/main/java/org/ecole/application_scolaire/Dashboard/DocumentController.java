package org.ecole.application_scolaire.Dashboard;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class DocumentController {

    @FXML private Text contentText;

    @FXML
    public void initialize() {
        contentText.setText("Document Content");
    }
}