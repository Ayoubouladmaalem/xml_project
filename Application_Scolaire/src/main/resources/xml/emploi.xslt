<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:output method="xml" indent="yes" />

    <xsl:template match="/">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <!-- Page Layout -->
            <fo:layout-master-set>
                <fo:simple-page-master master-name="simple" page-width="21cm" page-height="29.7cm" margin="2cm">
                    <fo:region-body margin-top="2cm"/>
                </fo:simple-page-master>
            </fo:layout-master-set>

            <!-- Page Content -->
            <fo:page-sequence master-reference="simple">
                <fo:flow flow-name="xsl-region-body">
                    <!-- Title -->
                    <fo:block font-size="18pt" font-family="Arial"  text-align="center" space-after="1cm" >
                        Emploi du Temps
                    </fo:block>

                    <!-- Loop Through Weeks -->
                    <xsl:for-each select="schedule/week">
                        <fo:block font-size="14pt" font-family="Arial"  space-after="0.5cm" >
                            <xsl:text>Semaine : </xsl:text><xsl:value-of select="@number"/>
                        </fo:block>

                        <!-- Loop Through Days -->
                        <xsl:for-each select="day">
                            <fo:block font-size="12pt" font-family="Arial" >
                                <xsl:value-of select="@name"/><xsl:text>, </xsl:text><xsl:value-of select="@date"/>
                            </fo:block>

                            <!-- Table for Day's Schedule -->
                            <fo:table table-layout="fixed" width="100%" space-before="0.3cm" border-collapse="separate" border-separation="3pt">
                                <!-- Define Columns -->
                                <fo:table-column column-width="3cm"/>
                                <fo:table-column column-width="5cm"/>
                                <fo:table-column column-width="5cm"/>
                                <fo:table-column column-width="3cm"/>

                                <!-- Table Header -->
                                <fo:table-header>
                                    <fo:table-row background-color="#F2F3F4">
                                        <fo:table-cell border="1pt solid black" padding="4pt">
                                            <fo:block font-weight="bold" text-align="center">Heure</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell border="1pt solid black" padding="4pt">
                                            <fo:block font-weight="bold" text-align="center">Matière</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell border="1pt solid black" padding="4pt">
                                            <fo:block font-weight="bold" text-align="center">Professeur</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell border="1pt solid black" padding="4pt">
                                            <fo:block font-weight="bold" text-align="center">Salle</fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                </fo:table-header>

                                <!-- Table Body -->
                                <fo:table-body>
                                    <xsl:for-each select="slot">
                                        <fo:table-row>
                                            <fo:table-cell border="1pt solid black" padding="4pt">
                                                <fo:block text-align="center" font-family="Arial">
                                                    <xsl:value-of select="@time"/>
                                                </fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell border="1pt solid black" padding="4pt">
                                                <fo:block text-align="center" font-family="Arial">
                                                    <xsl:value-of select="subject"/>
                                                </fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell border="1pt solid black" padding="4pt">
                                                <fo:block text-align="center" font-family="Arial">
                                                    <xsl:value-of select="teacher"/>
                                                </fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell border="1pt solid black" padding="4pt">
                                                <fo:block text-align="center" font-family="Arial">
                                                    <xsl:value-of select="room"/>
                                                </fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </xsl:for-each>
                                </fo:table-body>
                            </fo:table>
                        </xsl:for-each>
                    </xsl:for-each>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
</xsl:stylesheet>
