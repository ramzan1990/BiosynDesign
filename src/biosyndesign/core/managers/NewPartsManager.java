package biosyndesign.core.managers;

import biosyndesign.core.Main;
import biosyndesign.core.sbol.parts.*;
import biosyndesign.core.ui.MainWindow;
import biosyndesign.core.utils.ComboItem;
import biosyndesign.core.utils.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by Umarov on 1/27/2017.
 */
public class NewPartsManager {
    private ProjectState s;
    private MainWindow mainWindow;

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

    public void addReaction() {
        final JDialog frame = new JDialog(mainWindow, "New Reaction", true);
        JPanel base = new JPanel();
        base.setLayout(new BoxLayout(base, BoxLayout.PAGE_AXIS));

        JPanel jp0 = new JPanel();
        jp0.setLayout(new BoxLayout(jp0, BoxLayout.X_AXIS));

        JPanel jp1 = new JPanel();
        jp1.setLayout(new BoxLayout(jp1, BoxLayout.PAGE_AXIS));

        UI.addTo(jp1, new JLabel("Prefix URI"));
        JTextField prefixURI = new JTextField();
        int m = Math.max(20, prefixURI.getPreferredSize().height);
        UI.addTFTo(jp1, prefixURI, 300, m);

        UI.addTo(jp1, new JLabel("Reaction ID"));
        JTextField id = new JTextField();
        UI.addTFTo(jp1, id, 300, m);

        UI.addTo(jp1, new JLabel("Select compound to add to reactants or products"));
        JComboBox cmps = new JComboBox();
        for (Compound c : s.compounds) {
            cmps.addItem(new ComboItem(c.url, c.name));
        }
        UI.addCBTo(jp1, cmps, 300, m);

        UI.addTo(jp1, new JLabel("Reactants"));
        JTextArea reactants = new JTextArea();
        UI.addTATo(jp1, reactants, 300, 120);
        reactants.setMinimumSize(new Dimension(300, 120));

        JButton b1 = new JButton("Add Compound");
        b1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (reactants.getText().trim().length() != 0) {
                    reactants.setText(reactants.getText() + ",\n");
                }
                reactants.setText(reactants.getText() + ((ComboItem) cmps.getSelectedItem()).getValue());
            }
        });
        UI.addToRight(jp1, b1, false);

        UI.addTo(jp1, new JLabel("Products"));
        JTextArea products = new JTextArea();
        UI.addTATo(jp1, products, 300, 120);
        products.setMinimumSize(new Dimension(300, 120));

        JButton b2 = new JButton("Add Compound");
        b2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (products.getText().trim().length() != 0) {
                    products.setText(products.getText() + ",\n");
                }
                products.setText(products.getText() + ((ComboItem) cmps.getSelectedItem()).getValue());
            }
        });
        UI.addToRight(jp1, b2, false);

        UI.addTo(jp1, new JLabel("Reactants Stoichiometry"));
        JTextField rStoichiometry = new JTextField();
        UI.addTFTo(jp1, rStoichiometry, 300, m);

        UI.addTo(jp1, new JLabel("Products Stoichiometry"));
        JTextField pStoichiometry = new JTextField();
        UI.addTFTo(jp1, pStoichiometry, 300, m);

        JPanel jp2 = new JPanel();
        jp2.setLayout(new BoxLayout(jp2, BoxLayout.PAGE_AXIS));

        UI.addTo(jp2, new JLabel("Free Energy"));
        JTextField freeEnergy = new JTextField();
        UI.addTFTo(jp2, freeEnergy, 300, m);

        UI.addTo(jp2, new JLabel("Enzyme Class Schemes"));
        JTextField enzymeClassSchemes = new JTextField();
        UI.addTFTo(jp2, enzymeClassSchemes, 300, m);

        UI.addTo(jp2, new JLabel("Enzyme Class IDs"));
        JTextField enzymeClassIDs = new JTextField();
        UI.addTFTo(jp2, enzymeClassIDs, 300, m);

        UI.addTo(jp2, new JLabel("Source URI"));
        JTextField sourceURI = new JTextField();
        UI.addTFTo(jp2, sourceURI, 300, m);

        UI.addTo(jp2, new JLabel("Annotation Prefix URIs"));
        JTextField annotationPrefixURIs = new JTextField();
        UI.addTFTo(jp2, annotationPrefixURIs, 300, m);

        UI.addTo(jp2, new JLabel("Annotation Prefixes"));
        JTextField annotationPrefixes = new JTextField();
        UI.addTFTo(jp2, annotationPrefixes, 300, m);

        UI.addTo(jp2, new JLabel("Annotation Keys"));
        JTextField annotationKeys = new JTextField();
        UI.addTFTo(jp2, annotationKeys, 300, m);

        UI.addTo(jp2, new JLabel("Annotation Values"));
        JTextField annotationValues = new JTextField();
        UI.addTFTo(jp2, annotationValues, 300, m);
        int v = (int)(jp1.getPreferredSize().getHeight() - jp2.getPreferredSize().getHeight());
        jp2.add(Box.createRigidArea(new Dimension(10, v)));

        jp0.add(jp1);
        jp0.add(Box.createRigidArea(new Dimension(10, 0)));
        jp0.add(jp2);

        base.add(jp0);

        JButton b3 = new JButton("Add");
        UI.addToRight(base, b3, false);



        b3.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String path = s.projectPath + s.projectName + File.separator + "parts" + File.separator + id.getText();
                try {
                    PartsCreator.createReaction(path, prefixURI.getText(), id.getText(), sourceURI.getText(), split(reactants), split(products), splitD(rStoichiometry),
                            splitD(pStoichiometry), split(enzymeClassSchemes), split(enzymeClassIDs), split(annotationPrefixURIs), split(annotationPrefixes),
                            split(annotationKeys), split(annotationValues));
                } catch (Exception ex) {

                }
                Reaction r = new Reaction(id.getText(), "", path, Double.parseDouble(freeEnergy.getText()));
                r.setLocal(true);
                Main.pm.addParts(new Part[]{r}, true);
                r.info.add(prefixURI.getText());
                r.info.add(reactants.getText());
                r.info.add(products.getText());
                r.info.add(rStoichiometry.getText());
                r.info.add(pStoichiometry.getText());
                r.info.add(sourceURI.getText());
                r.info.add(annotationPrefixURIs.getText());
                r.info.add(annotationKeys.getText());
                r.info.add(annotationValues.getText());
                frame.setVisible(false);
                frame.dispose();
            }
        });
        frame.getContentPane().add(base);
        frame.pack();
        frame.setLocationRelativeTo(mainWindow);
        //frame.setResizable(false);
        frame.setVisible(true);
    }


    public void editReaction(Reaction r) {
        final JDialog frame = new JDialog(mainWindow, "Edit Reaction", true);
        JPanel base = new JPanel();
        base.setLayout(new BoxLayout(base, BoxLayout.PAGE_AXIS));

        JPanel jp0 = new JPanel();
        jp0.setLayout(new BoxLayout(jp0, BoxLayout.X_AXIS));

        JPanel jp1 = new JPanel();
        jp1.setLayout(new BoxLayout(jp1, BoxLayout.PAGE_AXIS));

        UI.addTo(jp1, new JLabel("Prefix URI"));
        JTextField prefixURI = new JTextField();
        prefixURI.setText(r.info.get(0));
        int m = Math.max(20, prefixURI.getPreferredSize().height);
        UI.addTFTo(jp1, prefixURI, 300, m);

        UI.addTo(jp1, new JLabel("Reaction ID"));
        JTextField id = new JTextField();
        id.setText(r.id);
        UI.addTFTo(jp1, id, 300, m);

        UI.addTo(jp1, new JLabel("Select compound to add to reactants or products"));
        JComboBox cmps = new JComboBox();
        for (Compound c : s.compounds) {
            cmps.addItem(new ComboItem(c.url, c.name));
        }
        UI.addCBTo(jp1, cmps, 300, m);

        UI.addTo(jp1, new JLabel("Reactants"));
        JTextArea reactants = new JTextArea();
        reactants.setText(r.info.get(1));
        UI.addTATo(jp1, reactants, 300, 120);
        reactants.setMinimumSize(new Dimension(300, 120));

        JButton b1 = new JButton("Add Compound");
        b1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (reactants.getText().trim().length() != 0) {
                    reactants.setText(reactants.getText() + ",\n");
                }
                reactants.setText(reactants.getText() + ((ComboItem) cmps.getSelectedItem()).getValue());
            }
        });
        UI.addToRight(jp1, b1, false);

        UI.addTo(jp1, new JLabel("Products"));
        JTextArea products = new JTextArea();
        products.setText(r.info.get(2));
        UI.addTATo(jp1, products, 300, 120);
        products.setMinimumSize(new Dimension(300, 120));

        JButton b2 = new JButton("Add Compound");
        b2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (products.getText().trim().length() != 0) {
                    products.setText(products.getText() + ",\n");
                }
                products.setText(products.getText() + ((ComboItem) cmps.getSelectedItem()).getValue());
            }
        });
        UI.addToRight(jp1, b2, false);

        UI.addTo(jp1, new JLabel("Reactants Stoichiometry"));
        JTextField rStoichiometry = new JTextField();
        rStoichiometry.setText(r.info.get(3));
        UI.addTFTo(jp1, rStoichiometry, 300, m);

        UI.addTo(jp1, new JLabel("Products Stoichiometry"));
        JTextField pStoichiometry = new JTextField();
        pStoichiometry.setText(r.info.get(4));
        UI.addTFTo(jp1, pStoichiometry, 300, m);

        JPanel jp2 = new JPanel();
        jp2.setLayout(new BoxLayout(jp2, BoxLayout.PAGE_AXIS));

        UI.addTo(jp2, new JLabel("Free Energy"));
        JTextField freeEnergy = new JTextField();
        freeEnergy.setText(r.energy+"");
        UI.addTFTo(jp2, freeEnergy, 300, m);

        UI.addTo(jp2, new JLabel("Enzyme Class Schemes"));
        JTextField enzymeClassSchemes = new JTextField();
        enzymeClassSchemes.setText(r.info.get(5));
        UI.addTFTo(jp2, enzymeClassSchemes, 300, m);

        UI.addTo(jp2, new JLabel("Enzyme Class IDs"));
        JTextField enzymeClassIDs = new JTextField();
        enzymeClassIDs.setText(r.info.get(6));
        UI.addTFTo(jp2, enzymeClassIDs, 300, m);

        UI.addTo(jp2, new JLabel("Source URI"));
        JTextField sourceURI = new JTextField();
        sourceURI.setText(r.info.get(7));
        UI.addTFTo(jp2, sourceURI, 300, m);

        UI.addTo(jp2, new JLabel("Annotation Prefix URIs"));
        JTextField annotationPrefixURIs = new JTextField();
        annotationPrefixURIs.setText(r.info.get(8));
        UI.addTFTo(jp2, annotationPrefixURIs, 300, m);

        UI.addTo(jp2, new JLabel("Annotation Prefixes"));
        JTextField annotationPrefixes = new JTextField();
        annotationPrefixes.setText(r.info.get(9));
        UI.addTFTo(jp2, annotationPrefixes, 300, m);

        UI.addTo(jp2, new JLabel("Annotation Keys"));
        JTextField annotationKeys = new JTextField();
        annotationKeys.setText(r.info.get(10));
        UI.addTFTo(jp2, annotationKeys, 300, m);

        UI.addTo(jp2, new JLabel("Annotation Values"));
        JTextField annotationValues = new JTextField();
        annotationValues.setText(r.info.get(11));
        UI.addTFTo(jp2, annotationValues, 300, m);
        int v = (int)(jp1.getPreferredSize().getHeight() - jp2.getPreferredSize().getHeight());
        jp2.add(Box.createRigidArea(new Dimension(10, v)));

        jp0.add(jp1);
        jp0.add(Box.createRigidArea(new Dimension(10, 0)));
        jp0.add(jp2);

        base.add(jp0);

        JButton b3 = new JButton("Done");
        UI.addToRight(base, b3, false);
        b3.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String path = s.projectPath + s.projectName + File.separator + "parts" + File.separator + id.getText();
                try {
                    PartsCreator.createReaction(path, prefixURI.getText(), id.getText(), sourceURI.getText(), split(reactants), split(products), splitD(rStoichiometry),
                            splitD(pStoichiometry), split(enzymeClassSchemes), split(enzymeClassIDs), split(annotationPrefixURIs), split(annotationPrefixes),
                            split(annotationKeys), split(annotationValues));
                } catch (Exception ex) {

                }
                if (!r.id.equals(id.getText())) {
                    File file = new File(s.projectPath + s.projectName + File.separator + "parts" + File.separator + r.id);
                    file.delete();
                }
                r.url = path;
                r.id = id.getText();
                r.energy = Integer.parseInt(freeEnergy.getText());
                r.info.clear();
                r.info.add(prefixURI.getText());
                r.info.add(reactants.getText());
                r.info.add(products.getText());
                r.info.add(rStoichiometry.getText());
                r.info.add(pStoichiometry.getText());
                r.info.add(sourceURI.getText());
                r.info.add(annotationPrefixURIs.getText());
                r.info.add(annotationKeys.getText());
                r.info.add(annotationValues.getText());
                frame.setVisible(false);
                frame.dispose();
            }
        });
        frame.getContentPane().add(base);
        frame.pack();
        frame.setLocationRelativeTo(mainWindow);
        frame.setVisible(true);
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


}
