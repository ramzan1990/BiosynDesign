<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:structure="http://www.cbrc.kaust.edu.sa/sbolme/annotation/protein/structure#" xmlns:prov="http://www.w3.org/ns/prov#" xmlns:catalytic_site="http://www.cbrc.kaust.edu.sa/sbolme/annotation/enzyme/structure/catalytic_site#" xmlns:ecnum="http://www.cbrc.kaust.edu.sa/sbolme/annotation/ecnum#" xmlns:protein="http://www.cbrc.kaust.edu.sa/sbolme/annotation/protein#" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:residue="http://www.cbrc.kaust.edu.sa/sbolme/annotation/protein/structure/residue#" xmlns:organism="http://www.cbrc.kaust.edu.sa/sbolme/annotation/kegg/organism#" xmlns:sbol="http://sbols.org/v2#" xmlns:dcterms="http://purl.org/dc/terms/"
  >

  <xsl:template match="/">
    <html>
      <body>
    <h3>Protein</h3>
    <xsl:apply-templates select="/rdf:RDF/sbol:ComponentDefinition"/>
    <xsl:apply-templates select="/rdf:RDF/sbol:Sequence"/>
    </body>
  </html>
      
  </xsl:template>

  <xsl:template match="/rdf:RDF/sbol:ComponentDefinition">
   <table class = "table">
    <tr>
      <td>SBOL Identity URI</td>
      <td><a target="_blank"  href="{@rdf:about}" download=""><xsl:value-of select="@rdf:about"/></a></td>        
    </tr>
    <tr>
      <td>SBOLME ID</td>
      <td><xsl:value-of select="sbol:displayId"/></td>
    </tr>
    <tr>
      <td>Name</td>
      <td><xsl:value-of select="dcterms:title"/></td>
    </tr>
    <tr>
      <td>Organism ID</td>
      <td><xsl:value-of select="organism:kegg_id"/></td>
    </tr>
    <tr>
      <td>Organism Name</td>
      <td><xsl:value-of select="organism:name"/></td>
    </tr>
    <tr>
      <td>EC number</td>
      <td><xsl:value-of select="ecnum:id"/></td>
    </tr>   
  </table> 
  <h3>Data sources</h3>
  <ul>
    <xsl:for-each select="/rdf:RDF/sbol:ComponentDefinition/protein:source">
      <li>
        <a target="_blank"  href="{.}"><xsl:value-of select="." /></a>
      </li>
    </xsl:for-each>
  </ul>
  <xsl:apply-templates select="structure:catalytic_sites"/>
</xsl:template>

<xsl:template match="structure:catalytic_sites">
  <h3>Catalytic sites</h3>
  <p>PDB ID: <a target="_blank"  href="{structure:information/@rdf:about}"><xsl:value-of select="structure:information/structure:pdb_id" /></a></p>
  <xsl:for-each select="structure:information/structure:catalytic_site/catalytic_site:information">
    <h4><a target="_blank"  href="{structure:residue/residue:information/@rdf:about}">Catalytic Site [<xsl:value-of select="catalytic_site:evidence" />]</a></h4>
    <table class = "table">
      <tr>
        <td>Type </td>
        <td>Chain</td>       
        <td style = "width: 200px;">PDB sequence number</td>
        <td>Functional location</td>    
      </tr>
      <xsl:for-each select="structure:residue/residue:information">
        <tr>
          <td><xsl:value-of select="residue:type" /></td>
          <td><xsl:value-of select="residue:chain" /></td>       
          <td><xsl:value-of select="residue:sequence_number" /></td>
          <td><xsl:value-of select="residue:functional_location" /></td>    
        </tr>
      </xsl:for-each>
    </table> 
  </xsl:for-each>
</xsl:template>

<xsl:template match="/rdf:RDF/sbol:Sequence">

  <h3>Amino acid sequence</h3>
  <p style="word-wrap:break-word"><xsl:value-of select="sbol:elements"/></p>
</xsl:template>


</xsl:stylesheet>