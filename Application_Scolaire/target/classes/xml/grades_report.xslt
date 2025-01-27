<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="xml" indent="yes"/>
    <xsl:param name="students-doc" select="document('students.xml')"/>
    <xsl:param name="modules-doc" select="document('module.xml')"/>

    <xsl:template match="/">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="default-page" page-width="21cm" page-height="29.7cm" margin="2cm">
                    <fo:region-body/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            <fo:page-sequence master-reference="default-page">
                <fo:flow flow-name="xsl-region-body">

                    <!-- Title -->
                    <fo:block font-size="16pt" font-weight="bold" text-align="center" margin-bottom="5mm">
                        UNIVERSITE ABDELMALEK ESSADI
                    </fo:block>
                    <fo:block font-size="14pt" text-align="center" margin-bottom="5mm">
                        Ecole Nationale des Sciences Appliquées de Tanger
                    </fo:block>
                    <fo:block font-size="12pt" text-align="center" margin-bottom="10mm">
                        Année universitaire : 2023-2024
                    </fo:block>
                    <fo:block font-size="14pt" font-weight="bold" text-align="center" margin-bottom="10mm">
                        RELEVE DE NOTES ET RESULTATS
                    </fo:block>

                    <!-- Student Information -->
                    <xsl:for-each select="grades/student">
                        <xsl:variable name="code_apogee" select="@code_apogee"/>
                        <xsl:for-each select="$students-doc/students/student[code_apogee=$code_apogee]">
                            <fo:block font-size="12pt" font-weight="bold" margin-bottom="2mm">
                                Nom : <xsl:value-of select="nom"/>
                            </fo:block>
                            <fo:block font-size="12pt" margin-bottom="2mm">
                                Prénom : <xsl:value-of select="prenom"/>
                            </fo:block>
                        </xsl:for-each>
                        <fo:block font-size="12pt" font-weight="bold" margin-bottom="2mm">
                            Code Apogée : <xsl:value-of select="@code_apogee"/>
                        </fo:block>

                        <!-- Modules Table -->
                        <fo:table border-collapse="separate" border-separation="1mm" width="100%" margin-top="5mm">
                            <fo:table-column column-width="40%"/>
                            <fo:table-column column-width="20%"/>
                            <fo:table-column column-width="20%"/>
                            <fo:table-column column-width="20%"/>
                            <fo:table-header>
                                <fo:table-row>
                                    <fo:table-cell background-color="#CCCCCC">
                                        <fo:block font-weight="bold">MODULE</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell background-color="#CCCCCC">
                                        <fo:block font-weight="bold">MOYENNE</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell background-color="#CCCCCC">
                                        <fo:block font-weight="bold">RESULTAT</fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-header>
                            <fo:table-body>
                                <xsl:for-each select="$modules-doc/modules/module">
                                    <xsl:variable name="module-code" select="@code"/>
                                    <xsl:variable name="module-intitule" select="@intitule"/>
                                    <xsl:variable name="module-grades" select="/grades/student/grade[@submodule=current()/submodule/@code]"/>
                                    <xsl:variable name="grades-count" select="count($module-grades)"/>

                                    <fo:table-row>
                                        <fo:table-cell>
                                            <fo:block>
                                                <xsl:value-of select="$module-intitule"/>
                                            </fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell>
                                            <fo:block>
                                                <xsl:choose>
                                                    <xsl:when test="$grades-count > 0">
                                                        <xsl:variable name="average" select="sum($module-grades) div $grades-count"/>
                                                        <xsl:value-of select="format-number($average, '0.00')"/>
                                                    </xsl:when>
                                                    <xsl:otherwise>
                                                        NaN
                                                    </xsl:otherwise>
                                                </xsl:choose>
                                            </fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell>
                                            <fo:block>
                                                <xsl:choose>
                                                    <xsl:when test="$grades-count > 0 and (sum($module-grades) div $grades-count) >= 12">V</xsl:when>
                                                    <xsl:otherwise>NV</xsl:otherwise>
                                                </xsl:choose>
                                            </fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                </xsl:for-each>
                            </fo:table-body>
                        </fo:table>
                    </xsl:for-each>

                    <!-- General Average -->
                    <fo:block font-size="12pt" font-weight="bold" margin-top="10mm">
                        Moyenne Générale :
                        <xsl:choose>
                            <xsl:when test="count(grades/student/grade) > 0">
                                <xsl:value-of select="format-number(sum(grades/student/grade) div count(grades/student/grade), '0.00')"/>
                            </xsl:when>
                            <xsl:otherwise>Non disponible</xsl:otherwise>
                        </xsl:choose>
                    </fo:block>

                    <!-- Decision -->
                    <fo:block font-size="12pt" font-weight="bold" margin-top="5mm">
                        Décision :
                        <xsl:choose>
                            <xsl:when test="count(grades/student/grade) > 0 and (sum(grades/student/grade) div count(grades/student/grade)) >= 12">
                                Résultat admis, l'étudiant(e) est affecté(e) à GINF3
                            </xsl:when>
                            <xsl:otherwise>
                                Résultat non admis, redoublement
                            </xsl:otherwise>
                        </xsl:choose>
                    </fo:block>

                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
</xsl:stylesheet>
