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
import java.util.Scanner;
import java.util.Vector;

import javax.management.relation.Role;
import javax.xml.namespace.QName;

import org.freehep.graphicsio.test.TestTransparency;
import org.sbolstandard.core2.*;

/**
 * Created by Umarov on 2/3/2017.
 */
public class PartsCreator {

    private static final String SBOLME_ANNOTATION_PREFIX_URL = "http://www.cbrc.kaust.edu.sa/sbolme/annotation";

    public static void createCompound(String f, String prefixURI, String compoundID, String smilesSeq, String sourceURI, String formula, String[] synonyms,
                                      String[] annotationPrefixURIs, String[] annotationPrefixes, String[] annotationKeys, String[] annotationValues) throws PartsCreationException {

        final String annotationPrefixURI = SBOLME_ANNOTATION_PREFIX_URL + "/compound#";
        final String annotationCompoundPrefix = "compound";
        final String annotationSynonym = "synonym";
        final String annotationFormula = "formula";
        final String compoundPrefixURI = prefixURI + "/parts/compound";
        final URI smilesEncoding = URI.create("http://www.opensmiles.org/opensmiles.html");

        SBOLDocument doc = new SBOLDocument();
        try {
            doc.setTypesInURIs(false);
            doc.setDefaultURIprefix(compoundPrefixURI);
            doc.addNamespace(URI.create(annotationPrefixURI), annotationCompoundPrefix);
            for (int i = 0; i < annotationPrefixURIs.length; i++) {
                doc.addNamespace(URI.create(annotationPrefixURIs[i]), annotationPrefixes[i]);
            }

            ComponentDefinition cdef = doc.createComponentDefinition(compoundID, ComponentDefinition.SMALL_MOLECULE);
            if (sourceURI != null) cdef.setWasDerivedFrom(new URI(sourceURI));
            if (smilesSeq != null) cdef.addSequence(doc.createSequence(compoundID + "_seq", smilesSeq, smilesEncoding));

            if (formula != null)
                cdef.createAnnotation(new QName(annotationPrefixURI, annotationFormula, annotationCompoundPrefix), formula);
            if (synonyms[0] != null) {
                cdef.setName(synonyms[0]);
                QName qName = new QName(annotationPrefixURI, annotationSynonym, annotationCompoundPrefix);
                for (int i = 1; i < synonyms.length; i++) cdef.createAnnotation(qName, synonyms[i]);
            }

            for (int i = 0; i < annotationPrefixURIs.length; i++) {
                QName qName = new QName(annotationPrefixURIs[i], annotationKeys[i], annotationPrefixes[i]);
                cdef.createAnnotation(qName, annotationValues[i]);
            }
            SBOLWriter.write(doc, f);
        } catch (Exception e) {
            throw new PartsCreationException(e.getMessage());
        }
    }

    private static void testCompoundCreation() {
        String f;
        String prefixURI;
        String compoundID;
        String smilesSeq;
        String sourceURI;
        String formula;
        String[] synonyms;
        String[] annotationPrefixURIs;
        String[] annotationPrefixes;
        String[] annotationKeys;
        String[] annotationValues;


        f = System.getProperty("user.home") + "/compound1.xml";
        System.out.println(f);
        prefixURI = "http://www.aaa.bbb";
        compoundID = "C00024";
        smilesSeq = "CC(=O)SCCNC(=O)CCNC(=O)C(C(C)(C)COP(=O)(O)OP(=O)(O)OCC1C(C(C(O1)N2C=NC3=C2N=CN=C3N)O)OP(=O)(O)O)O";
//		smilesSeq = null;
        sourceURI = "https://pubchem.ncbi.nlm.nih.gov/compound/444493";
        formula = "C23H38N7O17P3S";
        synonyms = new String[]{"Acetyl-CoA", "Acetyl-CoA"};
        annotationPrefixURIs = new String[]{"http://test.test", "http://test.test"};
        annotationPrefixes = new String[]{"test", "test"};
        annotationKeys = new String[]{"key1", "key2"};
        annotationValues = new String[]{"val1", "val2"};

        try {
            createCompound(f, prefixURI, compoundID, smilesSeq, sourceURI, formula, synonyms, annotationPrefixURIs, annotationPrefixes, annotationKeys, annotationValues);
        } catch (PartsCreationException e) {
            System.out.println("error");
            e.printStackTrace();
        }
        return;
    }


