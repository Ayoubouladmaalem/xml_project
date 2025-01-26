<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:template match="/">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="studentCard" page-height="11in" page-width="8.5in" margin="1in">
                    <fo:region-body/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            <fo:page-sequence master-reference="studentCard">
                <fo:flow flow-name="xsl-region-body">
                    <!-- Titre -->
                    <fo:block font-size="14pt" font-family="Arial" text-align="center" space-after="1cm">
                        Carte d'Étudiant
                    </fo:block>

                    <!-- Code Apogée -->
                    <fo:block font-size="12pt" font-family="Arial">
                        Code Apogée: <xsl:value-of select="/students/student/code_apogee"/>
                    </fo:block>

                    <!-- CIN -->
                    <fo:block font-size="12pt" font-family="Arial">
                        CIN: <xsl:value-of select="/students/student/CIN"/>
                    </fo:block>

                    <!-- CNE -->
                    <fo:block font-size="12pt" font-family="Arial">
                        CNE: <xsl:value-of select="/students/student/CNE"/>
                    </fo:block>

                    <!-- Nom -->
                    <fo:block font-size="12pt" font-family="Arial">
                        Nom: <xsl:value-of select="/students/student/nom"/>
                    </fo:block>

                    <!-- Prénom -->
                    <fo:block font-size="12pt" font-family="Arial">
                        Prénom: <xsl:value-of select="/students/student/prenom"/>
                    </fo:block>

                    <!-- Lieu de naissance -->
                    <fo:block font-size="12pt" font-family="Arial">
                        Lieu de naissance: <xsl:value-of select="/students/student/lieu_naissance"/>
                    </fo:block>

                    <!-- Date de naissance -->
                    <fo:block font-size="12pt" font-family="Arial">
                        Date de naissance: <xsl:value-of select="/students/student/date_naissance"/>
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
</xsl:stylesheet>
