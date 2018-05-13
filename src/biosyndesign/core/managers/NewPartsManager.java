package biosyndesign.core.managers;

import biosyndesign.core.Main;
import biosyndesign.core.sbol.parts.*;
import biosyndesign.core.ui.MainWindow;
import biosyndesign.core.utils.ComboItem;
import biosyndesign.core.utils.LabelField;
import biosyndesign.core.utils.UI;
import com.hp.hpl.jena.rdf.model.AnonId;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
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

    public void addCompound() {
        final JDialog frame = new JDialog(mainWindow, "New Compound", true);
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));

        UI.addTo(jp, new JLabel("Prefix URI"));
        JTextField prefixURI = new JTextField();
        int m = Math.max(20, prefixURI.getPreferredSize().height);
        UI.addTFTo(jp, prefixURI, 300, m);

        UI.addTo(jp, new JLabel("Compound ID"));
        JTextField id = new JTextField();
        UI.addTFTo(jp, id, 300, m);

        UI.addTo(jp, new JLabel("Synonyms"));
        JTextField synonyms = new JTextField();
        UI.addTFTo(jp, synonyms, 300, m);

        UI.addTo(jp, new JLabel("Formula"));
        JTextField formula = new JTextField();
        UI.addTFTo(jp, formula, 300, m);

        UI.addTo(jp, new JLabel("SMILES"));
        JTextField smiles = new JTextField();
        UI.addTFTo(jp, smiles, 300, m);

        UI.addTo(jp, new JLabel("Source URI"));
        JTextField sourceURI = new JTextField();
        UI.addTFTo(jp, sourceURI, 300, m);

        UI.addTo(jp, new JLabel("Annotation Prefix URIs"));
        JTextField annotationPrefixURIs = new JTextField();
        UI.addTFTo(jp, annotationPrefixURIs, 300, m);

        UI.addTo(jp, new JLabel("Annotation Prefixes"));
        JTextField annotationPrefixes = new JTextField();
        UI.addTFTo(jp, annotationPrefixes, 300, m);

        UI.addTo(jp, new JLabel("Annotation Keys"));
        JTextField annotationKeys = new JTextField();
        UI.addTFTo(jp, annotationKeys, 300, m);

        UI.addTo(jp, new JLabel("Annotation Values"));
        JTextField annotationValues = new JTextField();
        UI.addTFTo(jp, annotationValues, 300, m);

        //UI.addTo(jp, new JLabel("Charge"));
        //JTextField charge = new JTextField();
        //UI.addTFTo(jp, charge, 300, m);


        JButton b1 = new JButton("Add");
        UI.addToRight(jp, b1, false);
        b1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String path = s.projectPath + s.projectName + File.separator + "parts" + File.separator + id.getText();
                try {
                    PartsCreator.createCompound(path, prefixURI.getText(), id.getText(), smiles.getText(), sourceURI.getText(), formula.getText(), split(synonyms),
                            split(annotationPrefixURIs), split(annotationPrefixes), split(annotationKeys), split(annotationValues));
                } catch (Exception ex) {

                }
                Compound c = new Compound(id.getText(), split(synonyms)[0], path);
                c.smiles = smiles.getText();
                c.setLocal(true);
                Main.pm.addParts(new Part[]{c}, true);
                c.info.add(prefixURI.getText());
                c.info.add(synonyms.getText());
                c.info.add(formula.getText());
                c.info.add(sourceURI.getText());
                c.info.add(annotationPrefixURIs.getText());
                c.info.add(annotationKeys.getText());
                c.info.add(annotationValues.getText());
                frame.setVisible(false);
                frame.dispose();
            }
        });
        frame.getContentPane().add(jp);
        frame.pack();
        frame.setLocationRelativeTo(mainWindow);
        frame.setVisible(true);
    }

    public void editCompound(Compound c) {
        final JDialog frame = new JDialog(mainWindow, "Edit Compound", true);
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));

        UI.addTo(jp, new JLabel("Prefix URI"));
        JTextField prefixURI = new JTextField();
        prefixURI.setText(c.info.get(0));
        int m = Math.max(20, prefixURI.getPreferredSize().height);
        UI.addTFTo(jp, prefixURI, 300, m);

        UI.addTo(jp, new JLabel("Compound ID"));
        JTextField id = new JTextField();
        id.setText(c.id);
        UI.addTFTo(jp, id, 300, m);

        UI.addTo(jp, new JLabel("Synonyms"));
        JTextField synonyms = new JTextField();
        synonyms.setText(c.info.get(1));
        UI.addTFTo(jp, synonyms, 300, m);

        UI.addTo(jp, new JLabel("Formula"));
        JTextField formula = new JTextField();
        formula.setText(c.info.get(2));
        UI.addTFTo(jp, formula, 300, m);

        UI.addTo(jp, new JLabel("SMILES"));
        JTextField smiles = new JTextField();
        smiles.setText(c.smiles);
        UI.addTFTo(jp, smiles, 300, m);

        UI.addTo(jp, new JLabel("Source URI"));
        JTextField sourceURI = new JTextField();
        sourceURI.setText(c.info.get(3));
        UI.addTFTo(jp, sourceURI, 300, m);

        UI.addTo(jp, new JLabel("Annotation Prefix URIs"));
        JTextField annotationPrefixURIs = new JTextField();
        annotationPrefixURIs.setText(c.info.get(4));
        UI.addTFTo(jp, annotationPrefixURIs, 300, m);

        UI.addTo(jp, new JLabel("Annotation Prefixes"));
        JTextField annotationPrefixes = new JTextField();
        annotationPrefixes.setText(c.info.get(5));
        UI.addTFTo(jp, annotationPrefixes, 300, m);

        UI.addTo(jp, new JLabel("Annotation Keys"));
        JTextField annotationKeys = new JTextField();
        annotationKeys.setText(c.info.get(6));
        UI.addTFTo(jp, annotationKeys, 300, m);

        UI.addTo(jp, new JLabel("Annotation Values"));
        JTextField annotationValues = new JTextField();
        annotationValues.setText(c.info.get(7));
        UI.addTFTo(jp, annotationValues, 300, m);


        JButton b1 = new JButton("Done");
        UI.addToRight(jp, b1, false);
        b1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String path = s.projectPath + s.projectName + File.separator + "parts" + File.separator + id.getText();
                try {
                    PartsCreator.createCompound(path, prefixURI.getText(), id.getText(), smiles.getText(), sourceURI.getText(), formula.getText(), split(synonyms),
                            split(annotationPrefixURIs), split(annotationPrefixes), split(annotationKeys), split(annotationValues));
                } catch (Exception ex) {
                }
                if (!c.id.equals(id.getText())) {
                    File file = new File(s.projectPath + s.projectName + File.separator + "parts" + File.separator + c.id);
                    file.delete();
                }
                c.id = id.getText();
                c.name = split(synonyms)[0];
                c.url = path;
                c.smiles = smiles.getText();
                c.info.clear();
                c.info.add(prefixURI.getText());
                c.info.add(synonyms.getText());
                c.info.add(formula.getText());
                c.info.add(sourceURI.getText());
                c.info.add(annotationPrefixURIs.getText());
                c.info.add(annotationKeys.getText());
                c.info.add(annotationValues.getText());
                frame.setVisible(false);
                frame.dispose();
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
        LabelField prefixURI = new LabelField("Prefix URI");
        LabelField id = new LabelField("Reaction ID");
        pNorth.add(prefixURI);
        pNorth.add(id);
        LabelField sourceURI = new LabelField("Source URI");
        pNorth.add(sourceURI);
        LabelField freeEnergy = new LabelField("Free Energy");
        pNorth.add(freeEnergy);

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

        LabelField stoichiometry = new LabelField("Stoichiometry");
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

        JPanel pEnzymes = new JPanel();
        pEnzymes.setLayout(new BoxLayout(pEnzymes, BoxLayout.Y_AXIS));
        pEnzymes.setBorder(getBorder("Enzymes"));

        LabelField enzymeClassScheme = new LabelField("Enzyme Class Scheme");
        pEnzymes.add(enzymeClassScheme);

        LabelField enzymeClassID = new LabelField("Enzyme Class ID");
        pEnzymes.add(enzymeClassID);

        DefaultListModel enzymeModel = new DefaultListModel();
        JList enzymes = new JList(enzymeModel);
        UI.addTo(pEnzymes, enzymes, 300, 120);

        JPanel bpEnzymes = new JPanel();
        bpEnzymes.setLayout(new FlowLayout(FlowLayout.RIGHT));
        bpEnzymes.setPreferredSize(new Dimension(300, 2 * m));
        JButton bAddEnzyme = new JButton("Add");
        bAddEnzyme.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enzymeModel.addElement(new Enzyme(enzymeClassScheme.field.getText(), enzymeClassID.field.getText(), "", ""));
            }
        });
        JButton bDeleteEnzyme = new JButton("Delete");
        bDeleteEnzyme.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enzymeModel.remove(products.getSelectedIndex());
            }
        });

        bpEnzymes.add(bAddEnzyme);
        bpEnzymes.add(bDeleteEnzyme);
        pEnzymes.add(bpEnzymes);

        pRight.add(pEnzymes);

        JPanel pAnnotations = new JPanel();
        pAnnotations.setLayout(new BoxLayout(pAnnotations, BoxLayout.Y_AXIS));
        pAnnotations.setBorder(getBorder("Annotations"));

        LabelField annotationPrefixURI = new LabelField("Annotation Prefix URI");
        pAnnotations.add(annotationPrefixURI);

        LabelField annotationPrefix = new LabelField("Annotation Prefix");
        pAnnotations.add(annotationPrefix);

        LabelField annotationKey = new LabelField("Annotation Key");
        pAnnotations.add(annotationKey);

        LabelField annotationValue = new LabelField("Annotation Value");
        pAnnotations.add(annotationValue);

        DefaultListModel annotationModel = new DefaultListModel();
        JList annotations = new JList(annotationModel);
        UI.addTo(pAnnotations, annotations, 300, 120);

        JPanel bpAnnotation = new JPanel();
        bpAnnotation.setLayout(new FlowLayout(FlowLayout.RIGHT));
        bpAnnotation.setPreferredSize(new Dimension(300, 2 * m));
        JButton bAddAnnotation = new JButton("Add");
        bAddAnnotation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                annotationModel.addElement(new Annotation(annotationPrefixURI.field.getText(), annotationPrefix.field.getText(),
                        annotationKey.field.getText(), annotationValue.field.getText()));
            }
        });
        JButton bDeleteAnnotation = new JButton("Delete");
        bDeleteAnnotation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                annotationModel.remove(annotations.getSelectedIndex());
            }
        });

        bpAnnotation.add(bAddAnnotation);
        bpAnnotation.add(bDeleteAnnotation);
        pAnnotations.add(bpAnnotation);
        pRight.add(pAnnotations);
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
            freeEnergy.field.setText(oldReaction.energy + "");
            prefixURI.field.setText(oldReaction.info.get(0));
            sourceURI.field.setText(oldReaction.info.get(1));
            for (Compound c : oldReaction.reactants) {
                reactantsModel.addElement(new ReactionCompound(c, oldReaction.stoichiometry.get(c)));
            }
            for (Compound c : oldReaction.products) {
                reactantsModel.addElement(new ReactionCompound(c, oldReaction.stoichiometry.get(c)));
            }
            for (Annotation a : oldReaction.annotations) {
                annotationModel.addElement(a);
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
                String[] enzymeClassSchemes = new String[enzymeModel.size()];
                String[] enzymeClassIDs = new String[enzymeModel.size()];
                for (int i = 0; i < enzymeClassSchemes.length; i++) {
                    Enzyme enzyme = (Enzyme) enzymeModel.getElementAt(i);
                    enzymeClassSchemes[i] = enzyme.classScheme;
                    enzymeClassIDs[i] = enzyme.classID;
                }
                String[] annotationPrefixURIs = new String[annotationModel.size()];
                String[] annotationPrefixes = new String[annotationModel.size()];
                String[] annotationKeys = new String[annotationModel.size()];
                String[] annotationValues = new String[annotationModel.size()];
                for (int i = 0; i < annotationPrefixURIs.length; i++) {
                    Annotation annotation = (Annotation) annotationModel.getElementAt(i);
                    annotationPrefixURIs[i] = annotation.annotationPrefixURI;
                    annotationPrefixes[i] = annotation.annotationPrefix;
                    annotationKeys[i] = annotation.annotationKey;
                    annotationValues[i] = annotation.annotationValue;
                }
                try {
                    PartsCreator.createReaction(path, prefixURI.field.getText(), id.field.getText(), sourceURI.field.getText(),
                            reactants, products, rStoichiometry, pStoichiometry, enzymeClassSchemes, enzymeClassIDs,
                            annotationPrefixURIs, annotationPrefixes, annotationKeys, annotationValues);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                Reaction r = new Reaction(id.field.getText(), "", path, Double.parseDouble(freeEnergy.field.getText()));
                r.setLocal(true);
                r.info.add(prefixURI.field.getText());
                r.info.add(sourceURI.field.getText());
                for (int i = 0; i < annotationModel.size(); i++) {
                    r.annotations.add((Annotation) annotationModel.getElementAt(i));
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

    private TitledBorder getBorder(String t) {
        TitledBorder b = BorderFactory.createTitledBorder(t);
        b.setTitleJustification(TitledBorder.CENTER);
        return b;
    }

    public void addECNumber() {
        final JDialog frame = new JDialog(mainWindow, "New EC Number", true);
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));

        UI.addTo(jp, new JLabel("Prefix URI"));
        JTextField prefixURI = new JTextField();
        int m = Math.max(20, prefixURI.getPreferredSize().height);
        UI.addTFTo(jp, prefixURI, 300, m);

        UI.addTo(jp, new JLabel("ID"));
        JTextField id = new JTextField();
        UI.addTFTo(jp, id, 300, m);

        UI.addTo(jp, new JLabel("Enzyme Class Scheme"));
        JTextField enzymeClassScheme = new JTextField();
        UI.addTFTo(jp, enzymeClassScheme, 300, m);

        UI.addTo(jp, new JLabel("Enzyme Class ID"));
        JTextField enzymeClassID = new JTextField();
        UI.addTFTo(jp, enzymeClassID, 300, m);

        UI.addTo(jp, new JLabel("Synonyms"));
        JTextField synonyms = new JTextField();
        UI.addTFTo(jp, synonyms, 300, m);

        UI.addTo(jp, new JLabel("Formulas"));
        JTextField formulas = new JTextField();
        UI.addTFTo(jp, formulas, 300, m);

        JLabel l5 = new JLabel("Cofactors");
        UI.addTo(jp, l5);
        JTextField cofactors = new JTextField();
        UI.addTFTo(jp, cofactors, 300, m);

        UI.addTo(jp, new JLabel("Source URI"));
        JTextField sourceURI = new JTextField();
        UI.addTFTo(jp, sourceURI, 300, m);

        UI.addTo(jp, new JLabel("Annotation Prefix URIs"));
        JTextField annotationPrefixURIs = new JTextField();
        UI.addTFTo(jp, annotationPrefixURIs, 300, m);

        UI.addTo(jp, new JLabel("Annotation Prefixes"));
        JTextField annotationPrefixes = new JTextField();
        UI.addTFTo(jp, annotationPrefixes, 300, m);

        UI.addTo(jp, new JLabel("Annotation Keys"));
        JTextField annotationKeys = new JTextField();
        UI.addTFTo(jp, annotationKeys, 300, m);

        UI.addTo(jp, new JLabel("Annotation Values"));
        JTextField annotationValues = new JTextField();
        UI.addTFTo(jp, annotationValues, 300, m);

        JButton b1 = new JButton("Add");
        UI.addToRight(jp, b1, false);
        b1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ee) {
                String path = s.projectPath + s.projectName + File.separator + "parts" + File.separator + id.getText();
                try {
                    PartsCreator.createEnzymeClass(path, prefixURI.getText(), id.getText(), enzymeClassScheme.getText(), enzymeClassID.getText(), sourceURI.getText(), split(synonyms),
                            split(formulas), split(cofactors), split(annotationPrefixURIs), split(annotationPrefixes), split(annotationKeys), split(annotationValues));
                } catch (Exception ex) {

                }
                ECNumber e = new ECNumber(id.getText(), split(synonyms)[0], path, id.getText());
                e.classSheme = enzymeClassScheme.getText();
                e.classID = enzymeClassID.getText();
                e.setLocal(true);
                Main.pm.addParts(new Part[]{e}, true);
                e.info.add(prefixURI.getText());
                e.info.add(synonyms.getText());
                e.info.add(formulas.getText());
                e.info.add(cofactors.getText());
                e.info.add(sourceURI.getText());
                e.info.add(annotationPrefixURIs.getText());
                e.info.add(annotationKeys.getText());
                e.info.add(annotationValues.getText());
                frame.setVisible(false);
                frame.dispose();
            }
        });
        frame.getContentPane().add(jp);
        frame.pack();
        frame.setLocationRelativeTo(mainWindow);
        frame.setVisible(true);
    }

    public void editECNumber(ECNumber e) {
        final JDialog frame = new JDialog(mainWindow, "Edit EC Number", true);
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));

        UI.addTo(jp, new JLabel("Prefix URI"));
        JTextField prefixURI = new JTextField();
        prefixURI.setText(e.info.get(0));
        int m = Math.max(20, prefixURI.getPreferredSize().height);
        UI.addTFTo(jp, prefixURI, 300, m);

        UI.addTo(jp, new JLabel("ID"));
        JTextField id = new JTextField();
        id.setText(e.id);
        UI.addTFTo(jp, id, 300, m);

        UI.addTo(jp, new JLabel("Enzyme Class Scheme"));
        JTextField enzymeClassScheme = new JTextField();
        enzymeClassScheme.setText(e.classSheme);
        UI.addTFTo(jp, enzymeClassScheme, 300, m);

        UI.addTo(jp, new JLabel("Enzyme Class ID"));
        JTextField enzymeClassID = new JTextField();
        enzymeClassID.setText(e.classID);
        UI.addTFTo(jp, enzymeClassID, 300, m);

        UI.addTo(jp, new JLabel("Synonyms"));
        JTextField synonyms = new JTextField();
        synonyms.setText(e.info.get(1));
        UI.addTFTo(jp, synonyms, 300, m);

        UI.addTo(jp, new JLabel("Formulas"));
        JTextField formulas = new JTextField();
        formulas.setText(e.info.get(2));
        UI.addTFTo(jp, formulas, 300, m);

        JLabel l5 = new JLabel("Cofactors");
        UI.addTo(jp, l5);
        JTextField cofactors = new JTextField();
        cofactors.setText(e.info.get(3));
        UI.addTFTo(jp, cofactors, 300, m);

        UI.addTo(jp, new JLabel("Source URI"));
        JTextField sourceURI = new JTextField();
        sourceURI.setText(e.info.get(4));
        UI.addTFTo(jp, sourceURI, 300, m);

        UI.addTo(jp, new JLabel("Annotation Prefix URIs"));
        JTextField annotationPrefixURIs = new JTextField();
        annotationPrefixURIs.setText(e.info.get(5));
        UI.addTFTo(jp, annotationPrefixURIs, 300, m);

        UI.addTo(jp, new JLabel("Annotation Prefixes"));
        JTextField annotationPrefixes = new JTextField();
        annotationPrefixes.setText(e.info.get(6));
        UI.addTFTo(jp, annotationPrefixes, 300, m);

        UI.addTo(jp, new JLabel("Annotation Keys"));
        JTextField annotationKeys = new JTextField();
        annotationKeys.setText(e.info.get(7));
        UI.addTFTo(jp, annotationKeys, 300, m);

        UI.addTo(jp, new JLabel("Annotation Values"));
        JTextField annotationValues = new JTextField();
        annotationValues.setText(e.info.get(8));
        UI.addTFTo(jp, annotationValues, 300, m);


        JButton b1 = new JButton("Done");
        UI.addToRight(jp, b1, false);
        b1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ee) {
                String path = s.projectPath + s.projectName + File.separator + "parts" + File.separator + id.getText();
                try {
                    PartsCreator.createEnzymeClass(path, prefixURI.getText(), id.getText(), enzymeClassScheme.getText(), enzymeClassID.getText(), sourceURI.getText(), split(synonyms),
                            split(formulas), split(cofactors), split(annotationPrefixURIs), split(annotationPrefixes), split(annotationKeys), split(annotationValues));
                } catch (Exception ex) {

                }
                if (!e.id.equals(id.getText())) {
                    File file = new File(s.projectPath + s.projectName + File.separator + "parts" + File.separator + e.id);
                    file.delete();
                }
                e.classSheme = enzymeClassScheme.getText();
                e.classID = enzymeClassID.getText();
                e.id = id.getText();
                e.ecNumber = id.getText();
                e.name = split(synonyms)[0];
                e.url = path;
                e.info.add(prefixURI.getText());
                e.info.add(synonyms.getText());
                e.info.add(formulas.getText());
                e.info.add(cofactors.getText());
                e.info.add(sourceURI.getText());
                e.info.add(annotationPrefixURIs.getText());
                e.info.add(annotationKeys.getText());
                e.info.add(annotationValues.getText());
                frame.setVisible(false);
                frame.dispose();
            }
        });
        frame.getContentPane().add(jp);
        frame.pack();
        frame.setLocationRelativeTo(mainWindow);
        frame.setVisible(true);
    }

    public void addProtein() {
        final JDialog frame = new JDialog(mainWindow, "New Protein", true);
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));

        UI.addTo(jp, new JLabel("Prefix URI"));
        JTextField prefixURI = new JTextField();
        int m = Math.max(20, prefixURI.getPreferredSize().height);
        UI.addTFTo(jp, prefixURI, 300, m);

        UI.addTo(jp, new JLabel("Protein ID"));
        JTextField id = new JTextField();
        UI.addTFTo(jp, id, 300, m);

        UI.addTo(jp, new JLabel("Names"));
        JTextField synonyms = new JTextField();
        UI.addTFTo(jp, synonyms, 300, m);

        UI.addTo(jp, new JLabel("Organism ID"));
        JTextField organismID = new JTextField();
        UI.addTFTo(jp, organismID, 300, m);

        UI.addTo(jp, new JLabel("Organism Name"));
        JTextField organismName = new JTextField();
        UI.addTFTo(jp, organismName, 300, m);

        UI.addTo(jp, new JLabel("Organism URL"));
        JTextField organismURL = new JTextField();
        UI.addTFTo(jp, organismURL, 300, m);

        UI.addTo(jp, new JLabel("AA_SEQ"));
        JTextField aaSeq = new JTextField();
        UI.addTFTo(jp, aaSeq, 300, m);

        UI.addTo(jp, new JLabel("Enzyme Class Schemes"));
        JTextField enzymeClassSchemes = new JTextField();
        UI.addTFTo(jp, enzymeClassSchemes, 300, m);

        UI.addTo(jp, new JLabel("Enzyme Class IDs"));
        JTextField enzymeClassIDs = new JTextField();
        UI.addTFTo(jp, enzymeClassIDs, 300, m);

        UI.addTo(jp, new JLabel("Source URI"));
        JTextField sourceURI = new JTextField();
        UI.addTFTo(jp, sourceURI, 300, m);

        UI.addTo(jp, new JLabel("Annotation Prefix URIs"));
        JTextField annotationPrefixURIs = new JTextField();
        UI.addTFTo(jp, annotationPrefixURIs, 300, m);

        UI.addTo(jp, new JLabel("Annotation Prefixes"));
        JTextField annotationPrefixes = new JTextField();
        UI.addTFTo(jp, annotationPrefixes, 300, m);

        UI.addTo(jp, new JLabel("Annotation Keys"));
        JTextField annotationKeys = new JTextField();
        UI.addTFTo(jp, annotationKeys, 300, m);

        UI.addTo(jp, new JLabel("Annotation Values"));
        JTextField annotationValues = new JTextField();
        UI.addTFTo(jp, annotationValues, 300, m);

        JButton b1 = new JButton("Add");
        UI.addToRight(jp, b1, false);
        b1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String path = s.projectPath + s.projectName + File.separator + "parts" + File.separator + id.getText();
                try {
                    PartsCreator.createProtein(path, prefixURI.getText(), id.getText(), sourceURI.getText(), split(synonyms),
                            organismID.getText(), organismName.getText(), organismURL.getText(), aaSeq.getText(), split(enzymeClassSchemes),
                            split(enzymeClassIDs), split(annotationPrefixURIs), split(annotationPrefixes), split(annotationKeys),
                            split(annotationValues));
                } catch (Exception ex) {

                }
                Protein p = new Protein(id.getText(), split(synonyms)[0], path, split(enzymeClassIDs)[0]);
                p.sequence = aaSeq.getText();
                p.organism = organismName.getText();
                p.setLocal(true);
                p.info.add(prefixURI.getText());
                p.info.add(synonyms.getText());
                p.info.add(organismID.getText());
                p.info.add(organismURL.getText());
                p.info.add(enzymeClassSchemes.getText());
                p.info.add(enzymeClassIDs.getText());
                p.info.add(sourceURI.getText());
                p.info.add(annotationPrefixURIs.getText());
                p.info.add(annotationKeys.getText());
                p.info.add(annotationValues.getText());
                Main.pm.addParts(new Part[]{p}, true);
                Main.getLocalRepo().importCustomPart(new File(path));
                frame.setVisible(false);
                frame.dispose();
            }
        });
        frame.getContentPane().add(jp);
        frame.pack();
        frame.setLocationRelativeTo(mainWindow);
        frame.setVisible(true);
    }

    public void editProtein(Protein p) {
        final JDialog frame = new JDialog(mainWindow, "New Enzyme", true);
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));

        UI.addTo(jp, new JLabel("Prefix URI"));
        JTextField prefixURI = new JTextField();
        prefixURI.setText(p.info.get(0));
        int m = Math.max(20, prefixURI.getPreferredSize().height);
        UI.addTFTo(jp, prefixURI, 300, m);

        UI.addTo(jp, new JLabel("Protein ID"));
        JTextField id = new JTextField();
        id.setText(p.id);
        UI.addTFTo(jp, id, 300, m);

        UI.addTo(jp, new JLabel("Names"));
        JTextField synonyms = new JTextField();
        synonyms.setText(p.info.get(1));
        UI.addTFTo(jp, synonyms, 300, m);

        UI.addTo(jp, new JLabel("Organism ID"));
        JTextField organismID = new JTextField();
        organismID.setText(p.info.get(2));
        UI.addTFTo(jp, organismID, 300, m);

        UI.addTo(jp, new JLabel("Organism Name"));
        JTextField organismName = new JTextField();
        organismName.setText(p.organism);
        UI.addTFTo(jp, organismName, 300, m);

        UI.addTo(jp, new JLabel("Organism URL"));
        JTextField organismURL = new JTextField();
        organismURL.setText(p.info.get(3));
        UI.addTFTo(jp, organismURL, 300, m);

        UI.addTo(jp, new JLabel("AA_SEQ"));
        JTextField aaSeq = new JTextField();
        aaSeq.setText(p.sequence);
        UI.addTFTo(jp, aaSeq, 300, m);

        UI.addTo(jp, new JLabel("Enzyme Class Schemes"));
        JTextField enzymeClassSchemes = new JTextField();
        enzymeClassSchemes.setText(p.info.get(4));
        UI.addTFTo(jp, enzymeClassSchemes, 300, m);

        UI.addTo(jp, new JLabel("Enzyme Class IDs"));
        JTextField enzymeClassIDs = new JTextField();
        enzymeClassIDs.setText(p.info.get(5));
        UI.addTFTo(jp, enzymeClassIDs, 300, m);

        UI.addTo(jp, new JLabel("Source URI"));
        JTextField sourceURI = new JTextField();
        sourceURI.setText(p.info.get(6));
        UI.addTFTo(jp, sourceURI, 300, m);

        UI.addTo(jp, new JLabel("Annotation Prefix URIs"));
        JTextField annotationPrefixURIs = new JTextField();
        annotationPrefixURIs.setText(p.info.get(7));
        UI.addTFTo(jp, annotationPrefixURIs, 300, m);

        UI.addTo(jp, new JLabel("Annotation Prefixes"));
        JTextField annotationPrefixes = new JTextField();
        annotationPrefixes.setText(p.info.get(8));
        UI.addTFTo(jp, annotationPrefixes, 300, m);

        UI.addTo(jp, new JLabel("Annotation Keys"));
        JTextField annotationKeys = new JTextField();
        annotationKeys.setText(p.info.get(9));
        UI.addTFTo(jp, annotationKeys, 300, m);

        UI.addTo(jp, new JLabel("Annotation Values"));
        JTextField annotationValues = new JTextField();
        annotationValues.setText(p.info.get(10));
        UI.addTFTo(jp, annotationValues, 300, m);

        JButton b1 = new JButton("Add");
        UI.addToRight(jp, b1, false);
        b1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String path = s.projectPath + s.projectName + File.separator + "parts" + File.separator + id.getText();
                try {
                    PartsCreator.createProtein(path, prefixURI.getText(), id.getText(), sourceURI.getText(), split(synonyms),
                            organismID.getText(), organismName.getText(), organismURL.getText(), aaSeq.getText(), split(enzymeClassSchemes),
                            split(enzymeClassIDs), split(annotationPrefixURIs), split(annotationPrefixes), split(annotationKeys),
                            split(annotationValues));
                } catch (Exception ex) {

                }
                if (!p.id.equals(id.getText())) {
                    File file = new File(s.projectPath + s.projectName + File.separator + "parts" + File.separator + p.id);
                    file.delete();
                }
                p.id = id.getText();
                p.name = split(synonyms)[0];
                p.url = path;
                p.ecNumber = split(enzymeClassIDs)[0];
                p.sequence = aaSeq.getText();
                p.organism = organismName.getText();
                p.info.add(prefixURI.getText());
                p.info.add(synonyms.getText());
                p.info.add(organismID.getText());
                p.info.add(organismURL.getText());
                p.info.add(enzymeClassSchemes.getText());
                p.info.add(enzymeClassIDs.getText());
                p.info.add(sourceURI.getText());
                p.info.add(annotationPrefixURIs.getText());
                p.info.add(annotationKeys.getText());
                p.info.add(annotationValues.getText());
                frame.setVisible(false);
                frame.dispose();
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
