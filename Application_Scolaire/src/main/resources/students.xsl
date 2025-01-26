<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html" indent="yes"/>
    <xsl:template match="/">
        <html>
            <head>
                <title>Notes des étudiants</title>
            </head>
            <body>
                <h1>Student Grades</h1>
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
                            <td><xsl:value-of select="Cell[@Column='8']"/></td>
                        </tr>
                    </xsl:for-each>
                </table>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
