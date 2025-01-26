<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html" indent="yes"/>

    <xsl:template match="/">
        <html>
            <head>
                <title>Rattrapage Notes</title>
            </head>
            <body>
                <h1>Liste des étudiants après rattrapage</h1>
                <table border="1">
                    <tr>
                        <th>Prénom</th>
                        <th>Nom</th>
                        <th>Note</th>
                    </tr>
                    <xsl:for-each select="/Workbook/Row[position() &gt; 5]">
                        <tr>
                            <td><xsl:value-of select="Cell[@Column='5']"/></td>
                            <td><xsl:value-of select="Cell[@Column='4']"/></td>
                            <td>
                                <xsl:choose>
                                    <xsl:when test="Cell[@Column='8'] &lt; 8">
                                        <span style="background-color:red;">
                                            <xsl:value-of select="Cell[@Column='8']"/>
                                        </span>
                                    </xsl:when>
                                    <xsl:when test="Cell[@Column='8'] &gt;= 8 and Cell[@Column='8'] &lt; 12">
                                        <span style="background-color:orange;">
                                            <xsl:value-of select="Cell[@Column='8']"/>
                                        </span>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <span style="background-color:green;">
                                            <xsl:value-of select="Cell[@Column='8']"/>
                                        </span>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                    </xsl:for-each>
                </table>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
