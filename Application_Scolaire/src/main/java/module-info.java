module org.ecole.application_scolaire {
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires org.apache.poi.ooxml;
    requires javafx.web;
    requires java.desktop;
    requires org.apache.pdfbox;
    requires fop;

    opens org.ecole.application_scolaire to javafx.fxml;
    exports org.ecole.application_scolaire;
    exports org.ecole.application_scolaire.Dashboard;
    opens org.ecole.application_scolaire.Dashboard to javafx.fxml;
}