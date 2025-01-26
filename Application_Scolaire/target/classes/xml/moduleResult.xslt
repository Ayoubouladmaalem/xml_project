<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" indent="yes"/>

    <!-- Template to match the root element -->
    <xsl:template match="/moduleResult">
        <html>
            <head>
                <title>Student Results</title>
                <style>
                    table {
                    width: 100%;
                    border-collapse: collapse;
                    }
                    th, td {
                    border: 1px solid #ddd;
                    padding: 8px;
                    text-align: left;
                    }
                    th {
                    background-color: #f2f2f2;
                    }
                    .red {
                    background-color: red;
                    }
                    .orange {
                    background-color: orange;
                    }
                    .green {
                    background-color: green;
                    }
                </style>
            </head>
            <body>
                <h1>Student Results - <xsl:value-of select="@annee"/></h1>
                <table>
                    <thead>
                        <tr>
                            <th>Code Apogee</th>
                            <th>Nom</th>
                            <th>Prenom</th>

                            <!-- Dynamically generate column headers for submodules -->
                            <xsl:for-each select="student[1]/submodule">
                                <th><xsl:value-of select="@intitule"/></th>
                            </xsl:for-each>
                            <th>Average</th>
                        </tr>
                    </thead>
                    <tbody>
                        <xsl:apply-templates select="student"/>
                    </tbody>
                </table>
            </body>
        </html>
    </xsl:template>

    <!-- Template to match each student -->
    <xsl:template match="student">
        <tr>
            <td><xsl:value-of select="@codeApogee"/></td>
            <td><xsl:value-of select="nom"/></td>
            <td><xsl:value-of select="prenom"/></td>
            <!-- Dynamically generate columns for submodules -->
            <xsl:for-each select="submodule">
                <td><xsl:value-of select="@grade"/></td>
            </xsl:for-each>
            <!-- Calculate and display the average -->
            <td>
                <xsl:variable name="total">
                    <xsl:value-of select="sum(submodule/@grade)"/>
                </xsl:variable>
                <xsl:variable name="count">
                    <xsl:value-of select="count(submodule)"/>
                </xsl:variable>
                <xsl:variable name="average">
                    <xsl:value-of select="$total div $count"/>
                </xsl:variable>
                <xsl:attribute name="class">
                    <xsl:choose>
                        <xsl:when test="$average &lt; 8">red</xsl:when>
                        <xsl:when test="$average &gt;= 8 and $average &lt;= 12">orange</xsl:when>
                        <xsl:otherwise>green</xsl:otherwise>
                    </xsl:choose>
                </xsl:attribute>
                <xsl:value-of select="format-number($average, '0.00')"/>
            </td>
        </tr>
    </xsl:template>
</xsl:stylesheet>