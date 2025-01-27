<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:fo="http://www.w3.org/1999/XSL/Format"
        version="1.0"
>
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

    <!--
      This XSL-FO expects:
        <students>
          <student>
            <code_apogee>xxx</code_apogee>
            <nom>...</nom>
            <prenom>...</prenom>
          </student>
          ...
        </students>

      We'll produce two halves (TP1, TP2) with groups:
        - Each group normally 2 students
        - If there's exactly 1 leftover, last group is 3
    -->

    <!-- ========== Template for the entire /students doc ========== -->
    <xsl:template match="/students">
        <fo:root>

            <!-- ================= Page Layout ================= -->
            <fo:layout-master-set>
                <fo:simple-page-master master-name="A4"
                                       page-height="29.7cm"
                                       page-width="21cm"
                                       margin="2cm">
                    <fo:region-body/>
                </fo:simple-page-master>
            </fo:layout-master-set>

            <!-- ================= Page Sequence ================= -->
            <fo:page-sequence master-reference="A4">
                <fo:flow flow-name="xsl-region-body">

                    <!-- Title -->
                    <fo:block font-family="Arial"
                              font-size="16pt"
                              font-weight="bold"
                              text-align="center"
                              space-after="10mm">
                        Liste des TPs
                    </fo:block>

                    <!-- 1) We'll define the total count, a midpoint -->
                    <xsl:variable name="total" select="count(student)"/>
                    <xsl:variable name="mid" select="ceiling($total div 2)"/>

                    <!-- ~~~~~~~~~~~~~~~ TP1 ~~~~~~~~~~~~~~~ -->
                    <fo:block font-family="Arial"
                              font-size="12pt"
                              font-weight="bold"
                              space-after="5mm">
                        TP1
                    </fo:block>
                    <fo:table border-collapse="collapse"
                              table-layout="fixed"
                              font-family="Arial"
                              font-size="10pt"
                              width="100%">
                        <!-- We have 3 columns for each row => group of up to 3 students -->
                        <!-- But each column holds 1 student's data (code/nom/prenom) -->
                        <fo:table-column column-width="33%"/>
                        <fo:table-column column-width="33%"/>
                        <fo:table-column column-width="33%"/>

                        <!-- Table Header -->
                        <fo:table-header>
                            <fo:table-row background-color="#e0e0e0">
                                <fo:table-cell border="0.5pt solid #666" padding="3pt">
                                    <fo:block font-weight="bold" text-align="center">Etudiant 1</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="0.5pt solid #666" padding="3pt">
                                    <fo:block font-weight="bold" text-align="center">Etudiant 2</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="0.5pt solid #666" padding="3pt">
                                    <fo:block font-weight="bold" text-align="center">Etudiant 3 (si impair)</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-header>

                        <fo:table-body>
                            <!-- We'll gather the first half => positions 1..$mid -->
                            <xsl:variable name="tp1" select="student[position() &lt;= $mid]"/>
                            <xsl:call-template name="printGroups">
                                <xsl:with-param name="nodes" select="$tp1"/>
                            </xsl:call-template>
                        </fo:table-body>
                    </fo:table>

                    <!-- ~~~~~~~~~~~~~~~ TP2 ~~~~~~~~~~~~~~~ -->
                    <fo:block font-family="Arial"
                              font-size="12pt"
                              font-weight="bold"
                              space-before="10mm"
                              space-after="5mm">
                        TP2
                    </fo:block>
                    <fo:table border-collapse="collapse"
                              table-layout="fixed"
                              font-family="Arial"
                              font-size="10pt"
                              width="100%">
                        <fo:table-column column-width="33%"/>
                        <fo:table-column column-width="33%"/>
                        <fo:table-column column-width="33%"/>

                        <fo:table-header>
                            <fo:table-row background-color="#e0e0e0">
                                <fo:table-cell border="0.5pt solid #666" padding="3pt">
                                    <fo:block font-weight="bold" text-align="center">Etudiant 1</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="0.5pt solid #666" padding="3pt">
                                    <fo:block font-weight="bold" text-align="center">Etudiant 2</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="0.5pt solid #666" padding="3pt">
                                    <fo:block font-weight="bold" text-align="center">Etudiant 3 (si impair)</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-header>

                        <fo:table-body>
                            <xsl:variable name="tp2" select="student[position() &gt; $mid]"/>
                            <xsl:call-template name="printGroups">
                                <xsl:with-param name="nodes" select="$tp2"/>
                            </xsl:call-template>
                        </fo:table-body>
                    </fo:table>

                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>


    <!--
        Named template to print groups from a set of student nodes.
        Groups of 2 except if there's exactly 1 leftover at the end => we make last group = 3.
    -->
    <xsl:template name="printGroups">
        <xsl:param name="nodes"/>

        <!-- Let c = number of students in this half -->
        <xsl:variable name="c" select="count($nodes)"/>
        <!-- If leftover=1, we'll do one group of 3 at the end -->

        <xsl:choose>
            <!-- If c=0 => do nothing -->
            <xsl:when test="$c = 0">
                <!-- No rows -->
            </xsl:when>

            <!-- If c=1 => single group of 1 => or we treat it as 3? (the user said leftover 1 -> group of 3)
                 but there's no 2 more => so let's do single group of 1
            -->
            <xsl:when test="$c = 1">
                <fo:table-row>
                    <!-- Single student => col1 => the student, col2 => empty, col3 => empty -->
                    <xsl:variable name="st1" select="$nodes[1]"/>
                    <fo:table-cell border="0.5pt solid #999" padding="3pt">
                        <fo:block text-align="center">
                            <xsl:value-of select="concat( $st1/nom, ' ', $st1/prenom)"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell border="0.5pt solid #999" padding="3pt">
                        <fo:block/>
                    </fo:table-cell>
                    <fo:table-cell border="0.5pt solid #999" padding="3pt">
                        <fo:block/>
                    </fo:table-cell>
                </fo:table-row>
            </xsl:when>

            <!-- If leftover=1 => c mod 2=1 and c >=3 => we'll do pairs for c-3, then last group of 3 -->
            <!-- We'll handle that by recursion approach: take 2 at a time until c=3, then a group of 3. -->
            <xsl:otherwise>
                <xsl:choose>
                    <!-- If c>3 and c mod 2=1 => we do a pair, then recurse until we are left with c=3, then group of 3 -->
                    <xsl:when test="($c &gt; 3) and ($c mod 2 = 1)">
                        <!-- first 2 students => group of 2 -->
                        <fo:table-row>
                            <xsl:variable name="st1" select="$nodes[1]"/>
                            <xsl:variable name="st2" select="$nodes[2]"/>
                            <fo:table-cell border="0.5pt solid #999" padding="3pt">
                                <fo:block text-align="center">
                                    <xsl:value-of select="concat( $st1/nom, ' ', $st1/prenom)"/>
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell border="0.5pt solid #999" padding="3pt">
                                <fo:block text-align="center">
                                    <xsl:value-of select="concat( $st2/nom, ' ', $st2/prenom)"/>
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell border="0.5pt solid #999" padding="3pt">
                                <fo:block/>
                            </fo:table-cell>
                        </fo:table-row>

                        <!-- Recurse on the rest (minus these 2) -->
                        <xsl:call-template name="printGroups">
                            <xsl:with-param name="nodes" select="$nodes[position() &gt; 2]"/>
                        </xsl:call-template>
                    </xsl:when>

                    <!-- If c mod 2=0 => group of 2 at a time -->
                    <xsl:when test="$c mod 2 = 0">
                        <!-- first 2 students => group of 2 -->
                        <fo:table-row>
                            <xsl:variable name="st1" select="$nodes[1]"/>
                            <xsl:variable name="st2" select="$nodes[2]"/>
                            <fo:table-cell border="0.5pt solid #999" padding="3pt">
                                <fo:block text-align="center">
                                    <xsl:value-of select="concat( $st1/nom, ' ', $st1/prenom)"/>
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell border="0.5pt solid #999" padding="3pt">
                                <fo:block text-align="center">
                                    <xsl:value-of select="concat( $st2/nom, ' ', $st2/prenom)"/>
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell border="0.5pt solid #999" padding="3pt">
                                <fo:block/>
                            </fo:table-cell>
                        </fo:table-row>

                        <!-- Recurse on the rest -->
                        <xsl:call-template name="printGroups">
                            <xsl:with-param name="nodes" select="$nodes[position() &gt; 2]"/>
                        </xsl:call-template>
                    </xsl:when>

                    <!-- If c=3 => one group of 3 -->
                    <xsl:when test="$c = 3">
                        <fo:table-row>
                            <xsl:variable name="st1" select="$nodes[1]"/>
                            <xsl:variable name="st2" select="$nodes[2]"/>
                            <xsl:variable name="st3" select="$nodes[3]"/>
                            <fo:table-cell border="0.5pt solid #999" padding="3pt">
                                <fo:block text-align="center">
                                    <xsl:value-of select="concat( $st1/nom, ' ', $st1/prenom)"/>
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell border="0.5pt solid #999" padding="3pt">
                                <fo:block text-align="center">
                                    <xsl:value-of select="concat( $st2/nom, ' ', $st2/prenom)"/>
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell border="0.5pt solid #999" padding="3pt">
                                <fo:block text-align="center">
                                    <xsl:value-of select="concat( $st3/nom, ' ', $st3/prenom)"/>
                                </fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                    </xsl:when>

                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>
