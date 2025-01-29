<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:template match="/">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <!-- Define the page layout -->
                <fo:simple-page-master master-name="studentCard" page-height="5.5in" page-width="8.5in" margin="0.5in">
                    <fo:region-body margin="0.5in"/>
                </fo:simple-page-master>
            </fo:layout-master-set>

            <fo:page-sequence master-reference="studentCard">
                <fo:flow flow-name="xsl-region-body">
                    <!-- Header Section -->
                    <fo:block font-size="18pt" font-family="Arial" font-weight="bold" text-align="center" color="#2c3e50" space-after="0.5cm">
                        Carte d'Étudiant
                    </fo:block>

                    <!-- Student Information Section -->
                    <fo:block border="1pt solid #3498db" padding="0.5cm" background-color="#ecf0f1">
                        <!-- Code Apogée -->
                        <fo:block font-size="12pt" font-family="Arial" color="#34495e" space-after="0.2cm">
                            <fo:inline font-weight="bold">Code Apogée:</fo:inline>
                            <xsl:value-of select="/students/student/code_apogee"/>
                        </fo:block>

                        <!-- CIN -->
                        <fo:block font-size="12pt" font-family="Arial" color="#34495e" space-after="0.2cm">
                            <fo:inline font-weight="bold">CIN:</fo:inline>
                            <xsl:value-of select="/students/student/CIN"/>
                        </fo:block>

                        <!-- CNE -->
                        <fo:block font-size="12pt" font-family="Arial" color="#34495e" space-after="0.2cm">
                            <fo:inline font-weight="bold">CNE:</fo:inline>
                            <xsl:value-of select="/students/student/CNE"/>
                        </fo:block>

                        <!-- Nom -->
                        <fo:block font-size="12pt" font-family="Arial" color="#34495e" space-after="0.2cm">
                            <fo:inline font-weight="bold">Nom:</fo:inline>
                            <xsl:value-of select="/students/student/nom"/>
                        </fo:block>

                        <!-- Prénom -->
                        <fo:block font-size="12pt" font-family="Arial" color="#34495e" space-after="0.2cm">
                            <fo:inline font-weight="bold">Prénom:</fo:inline>
                            <xsl:value-of select="/students/student/prenom"/>
                        </fo:block>

                        <!-- Lieu de naissance -->
                        <fo:block font-size="12pt" font-family="Arial" color="#34495e" space-after="0.2cm">
                            <fo:inline font-weight="bold">Lieu de naissance:</fo:inline>
                            <xsl:value-of select="/students/student/lieu_naissance"/>
                        </fo:block>

                        <!-- Date de naissance -->
                        <fo:block font-size="12pt" font-family="Arial" color="#34495e">
                            <fo:inline font-weight="bold">Date de naissance:</fo:inline>
                            <xsl:value-of select="/students/student/date_naissance"/>
                        </fo:block>
                    </fo:block>

                    <!-- Footer Section -->
                    <fo:block font-size="10pt" font-family="Arial" text-align="center" color="#7f8c8d" margin-top="1cm">
                        ENSA TANGER - Tous droits réservés
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
</xsl:stylesheet>