package biosyndesign.core.ui;

import biosyndesign.core.sbol.*;
import biosyndesign.core.utils.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by Umarov on 1/27/2017.
 */
public class NewParts {

    public static void addCompound(GUI mainWindow) {
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
                String path = Main.s.projectPath + Main.s.projectName + File.separator + "parts" + File.separator + id.getText();
                PartsCreator.newCompound(path, id.getText(), split(synonyms), split(extLinks), formula.getText(), smiles.getText(), charge.getText());
                Main.pm.addParts(new Part[]{new Compound(id.getText(), split(synonyms)[0], path).setLocal(true)});
                frame.setVisible(false);
                frame.dispose();
            }
        });
        frame.getContentPane().add(jp);
        frame.pack();
        frame.setLocationRelativeTo(mainWindow);
        frame.setVisible(true);
    }



    public static void addReaction(GUI mainWindow) {
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
                String path = Main.s.projectPath + Main.s.projectName + File.separator + "parts" + File.separator + id.getText() ;
                PartsCreator.newReaction(path, split(reactants), split(products), splitInt(rStoichiometry),
                        splitInt(pStoichiometry), split(ECNumbers), kReaction.getText(), id.getText(), freeEnergy.getText());
                Main.pm.addParts(new Part[]{new Reaction(id.getText(), "", path, Integer.parseInt(freeEnergy.getText())).setLocal(true)});
                frame.setVisible(false);
                frame.dispose();
            }
        });
        frame.getContentPane().add(jp);
        frame.pack();
        frame.setLocationRelativeTo(mainWindow);
        frame.setVisible(true);
    }



    public static void addECNumber(GUI mainWindow) {
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

        UI.addTo(jp,  new JLabel("External Link"));
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
            public void actionPerformed(ActionEvent e) {
                String path = Main.s.projectPath + Main.s.projectName + File.separator + "parts" + File.separator + id.getText();
                PartsCreator.newECNumber(path, id.getText(), split(names), split(synonyms), extLinks.getText(), split(formulas), split(cofactors));
                Main.pm.addParts(new Part[]{new ECNumber(id.getText(), split(names)[0], path, id.getText()).setLocal(true)});
                frame.setVisible(false);
                frame.dispose();
            }
        });
        frame.getContentPane().add(jp);
        frame.pack();
        frame.setLocationRelativeTo(mainWindow);
        frame.setVisible(true);
    }

    public static void addEnzyme(GUI mainWindow) {
        final JDialog frame = new JDialog(mainWindow, "New Compound", true);
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));

        UI.addTo(jp,  new JLabel("Protein ID"));
        JTextField id = new JTextField();
        int m = Math.max(20, id.getPreferredSize().height);
        UI.addTFTo(jp, id, 300, m);

        UI.addTo(jp,  new JLabel("Names"));
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
                String path = Main.s.projectPath + Main.s.projectName + File.separator + "parts" + File.separator + id.getText();
                PartsCreator.newProtein(path, id.getText(), split(synonyms), organismID.getText(), organismName.getText(), organismURL.getText(), split(extLinks), aaSeq.getText(), split(ECNumbers));
                Main.pm.addParts(new Part[]{new Protein(id.getText(), split(synonyms)[0], path, split(ECNumbers)[0]).setLocal(true)});
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
        for(int i =0; i<iv.length; i++){
            iv[i] = Integer.parseInt(sv[i]);
        }
        return iv;
    }
}
