<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
  xmlns:prov="http://www.w3.org/ns/prov#"
  xmlns:compound="http://www.cbrc.kaust.edu.sa/sbolme/annotation/compound#"
  xmlns:sbol="http://sbols.org/v2#"
  xmlns:drug="http://www.cbrc.kaust.edu.sa/sbolme/annotation/compound/drug#"
  xmlns:thermodynamics="http://www.cbrc.kaust.edu.sa/sbolme/annotation/thermodynamics#"
  xmlns:dcterms="http://purl.org/dc/terms/"
  xmlns:target="http://www.cbrc.kaust.edu.sa/sbolme/annotation/compound/drug/target#" 
  xmlns:enzyme="http://www.cbrc.kaust.edu.sa/sbolme/annotation/compound/drug/enzyme#" 
  xmlns:carrier="http://www.cbrc.kaust.edu.sa/sbolme/annotation/compound/drug/carrier#" 
  xmlns:transporter="http://www.cbrc.kaust.edu.sa/sbolme/annotation/compound/drug/transporter#" 
  >

  <xsl:template match="/">
    <html>
      <body>
    <h3>Compound</h3>
    <xsl:apply-templates select="/rdf:RDF/sbol:ComponentDefinition"/>
    <xsl:apply-templates select="/rdf:RDF/sbol:Sequence"/>
    </body>
  </html>
  </xsl:template>

  <xsl:template match="/rdf:RDF/sbol:ComponentDefinition">
    <xsl:variable name="t1">
      <xsl:value-of select="compound:kegg_id"/>
    </xsl:variable>
    <xsl:variable name="t2">
      <xsl:value-of select="substring($t1, string-length($t1) - 1, string-length($t1))" />
    </xsl:variable>
    <div >
    <table class="table" style="display:inline-block;vertical-align:top;width:700px">
      <tr>
        <td>SBOL Identity URI</td>
        <td><a target="_blank" href="{@rdf:about}" download=""><xsl:value-of select="@rdf:about"/></a></td>        
      </tr>
      <tr>
        <td>SBOLME ID</td>
        <td><xsl:value-of select="sbol:displayId"/></td>
      </tr>
      <tr>
        <td>KEGG ID</td>
        <td><xsl:value-of select="compound:kegg_id"/></td>
      </tr>
      <tr>
        <td>Chemical formula</td>
        <td><xsl:value-of select="compound:formula"/></td>
      </tr>
      <tr>
        <td>Compound name</td>
        <td>
          <xsl:choose>
            <xsl:when test="compound:synonym"><xsl:value-of select="compound:synonym"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="compound:composition"/></xsl:otherwise>
          </xsl:choose>
        </td>
      </tr>
    </table>
  </div>
    <h3>Data sources</h3>
    <ul>
      <xsl:for-each select="/rdf:RDF/sbol:ComponentDefinition/compound:source">
        <li>
          <a target="_blank" href="{.}"><xsl:value-of select="." /></a>
        </li>
      </xsl:for-each>
    </ul>
    <xsl:apply-templates select="compound:drug"/>
  </xsl:template>

  <xsl:template match="compound:drug">
    <h3>Drug information</h3>
    <a target="_blank" href="{drug:information/@rdf:about}"><xsl:value-of select="drug:information/@rdf:about"/></a>
    <xsl:apply-templates select="drug:information/drug:targets"/>
    <xsl:apply-templates select="drug:information/drug:enzymes"/>
    <xsl:apply-templates select="drug:information/drug:carriers"/>
    <xsl:apply-templates select="drug:information/drug:transporters"/>
  </xsl:template>

  <xsl:template match="drug:information/drug:targets">
   <h4>Targets</h4>
   <table class = "table">
    <tr>
      <td>Uniprot ID</td>
      <td>KEGG Gene URL</td>   
      <td>SBOLme ID</td>      
    </tr>
    <xsl:for-each select="drug:targets_info/drug:target/drug:target_info">
      <tr>
        <td><xsl:value-of select="target:uniprot_id" /></td>
        <td><a target="_blank" href="{target:kegg_url/@rdf:resource}"><xsl:value-of select="target:kegg_url/@rdf:resource"/></a></td>
        <td><a target="_blank" href="{target:sbol_identity/@rdf:resource}"><xsl:value-of select="target:sbolme_id"/></a></td>
      </tr>
    </xsl:for-each>
  </table> 