    public static void createReaction(String f, String prefixURI, String reactionID, String sourceURI, String[] synonyms, String[] reactants, String[] products, double[] reactantsStoichiometry, double[] productsStoichiometry,
                                      String[] enzymeClassSchemes, String[] enzymeClassIDs, String[] annotationPrefixURIs, String[] annotationPrefixes, String[] annotationKeys, String[] annotationValues)
            throws PartsCreationException {

        final String reactionPrefixURI = prefixURI + "/parts/reaction";
        final String genericEnzymeURI = prefixURI + "/parts/enzyme/ENZYME";

        final String annotationSynonym = "synonym";
        final String annotationEnzymeClassPrefixURI = SBOLME_ANNOTATION_PREFIX_URL + "/enzyme_class#";
        final String annotationEnzymeClassPrefix = "enzyme_class";
        //final String annotationEnzymeClassScheme = "scheme";
        final String annotationEnzymeClassID = "id";

        final String reactionAnnotationPrefixURI = SBOLME_ANNOTATION_PREFIX_URL + "/reaction#";
        final String annotationPrefix = "reaction";
        final String annotationStoichiometry = "stoichiometry";

        SBOLDocument doc = new SBOLDocument();
        try {
            doc.setTypesInURIs(false);
            doc.setDefaultURIprefix(reactionPrefixURI);
            doc.addNamespace(URI.create(reactionAnnotationPrefixURI), annotationPrefix);
            doc.addNamespace(URI.create(annotationEnzymeClassPrefixURI), annotationEnzymeClassPrefix);
            for (int i = 0; i < annotationPrefixURIs.length; i++) {
                doc.addNamespace(URI.create(annotationPrefixURIs[i]), annotationPrefixes[i]);
            }

            ModuleDefinition mdef = doc.createModuleDefinition(reactionID);
            if (sourceURI != null) mdef.setWasDerivedFrom(new URI(sourceURI));

            if (synonyms[0] != null) {
                mdef.setName(synonyms[0]);
                QName qName = new QName(reactionAnnotationPrefixURI, annotationSynonym, annotationPrefix);
                for (int i = 1; i < synonyms.length; i++) mdef.createAnnotation(qName, synonyms[i]);
            }


            if (enzymeClassIDs.length > 0) {
                FunctionalComponent fCom = mdef.createFunctionalComponent("enzyme", AccessType.PUBLIC, URI.create(genericEnzymeURI), DirectionType.INOUT);
                for (int i = 0; i < enzymeClassIDs.length; i++) {
                    //fCom.createAnnotation(new QName(annotationEnzymeClassPrefixURI, annotationEnzymeClassScheme, annotationEnzymeClassPrefix), enzymeClassSchemes[i]);
                    //fCom.createAnnotation(new QName(annotationEnzymeClassPrefixURI, annotationEnzymeClassID, annotationEnzymeClassPrefix), enzymeClassIDs[i]);
                    fCom.createAnnotation(new QName(annotationEnzymeClassPrefixURI, annotationEnzymeClassID, annotationEnzymeClassPrefix), enzymeClassSchemes[i] + ":" + enzymeClassIDs[i]);
                }
            }
            for (int i = 0; i < reactants.length; i++) {
                mdef.createFunctionalComponent("reactant_" + Integer.toString(i + 1), AccessType.PUBLIC, URI.create(reactants[i]), DirectionType.INOUT);
            }
            for (int i = 0; i < products.length; i++) {
                mdef.createFunctionalComponent("product_" + Integer.toString(i + 1), AccessType.PUBLIC, URI.create(products[i]), DirectionType.INOUT);
            }

            Interaction reaction = mdef.createInteraction("transformation", SystemsBiologyOntology.BIOCHEMICAL_REACTION);
            for (int i = 0; i < reactantsStoichiometry.length; i++) {
                String displayID = reactants[i].substring(reactants[i].lastIndexOf("/") + 1);
                Participation participation = reaction.createParticipation(displayID, "reactant_" + Integer.toString(i + 1), SystemsBiologyOntology.REACTANT);
                participation.createAnnotation(new QName(reactionAnnotationPrefixURI, annotationStoichiometry, annotationPrefix), reactantsStoichiometry[i]);
            }

            for (int i = 0; i < productsStoichiometry.length; i++) {
                String displayID = products[i].substring(products[i].lastIndexOf("/") + 1);
                Participation participation = reaction.createParticipation(displayID, "product_" + Integer.toString(i + 1), SystemsBiologyOntology.PRODUCT);
                participation.createAnnotation(new QName(reactionAnnotationPrefixURI, annotationStoichiometry, annotationPrefix), productsStoichiometry[i]);
            }
            if (enzymeClassIDs.length > 0) {
                reaction.createParticipation("catalyst", "enzyme", SystemsBiologyOntology.MODIFIER);
            }

            for (int i = 0; i < annotationPrefixURIs.length; i++) {
                QName qName = new QName(annotationPrefixURIs[i], annotationKeys[i], annotationPrefixes[i]);
                mdef.createAnnotation(qName, annotationValues[i]);
            }

            SBOLWriter.write(doc, f);
        } catch (Exception e) {
            throw new PartsCreationException(e.getMessage());
        }
    }


