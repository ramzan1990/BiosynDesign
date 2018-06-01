package biosyndesign.core.managers;

import biosyndesign.core.Main;
import biosyndesign.core.sbol.parts.*;
import biosyndesign.core.ui.MainWindow;
import biosyndesign.core.utils.LabelField;
import biosyndesign.core.utils.UI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Scanner;

/**
 * Created by Umarov on 1/27/2017.
 */
public class NewPartsManager {
    private ProjectState s;
    private MainWindow mainWindow;
    private static int m = new JTextField().getPreferredSize().height;

    public NewPartsManager(ProjectState s, MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.s = s;
    }

    public void addCompound(Compound oldCompound) {
        final JDialog frame = new JDialog(mainWindow, "New Compound", true);
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));

        LabelField id = new LabelField("Compound ID", "R00209");
        jp.add(id);
        LabelField synonyms = new LabelField("Synonyms", "Pyruvate");
        jp.add(synonyms);
        LabelField formula = new LabelField("Formula", "");
        jp.add(formula);
        LabelField smiles = new LabelField("SMILES", "");
        jp.add(smiles);
        LabelField sourceURI = new LabelField("Source URI", "http://www.genome.jp/dbget-bin/www_bget?R00209");
        jp.add(sourceURI);
        AnnotationsPanel annotationsPanel = new AnnotationsPanel(m);
        jp.add(annotationsPanel);

        if (oldCompound != null) {
            id.field.setText(oldCompound.id);
            synonyms.field.setText(oldCompound.info.get(0));
            formula.field.setText(oldCompound.info.get(1));
            sourceURI.field.setText(oldCompound.info.get(2));
            for (Annotation a : oldCompound.annotations) {
                annotationsPanel.annotations.addElement(a);
            }
        }

        JButton b1 = new JButton("Add");
        UI.addToRight(jp, b1, false);
        b1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String path = s.projectPath + s.projectName + File.separator + "parts" + File.separator + id.field.getText();
                String[] annotationPrefixURIs = new String[annotationsPanel.annotations.size()];
                String[] annotationPrefixes = new String[annotationsPanel.annotations.size()];
                String[] annotationKeys = new String[annotationsPanel.annotations.size()];
                String[] annotationValues = new String[annotationsPanel.annotations.size()];
                for (int i = 0; i < annotationPrefixURIs.length; i++) {
                    Annotation annotation = (Annotation) annotationsPanel.annotations.getElementAt(i);
                    annotationPrefixURIs[i] = annotation.annotationPrefixURI;
                    annotationPrefixes[i] = annotation.annotationPrefix;
                    annotationKeys[i] = annotation.annotationKey;
                    annotationValues[i] = annotation.annotationValue;
                }
                try {
                    PartsCreator.createCompound(path, s.localPrefix, id.field.getText(), smiles.field.getText(), sourceURI.field.getText(), formula.field.getText(), split(synonyms.field),
                            annotationPrefixURIs, annotationPrefixes, annotationKeys, annotationValues);
                } catch (Exception ex) {

                }

                Compound c = new Compound(id.field.getText(), split(synonyms.field)[0], path);
                for (int i = 0; i < annotationsPanel.annotations.size(); i++) {
                    c.annotations.add((Annotation) annotationsPanel.annotations.getElementAt(i));
                }
                c.smiles = smiles.field.getText();
                c.setLocal(true);
                Main.pm.addParts(new Part[]{c}, true);
                c.info.add(synonyms.field.getText());
                c.info.add(formula.field.getText());
                c.info.add(sourceURI.field.getText());
                frame.setVisible(false);
                frame.dispose();
                if (oldCompound != null && !c.id.equals(oldCompound.id)) {
                    File file = new File(s.projectPath + s.projectName + File.separator + "parts" + File.separator + oldCompound.id);
                    file.delete();
                }
            }
        });
        frame.getContentPane().add(jp);
        frame.pack();
        frame.setLocationRelativeTo(mainWindow);
        frame.setVisible(true);
    }

    public void addReaction(Reaction oldReaction) {
        final JDialog frame = new JDialog(mainWindow, "Custom Reaction", true);
        JPanel base = new JPanel();
        base.setLayout(new BoxLayout(base, BoxLayout.PAGE_AXIS));

        JPanel pCenter = new JPanel();
        pCenter.setLayout(new BoxLayout(pCenter, BoxLayout.X_AXIS));

        JPanel pLeft = new JPanel();
        pLeft.setLayout(new BoxLayout(pLeft, BoxLayout.PAGE_AXIS));

        JPanel pNorth = new JPanel();
        GridLayout gl = new GridLayout(0, 2);
        gl.setHgap(31);
        pNorth.setBorder(getBorder("General"));
        pNorth.setLayout(gl);
        LabelField id = new LabelField("Reaction ID", "R00209");
        pNorth.add(id);
        LabelField sourceURI = new LabelField("Source URI", "http://www.genome.jp/dbget-bin/www_bget?R00209");
        pNorth.add(sourceURI);

        base.add(pNorth);
        base.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel pCompounds = new JPanel();
        pCompounds.setLayout(new BoxLayout(pCompounds, BoxLayout.Y_AXIS));
        pCompounds.setBorder(getBorder("Compounds"));

        UI.addTo(pCompounds, new JLabel("Select compound to add to reactants or products"));
        JComboBox cmps = new JComboBox();
        for (Compound c : s.compounds) {
            cmps.addItem(c);
        }
        UI.addTo(pCompounds, cmps, 300, m);

        LabelField stoichiometry = new LabelField("Stoichiometry", "1");
        pCompounds.add(stoichiometry);

        UI.addTo(pCompounds, new JLabel("Reactants"));
        DefaultListModel reactantsModel = new DefaultListModel();
        JList reactants = new JList(reactantsModel);
        UI.addTo(pCompounds, reactants, 300, 120);

        JPanel bpReactants = new JPanel();
        bpReactants.setLayout(new FlowLayout(FlowLayout.RIGHT));
        bpReactants.setPreferredSize(new Dimension(300, 2 * m));
        JButton bAddReactant = new JButton("Add");
        bAddReactant.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reactantsModel.addElement(new ReactionCompound((Compound) cmps.getSelectedItem(), Integer.parseInt(stoichiometry.field.getText())));
            }
        });
        JButton bDeleteReactant = new JButton("Delete");
        bDeleteReactant.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reactantsModel.remove(reactants.getSelectedIndex());
            }
        });

        bpReactants.add(bAddReactant);
        bpReactants.add(bDeleteReactant);
        pCompounds.add(bpReactants);

        UI.addTo(pCompounds, new JLabel("Products"));
        DefaultListModel productsModel = new DefaultListModel();
        JList products = new JList(productsModel);
        UI.addTo(pCompounds, products, 300, 120);

        JPanel bpProducts = new JPanel();
        bpProducts.setLayout(new FlowLayout(FlowLayout.RIGHT));
        bpProducts.setPreferredSize(new Dimension(300, 2 * m));
        JButton bAddProduct = new JButton("Add");
        bAddProduct.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                productsModel.addElement(new ReactionCompound((Compound) cmps.getSelectedItem(), Integer.parseInt(stoichiometry.field.getText())));
            }
        });
        JButton bDeleteProduct = new JButton("Delete");
        bDeleteProduct.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                productsModel.remove(products.getSelectedIndex());
            }
        });

        bpProducts.add(bAddProduct);
        bpProducts.add(bDeleteProduct);
        pCompounds.add(bpProducts);

        pLeft.add(pCompounds);

        JPanel pRight = new JPanel();
        pRight.setLayout(new BoxLayout(pRight, BoxLayout.PAGE_AXIS));


        EnzymesPanel ep = new EnzymesPanel(m);
        pRight.add(ep);

        AnnotationsPanel annotationsPanel = new AnnotationsPanel(m);
        pRight.add(annotationsPanel);
        int v = (int) (pLeft.getPreferredSize().getHeight() - pRight.getPreferredSize().getHeight());
        if (v > 0) {
            pRight.add(Box.createRigidArea(new Dimension(10, v)));
        } else if (v < 0) {
            pLeft.add(Box.createRigidArea(new Dimension(10, Math.abs(v))));
        }
        pCenter.add(pLeft);
        pCenter.add(Box.createRigidArea(new Dimension(20, 0)));
        pCenter.add(pRight);

        base.add(pCenter);

        if (oldReaction != null) {
            id.field.setText(oldReaction.id);
            sourceURI.field.setText(oldReaction.info.get(0));
            for (Compound c : oldReaction.reactants) {
                reactantsModel.addElement(new ReactionCompound(c, oldReaction.stoichiometry.get(c)));
            }
            for (Enzyme e : oldReaction.ec) {
                ep.enzymes.addElement(e);
            }
            for (Compound c : oldReaction.products) {
                reactantsModel.addElement(new ReactionCompound(c, oldReaction.stoichiometry.get(c)));
            }
            for (Annotation a : oldReaction.annotations) {
                annotationsPanel.annotations.addElement(a);
            }
        }

        JButton addReaction = new JButton("Add");
        UI.addToRight(base, addReaction, false);
        addReaction.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String path = s.projectPath + s.projectName + File.separator + "parts" + File.separator + id.field.getText();
                String[] reactants = new String[reactantsModel.size()];
                double[] rStoichiometry = new double[reactantsModel.size()];
                for (int i = 0; i < reactants.length; i++) {
                    ReactionCompound rc = (ReactionCompound) reactantsModel.getElementAt(i);
                    reactants[i] = rc.c.url;
                    rStoichiometry[i] = rc.s;
                }
                String[] products = new String[productsModel.size()];
                double[] pStoichiometry = new double[productsModel.size()];
                for (int i = 0; i < products.length; i++) {
                    ReactionCompound rc = (ReactionCompound) productsModel.getElementAt(i);
                    products[i] = rc.c.url;
                    pStoichiometry[i] = rc.s;
                }
                String[] enzymeClassSchemes = new String[ep.enzymes.size()];
                String[] enzymeClassIDs = new String[ep.enzymes.size()];
                for (int i = 0; i < enzymeClassSchemes.length; i++) {
                    Enzyme enzyme = (Enzyme) ep.enzymes.getElementAt(i);
                    enzymeClassSchemes[i] = enzyme.classScheme;
                    enzymeClassIDs[i] = enzyme.classID;
                }
                String[] annotationPrefixURIs = new String[annotationsPanel.annotations.size()];
                String[] annotationPrefixes = new String[annotationsPanel.annotations.size()];
                String[] annotationKeys = new String[annotationsPanel.annotations.size()];
                String[] annotationValues = new String[annotationsPanel.annotations.size()];
                for (int i = 0; i < annotationPrefixURIs.length; i++) {
                    Annotation annotation = (Annotation) annotationsPanel.annotations.getElementAt(i);
                    annotationPrefixURIs[i] = annotation.annotationPrefixURI;
                    annotationPrefixes[i] = annotation.annotationPrefix;
                    annotationKeys[i] = annotation.annotationKey;
                    annotationValues[i] = annotation.annotationValue;
                }
                try {
                    PartsCreator.createReaction(path, s.localPrefix, id.field.getText(), sourceURI.field.getText(),
                            reactants, products, rStoichiometry, pStoichiometry, enzymeClassSchemes, enzymeClassIDs,
                            annotationPrefixURIs, annotationPrefixes, annotationKeys, annotationValues);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                Reaction r = new Reaction(id.field.getText(), "", path, -1);
                r.setLocal(true);
                r.info.add(sourceURI.field.getText());
                for (int i = 0; i < annotationsPanel.annotations.size(); i++) {
                    r.annotations.add((Annotation) annotationsPanel.annotations.getElementAt(i));
                }
                Main.pm.addParts(new Part[]{r}, true);
                frame.setVisible(false);
                frame.dispose();
                if (oldReaction != null && !r.id.equals(oldReaction.id)) {
                    File file = new File(s.projectPath + s.projectName + File.separator + "parts" + File.separator + oldReaction.id);
                    file.delete();
                }
            }
        });
        frame.getContentPane().add(base);
        frame.pack();
        frame.setLocationRelativeTo(mainWindow);
        //frame.setResizable(false);
        frame.setVisible(true);
    }

    public static TitledBorder getBorder(String t) {
        TitledBorder b = BorderFactory.createTitledBorder(t);
        b.setTitleJustification(TitledBorder.CENTER);
        return b;
    }

    public void addEnzyme(Enzyme oldEnzyme) {
        final JDialog frame = new JDialog(mainWindow, "New EC Number", true);
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));
        LabelField id = new LabelField("Enzyme ID", "R00209");
        jp.add(id);
        LabelField sourceURI = new LabelField("Source URI", "http://www.genome.jp/dbget-bin/www_bget?R00209");
        jp.add(sourceURI);
        LabelField enzymeClassScheme = new LabelField("Enzyme Class Scheme");
        jp.add(enzymeClassScheme);
        LabelField enzymeClassID = new LabelField("Enzyme Class ID");
        jp.add(enzymeClassID);
        LabelField synonyms = new LabelField("Synonyms", "Pyruvate");
        jp.add(synonyms);
        LabelField formulas = new LabelField("Formulas", "");
        jp.add(formulas);
        LabelField cofactors = new LabelField("Cofactors");
        jp.add(cofactors);

        AnnotationsPanel annotationsPanel = new AnnotationsPanel(m);
        jp.add(annotationsPanel);

        if (oldEnzyme != null) {
            id.field.setText(oldEnzyme.id);
            enzymeClassScheme.field.setText(oldEnzyme.classScheme);
            enzymeClassID.field.setText(oldEnzyme.classID);
            synonyms.field.setText(oldEnzyme.info.get(0));
            formulas.field.setText(oldEnzyme.info.get(1));
            cofactors.field.setText(oldEnzyme.info.get(2));
            sourceURI.field.setText(oldEnzyme.info.get(3));
            for (Annotation a : oldEnzyme.annotations) {
                annotationsPanel.annotations.addElement(a);
            }
        }
        JButton b1 = new JButton("Add");
        UI.addToRight(jp, b1, false);
        b1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ee) {
                String path = s.projectPath + s.projectName + File.separator + "parts" + File.separator + id.field.getText();
                String[] annotationPrefixURIs = new String[annotationsPanel.annotations.size()];
                String[] annotationPrefixes = new String[annotationsPanel.annotations.size()];
                String[] annotationKeys = new String[annotationsPanel.annotations.size()];
                String[] annotationValues = new String[annotationsPanel.annotations.size()];
                for (int i = 0; i < annotationPrefixURIs.length; i++) {
                    Annotation annotation = (Annotation) annotationsPanel.annotations.getElementAt(i);
                    annotationPrefixURIs[i] = annotation.annotationPrefixURI;
                    annotationPrefixes[i] = annotation.annotationPrefix;
                    annotationKeys[i] = annotation.annotationKey;
                    annotationValues[i] = annotation.annotationValue;
                }
                try {
                    PartsCreator.createEnzymeClass(path, s.localPrefix, id.field.getText(), enzymeClassScheme.field.getText(), enzymeClassID.field.getText(), sourceURI.field.getText(), split(synonyms.field),
                            split(formulas.field), split(cofactors.field),  annotationPrefixURIs, annotationPrefixes, annotationKeys, annotationValues);
                } catch (Exception ex) {

                }
                Enzyme e = new Enzyme(id.field.getText(), split(synonyms.field)[0], path, enzymeClassScheme.field.getText(), enzymeClassID.field.getText());
                e.setLocal(true);
                Main.pm.addParts(new Part[]{e}, true);
                e.info.add(synonyms.field.getText());
                e.info.add(formulas.field.getText());
                e.info.add(cofactors.field.getText());
                e.info.add(sourceURI.field.getText());
                frame.setVisible(false);
                frame.dispose();
                if (oldEnzyme != null && !e.id.equals(oldEnzyme.id)) {
                    File file = new File(s.projectPath + s.projectName + File.separator + "parts" + File.separator + oldEnzyme.id);
                    file.delete();
                }
            }
        });
        frame.getContentPane().add(jp);
        frame.pack();
        frame.setLocationRelativeTo(mainWindow);
        frame.setVisible(true);
    }

    public void addProtein(Protein oldProtein) {
        final JDialog frame = new JDialog(mainWindow, "New Protein", true);
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));

        JPanel pCenter = new JPanel();
        pCenter.setLayout(new BoxLayout(pCenter, BoxLayout.X_AXIS));

        JPanel pLeft = new JPanel();
        pLeft.setLayout(new BoxLayout(pLeft, BoxLayout.PAGE_AXIS));

        JPanel pNorth = new JPanel();
        GridLayout gl = new GridLayout(0, 2);
        gl.setHgap(31);
        pNorth.setBorder(getBorder("General"));
        pNorth.setLayout(gl);

        LabelField id = new LabelField("Protein ID");
        pNorth.add(id);
        LabelField sourceURI = new LabelField("Source URI");
        pNorth.add(sourceURI);

        LabelField synonyms = new LabelField("Names");
        pLeft.add(synonyms);
        LabelField aaSeq = new LabelField("AA_SEQ");
        pLeft.add(aaSeq);
        JPanel pOrganisms = new JPanel();
        pOrganisms.setLayout(new BoxLayout(pOrganisms, BoxLayout.Y_AXIS));
        pOrganisms.setBorder(getBorder("Organisms"));

        LabelField organismID = new LabelField("Organism ID", "aaa");
        pOrganisms.add(organismID);
        LabelField organismName = new LabelField("Organism Name", "1.2.3.4");
        pOrganisms.add(organismName);
        LabelField organismURL = new LabelField("Organism URL", "1.2.3.4");
        pOrganisms.add(organismURL);
        pLeft.add(pOrganisms);

        JPanel pRight = new JPanel();
        pRight.setLayout(new BoxLayout(pRight, BoxLayout.PAGE_AXIS));
        JPanel pEnzymes = new JPanel();
        pEnzymes.setLayout(new BoxLayout(pEnzymes, BoxLayout.Y_AXIS));
        pEnzymes.setBorder(getBorder("Enzymes"));

        LabelField enzymeClassScheme = new LabelField("Enzyme Class Scheme", "ec");
        pEnzymes.add(enzymeClassScheme);

        LabelField enzymeClassID = new LabelField("Enzyme Class ID", "1.2.3.4");
        pEnzymes.add(enzymeClassID);
        pRight.add(pEnzymes);
        AnnotationsPanel annotationsPanel = new AnnotationsPanel(m);
        pRight.add(annotationsPanel);
        jp.add(pNorth);
        jp.add(Box.createRigidArea(new Dimension(0, 10)));
        pCenter.add(pLeft);
        pCenter.add(Box.createRigidArea(new Dimension(20, 0)));
        pCenter.add(pRight);
        jp.add(pCenter);
        if(oldProtein != null){
            id.field.setText(oldProtein.id);
            enzymeClassScheme.field.setText(oldProtein.enzymeClassScheme);
            enzymeClassID.field.setText(oldProtein.enzymeID);
            synonyms.field.setText(oldProtein.info.get(0));
            sourceURI.field.setText(oldProtein.info.get(1));
            for (Annotation a : oldProtein.annotations) {
                annotationsPanel.annotations.addElement(a);
            }
        }
        JButton b1 = new JButton("Add");
        UI.addToRight(jp, b1, false);
        b1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String path = s.projectPath + s.projectName + File.separator + "parts" + File.separator + id.field.getText();
                String[] annotationPrefixURIs = new String[annotationsPanel.annotations.size()];
                String[] annotationPrefixes = new String[annotationsPanel.annotations.size()];
                String[] annotationKeys = new String[annotationsPanel.annotations.size()];
                String[] annotationValues = new String[annotationsPanel.annotations.size()];
                for (int i = 0; i < annotationPrefixURIs.length; i++) {
                    Annotation annotation = (Annotation) annotationsPanel.annotations.getElementAt(i);
                    annotationPrefixURIs[i] = annotation.annotationPrefixURI;
                    annotationPrefixes[i] = annotation.annotationPrefix;
                    annotationKeys[i] = annotation.annotationKey;
                    annotationValues[i] = annotation.annotationValue;
                }
                try {
                    PartsCreator.createProtein(path, s.localPrefix, id.field.getText(), sourceURI.field.getText(), split(synonyms.field),
                            organismID.field.getText(), organismName.field.getText(), organismURL.field.getText(), aaSeq.field.getText(),
                            new String[]{enzymeClassScheme.field.getText()},new String[]{enzymeClassID.field.getText()},
                            annotationPrefixURIs, annotationPrefixes,annotationKeys, annotationValues);
                } catch (Exception ex) {

                }
                Protein p = new Protein(id.field.getText(), split(synonyms.field)[0], path, enzymeClassID.field.getText());
                p.sequence = aaSeq.field.getText();
                p.organism = new Organism(organismID.field.getText(), organismName.field.getText(), organismURL.field.getText());
                p.setLocal(true);
                p.info.add(synonyms.field.getText());
                p.info.add(sourceURI.field.getText());
                Main.pm.addParts(new Part[]{p}, true);
                Main.getLocalRepo().importCustomPart(new File(path));
                frame.setVisible(false);
                frame.dispose();
                if (oldProtein != null && !p.id.equals(oldProtein.id)) {
                    File file = new File(s.projectPath + s.projectName + File.separator + "parts" + File.separator + oldProtein.id);
                    file.delete();
                }
            }
        });
        frame.getContentPane().add(jp);
        frame.pack();
        frame.setLocationRelativeTo(mainWindow);
        frame.setVisible(true);
    }

    private static String[] split(JTextField tf) {
        return tf.getText().split("\\s*,\\s*");
    }

    private static String[] split(JTextArea ta) {
        return ta.getText().split("\\s*,\\s*");
    }

    private static double[] splitD(JTextField tf) {
        String[] sv = tf.getText().split("\\s*,\\s*");
        double[] dv = new double[sv.length];
        for (int i = 0; i < dv.length; i++) {
            dv[i] = Double.parseDouble(sv[i]);
        }
        return dv;
    }

    public static void main(String[] args) {
        //testCompoundCreation();
        //testReactionCreation();
        //testEnzymeClassCreation();
        //testProteinCreation();
        try {
            Scanner scan1 = new Scanner(new File("C:\\Users\\Jumee\\Desktop\\test2\\test2\\parts\\R00209"));
            //scan1.useDelimiter("\\Z");
            //String content1 = scan1.next();

            Scanner scan2 = new Scanner(new File("C:\\Users\\Jumee\\reaction1.xml"));
            //scan2.useDelimiter("\\Z");
            //String content2 = scan.next();
            int i = 1;
            while (scan1.hasNextLine()) {
                String line1 = scan1.nextLine();
                String line2 = scan2.nextLine();
                if (!line1.equals(line2)) {
                    System.out.println("Error at " + i);
                }
                i++;
            }
            if (scan2.hasNextLine()) {
                System.out.println("Second one is bigger!");
            }
            System.out.println("Done!");
        } catch (Exception ex) {

        }
    }

}
class AnnotationsPanel extends JPanel{
    public DefaultListModel annotations;

