<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
xmlns:sbol="http://sbols.org/v2#"
xmlns:dcterms="http://purl.org/dc/terms/"
xmlns:compound="http://www.cbrc.kaust.edu.sa/sbolme/annotation/compound"
xmlns:enzyme_class="http://www.cbrc.kaust.edu.sa/sbolme/annotation/enzyme_class#"
>

<xsl:template match="/">
    <html>
      <body>
  <h3>Enzyme</h3>
  <xsl:apply-templates select="/rdf:RDF/sbol:ComponentDefinition"/>
  </body>
  </html>
</xsl:template>

<xsl:template match="/rdf:RDF/sbol:ComponentDefinition">
 <table class = "table">
 	<tr>
        <td>SBOL Identity URI</td>
        <td><a target="_blank" href="{@rdf:about}" download=""><xsl:value-of select="@rdf:about"/></a></td>        
	 </tr>
	 <tr>
        <td>Enzyme Class ID</td>
        <td><xsl:value-of select="enzyme_class:id"/></td>
	 </tr>
	  <tr>
        <td>Title</td>
        <td><xsl:value-of select="dcterms:title"/></td>
	 </tr>
	 <tr>
        <td>Cofactor info</td>
        <td><xsl:value-of select="enzyme_class:id"/></td>
	 </tr>
 </table>	
<h3>Data sources</h3>
<ul>
<xsl:for-each select="enzyme_class:source">
        <li>
            <a target="_blank" href="{.}"><xsl:value-of select="." /></a>
        </li>
</xsl:for-each>
</ul>

<xsl:choose>
  <xsl:when test="enzyme_class:synonym"><h3>Names</h3></xsl:when>
</xsl:choose>


<ul>
<xsl:for-each select="enzyme_class:synonym">
        <li>
            <xsl:value-of select="." />
        </li>
</xsl:for-each>
</ul>
<h3>Reaction equations</h3>
<ul>
<xsl:for-each select="enzyme_class:formula">
        <li>
            <xsl:value-of select="." />
        </li>
</xsl:for-each>
</ul>
</xsl:template>


</xsl:stylesheet>