package biosyndesign.core.managers;

import biosyndesign.core.Main;
import biosyndesign.core.sbol.*;
import biosyndesign.core.ui.MainWindow;
import biosyndesign.core.utils.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by Umarov on 1/27/2017.
 */
public class LocalPartsManager {
    private ProjectState s;
    private MainWindow mainWindow;

    public LocalPartsManager(ProjectState s, MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.s = s;
    }

    public  void addCompound() {
        final JDialog frame = new JDialog(mainWindow, "New Compound", true);
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));

        JLabel l1 = new JLabel("Compound ID");
        UI.addTo(jp, l1);
        JTextField id = new JTextField();
        int m = Math.max(20, id.getPreferredSize().height);
        UI.addTFTo(jp, id, 300, m);

        JLabel l2 = new JLabel("Synonyms");
        UI.addTo(jp, l2);
        JTextField synonyms = new JTextField();
        UI.addTFTo(jp, synonyms, 300, m);

        JLabel l3 = new JLabel("External Links");
        UI.addTo(jp, l3);
        JTextField extLinks = new JTextField();
        UI.addTFTo(jp, extLinks, 300, m);

        JLabel l4 = new JLabel("Formula");
        UI.addTo(jp, l4);
        JTextField formula = new JTextField();
        UI.addTFTo(jp, formula, 300, m);

        JLabel l5 = new JLabel("SMILES");
        UI.addTo(jp, l5);
        JTextField smiles = new JTextField();
        UI.addTFTo(jp, smiles, 300, m);

        JLabel l6 = new JLabel("Charge");
        UI.addTo(jp, l6);
        JTextField charge = new JTextField();
        UI.addTFTo(jp, charge, 300, m);


        JButton b1 = new JButton("Add");
        UI.addToRight(jp, b1, false);
        b1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String path = s.projectPath + s.projectName + File.separator + "parts" + File.separator + id.getText();
                PartsCreator.newCompound(path, id.getText(), split(synonyms), split(extLinks), formula.getText(), smiles.getText(), charge.getText(), s.prefix);
                Compound c = new Compound(id.getText(), split(synonyms)[0], path);
                c.setLocal(true);
                Main.pm.addParts(new Part[]{c});
                c.info.add(synonyms.getText());
                c.info.add(extLinks.getText());
                c.info.add(formula.getText());
                c.info.add(smiles.getText());
                c.info.add(charge.getText());
                frame.setVisible(false);
                frame.dispose();
            }
        });
        frame.getContentPane().add(jp);
        frame.pack();
        frame.setLocationRelativeTo(mainWindow);
        frame.setVisible(true);
    }

    public  void editCompound(Compound c) {
        final JDialog frame = new JDialog(mainWindow, "Edit Compound", true);
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));

        JLabel l1 = new JLabel("Compound ID");
        UI.addTo(jp, l1);
        JTextField id = new JTextField();
        id.setText(c.id);
        int m = Math.max(20, id.getPreferredSize().height);
        UI.addTFTo(jp, id, 300, m);

        JLabel l2 = new JLabel("Synonyms");
        UI.addTo(jp, l2);
        JTextField synonyms = new JTextField();
        synonyms.setText(c.info.get(0));
        UI.addTFTo(jp, synonyms, 300, m);

        JLabel l3 = new JLabel("External Links");
        UI.addTo(jp, l3);
        JTextField extLinks = new JTextField();
        extLinks.setText(c.info.get(1));
        UI.addTFTo(jp, extLinks, 300, m);

        JLabel l4 = new JLabel("Formula");
        UI.addTo(jp, l4);
        JTextField formula = new JTextField();
        formula.setText(c.info.get(2));
        UI.addTFTo(jp, formula, 300, m);

        JLabel l5 = new JLabel("SMILES");
        UI.addTo(jp, l5);
        JTextField smiles = new JTextField();
        smiles.setText(c.info.get(3));
        UI.addTFTo(jp, smiles, 300, m);

        JLabel l6 = new JLabel("Charge");
        UI.addTo(jp, l6);
        JTextField charge = new JTextField();
        charge.setText(c.info.get(4));
        UI.addTFTo(jp, charge, 300, m);


        JButton b1 = new JButton("Done");
        UI.addToRight(jp, b1, false);
        b1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String path = s.projectPath + s.projectName + File.separator + "parts" + File.separator + id.getText();
                PartsCreator.newCompound(path, id.getText(), split(synonyms), split(extLinks), formula.getText(), smiles.getText(), charge.getText(), s.prefix);

                if (!c.id.equals(id.getText())) {
                    File file = new File(s.projectPath + s.projectName + File.separator + "parts" + File.separator + c.id);
                    file.delete();
                }
                c.id = id.getText();
                c.name = split(synonyms)[0];
                c.url = path;
                c.info.clear();
                c.info.add(synonyms.getText());
                c.info.add(extLinks.getText());
                c.info.add(formula.getText());
                c.info.add(smiles.getText());
                c.info.add(charge.getText());
                frame.setVisible(false);
                frame.dispose();
            }
        });
        frame.getContentPane().add(jp);
        frame.pack();
        frame.setLocationRelativeTo(mainWindow);
        frame.setVisible(true);
    }

    public  void addReaction() {
        final JDialog frame = new JDialog(mainWindow, "New Reaction", true);
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));
        JLabel l1 = new JLabel("Reaction ID");
        UI.addTo(jp, l1);
        JTextField id = new JTextField();
        int m = Math.max(20, id.getPreferredSize().height);
        id.setPreferredSize(new Dimension(300, m));
        UI.addTo(jp, id);

        JLabel l2 = new JLabel("REACTANTS");
        UI.addTo(jp, l2);
        JTextField reactants = new JTextField();
        UI.addTFTo(jp, reactants, 300, m);

        JLabel l3 = new JLabel("PRODUCTS");
        UI.addTo(jp, l3);
        JTextField products = new JTextField();
        UI.addTFTo(jp, products, 300, m);

        JLabel l4 = new JLabel("REACTANTS_STOICHIOMETRY");
        UI.addTo(jp, l4);
        JTextField rStoichiometry = new JTextField();
        UI.addTFTo(jp, rStoichiometry, 300, m);

        JLabel l5 = new JLabel("PRODUCTS_STOICHIOMETRY");
        UI.addTo(jp, l5);
        JTextField pStoichiometry = new JTextField();
        UI.addTFTo(jp, pStoichiometry, 300, m);

        JLabel l6 = new JLabel("EC_NUMBERS");
        UI.addTo(jp, l6);
        JTextField ECNumbers = new JTextField();
        UI.addTFTo(jp, ECNumbers, 300, m);

        JLabel l7 = new JLabel("KEGG_REACTION");
        UI.addTo(jp, l7);
        JTextField kReaction = new JTextField();
        UI.addTFTo(jp, kReaction, 300, m);

        JLabel l8 = new JLabel("FREE_ENERGY_VALUE");
        UI.addTo(jp, l8);
        JTextField freeEnergy = new JTextField();
        UI.addTFTo(jp, freeEnergy, 300, m);

        JButton b1 = new JButton("Add");
        UI.addToRight(jp, b1, false);
        b1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String path = s.projectPath + s.projectName + File.separator + "parts" + File.separator + id.getText();
                PartsCreator.newReaction(path, split(reactants), split(products), splitInt(rStoichiometry),
                        splitInt(pStoichiometry), split(ECNumbers), kReaction.getText(), id.getText(), freeEnergy.getText(), s.prefix);
                Reaction r = new Reaction(id.getText(), "", path, Integer.parseInt(freeEnergy.getText()));
                r.setLocal(true);
                Main.pm.addParts(new Part[]{r});
                r.info.add(reactants.getText());
                r.info.add(products.getText());
                r.info.add(rStoichiometry.getText());
                r.info.add(pStoichiometry.getText());
                r.info.add(ECNumbers.getText());
                r.info.add(kReaction.getText());
                r.info.add(freeEnergy.getText());
                frame.setVisible(false);
                frame.dispose();
            }
        });
        frame.getContentPane().add(jp);
        frame.pack();
        frame.setLocationRelativeTo(mainWindow);
        frame.setVisible(true);
    }

    public void editReaction(Reaction r) {
        final JDialog frame = new JDialog(mainWindow, "Edit Reaction", true);
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));
        JLabel l1 = new JLabel("Reaction ID");
        UI.addTo(jp, l1);
        JTextField id = new JTextField();
        id.setText(r.id);
        int m = Math.max(20, id.getPreferredSize().height);
        id.setPreferredSize(new Dimension(300, m));
        UI.addTo(jp, id);

        JLabel l2 = new JLabel("REACTANTS");
        UI.addTo(jp, l2);
        JTextField reactants = new JTextField();
        reactants.setText(r.info.get(0));
        UI.addTFTo(jp, reactants, 300, m);

        JLabel l3 = new JLabel("PRODUCTS");
        UI.addTo(jp, l3);
        JTextField products = new JTextField();
        products.setText(r.info.get(1));
        UI.addTFTo(jp, products, 300, m);

        JLabel l4 = new JLabel("REACTANTS_STOICHIOMETRY");
        UI.addTo(jp, l4);
        JTextField rStoichiometry = new JTextField();
        rStoichiometry.setText(r.info.get(2));
        UI.addTFTo(jp, rStoichiometry, 300, m);

        JLabel l5 = new JLabel("PRODUCTS_STOICHIOMETRY");
        UI.addTo(jp, l5);
        JTextField pStoichiometry = new JTextField();
        pStoichiometry.setText(r.info.get(3));
        UI.addTFTo(jp, pStoichiometry, 300, m);

        JLabel l6 = new JLabel("EC_NUMBERS");
        UI.addTo(jp, l6);
        JTextField ECNumbers = new JTextField();
        ECNumbers.setText(r.info.get(4));
        UI.addTFTo(jp, ECNumbers, 300, m);

        JLabel l7 = new JLabel("KEGG_REACTION");
        UI.addTo(jp, l7);
        JTextField kReaction = new JTextField();
        kReaction.setText(r.info.get(5));
        UI.addTFTo(jp, kReaction, 300, m);

        JLabel l8 = new JLabel("FREE_ENERGY_VALUE");
        UI.addTo(jp, l8);
        JTextField freeEnergy = new JTextField();
        freeEnergy.setText(r.info.get(6));
        UI.addTFTo(jp, freeEnergy, 300, m);

        JButton b1 = new JButton("Done");
        UI.addToRight(jp, b1, false);
        b1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String path = s.projectPath + s.projectName + File.separator + "parts" + File.separator + id.getText();
                PartsCreator.newReaction(path, split(reactants), split(products), splitInt(rStoichiometry),
                        splitInt(pStoichiometry), split(ECNumbers), kReaction.getText(), id.getText(), freeEnergy.getText(), s.prefix);
                if (!r.id.equals(id.getText())) {
                    File file = new File(s.projectPath + s.projectName + File.separator + "parts" + File.separator + r.id);
                    file.delete();
                }
                r.url = path;
                r.id = id.getText();
                r.energy = Integer.parseInt(freeEnergy.getText());
                r.info.clear();
                r.info.add(reactants.getText());
                r.info.add(products.getText());
                r.info.add(rStoichiometry.getText());
                r.info.add(pStoichiometry.getText());
                r.info.add(ECNumbers.getText());
                r.info.add(kReaction.getText());
                r.info.add(freeEnergy.getText());
                frame.setVisible(false);
                frame.dispose();
            }
        });
        frame.getContentPane().add(jp);
        frame.pack();
        frame.setLocationRelativeTo(mainWindow);
        frame.setVisible(true);
    }


    public  void addECNumber() {
        final JDialog frame = new JDialog(mainWindow, "New EC Number", true);
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));

        UI.addTo(jp, new JLabel("EC Number"));
        JTextField id = new JTextField();
        int m = Math.max(20, id.getPreferredSize().height);
        UI.addTFTo(jp, id, 300, m);

        UI.addTo(jp, new JLabel("Names"));
        JTextField names = new JTextField();
        UI.addTFTo(jp, names, 300, m);

        UI.addTo(jp, new JLabel("Synonyms"));
        JTextField synonyms = new JTextField();
        UI.addTFTo(jp, synonyms, 300, m);

        UI.addTo(jp, new JLabel("External Link"));
        JTextField extLinks = new JTextField();
        UI.addTFTo(jp, extLinks, 300, m);

        UI.addTo(jp, new JLabel("Formulas"));
        JTextField formulas = new JTextField();
        UI.addTFTo(jp, formulas, 300, m);

        JLabel l5 = new JLabel("Cofactors");
        UI.addTo(jp, l5);
        JTextField cofactors = new JTextField();
        UI.addTFTo(jp, cofactors, 300, m);


        JButton b1 = new JButton("Add");
        UI.addToRight(jp, b1, false);
        b1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ee) {
                String path = s.projectPath + s.projectName + File.separator + "parts" + File.separator + id.getText();
                PartsCreator.newECNumber(path, id.getText(), split(names), split(synonyms), extLinks.getText(), split(formulas), split(cofactors), s.prefix);
                ECNumber e = new ECNumber(id.getText(), split(names)[0], path, id.getText());
                e.setLocal(true);
                Main.pm.addParts(new Part[]{e});
                e.info.add(names.getText());
                e.info.add(synonyms.getText());
                e.info.add(extLinks.getText());
                e.info.add(formulas.getText());
                e.info.add(cofactors.getText());
                frame.setVisible(false);
                frame.dispose();
            }
        });
        frame.getContentPane().add(jp);
        frame.pack();
        frame.setLocationRelativeTo(mainWindow);
        frame.setVisible(true);
    }

    public  void editECNumber(ECNumber e) {
        final JDialog frame = new JDialog(mainWindow, "Edit EC Number", true);
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));

        UI.addTo(jp, new JLabel("EC Number"));
        JTextField id = new JTextField();
        id.setText(e.id);
        int m = Math.max(20, id.getPreferredSize().height);
        UI.addTFTo(jp, id, 300, m);

        UI.addTo(jp, new JLabel("Names"));
        JTextField names = new JTextField();
        names.setText(e.info.get(0));
        UI.addTFTo(jp, names, 300, m);

        UI.addTo(jp, new JLabel("Synonyms"));
        JTextField synonyms = new JTextField();
        synonyms.setText(e.info.get(1));
        UI.addTFTo(jp, synonyms, 300, m);

        UI.addTo(jp, new JLabel("External Link"));
        JTextField extLinks = new JTextField();
        extLinks.setText(e.info.get(2));
        UI.addTFTo(jp, extLinks, 300, m);

        UI.addTo(jp, new JLabel("Formulas"));
        JTextField formulas = new JTextField();
        formulas.setText(e.info.get(3));
        UI.addTFTo(jp, formulas, 300, m);

        JLabel l5 = new JLabel("Cofactors");
        UI.addTo(jp, l5);
        JTextField cofactors = new JTextField();
        cofactors.setText(e.info.get(4));
        UI.addTFTo(jp, cofactors, 300, m);


        JButton b1 = new JButton("Done");
        UI.addToRight(jp, b1, false);
        b1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ee) {
                String path = s.projectPath + s.projectName + File.separator + "parts" + File.separator + id.getText();
                PartsCreator.newECNumber(path, id.getText(), split(names), split(synonyms), extLinks.getText(), split(formulas), split(cofactors), s.prefix);
                if (!e.id.equals(id.getText())) {
                    File file = new File(s.projectPath + s.projectName + File.separator + "parts" + File.separator + e.id);
                    file.delete();
                }
                e.id = id.getText();
                e.ecNumber = id.getText();
                e.name = split(names)[0];
                e.url = path;
                e.info.clear();
                e.info.add(names.getText());
                e.info.add(synonyms.getText());
                e.info.add(extLinks.getText());
                e.info.add(formulas.getText());
                e.info.add(cofactors.getText());
                frame.setVisible(false);
                frame.dispose();
            }
        });
        frame.getContentPane().add(jp);
        frame.pack();
        frame.setLocationRelativeTo(mainWindow);
        frame.setVisible(true);
    }

    public  void addEnzyme() {
        final JDialog frame = new JDialog(mainWindow, "New Compound", true);
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));

        UI.addTo(jp, new JLabel("Protein ID"));
        JTextField id = new JTextField();
        int m = Math.max(20, id.getPreferredSize().height);
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

        UI.addTo(jp, new JLabel("External Links"));
        JTextField extLinks = new JTextField();
        UI.addTFTo(jp, extLinks, 300, m);

        UI.addTo(jp, new JLabel("AA_SEQ"));
        JTextField aaSeq = new JTextField();
        UI.addTFTo(jp, aaSeq, 300, m);

        UI.addTo(jp, new JLabel("EC Numbers"));
        JTextField ECNumbers = new JTextField();
        UI.addTFTo(jp, ECNumbers, 300, m);

        JButton b1 = new JButton("Add");
        UI.addToRight(jp, b1, false);
        b1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String path = s.projectPath + s.projectName + File.separator + "parts" + File.separator + id.getText();
                PartsCreator.newProtein(path, id.getText(), split(synonyms), organismID.getText(), organismName.getText(),
                        organismURL.getText(), split(extLinks), aaSeq.getText(), split(ECNumbers), s.prefix);
                Protein p = new Protein(id.getText(), split(synonyms)[0], path, split(ECNumbers)[0]);
                p.setLocal(true);
                p.info.add(synonyms.getText());
                p.info.add(organismID.getText());
                p.info.add(organismName.getText());
                p.info.add(organismURL.getText());
                p.info.add(extLinks.getText());
                p.info.add(aaSeq.getText());
                p.info.add(ECNumbers.getText());
                Main.pm.addParts(new Part[]{p});
                frame.setVisible(false);
                frame.dispose();
            }
        });
        frame.getContentPane().add(jp);
        frame.pack();
        frame.setLocationRelativeTo(mainWindow);
        frame.setVisible(true);
    }

    public void editEnzyme(Protein p) {
        final JDialog frame = new JDialog(mainWindow, "New Compound", true);
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));

        UI.addTo(jp, new JLabel("Protein ID"));
        JTextField id = new JTextField();
        id.setText(p.id);
        int m = Math.max(20, id.getPreferredSize().height);
        UI.addTFTo(jp, id, 300, m);

        UI.addTo(jp, new JLabel("Names"));
        JTextField synonyms = new JTextField();
        synonyms.setText(p.info.get(0));
        UI.addTFTo(jp, synonyms, 300, m);

        UI.addTo(jp, new JLabel("Organism ID"));
        JTextField organismID = new JTextField();
        organismID.setText(p.info.get(1));
        UI.addTFTo(jp, organismID, 300, m);

        UI.addTo(jp, new JLabel("Organism Name"));
        JTextField organismName = new JTextField();
        organismName.setText(p.info.get(2));
        UI.addTFTo(jp, organismName, 300, m);

        UI.addTo(jp, new JLabel("Organism URL"));
        JTextField organismURL = new JTextField();
        organismURL.setText(p.info.get(3));
        UI.addTFTo(jp, organismURL, 300, m);

        UI.addTo(jp, new JLabel("External Links"));
        JTextField extLinks = new JTextField();
        extLinks.setText(p.info.get(4));
        UI.addTFTo(jp, extLinks, 300, m);

        UI.addTo(jp, new JLabel("AA_SEQ"));
        JTextField aaSeq = new JTextField();
        aaSeq.setText(p.info.get(5));
        UI.addTFTo(jp, aaSeq, 300, m);

        UI.addTo(jp, new JLabel("EC Numbers"));
        JTextField ECNumbers = new JTextField();
        ECNumbers.setText(p.info.get(6));
        UI.addTFTo(jp, ECNumbers, 300, m);

        JButton b1 = new JButton("Add");
        UI.addToRight(jp, b1, false);
        b1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String path = s.projectPath + s.projectName + File.separator + "parts" + File.separator + id.getText();
                PartsCreator.newProtein(path, id.getText(), split(synonyms), organismID.getText(), organismName.getText(),
                        organismURL.getText(), split(extLinks), aaSeq.getText(), split(ECNumbers), s.prefix);
                if (!p.id.equals(id.getText())) {
                    File file = new File(s.projectPath + s.projectName + File.separator + "parts" + File.separator + p.id);
                    file.delete();
                }
                p.id = id.getText();
                p.name = split(synonyms)[0];
                p.url = path;
                p.ecNumber = split(ECNumbers)[0];
                p.info.clear();
                p.info.add(synonyms.getText());
                p.info.add(organismID.getText());
                p.info.add(organismName.getText());
                p.info.add(organismURL.getText());
                p.info.add(extLinks.getText());
                p.info.add(aaSeq.getText());
                p.info.add(ECNumbers.getText());
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
        return tf.getText().split("\\s+,\\s+");
    }

    private static int[] splitInt(JTextField tf) {
        String[] sv = tf.getText().split("\\s+,\\s+");
        int[] iv = new int[sv.length];
        for (int i = 0; i < iv.length; i++) {
            iv[i] = Integer.parseInt(sv[i]);
        }
        return iv;
    }
}