    private static void testReactionCreation() {
        String f;
        String prefixURI;
        String reactionID;
        String sourceURI;
        String [] synonyms;
        String[] reactants;
        String[] products;
        double[] reactantsStoichiometry;
        double[] productsStoichiometry;
        String[] enzymeClassSchemes;
        String[] enzymeClassIDs;
        String[] annotationPrefixURIs;
        String[] annotationPrefixes;
        String[] annotationKeys;
        String[] annotationValues;


        f = System.getProperty("user.home") + "/reaction1.xml";
        System.out.println(f);
        prefixURI = "http://www.aaa.bbb";
        reactionID = "R00209";
        sourceURI = "http://www.genome.jp/dbget-bin/www_bget?R00209";
        synonyms = new String[]{"Methylmalonyl-CoA carboxyltransferase", "Transcarboxylase"};
        reactants = new String[]{
                "http://www.cbrc.kaust.edu.sa/sbolme/parts/compound/ME_C00003",
                "http://www.cbrc.kaust.edu.sa/sbolme/parts/compound/ME_C00010",
                "http://www.cbrc.kaust.edu.sa/sbolme/parts/compound/ME_C00022"
        };
        reactantsStoichiometry = new double[]{1, 1, 1};
        products = new String[]{
                "http://www.cbrc.kaust.edu.sa/sbolme/parts/compound/ME_C00011",
                "http://www.cbrc.kaust.edu.sa/sbolme/parts/compound/ME_C00024",
                "http://www.cbrc.kaust.edu.sa/sbolme/parts/compound/ME_C00004",
                "http://www.cbrc.kaust.edu.sa/sbolme/parts/compound/ME_C00080"
        };
        productsStoichiometry = new double[]{1, 1, 1, 1};
        enzymeClassSchemes = new String[]{"ec", "ec", "ec"};
        enzymeClassIDs = new String[]{"1.2.4.1", "1.8.1.4", "2.3.1.12"};
        annotationPrefixURIs = new String[]{"http://test.test", "http://test.test"};
        annotationPrefixes = new String[]{"test", "test"};
        annotationKeys = new String[]{"key1", "key2"};
        annotationValues = new String[]{"val1", "val2"};

        try {
            createReaction(f, prefixURI, reactionID, sourceURI, synonyms, reactants, products, reactantsStoichiometry, productsStoichiometry,
                    enzymeClassSchemes, enzymeClassIDs, annotationPrefixURIs, annotationPrefixes, annotationKeys, annotationValues);
        } catch (PartsCreationException e) {
            System.out.println("error");
            e.printStackTrace();
        }

        return;
    }


