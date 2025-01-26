<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format">

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/releveNotes">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <!-- Page Layout -->
            <fo:layout-master-set>
                <fo:simple-page-master master-name="a4" page-width="21cm" page-height="29.7cm" margin="2cm">
                    <fo:region-body margin-top="2cm" margin-bottom="2cm"/>
                </fo:simple-page-master>
            </fo:layout-master-set>

            <!-- Page Content -->
            <fo:page-sequence master-reference="a4">
                <fo:flow flow-name="xsl-region-body">
                    <!-- Title -->
                    <fo:block font-size="18pt" font-family="Arial" text-align="center" space-after="1cm">
                        Relevé de Notes
                    </fo:block>

                    <!-- Student Details -->
                    <fo:block font-size="12pt" font-family="Arial" space-after="0.5cm">
                        Nom : <xsl:value-of select="etudiant/nom"/>
                    </fo:block>
                    <fo:block font-size="12pt" font-family="Arial" space-after="0.5cm">
                        Prénom : <xsl:value-of select="etudiant/prenom"/>
                    </fo:block>
                    <fo:block font-size="12pt" font-family="Arial" space-after="0.5cm">
                        CNE : <xsl:value-of select="etudiant/cne"/>
                    </fo:block>
                    <fo:block font-size="12pt" font-family="Arial" space-after="0.5cm">
                        Classe : <xsl:value-of select="etudiant/classe"/>
                    </fo:block>
                    <fo:block font-size="12pt" font-family="Arial" space-after="0.5cm">
                        Année Universitaire : <xsl:value-of select="etudiant/anneeUniversitaire"/>
                    </fo:block>

                    <!-- Modules Table -->
                    <fo:table table-layout="fixed" width="100%" space-before="1cm">
                        <fo:table-column column-width="5cm"/>
                        <fo:table-column column-width="2cm"/>
                        <fo:table-column column-width="2cm"/>
                        <fo:table-column column-width="3cm"/>

                        <fo:table-header>
                            <fo:table-row>
                                <fo:table-cell border="0.5pt solid black">
                                    <fo:block font-weight="bold" text-align="center">Module</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="0.5pt solid black">
                                    <fo:block font-weight="bold" text-align="center">Moyenne</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="0.5pt solid black">
                                    <fo:block font-weight="bold" text-align="center">Résultat</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="0.5pt solid black">
                                    <fo:block font-weight="bold" text-align="center">Session</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-header>

                        <fo:table-body>
                            <xsl:for-each select="modules/module">
                                <fo:table-row>
                                    <fo:table-cell border="0.5pt solid black">
                                        <fo:block text-align="center"><xsl:value-of select="nom"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="0.5pt solid black">
                                        <fo:block text-align="center"><xsl:value-of select="moyenne"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="0.5pt solid black">
                                        <fo:block text-align="center"><xsl:value-of select="resultat"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="0.5pt solid black">
                                        <fo:block text-align="center"><xsl:value-of select="session"/></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:for-each>
                        </fo:table-body>
                    </fo:table>

                    <!-- Summary -->
                    <fo:block font-size="12pt" font-family="Arial" space-before="1cm">
                        Moyenne Générale : <xsl:value-of select="moyenneGenerale"/>
                    </fo:block>
                    <fo:block font-size="12pt" font-family="Arial" space-before="0.5cm">
                        Décision : <xsl:value-of select="decision"/>
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
</xsl:stylesheet>
