<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
        version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:fo="http://www.w3.org/1999/XSL/Format"
>
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>

    <!-- We match the root <students> and assume there's exactly one <student> inside -->
    <xsl:template match="/students">
        <fo:root>

            <!-- ============ Page Layout ============ -->
            <fo:layout-master-set>
                <fo:simple-page-master master-name="A4"
                                       page-height="29.7cm"
                                       page-width="21cm"
                                       margin-top="2cm"
                                       margin-bottom="2cm"
                                       margin-left="2cm"
                                       margin-right="2cm">
                    <fo:region-body />
                </fo:simple-page-master>
            </fo:layout-master-set>

            <!-- ============ Single Page ============ -->
            <fo:page-sequence master-reference="A4">
                <fo:flow flow-name="xsl-region-body">

                    <!-- Top "Letterhead" or Institution Info -->
                    <fo:block font-family="Arial" font-size="10pt" text-align="left" space-after="1cm">
                        <xsl:text>ROYAUME DU MAROC </xsl:text><fo:inline>  </fo:inline>
                        <fo:block>Université Abdelmalek Essaâdi</fo:block>
                        <fo:block>École Nationale des Sciences Appliquées de Tanger</fo:block>
                        <fo:block>Service des Affaires Estudiantines</fo:block>
                    </fo:block>

                    <!-- Title: Attestation de Scolarité -->
                    <fo:block font-family="Arial" font-size="16pt" font-weight="bold"
                              text-align="center" space-after="1cm">
                        ATTESTATION DE SCOLARITÉ
                    </fo:block>

                    <!-- Intro Paragraph -->
                    <fo:block font-family="Arial" font-size="11pt" text-align="justify"
                              line-height="14pt" space-after="12pt">
                        Le Directeur de l’École Nationale des Sciences Appliquées de Tanger atteste
                        que l’étudiant :
                    </fo:block>

                    <!-- Extract the single student -->
                    <xsl:variable name="stud" select="student[1]" />

                    <!-- Student Name Line with M/Mme prefix -->
                    <fo:block font-family="Arial" font-size="12pt"
                              font-weight="bold" space-after="10pt">
                        <xsl:text>M/Mme </xsl:text>
                        <xsl:value-of select="$stud/nom"/>
                        <xsl:text> </xsl:text>
                        <xsl:value-of select="$stud/prenom"/>
                    </fo:block>

                    <!-- ID Info -->
                    <fo:block font-family="Arial" font-size="11pt" space-after="6pt">
                        Numéro de la carte d’identité nationale :
                        <fo:inline font-weight="bold">
                            <xsl:value-of select="$stud/CIN"/>
                        </fo:inline>
                    </fo:block>
                    <fo:block font-family="Arial" font-size="11pt" space-after="6pt">
                        Code national de l’étudiant(e) :
                        <fo:inline font-weight="bold">
                            <xsl:value-of select="$stud/CNE"/>
                        </fo:inline>
                    </fo:block>

                    <!-- Birth Info -->
                    <fo:block font-family="Arial" font-size="11pt" space-after="6pt">
                        né(e) le
                        <fo:inline font-weight="bold">
                            <xsl:value-of select="$stud/date_naissance"/>
                        </fo:inline>
                        <xsl:text> à </xsl:text>
                        <fo:inline font-weight="bold">
                            <xsl:value-of select="$stud/lieu_naissance"/>
                        </fo:inline>
                    </fo:block>

                    <!-- Studies Info -->
                    <fo:block font-family="Arial" font-size="11pt" space-before="12pt" space-after="6pt">
                        poursuit ses études à l’École Nationale des Sciences Appliquées de Tanger
                        <fo:inline>pour l’année universitaire 2024/2025.</fo:inline>
                    </fo:block>

                    <!-- Add the Diplôme d'Ingénieur -->
                    <fo:block font-family="Arial" font-size="11pt" space-after="6pt">
                        Diplôme : Ingénieur en Génie Informatique.
                    </fo:block>



                    <!-- Date + Signature line (hard-coded for example) -->
                    <fo:block font-family="Arial" font-size="11pt" space-after="8pt">
                        Fait à TANGER
                    </fo:block>
                    <fo:block font-family="Arial" font-size="11pt" space-before="1cm" text-align="left">
                        Le Directeur
                    </fo:block>
                    <fo:block font-family="Arial" font-size="11pt" space-before="0.5cm" text-align="left">
                        <fo:inline font-weight="bold">
                            Taha Ayoub
                        </fo:inline>
                    </fo:block>

                    <!-- Footer Info -->
                    <fo:block font-size="9pt" font-family="Arial" text-align="center"
                              space-before="2cm" color="#555555">
                        Le présent document n'est délivré qu'en un seul exemplaire.
                        <fo:block>
                            Il appartient à l’étudiant d’en faire des photocopies certifiées conformes.
                        </fo:block>
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>

</xsl:stylesheet>