    public static void createEnzymeClass(String f, String prefixURI, String id, String enzymeClassScheme, String enzymeClassID, String sourceURI, String[] synonyms, String[] formula, String[] cofactors,
                                         String[] annotationPrefixURIs, String[] annotationPrefixes, String[] annotationKeys, String[] annotationValues) throws PartsCreationException {

        final String enzymeClassPrefixURI = prefixURI + "/parts/enzyme_class";
        final String annotationEnzymeClassPrefixURI = SBOLME_ANNOTATION_PREFIX_URL + "/enzyme_class#";
        final String annotationEnzymeClassPrefix = "enzyme_class";
        //final String annotationEnzymeClassScheme = "scheme";
        final String annotationEnzymeClassID = "id";

        final String annotationSynonym = "synonym";
        final String annotationFormula = "formula";
        final String annotationCofactor = "cofactor";

        final String sbolID = id;
        SBOLDocument doc = new SBOLDocument();

        try {
            doc.setTypesInURIs(false);
            doc.addNamespace(URI.create(annotationEnzymeClassPrefixURI), annotationEnzymeClassPrefix);
            for (int i = 0; i < annotationPrefixURIs.length; i++) {
                doc.addNamespace(URI.create(annotationPrefixURIs[i]), annotationPrefixes[i]);
            }
            doc.setDefaultURIprefix(enzymeClassPrefixURI);

            ComponentDefinition cdef = doc.createComponentDefinition(sbolID, ComponentDefinition.PROTEIN);
            if (synonyms.length > 0) cdef.setName(synonyms[0]);
            if (sourceURI != null) cdef.setWasDerivedFrom(new URI(sourceURI));

//	        cdef.createAnnotation(new QName(annotationEnzymeClassPrefixURI, annotationEnzymeClassScheme, annotationEnzymeClassPrefix), enzymeClassScheme);
//	        cdef.createAnnotation(new QName(annotationEnzymeClassPrefixURI, annotationEnzymeClassID, annotationEnzymeClassPrefix), enzymeClassID);
            cdef.createAnnotation(new QName(annotationEnzymeClassPrefixURI, annotationEnzymeClassID, annotationEnzymeClassPrefix), enzymeClassScheme + ":" + enzymeClassID);

            QName qName = new QName(annotationEnzymeClassPrefixURI, annotationSynonym, annotationEnzymeClassPrefix);
            for (int i = 0; i < synonyms.length; i++) cdef.createAnnotation(qName, synonyms[i]);

            qName = new QName(annotationEnzymeClassPrefixURI, annotationFormula, annotationEnzymeClassPrefix);
            for (int i = 0; i < formula.length; i++) cdef.createAnnotation(qName, formula[i]);

            qName = new QName(annotationEnzymeClassPrefixURI, annotationCofactor, annotationEnzymeClassPrefix);
            for (int i = 0; i < cofactors.length; i++) cdef.createAnnotation(qName, cofactors[i]);

            for (int i = 0; i < annotationPrefixURIs.length; i++) {
                qName = new QName(annotationPrefixURIs[i], annotationKeys[i], annotationPrefixes[i]);
                cdef.createAnnotation(qName, annotationValues[i]);
            }

            SBOLWriter.write(doc, f);
        } catch (Exception e) {
            throw new PartsCreationException(e.getMessage());
        }
    }

    private static void testEnzymeClassCreation() {
        String f;
        String prefixURI;
        String id;
        String enzymeClassScheme;
        String enzymeClassID;
        String sourceURI;
        String[] synonyms;
        String[] formula;
        String[] cofactors;
        String[] annotationPrefixURIs;
        String[] annotationPrefixes;
        String[] annotationKeys;
        String[] annotationValues;

        f = System.getProperty("user.home") + "/enzyme_class1.xml";
        System.out.println(f);
        prefixURI = "http://www.aaa.bbb";
        id = "ec_2_1_3_1";
        enzymeClassScheme = "ec";
        enzymeClassID = "2.1.3.1";
        sourceURI = "http://www.chem.qmul.ac.uk/iubmb/ecnum/EC2/1/3/1.html";
        synonyms = new String[]{"Methylmalonyl-CoA carboxyltransferase", "Transcarboxylase"};
        formula = new String[]{"(S)-methylmalonyl-CoA + pyruvate = propanoyl-CoA + oxaloacetate"};
        cofactors = new String[]{"Biotin", "Cobalt cation", "Zn(2+)"};

        annotationPrefixURIs = new String[]{"http://test.test", "http://test.test"};
        annotationPrefixes = new String[]{"test", "test"};
        annotationKeys = new String[]{"key1", "key2"};
        annotationValues = new String[]{"val1", "val2"};

        try {
            createEnzymeClass(f, prefixURI, id, enzymeClassScheme, enzymeClassID, sourceURI, synonyms, formula, cofactors, annotationPrefixURIs, annotationPrefixes, annotationKeys, annotationValues);
        } catch (PartsCreationException e) {
            System.out.println("error");
            e.printStackTrace();
        }

        return;
    }

