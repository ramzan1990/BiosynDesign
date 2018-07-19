<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:reaction="http://www.cbrc.kaust.edu.sa/sbolme/annotation/reaction#" xmlns:enzyme="http://www.cbrc.kaust.edu.sa/sbolme/annotation/enzyme#" xmlns:prov="http://www.w3.org/ns/prov#" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:sbol="http://sbols.org/v2#" xmlns:thermodynamics="http://www.cbrc.kaust.edu.sa/sbolme/annotation/thermodynamics#" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:enzyme_class="http://www.cbrc.kaust.edu.sa/sbolme/annotation/enzyme_class#"
>

    <xsl:template match="/">
        <html>
            <body>
                <h3>Reaction</h3>
                <xsl:apply-templates select="/rdf:RDF/sbol:ModuleDefinition"/>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="/rdf:RDF/sbol:ModuleDefinition">
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
                <td>KEGG ID</td>
                <td><xsl:value-of select="reaction:kegg_id"/></td>
            </tr>
            <tr>
                <td>KEGG URL</td>
                <td><xsl:value-of select="reaction:source"/></td>
            </tr>
        </table>

        <h3>Reactants</h3>
        <table class = "table">
            <tr>
                <td>
                    SBOL Identity URI
                </td>
                <td>
                    Stoichiometry
                </td>
            </tr>
            <xsl:for-each select="sbol:functionalComponent/sbol:FunctionalComponent">
                <xsl:variable name="variable1">
                    <xsl:value-of select="'reactant' = substring( sbol:displayId, 1, 8)"/>
                </xsl:variable>
                <xsl:choose>
                    <xsl:when test="$variable1='true'">
                        <xsl:variable name="variable2">
                            <xsl:value-of select="sbol:displayId"/>
                        </xsl:variable>
                        <tr>
                            <td><xsl:value-of select="sbol:definition/@rdf:resource" /></td>
                            <td>
                                <xsl:for-each select="../../sbol:interaction/sbol:Interaction/sbol:participation/sbol:Participation">
                                    <xsl:variable name="variable3">
                                        <xsl:value-of select="contains( sbol:participant/@rdf:resource, $variable2)"/>
                                    </xsl:variable>
                                    <xsl:choose>
                                        <xsl:when test="$variable3='true'">
                                            <xsl:value-of select="reaction:stoichiometry" />
                                        </xsl:when>
                                    </xsl:choose>
                                </xsl:for-each>
                            </td>
                        </tr>
                    </xsl:when>
                </xsl:choose>
            </xsl:for-each>
        </table>



        <h3>Products</h3>
        <table class = "table">
            <tr>
                <td>
                    SBOL Identity URI
                </td>
                <td>
                    Stoichiometry
                </td>
            </tr>
            <xsl:for-each select="sbol:functionalComponent/sbol:FunctionalComponent">
                <xsl:variable name="variable1">
                    <xsl:value-of select="'product' = substring( sbol:displayId, 1, 7)"/>
                </xsl:variable>
                <xsl:choose>
                    <xsl:when test="$variable1='true'">
                        <xsl:variable name="variable2">
                            <xsl:value-of select="sbol:displayId"/>
                        </xsl:variable>
                        <tr>
                            <td><xsl:value-of select="sbol:definition/@rdf:resource" /></td>
                            <td>
                                <xsl:for-each select="../../sbol:interaction/sbol:Interaction/sbol:participation/sbol:Participation">
                                    <xsl:variable name="variable3">
                                        <xsl:value-of select="contains( sbol:participant/@rdf:resource, $variable2)"/>
                                    </xsl:variable>
                                    <xsl:choose>
                                        <xsl:when test="$variable3='true'">
                                            <xsl:value-of select="reaction:stoichiometry" />
                                        </xsl:when>
                                    </xsl:choose>
                                </xsl:for-each>
                            </td>
                        </tr>
                    </xsl:when>
                </xsl:choose>
            </xsl:for-each>
        </table>

        <h3>Enzymes</h3>
        <ul>
            <xsl:for-each select="sbol:functionalComponent/sbol:FunctionalComponent/enzyme_class:id">
                <li>
                    <xsl:value-of select="." />
                </li>
            </xsl:for-each>
        </ul>

        <xsl:apply-templates select="thermodynamics:standard_Gibbs_free_energy/thermodynamics:information"/>


    </xsl:template>

    <xsl:template match="thermodynamics:standard_Gibbs_free_energy/thermodynamics:information">
        <h3>Standard Reaction Gibbs Free Energy</h3>
        <table class = "table">
            <tr>
                <td>Energy </td>
                <td><xsl:value-of select="thermodynamics:value"/>     &#160;     <xsl:value-of select="thermodynamics:unit"/></td>
            </tr>
            <tr>
                <td>Estimation tool</td>
                <td><xsl:value-of select="thermodynamics:estimator"/></td>
            </tr>
            <tr>
                <td>pH</td>
                <td><xsl:value-of select="thermodynamics:pH"/></td>
            </tr>
            <tr>
                <td>Absolute temperature</td>
                <td><xsl:value-of select="thermodynamics:absolute_temperature"/></td>
            </tr>
        </table>
    </xsl:template>


</xsl:stylesheet>