    public AnnotationsPanel(int m){
        JPanel pAnnotations = this;
        pAnnotations.setLayout(new BoxLayout(pAnnotations, BoxLayout.Y_AXIS));
        pAnnotations.setBorder(NewPartsManager.getBorder("Annotations"));

        LabelField annotationPrefixURI = new LabelField("Annotation Prefix URI", "a1");
        pAnnotations.add(annotationPrefixURI);

        LabelField annotationPrefix = new LabelField("Annotation Prefix", "a2");
        pAnnotations.add(annotationPrefix);

        LabelField annotationKey = new LabelField("Annotation Key", "a3");
        pAnnotations.add(annotationKey);

        LabelField annotationValue = new LabelField("Annotation Value", "a4");
        pAnnotations.add(annotationValue);

        annotations = new DefaultListModel();
        JList annotationsList = new JList(annotations);
        UI.addTo(pAnnotations, annotationsList, 300, 120);

        JPanel bpAnnotation = new JPanel();
        bpAnnotation.setLayout(new FlowLayout(FlowLayout.RIGHT));
        bpAnnotation.setPreferredSize(new Dimension(300, 2 * m));
        JButton bAddAnnotation = new JButton("Add");
        bAddAnnotation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                annotations.addElement(new Annotation(annotationPrefixURI.field.getText(), annotationPrefix.field.getText(),
                        annotationKey.field.getText(), annotationValue.field.getText()));
            }
        });
        JButton bDeleteAnnotation = new JButton("Delete");
        bDeleteAnnotation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                annotations.remove(annotationsList.getSelectedIndex());
            }
        });

        bpAnnotation.add(bAddAnnotation);
        bpAnnotation.add(bDeleteAnnotation);
        pAnnotations.add(bpAnnotation);
    }

}
class EnzymesPanel extends JPanel{
    DefaultListModel enzymes;