</xsl:template>
<xsl:template match="drug:information/drug:enzymes">
 <h4>Enzymes</h4>
 <table class = "table">
  <tr>
    <td>Uniprot ID</td>
    <td>KEGG Gene URL</td>   
    <td>SBOLme ID</td>      
  </tr>
  <xsl:for-each select="drug:enzymes_info/drug:enzyme/drug:enzyme_info">
    <tr>
      <td><xsl:value-of select="enzyme:uniprot_id" /></td>
      <td><a target="_blank" href="{enzyme:kegg_url/@rdf:resource}"><xsl:value-of select="enzyme:kegg_url/@rdf:resource"/></a></td>
      <td><a target="_blank" href="{enzyme:sbol_identity/@rdf:resource}"><xsl:value-of select="enzyme:sbolme_id"/></a></td>
    </tr>
  </xsl:for-each>
</table> 
</xsl:template>
<xsl:template match="drug:information/drug:carriers">
 <h4>Carriers</h4>
 <table class = "table">
  <tr>
    <td>Uniprot ID</td>
    <td>KEGG Gene URL</td>   
    <td>SBOLme ID</td>      
  </tr>
  <xsl:for-each select="drug:carriers_info/drug:carrier/drug:carrier_info">
    <tr>
      <td><xsl:value-of select="carrier:uniprot_id" /></td>
      <td><a target="_blank" href="{carrier:kegg_url/@rdf:resource}"><xsl:value-of select="carrier:kegg_url/@rdf:resource"/></a></td>
      <td><a target="_blank" href="{carrier:sbol_identity/@rdf:resource}"><xsl:value-of select="carrier:sbolme_id"/></a></td>
    </tr>
  </xsl:for-each>
</table> 
</xsl:template>
<xsl:template match="drug:information/drug:transporters">
  <h4>Transporters</h4>
  <table class = "table">
    <tr>
      <td>Uniprot ID</td>
      <td>KEGG Gene URL</td>   
      <td>SBOLme ID</td>      
    </tr>
    <xsl:for-each select="drug:transporters_info/drug:transporter/drug:transporter_info">
      <tr>
        <td><xsl:value-of select="transporter:uniprot_id" /></td>
        <td><a target="_blank" href="{transporter:kegg_url/@rdf:resource}"><xsl:value-of select="transporter:kegg_url/@rdf:resource"/></a></td>
        <td><a target="_blank" href="{transporter:sbol_identity/@rdf:resource}"><xsl:value-of select="transporter:sbolme_id"/></a></td>
      </tr>
    </xsl:for-each>
  </table> 
</xsl:template>
<xsl:template match="/rdf:RDF/sbol:Sequence">
  <xsl:variable name="v1">
    <xsl:value-of select="sbol:elements"/>
  </xsl:variable>

  <xsl:variable name="v2">
    <xsl:call-template name="replace-string">
      <xsl:with-param name="text" select="$v1"/>
      <xsl:with-param name="replace" select="'+'" />
      <xsl:with-param name="with" select="'%2B'"/>
    </xsl:call-template>
  </xsl:variable>

<!--
<xsl:variable name="v3" select="translate($v2,'=','%3D')"/>
<xsl:variable name="v4" select="translate($v3,')','%29')"/>
-->
<h3>SMILES</h3>
<a target="_blank" href = "http://www.chemicalize.org/structure/#!mol={$v2}&amp;source=calculate"><xsl:value-of select="sbol:elements"/></a>
</xsl:template>

<xsl:template name="replace-string">
  <xsl:param name="text"/>
  <xsl:param name="replace"/>
  <xsl:param name="with"/>
  <xsl:choose>
    <xsl:when test="contains($text,$replace)">
      <xsl:value-of select="substring-before($text,$replace)"/>
      <xsl:value-of select="$with"/>
      <xsl:call-template name="replace-string">
        <xsl:with-param name="text"
          select="substring-after($text,$replace)"/>
          <xsl:with-param name="replace" select="$replace"/>
          <xsl:with-param name="with" select="$with"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$text"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>