    public static void createProtein(String f, String prefixURI, String proteinID, String sourceURI, String[] synonyms, String organismID,
                                     String organismName, String organismURI, String aaSeq, String[] enzymeClassSchemes, String[] enzymeClassIDs,
                                     String[] annotationPrefixURIs, String[] annotationPrefixes, String[] annotationKeys, String[] annotationValues) throws PartsCreationException {

        final String proteinPrefixURI = prefixURI + "/parts/protein/" + organismID;

        final String annotationProteinPrefix = "protein";
        final String annotationProteinPrefixURI = SBOLME_ANNOTATION_PREFIX_URL + "/protein#";

        final String annotationEnzymeClassPrefixURI = SBOLME_ANNOTATION_PREFIX_URL + "/enzyme_class#";
        final String annotationEnzymeClassPrefix = "enzyme_class";
        final String annotationEnzymeClassID = "id";

        final String annotationOrganismPrefixURI = SBOLME_ANNOTATION_PREFIX_URL + "/organism#";
        final String annotationOrganismPrefix = "organism";
        final String annotationOrganismSource = "source";
        final String annotationOrganismID = "id";
        final String annotationOrganismName = "name";

        SBOLDocument doc = new SBOLDocument();

        try {
            doc.setTypesInURIs(false);
            doc.addNamespace(URI.create(annotationProteinPrefixURI), annotationProteinPrefix);
            doc.addNamespace(URI.create(annotationOrganismPrefixURI), annotationOrganismPrefix);
            for (int i = 0; i < annotationPrefixURIs.length; i++) {
                doc.addNamespace(URI.create(annotationPrefixURIs[i]), annotationPrefixes[i]);
            }

            doc.setDefaultURIprefix(proteinPrefixURI);
            ComponentDefinition cdef = doc.createComponentDefinition(proteinID, ComponentDefinition.PROTEIN);
            if (sourceURI != null) cdef.setWasDerivedFrom(new URI(sourceURI));

            if (synonyms.length > 0) cdef.setName(synonyms[0]);

            QName qName = new QName(annotationProteinPrefixURI, annotationOrganismID, annotationOrganismPrefix);
            cdef.createAnnotation(qName, organismID);

            qName = new QName(annotationProteinPrefixURI, annotationOrganismName, annotationOrganismPrefix);
            cdef.createAnnotation(qName, organismName);

            qName = new QName(annotationProteinPrefixURI, annotationOrganismSource, annotationOrganismPrefix);
            cdef.createAnnotation(qName, organismURI);

            qName = new QName(annotationEnzymeClassPrefixURI, annotationEnzymeClassID, annotationEnzymeClassPrefix);
            for (int i = 0; i < enzymeClassIDs.length; i++)
                cdef.createAnnotation(qName, enzymeClassSchemes[i] + ":" + enzymeClassIDs[i]);


            if (aaSeq != null) cdef.addSequence(doc.createSequence(proteinID + "_seq", aaSeq, Sequence.IUPAC_PROTEIN));

            for (int i = 0; i < annotationPrefixURIs.length; i++) {
                qName = new QName(annotationPrefixURIs[i], annotationKeys[i], annotationPrefixes[i]);
                cdef.createAnnotation(qName, annotationValues[i]);
            }
            SBOLWriter.write(doc, f);
        } catch (Exception e) {
            throw new PartsCreationException(e.getMessage());
        }
    }
    public static void createProtein(String f, String prefixURI, String proteinID, String sourceURI, String[] synonyms, String organismID,
                                     String organismName, String organismURI, String aaSeq, String cds, String[] enzymeClassSchemes, String[] enzymeClassIDs,
                                     String[] annotationPrefixURIs, String[] annotationPrefixes, String[] annotationKeys, String[] annotationValues) throws PartsCreationException {

        final String proteinPrefixURI = prefixURI + "/parts/protein/" + organismID;

        final String annotationProteinPrefix = "protein";
        final String annotationProteinPrefixURI = SBOLME_ANNOTATION_PREFIX_URL + "/protein#";

        final String annotationEnzymeClassPrefixURI = SBOLME_ANNOTATION_PREFIX_URL + "/enzyme_class#";
        final String annotationEnzymeClassPrefix = "enzyme_class";
        final String annotationEnzymeClassID = "id";

        final String annotationOrganismPrefixURI = SBOLME_ANNOTATION_PREFIX_URL + "/organism#";
        final String annotationOrganismPrefix = "organism";
        final String annotationOrganismSource = "source";
        final String annotationOrganismID = "id";
        final String annotationOrganismName = "name";

        SBOLDocument doc = new SBOLDocument();

        try {
            doc.setTypesInURIs(false);
            doc.addNamespace(URI.create(annotationProteinPrefixURI), annotationProteinPrefix);
            doc.addNamespace(URI.create(annotationOrganismPrefixURI), annotationOrganismPrefix);

            for (int i = 0; i < annotationPrefixURIs.length; i++) {
                doc.addNamespace(URI.create(annotationPrefixURIs[i]), annotationPrefixes[i]);
            }

            doc.setDefaultURIprefix(proteinPrefixURI);
            ComponentDefinition cdef = doc.createComponentDefinition(proteinID, ComponentDefinition.PROTEIN);
            if (sourceURI != null) cdef.setWasDerivedFrom(new URI(sourceURI));

            if (synonyms.length > 0) cdef.setName(synonyms[0]);

            QName qName = new QName(annotationProteinPrefixURI, annotationOrganismID, annotationOrganismPrefix);
            cdef.createAnnotation(qName, organismID);

            qName = new QName(annotationProteinPrefixURI, annotationOrganismName, annotationOrganismPrefix);
            cdef.createAnnotation(qName, organismName);

            qName = new QName(annotationProteinPrefixURI, annotationOrganismSource, annotationOrganismPrefix);
            cdef.createAnnotation(qName, organismURI);

            qName = new QName(annotationEnzymeClassPrefixURI, annotationEnzymeClassID, annotationEnzymeClassPrefix);
            for (int i = 0; i < enzymeClassIDs.length; i++)
                cdef.createAnnotation(qName, enzymeClassSchemes[i] + ":" + enzymeClassIDs[i]);


            if (aaSeq != null) cdef.addSequence(doc.createSequence(proteinID + "_seq", aaSeq, Sequence.IUPAC_PROTEIN));

            if( cds != null ) {
                for (int i = 0; i < annotationPrefixURIs.length; i++) {
                    qName = new QName(annotationPrefixURIs[i], annotationKeys[i], annotationPrefixes[i]);
                    cdef.createAnnotation(qName, annotationValues[i]);
                }

                cdef = doc.createComponentDefinition(proteinID + "_cds", ComponentDefinition.DNA);
                if (sourceURI != null) cdef.setWasDerivedFrom(new URI(sourceURI));

                if (synonyms.length > 0) cdef.setName(synonyms[0]);
                cdef.addRole(SequenceOntology.CDS);

                qName = new QName(annotationProteinPrefixURI, annotationOrganismID, annotationOrganismPrefix);
                cdef.createAnnotation(qName, organismID);

                qName = new QName(annotationProteinPrefixURI, annotationOrganismName, annotationOrganismPrefix);
                cdef.createAnnotation(qName, organismName);

                qName = new QName(annotationProteinPrefixURI, annotationOrganismSource, annotationOrganismPrefix);
                cdef.createAnnotation(qName, organismURI);

                cdef.addSequence(doc.createSequence(proteinID + "_coding_seq", cds, Sequence.IUPAC_DNA));
            }

            SBOLWriter.write(doc, f);
        } catch (Exception e) {
            throw new PartsCreationException(e.getMessage());
        }
    }