    public EnzymesPanel(int m){
        JPanel pEnzymes = this;
        pEnzymes.setLayout(new BoxLayout(pEnzymes, BoxLayout.Y_AXIS));
        pEnzymes.setBorder(NewPartsManager.getBorder("Enzymes"));

        LabelField enzymeClassScheme = new LabelField("Enzyme Class Scheme", "ec");
        pEnzymes.add(enzymeClassScheme);

        LabelField enzymeClassID = new LabelField("Enzyme Class ID", "1.2.3.4");
        pEnzymes.add(enzymeClassID);

        enzymes = new DefaultListModel();
        JList enzymesList = new JList(enzymes);
        UI.addTo(pEnzymes, enzymesList, 300, 120);

        JPanel bpEnzymes = new JPanel();
        bpEnzymes.setLayout(new FlowLayout(FlowLayout.RIGHT));
        bpEnzymes.setPreferredSize(new Dimension(300, 2 * m));
        JButton bAddEnzyme = new JButton("Add");
        bAddEnzyme.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = Enzyme.getID(enzymeClassScheme.field.getText(), enzymeClassID.field.getText());
                enzymes.addElement(new Enzyme(id, "", "", enzymeClassScheme.field.getText(), enzymeClassID.field.getText()));
            }
        });
        JButton bDeleteEnzyme = new JButton("Delete");
        bDeleteEnzyme.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enzymes.remove(enzymesList.getSelectedIndex());
            }
        });

        bpEnzymes.add(bAddEnzyme);
        bpEnzymes.add(bDeleteEnzyme);
        pEnzymes.add(bpEnzymes);
    }
}
class OrganismPanel extends JPanel {

