module org.ecole.application_scolaire {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens org.ecole.application_scolaire to javafx.fxml;
    exports org.ecole.application_scolaire;
}