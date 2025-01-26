<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/releveNotes">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="a4" page-height="29.7cm" page-width="21cm" margin="2cm">
                    <fo:region-body margin="2cm"/>
                </fo:simple-page-master>
            </fo:layout-master-set>

            <fo:page-sequence master-reference="a4">
                <fo:flow flow-name="xsl-region-body">
                    <fo:block font-family="Arial" font-size="12pt" text-align="center" font-weight="bold">
                        Relevé de Notes - <xsl:value-of select="etudiant/nom"/> <xsl:value-of select="etudiant/prenom"/>
                    </fo:block>
                    <fo:block>
                        <xsl:text>Classe : </xsl:text>
                        <xsl:value-of select="etudiant/classe"/>
                    </fo:block>
                    <fo:block>
                        <xsl:text>Année Universitaire : </xsl:text>
                        <xsl:value-of select="etudiant/anneeUniversitaire"/>
                    </fo:block>
                    <fo:block space-before="1cm">
                        <fo:table table-layout="fixed" width="100%">
                            <fo:table-column column-width="5cm"/>
                            <fo:table-column column-width="2cm"/>
                            <fo:table-column column-width="2cm"/>
                            <fo:table-column column-width="3cm"/>
                            <fo:table-header>
                                <fo:table-row>
                                    <fo:table-cell><fo:block>Module</fo:block></fo:table-cell>
                                    <fo:table-cell><fo:block>Moyenne</fo:block></fo:table-cell>
                                    <fo:table-cell><fo:block>Résultat</fo:block></fo:table-cell>
                                    <fo:table-cell><fo:block>Session</fo:block></fo:table-cell>
                                </fo:table-row>
                            </fo:table-header>
                            <fo:table-body>
                                <xsl:for-each select="modules/module">
                                    <fo:table-row>
                                        <fo:table-cell><fo:block><xsl:value-of select="nom"/></fo:block></fo:table-cell>
                                        <fo:table-cell><fo:block><xsl:value-of select="moyenne"/></fo:block></fo:table-cell>
                                        <fo:table-cell><fo:block><xsl:value-of select="resultat"/></fo:block></fo:table-cell>
                                        <fo:table-cell><fo:block><xsl:value-of select="session"/></fo:block></fo:table-cell>
                                    </fo:table-row>
                                </xsl:for-each>
                            </fo:table-body>
                        </fo:table>
                    </fo:block>
                    <fo:block>
                        Moyenne Générale : <xsl:value-of select="moyenneGenerale"/>
                    </fo:block>
                    <fo:block>
                        Décision : <xsl:value-of select="decision"/>
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
</xsl:stylesheet>