    DefaultListModel organisms;

    public OrganismPanel(int m){
        JPanel pOrganisms = this;
        pOrganisms.setLayout(new BoxLayout(pOrganisms, BoxLayout.Y_AXIS));
        pOrganisms.setBorder(NewPartsManager.getBorder("Organisms"));

        LabelField organismID = new LabelField("Organism ID", "aaa");
        pOrganisms.add(organismID);
        LabelField organismName = new LabelField("Organism Name", "1.2.3.4");
        pOrganisms.add(organismName);
        LabelField organismURL = new LabelField("Organism URL", "1.2.3.4");
        pOrganisms.add(organismURL);

        organisms = new DefaultListModel();
        JList organismsList = new JList(organisms);
        UI.addTo(pOrganisms, organismsList, 300, 120);

        JPanel bpOrganisms = new JPanel();
        bpOrganisms.setLayout(new FlowLayout(FlowLayout.RIGHT));
        bpOrganisms.setPreferredSize(new Dimension(300, 2 * m));
        JButton bAddOrganism = new JButton("Add");
        bAddOrganism.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                organisms.addElement(new Organism(organismID.field.getText(),
                        organismName.field.getText(), organismURL.field.getText()));
            }
        });
        JButton bDeleteOrganism = new JButton("Delete");
        bDeleteOrganism.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                organisms.remove(organismsList.getSelectedIndex());
            }
        });

        bpOrganisms.add(bAddOrganism);
        bpOrganisms.add(bDeleteOrganism);
        pOrganisms.add(bpOrganisms);
    }
}