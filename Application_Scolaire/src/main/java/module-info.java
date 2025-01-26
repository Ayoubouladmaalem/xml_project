module org.ecole.application_scolaire {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires org.apache.poi.ooxml;
    requires java.desktop;

    opens org.ecole.application_scolaire to javafx.fxml;
    exports org.ecole.application_scolaire;
    exports org.ecole.application_scolaire.Dashboard;
    opens org.ecole.application_scolaire.Dashboard to javafx.fxml;
}