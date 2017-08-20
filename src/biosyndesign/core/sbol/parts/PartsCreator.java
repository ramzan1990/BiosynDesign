package biosyndesign.core.sbol.parts;

import java.awt.print.Printable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Vector;
import javax.xml.namespace.QName;

import org.sbolstandard.core2.*;

/**
 * Created by Umarov on 2/3/2017.
 */
public class PartsCreator {



    public static void newCompound(String f, String COMPOUND_ID, String[] SYNONYMS, String[] EXTERNAL_LINKS, String FORMULA, String SMILES, String CHARGE, String prefix) {
        String ANNOTATION_PREFIX_URI = prefix + "/annotation/compound";
        String ANNOTATION_PREFIX = "compound";
        String ANNOTATION_SYNONYM = "synonym";
        String ANNOTATION_LINK = "source";
        String ANNOTATION_FORMULA = "formula";
        String ANNOTATION_CHARGE = "charge";
        String URI_PREFIX = prefix;
        URI SMILES_ENCODING = URI.create("http://opensmiles.org/opensmiles.html");

        try {
            SBOLDocument doc = new SBOLDocument();
            doc.setDefaultURIprefix(ANNOTATION_PREFIX_URI);
            doc.setTypesInURIs(true);
            doc.addNamespace(URI.create(ANNOTATION_PREFIX_URI), ANNOTATION_PREFIX);
            doc.setDefaultURIprefix(URI_PREFIX);

            ComponentDefinition cdef = doc.createComponentDefinition(COMPOUND_ID, ComponentDefinition.SMALL_MOLECULE);
            cdef.setName(SYNONYMS[0]);
            cdef.setWasDerivedFrom(new URI(EXTERNAL_LINKS[0]));
            if (!SMILES.equals("NULL"))
                cdef.addSequence(doc.createSequence(COMPOUND_ID + "_seq", SMILES, SMILES_ENCODING));
            if (!FORMULA.equals("NULL"))
                cdef.createAnnotation(new QName(ANNOTATION_PREFIX_URI, ANNOTATION_FORMULA, ANNOTATION_PREFIX), FORMULA);
            if (!CHARGE.equals("NULL"))
                cdef.createAnnotation(new QName(ANNOTATION_PREFIX_URI, ANNOTATION_CHARGE, ANNOTATION_PREFIX), CHARGE);

            QName qName = new QName(ANNOTATION_PREFIX_URI, ANNOTATION_SYNONYM, ANNOTATION_PREFIX);
            for (int i = 0; i < SYNONYMS.length; i++) cdef.createAnnotation(qName, SYNONYMS[i]);

            qName = new QName(ANNOTATION_PREFIX_URI, ANNOTATION_LINK, ANNOTATION_PREFIX);
            for (int i = 0; i < EXTERNAL_LINKS.length; i++) cdef.createAnnotation(qName, EXTERNAL_LINKS[i]);

            SBOLWriter.write(doc, f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void newReaction(String f, String[] REACTANTS, String[] PRODUCTS, int[] REACTANTS_STOICHIOMETRY,
                            int[] PRODUCTS_STOICHIOMETRY, String[] EC_NUMBERS,  String KEGG_REACTION, String REACTION_ID,
                            String FREE_ENERGY_VALUE, String prefix) {
        String URI_PREFIX =prefix+  "/reaction";
        String URI_COMPOUND_PREFIX = prefix + "/compound/cd/";
        String URI_ENZYME_PREFIX = prefix + "/enzyme/cd/";

        String ANNOTATION_PREFIX_URI = prefix + "/annotation/reaction";
        String ANNOTATION_PREFIX = "reaction";
        String ANNOTATION_STOICHIOMETRY = "stoichiometry";

        String ANNOTATION_THERMODYNAMICS_PREFIX_URI = prefix + "/annotation/thermodynamics";
        String ANNOTATION_THERMODYNAMICS_PREFIX = "thermodynamics";
        String ANNOTATION_FREE_ENERGY = "standard_Gibbs_free_energy";
        String ANNOTATION_FREE_ENERGY_ESTIMATOR = "estimator";
        String ANNOTATION_FREE_ENERGY_VALUE = "value";
        String ANNOTATION_FREE_ENERGY_UNIT = "unit";
        String ANNOTATION_FREE_ENERGY_PH = "pH";
        String ANNOTATION_FREE_ENERGY_TEMP = "absolute_temperature";

        String VERSION = "1.0";

        // these 4 below are fixed for our free energy data.
        String FREE_ENERGY_UNIT = "kilojoule per mole";
        String FREE_ENERGY_PH = "7";
        String FREE_ENERGY_TEMP = "298.15";
        String FREE_ENERGY_ESTIMATOR = "http://equilibrator.weizmann.ac.il";
        try {
            for (int j = 0; j < EC_NUMBERS.length; j++) {
                SBOLDocument doc = new SBOLDocument();
                doc.addNamespace(URI.create(ANNOTATION_PREFIX_URI), ANNOTATION_PREFIX);
                doc.addNamespace(URI.create(ANNOTATION_THERMODYNAMICS_PREFIX_URI), ANNOTATION_THERMODYNAMICS_PREFIX);
                doc.setDefaultURIprefix(URI_PREFIX);

                String ecNumber = null;
                String modID = null;
                ModuleDefinition mdef = null;
                if (!EC_NUMBERS[j].equals("NULL")) {
                    ecNumber = "EC_" + EC_NUMBERS[j].replace('.', '_');
                    modID = REACTION_ID + "_with_" + ecNumber;
                    mdef = doc.createModuleDefinition(modID);
                    mdef.createFunctionalComponent("modifier", AccessType.PUBLIC, URI.create(URI_ENZYME_PREFIX + ecNumber), DirectionType.INOUT);
                } else {
                    modID = REACTION_ID;
                    mdef = doc.createModuleDefinition(modID);
                }

                for (int i = 0; i < REACTANTS.length; i++) {
                    mdef.createFunctionalComponent(REACTANTS[i] + "_reactant", AccessType.PUBLIC, URI.create(URI_COMPOUND_PREFIX + REACTANTS[i]), DirectionType.INOUT);
                }
                for (int i = 0; i < PRODUCTS.length; i++) {
                    mdef.createFunctionalComponent(PRODUCTS[i] + "_product", AccessType.PUBLIC, URI.create(URI_COMPOUND_PREFIX + PRODUCTS[i]), DirectionType.INOUT);
                }

                Interaction reaction = mdef.createInteraction("transformation", SystemsBiologyOntology.BIOCHEMICAL_REACTION);
                if (!EC_NUMBERS[j].equals("NULL")) {
                    reaction.createParticipation(ecNumber, "modifier", SystemsBiologyOntology.MODIFIER);
                }

                for (int i = 0; i < REACTANTS.length; i++) {
                    Participation participation = reaction.createParticipation(REACTANTS[i] + "_reactant", REACTANTS[i] + "_reactant", SystemsBiologyOntology.REACTANT);
                    participation.createAnnotation(new QName(ANNOTATION_PREFIX_URI, ANNOTATION_STOICHIOMETRY, ANNOTATION_PREFIX), REACTANTS_STOICHIOMETRY[i]);
                }
                for (int i = 0; i < PRODUCTS.length; i++) {
                    Participation participation = reaction.createParticipation(PRODUCTS[i] + "_product", PRODUCTS[i] + "_product", SystemsBiologyOntology.PRODUCT);
                    participation.createAnnotation(new QName(ANNOTATION_PREFIX_URI, ANNOTATION_STOICHIOMETRY, ANNOTATION_PREFIX), PRODUCTS_STOICHIOMETRY[i]);
                }

                if (!FREE_ENERGY_VALUE.equals("0")) {
                    Vector<Annotation> vec = new Vector<Annotation>();
                    vec.addElement(new Annotation(new QName(ANNOTATION_THERMODYNAMICS_PREFIX_URI, ANNOTATION_FREE_ENERGY_VALUE, ANNOTATION_THERMODYNAMICS_PREFIX), FREE_ENERGY_VALUE));
                    vec.addElement(new Annotation(new QName(ANNOTATION_THERMODYNAMICS_PREFIX_URI, ANNOTATION_FREE_ENERGY_UNIT, ANNOTATION_THERMODYNAMICS_PREFIX), FREE_ENERGY_UNIT));
                    vec.addElement(new Annotation(new QName(ANNOTATION_THERMODYNAMICS_PREFIX_URI, ANNOTATION_FREE_ENERGY_ESTIMATOR, ANNOTATION_THERMODYNAMICS_PREFIX), FREE_ENERGY_ESTIMATOR));
                    vec.addElement(new Annotation(new QName(ANNOTATION_THERMODYNAMICS_PREFIX_URI, ANNOTATION_FREE_ENERGY_PH, ANNOTATION_THERMODYNAMICS_PREFIX), FREE_ENERGY_PH));
                    vec.addElement(new Annotation(new QName(ANNOTATION_THERMODYNAMICS_PREFIX_URI, ANNOTATION_FREE_ENERGY_TEMP, ANNOTATION_THERMODYNAMICS_PREFIX), FREE_ENERGY_TEMP));
                    mdef.createAnnotation(new QName(ANNOTATION_THERMODYNAMICS_PREFIX_URI, ANNOTATION_FREE_ENERGY, ANNOTATION_THERMODYNAMICS_PREFIX), new QName(ANNOTATION_THERMODYNAMICS_PREFIX_URI, "information", ANNOTATION_THERMODYNAMICS_PREFIX), mdef.getIdentity(), vec);
                }
                SBOLWriter.write(doc, f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void newECNumber(String f, String EC_NUMBER,String[] NAMES,  String[] SYNONYMS, String EXTERNAL_LINK, String[] FORMULAS, String[] COFACTORS, String prefix ) {
        String ANNOTATION_PREFIX_URI = prefix + "/annotation/enzyme";
        String ANNOTATION_PREFIX = "enzyme";
        String ANNOTATION_SYNONYM = "synonym";
        String ANNOTATION_LINK = "source";
        String ANNOTATION_FORMULA = "formula";
        String ANNOTATION_COFACTOR = "cofactor";
        String URI_PREFIX = prefix;

//        // this one is from ID.  ID is 2.3.1.22 here, and this is changed to EC_2_3_1_22.
//        String EC_NUMBER = "EC_2_3_1_22";
//        // this is from DE.  The final period should be removed.
//        String[] NAMES = {"2-acylglycerol O-acyltransferase"};
//        // this is from AN.  The final period should be removed.
//        String[] SYNONYMS = {"Acylglycerol palmitoyltransferase", "Monoglyceride acyltransferase"};
//        // the URL of EC number.  For EC:2.3.1.22, the URL is http://www.chem.qmul.ac.uk/iubmb/enzyme/EC2/3/1/22.html
//        String EXTERNAL_LINK = "http://www.chem.qmul.ac.uk/iubmb/enzyme/EC" + EC_NUMBER.replace("EC_", "").replace("_", "/") + ".html";
//        // this is from CA.  The final period should be removed.
//        String[] FORMULAS = {"Acyl-CoA + 2-acylglycerol = CoA + diacylglycerol"};
//        // this is from CF.  The final period should be removed.
//        String[] COFACTORS = {"NULL"};
        try {
            SBOLDocument doc = new SBOLDocument();
            QName qName = null;
            doc.setDefaultURIprefix(ANNOTATION_PREFIX_URI);
            doc.setTypesInURIs(true);
            doc.addNamespace(URI.create(ANNOTATION_PREFIX_URI), ANNOTATION_PREFIX);
            doc.setDefaultURIprefix(URI_PREFIX);

            ComponentDefinition cdef = doc.createComponentDefinition(EC_NUMBER, ComponentDefinition.PROTEIN);
            cdef.setName(NAMES[0]);

            qName = new QName(ANNOTATION_PREFIX_URI, ANNOTATION_SYNONYM, ANNOTATION_PREFIX);
            for (int i = 1; i < NAMES.length; i++) if (!NAMES[i].equals("NULL")) cdef.createAnnotation(qName, NAMES[i]);

            qName = new QName(ANNOTATION_PREFIX_URI, ANNOTATION_SYNONYM, ANNOTATION_PREFIX);
            for (int i = 0; i < SYNONYMS.length; i++)
                if (!SYNONYMS[i].equals("NULL")) cdef.createAnnotation(qName, SYNONYMS[i]);

            qName = new QName(ANNOTATION_PREFIX_URI, ANNOTATION_LINK, ANNOTATION_PREFIX);
            if (!EXTERNAL_LINK.equals("NULL")) cdef.createAnnotation(qName, EXTERNAL_LINK);

            qName = new QName(ANNOTATION_PREFIX_URI, ANNOTATION_FORMULA, ANNOTATION_PREFIX);
            for (int i = 0; i < FORMULAS.length; i++)
                if (!FORMULAS[i].equals("NULL")) cdef.createAnnotation(qName, FORMULAS[i]);

            qName = new QName(ANNOTATION_PREFIX_URI, ANNOTATION_COFACTOR, ANNOTATION_PREFIX);
            for (int i = 0; i < COFACTORS.length; i++)
                if (!COFACTORS[i].equals("NULL")) cdef.createAnnotation(qName, COFACTORS[i]);
            SBOLWriter.write(doc, f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void newProtein(String f, String PROTEIN_ID,  String[] PROTEIN_NAMES, String ORGANISM_ID,
                                  String ORGANISM_NAME, String ORGANISM_URL, String[] EXTERNAL_LINKS,  String AA_SEQ,  String[] EC_NUMBERS, String prefix) {
        String ANNOTATION_PREFIX_URI = prefix + "/annotation/protein";
        String ANNOTATION_ORGANISM_PREFIX_URI = prefix +  "/annotation/kegg/organism";
        String ANNOTATION_ORGANISM_URL = "link";
        String ANNOTATION_ORGANISM_ID = "id";
        String ANNOTATION_ORGANISM_NAME = "name";
        String ANNOTATION_PREFIX = "protein";
        String ANNOTATION_ORGANISM_PREFIX = "organism";
        String ANNOTATION_LINK = "source";
        String ANNOTATION_EC = "ec_number";
        String ANNOTATION_SYNONYM = "synonym";
        String URI_PREFIX = prefix;
//
//        // this ID is from KEGG ID
//        String PROTEIN_ID = "Hhal_1820";
//        // this name is from te KO field
//        String[] PROTEIN_NAMES = {"tyrosine ammonia-lyase", "aaa"};
//
//        // this ID is KEGG organism ID
//        String ORGANISM_ID = "hha";
//        // this name is from Organism field.
//        String ORGANISM_NAME = "Halorhodospira halophila";
//        String ORGANISM_URL = "http://www.genome.jp/kegg-bin/show_organism?org=" + ORGANISM_ID;
//        // this is from a field called "Other DBs"
//        String[] EXTERNAL_LINKS = {"http://www.ncbi.nlm.nih.gov/protein/ABM62584", "http://maple.lsd.ornl.gov/cgi-bin/JGI_microbial/gene_viewer.cgi?org=hhal&chr=12oct06&contig=Contig26&gene=Hhal1820", "http://www.uniprot.org/uniprot/A1WY22"};
//        // amino acid sequence
//        String AA_SEQ = "MAEVDLAGSLSAADIEAIGYGHRTATVSPTGWKRLRSAEAYLQRLVDERRQVYGVTTGYGPLATSRIDPSASRTLQRNLVYHLCSGVGEPLSRCHTRATLGARIASVTRGHSGVTPAVVERLLAWLEHDVVPEVPAIGTVGASGDLTPLAHVARALMGEGRVCINGGEWEPADAAQRRLGWEPWTLDGKDAIALVNGTSTTAGICAVNGAGAERAAGVCAVLGMVYAELLGGHAEAFQPAIGAVRPHPGQMRAHAWLTALAEDSQRLQPWTGTPPRLTEGQEAVLPDQPLPQDPYSIRCLPQALGAVLDSITFHNQTVASELDAASDNPLLFPDEGRVLHGGNFFGQHLAFAADALNNAVVQLALHSERRISRITDSTRSGFPAFMQPRQTGLHSGFMGAQVTASALVAEMRTGAHPASIQSIPTNADNQDIVPMSTRAARQAATNLDHLQRILAIEALVLAQGLELADGVGFSSSARRTLGWVRELAPPLEDDRPLAEEIARVAAALATPYQAHRLVAGLPGAPPGPAS";
//        // if this protein is an enzyme, put its EC number.  The value is from the KO field.
//        String[] EC_NUMBERS = {"4.3.1.23", "4.3.1.24"};


        String ANNOTATION_STRUCTURE_PREFIX_URI = prefix + "/annotation/protein/structure#";
        String ANNOTATION_CATALYTIC_SITE_PREFIX = "catalytic_site";
        String ANNOTATION_CATALYTIC_SITE_PREFIX_URI = prefix + "/annotation/enzyme/structure/catalytic_site#";
        String ANNOTATION_STRUCTURE_PREFIX = "structure";
        String ANNOTATION_CATALYTIC_SITES = "catalytic_sites";
        String ANNOTATION_CATALYTIC_SITE = "catalytic_site";
        String ANNOTATION_CATALYTIC_SITE_RESIDUE = "residue";
        String ANNOTATION_CATALYTIC_SITE_EVIDENCE = "evidence";
        String ANNOTATION_RESIDUE_PREFIX_URI = prefix + "/annotation/protein/structure/residue#";
        String ANNOTATION_RESIDUE_PREFIX = "residue";
        String ANNOTATION_RESIDUE_TYPE = "type";
        String ANNOTATION_RESIDUE_CHAIN = "chain";
        String ANNOTATION_RESIDUE_PDB_NUMBER = "PDB_number";
        String ANNOTATION_RESIDUE_UNIPROTKB_NUMBER = "UniProtKB_number";


        String[] SITE_LINKS = {
                "http://www.ebi.ac.uk/thornton-srv/databases/CSA/SearchBySite.php?PDBID=1ae4&siteNum=1&type=HOM",
                "http://www.ebi.ac.uk/thornton-srv/databases/CSA/SearchBySite.php?PDBID=1ae4&siteNum=2&type=HOM",
                "http://www.ebi.ac.uk/thornton-srv/databases/CSA/SearchBySite.php?PDBID=1ae4&siteNum=3&type=HOM"
        };

        String[] RESIDUE_TYPES = {
                "Lys$Asp$Tyr$His",
                "Lys$Tyr",
                "Lys$Asp$Tyr"
        };

        String[] RESIDUE_CHAINS = {
                "A$A$A$A",
                "A$A",
                "A$A$A"
        };

        String[] RESIDUE_PDB_NUMBERS = {
                "80$45$50$113",
                "80$50",
                "80$45$50"
        };

        String[] RESIDUE_UNIPROTKB_NUMBERS = {
                "80$45$50$113",
                "80$50",
                "80$45$50"
        };

        String[] SITE_EVIDENCE = {
                "homology",
                "homology",
                "homology"
        };


        QName siteLinkQName = null;
        QName siteEvidenceQName = null;
        QName residueTypeQName = null;
        QName residueChainQName = null;
        QName residuePDBQName = null;
        QName residueUniProtQName = null;
        QName catalyticSiteResidue = null;
        try {
            SBOLDocument doc = new SBOLDocument();
            QName qName = null;
            doc.addNamespace(URI.create(ANNOTATION_PREFIX_URI), ANNOTATION_PREFIX);
            doc.addNamespace(URI.create(ANNOTATION_ORGANISM_PREFIX_URI), ANNOTATION_ORGANISM_PREFIX);
            doc.setDefaultURIprefix(URI_PREFIX);
            doc.setTypesInURIs(true);

            ComponentDefinition cdef = doc.createComponentDefinition(PROTEIN_ID, ComponentDefinition.PROTEIN);
            cdef.setName(PROTEIN_NAMES[0]);
            if (!AA_SEQ.equals("NULL"))
                cdef.addSequence(doc.createSequence(PROTEIN_ID + "_seq", AA_SEQ, Sequence.IUPAC_PROTEIN));
            for (int i = 0; i < EC_NUMBERS.length; i++) {
                if (!EC_NUMBERS[i].equals("NULL")) {
                    qName = new QName(ANNOTATION_PREFIX_URI, ANNOTATION_EC, ANNOTATION_PREFIX);
                    cdef.createAnnotation(qName, EC_NUMBERS[i]);
                }
            }
            qName = new QName(ANNOTATION_PREFIX_URI, ANNOTATION_ORGANISM_URL, ANNOTATION_ORGANISM_PREFIX);
            cdef.createAnnotation(qName, ORGANISM_URL);
            qName = new QName(ANNOTATION_PREFIX_URI, ANNOTATION_ORGANISM_ID, ANNOTATION_ORGANISM_PREFIX);
            cdef.createAnnotation(qName, ORGANISM_ID);
            qName = new QName(ANNOTATION_PREFIX_URI, ANNOTATION_ORGANISM_NAME, ANNOTATION_ORGANISM_PREFIX);
            cdef.createAnnotation(qName, ORGANISM_NAME);

            qName = new QName(ANNOTATION_PREFIX_URI, ANNOTATION_SYNONYM, ANNOTATION_PREFIX);
            for (int i = 0; i < PROTEIN_NAMES.length; i++) cdef.createAnnotation(qName, PROTEIN_NAMES[i]);

            qName = new QName(ANNOTATION_PREFIX_URI, ANNOTATION_LINK, ANNOTATION_PREFIX);
            for (int i = 0; i < EXTERNAL_LINKS.length; i++) cdef.createAnnotation(qName, URI.create(EXTERNAL_LINKS[i]));


            //setAnnotationOfCatalyticSites
            if (RESIDUE_TYPES[0].equals("NULL")) {
                return;
            }
            Vector<Annotation> vec = new Vector<Annotation>();

            siteEvidenceQName = new QName(ANNOTATION_CATALYTIC_SITE_PREFIX_URI, ANNOTATION_CATALYTIC_SITE_EVIDENCE, ANNOTATION_CATALYTIC_SITE_PREFIX);
            catalyticSiteResidue = new QName(ANNOTATION_RESIDUE_PREFIX_URI, ANNOTATION_CATALYTIC_SITE_RESIDUE, ANNOTATION_STRUCTURE_PREFIX);
            residueTypeQName = new QName(ANNOTATION_RESIDUE_PREFIX_URI, ANNOTATION_RESIDUE_TYPE, ANNOTATION_RESIDUE_PREFIX);
            residueChainQName = new QName(ANNOTATION_RESIDUE_PREFIX_URI, ANNOTATION_RESIDUE_CHAIN, ANNOTATION_RESIDUE_PREFIX);
            residuePDBQName = new QName(ANNOTATION_RESIDUE_PREFIX_URI, ANNOTATION_RESIDUE_PDB_NUMBER, ANNOTATION_RESIDUE_PREFIX);
            residueUniProtQName = new QName(ANNOTATION_RESIDUE_PREFIX_URI, ANNOTATION_RESIDUE_UNIPROTKB_NUMBER, ANNOTATION_RESIDUE_PREFIX);


            for (int j = 0; j < RESIDUE_TYPES.length; j++) {
                int siteNum = j;
                String[] residueTypes = RESIDUE_TYPES[siteNum].split("\\$");
                String[] residueChains = RESIDUE_CHAINS[siteNum].split("\\$");
                String[] residuePDBs = RESIDUE_PDB_NUMBERS[siteNum].split("\\$");
                String[] residueUniProts = RESIDUE_UNIPROTKB_NUMBERS[siteNum].split("\\$");
                Vector<Annotation> vec1 = new Vector<Annotation>();
                URI siteURI = new URL(SITE_LINKS[siteNum]).toURI();
                for (int i = 0; i < residueTypes.length; i++) {
                    //createAnnotationOfResidue
                    Vector<Annotation> vec2 = new Vector<Annotation>();
                    QName qName2 = null;

                    qName2 = new QName(ANNOTATION_RESIDUE_PREFIX_URI, ANNOTATION_RESIDUE_TYPE, ANNOTATION_RESIDUE_PREFIX);
                    vec2.add(new Annotation(qName2, residueTypes[i]));

                    qName2 = new QName(ANNOTATION_RESIDUE_PREFIX_URI, ANNOTATION_RESIDUE_CHAIN, ANNOTATION_RESIDUE_PREFIX);
                    vec2.add(new Annotation(qName2, residueChains[i]));

                    if (!residuePDBs[i].equals("NULL")) {
                        qName2 = new QName(ANNOTATION_RESIDUE_PREFIX_URI, ANNOTATION_RESIDUE_PDB_NUMBER, ANNOTATION_RESIDUE_PREFIX);
                        vec2.add(new Annotation(qName2, new Integer(residuePDBs[i])));
                    }

                    if (!residueUniProts[i].equals("NULL")) {
                        qName2 = new QName(ANNOTATION_RESIDUE_PREFIX_URI, ANNOTATION_RESIDUE_UNIPROTKB_NUMBER, ANNOTATION_RESIDUE_PREFIX);
                        vec2.add(new Annotation(qName2, new Integer(residueUniProts[i])));
                    }

                    qName2 = new QName(ANNOTATION_RESIDUE_PREFIX_URI, "information", ANNOTATION_STRUCTURE_PREFIX);
                    vec1.add(new Annotation(catalyticSiteResidue, qName2, siteURI, vec2));
                }
                vec1.add(new Annotation(siteEvidenceQName, SITE_EVIDENCE[siteNum]));
                QName qName1 = new QName(ANNOTATION_STRUCTURE_PREFIX_URI, ANNOTATION_CATALYTIC_SITE, ANNOTATION_STRUCTURE_PREFIX);
                vec.addElement(new Annotation(qName1, new QName(ANNOTATION_STRUCTURE_PREFIX_URI, "information", ANNOTATION_STRUCTURE_PREFIX), siteURI, vec1));
            }

            QName sitesQName = new QName(ANNOTATION_STRUCTURE_PREFIX_URI, ANNOTATION_CATALYTIC_SITES, ANNOTATION_STRUCTURE_PREFIX);
            cdef.createAnnotation(sitesQName, new QName(ANNOTATION_STRUCTURE_PREFIX_URI, "information", ANNOTATION_STRUCTURE_PREFIX), cdef.getIdentity(), vec);

            SBOLWriter.write(doc, f);
        } catch (Exception e) {

        }
    }

}