    private static void testProteinCreation() {
        String f;
        String prefixURI;
        String proteinID;
        String sourceURI;
        String[] synonyms;
        String organismID;
        String organismName;
        String organismURI;
        String aaSeq;
        String cds;
        String[] enzymeClassSchemes;
        String[] enzymeClassIDs;
        String[] annotationPrefixURIs;
        String[] annotationPrefixes;
        String[] annotationKeys;
        String[] annotationValues;

        f = System.getProperty("user.home") + "/protein1.xml";
        System.out.println(f);
        prefixURI = "http://www.aaa.bbb";
        proteinID = "caz_CARG_02115";
        sourceURI = "http://www.uniprot.org/uniprot/U3GVT3";
        synonyms = new String[]{"methylmalonyl-CoA carboxyltransferase 1.3S subunit"};
        organismID = "caz";
        organismName = "Corynebacterium argentoratense";
        organismURI = "http://www.genome.jp/kegg-bin/show_organism?org=" + organismID;
        aaSeq = "MKLKVTVNGIAYSVDVEVEEETRQLGSIVFGSSPTNTPAAPTTASVQGVSANAIAAPLAGSVSKVLVAEGDAIEAGQVLLVLEAMKMETEITAPKAGTVGAIHVSEGDAVQGGQGLIEIDD";
        cds = 	"atgaaacttaaggtgaccgtcaacggtattgcctactccgtcgacgtcgaggtggaggaa" +
                "gagacccgccagctcggttcgattgtgttcggctctagcccgaccaacactcccgcagcc" +
                "ccgacgaccgcgtccgtccagggcgtgtctgcaaacgctatcgcagccccccttgcgggc" +
                "tccgtatccaaggtgcttgttgcagagggcgacgccattgaggcaggtcaagtcctgctc" +
                "gtgctggaagccatgaaaatggaaacggaaattaccgcgccgaaggccggcaccgtcggt" +
                "gctatccacgtgtccgagggcgatgcggttcaaggcggacagggcctcattgaaatcgat" +
                "gactaa";
        cds = null;
        enzymeClassSchemes = new String[]{"ec"};
        enzymeClassIDs = new String[]{"2.1.3.1"};

        annotationPrefixURIs = new String[]{"http://test.test", "http://test.test"};
        annotationPrefixes = new String[]{"test", "test"};
        annotationKeys = new String[]{"key1", "key2"};
        annotationValues = new String[]{"val1", "val2"};

        try {
            createProtein(f, prefixURI, proteinID, sourceURI, synonyms, organismID, organismName, organismURI, aaSeq, cds, enzymeClassSchemes, enzymeClassIDs, annotationPrefixURIs, annotationPrefixes, annotationKeys, annotationValues);
        } catch (PartsCreationException e) {
            System.out.println("error");
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {
        //testCompoundCreation();
        //testReactionCreation();
        //testEnzymeClassCreation();
        testProteinCreation();
        /*
        try {
            Scanner scan1 = new Scanner(new File("C:\\Users\\Jumee\\Desktop\\test2\\test2\\parts\\R00209"));
            //scan1.useDelimiter("\\Z");
            //String content1 = scan1.next();

            Scanner scan2 = new Scanner(new File("C:\\Users\\Jumee\\reaction1.xml"));
            //scan2.useDelimiter("\\Z");
            //String content2 = scan.next();
            int i = 1;
            while(scan1.hasNextLine()){
                String line1 = scan1.nextLine();
                String line2 = scan2.nextLine();
                if(!line1.equals(line2)){
                    System.out.println("Error at " + i);
                }
                i++;
            }
            if(scan2.hasNextLine()){
                System.out.println("Second one is bigger!");
            }
            System.out.println("Done!");
        } catch (Exception ex) {

        }
        */
    }

}
