package biosyndesign.core.ui;

import biosyndesign.core.sbol.*;
import biosyndesign.core.ui.popups.CompoundCellPopUp;
import biosyndesign.core.ui.popups.ReactionCellPopUp;
import biosyndesign.core.utils.Common;
import biosyndesign.core.utils.UI;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import org.openscience.cdk.fingerprint.HybridizationFingerprinter;
import org.openscience.cdk.fingerprint.IBitFingerprint;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.similarity.Tanimoto;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;

import javax.swing.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;

/**
 * Created by Umarov on 2/21/2017.
 */
public class PartsManager {
    private GUI mainWindow;
    private ProjectState s;
    private GraphManager gm;

    public static SBOLInterface sInt;


    public PartsManager(ProjectState s, GUI mainWindow, GraphManager gm) {
        sInt = new SBOLme(s.prefix);
        this.s = s;
        this.mainWindow = mainWindow;
        this.gm = gm;
    }

    private void saveXML(Part p) {
        try {
            System.out.println("saving...");
            URL website = new URL(s.prefix + "/" + p.url);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(s.projectPath + s.projectName + File.separator + "parts" + File.separator + p.id);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            System.out.println("saving done");
        } catch (Exception e) {

        }
    }

    public void addParts(Part[] p) {
        System.out.println("Adding Parts");
        new Thread() {
            public void run() {
                for (int i = 0; i < p.length; i++) {
                    try {
                        if (!p[i].local) {
                            saveXML(p[i]);
                        }
                        String xml = new String(Files.readAllBytes(Paths.get(s.projectPath + s.projectName + File.separator + "parts" + File.separator + p[i].id)));
                        if (p[i] instanceof Reaction) {
                            if (s.reactions.contains(p[i])) {
                                continue;
                            }
                            Reaction r = (Reaction) p[i];
                            s.reactions.add(r);
                            //adding reactions compounds

                            String pattern1 = "<sbol:definition rdf:resource=\"http://www.cbrc.kaust.edu.sa/sbolme/parts/compound/";
                            String pattern2 = "\"/>";
                            Pattern pat = Pattern.compile(Pattern.quote(pattern1) + "(.*?)" + Pattern.quote(pattern2));
                            Matcher m = pat.matcher(xml);
                            while (m.find()) {
                                String id = m.group(1);
                                Compound op = null;
                                for (Compound c : s.compounds) {
                                    if (c.id.equals(id)) {
                                        op = c;
                                        break;
                                    }
                                }
                                if (op == null) {
                                    System.out.println("need to add compound");
                                    op = (Compound) sInt.findParts(0, 0, id)[0];
                                    System.out.println("compound added");
                                    s.compounds.add(op);
                                    saveXML(op);
                                    String xmlC = new String(Files.readAllBytes(Paths.get(s.projectPath + s.projectName + File.separator + "parts" + File.separator + op.id)));
                                    op.name = Common.between(xmlC, "<dcterms:title>", "</dcterms:title>");
                                    try {
                                        op.smiles = Common.between(xmlC, "<sbol:elements>", "</sbol:elements>");
                                    } catch (Exception e) {
                                    }
                                }
                                r.compounds.add(op);
                                if (xml.contains(op.id + "_product")) {
                                    r.products.add(op);
                                } else {
                                    r.reactants.add(op);
                                }
                            }
                            //adding reactions ec numbers
                            pattern1 = "<ecnum:id>";
                            pattern2 = "</ecnum:id>";

                            pat = Pattern.compile(Pattern.quote(pattern1) + "(.*?)" + Pattern.quote(pattern2));
                            m = pat.matcher(xml);
                            while (m.find()) {
                                String id = m.group(1);
                                ECNumber op = null;
                                for (ECNumber c : s.ecNumbers) {
                                    if (c.id.equals(id)) {
                                        op = c;
                                        break;
                                    }
                                }
                                if (op == null) {
                                    op = sInt.findECNumber(id);
                                    if (op != null) {
                                        s.ecNumbers.add(op);
                                        saveXML(op);
                                    }
                                }
                                if (op != null) {
                                    r.ec.add(op);
                                } else {
                                    r.partialEC = id;
                                }
                            }
                        } else {
                            if (s.compounds.contains(p[i])) {
                                continue;
                            }
                            p[i].name = Common.between(xml, "<dcterms:title>", "</dcterms:title>");
                            try {
                                ((Compound) p[i]).smiles = Common.between(xml, "<sbol:elements>", "</sbol:elements>");
                            } catch (Exception e) {
                            }
                            s.compounds.add((Compound) p[i]);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    System.out.println("Part Added");
                }
                gm.updateGraph();
                mainWindow.setStatusLabel("Parts Added");
            }
        }.start();
    }

    public void showInfo(Compound c) {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Templates template = factory.newTemplates(new StreamSource(
                    new FileInputStream("xsl" + File.separator + "compound.xsl")));
            Transformer xformer = template.newTransformer();
            Source source = new StreamSource(new FileInputStream(s.projectPath + s.projectName + File.separator + "parts" + File.separator + c.id));
            Result result = new StreamResult(new FileOutputStream("temp.html"));
            xformer.transform(source, result);
            String html = new String(Files.readAllBytes(Paths.get("temp.html")));
            JEditorPane edit1 = new JEditorPane("text/html", html);


            String smiles = c.smiles;
            ImageComponent ic = null;
            if (smiles != null) {
                try {
                    IChemObjectBuilder bldr
                            = SilentChemObjectBuilder.getInstance();
                    SmilesParser smipar = new SmilesParser(bldr);
                    IAtomContainer mol = smipar.parseSmiles(smiles);
                    ic = new ImageComponent(mol);
                    //createFrame("Test", );
                } catch (Exception ex) {
                }

            }
            ScrollPane sp = new ScrollPane();
            sp.add(edit1);
            sp.setPreferredSize(new Dimension(edit1.getPreferredSize().width + 40, 600));
            final JDialog frame = new JDialog(mainWindow, "", true);
            if (ic != null) {
                ic.setPreferredSize(new Dimension(100, 100));
                frame.getContentPane().add(ic, BorderLayout.NORTH);
            }
            frame.getContentPane().add(sp, BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(mainWindow);
            frame.setVisible(true);
        } catch (Exception e) {
        }

    }

    public void findReactions(Compound cell) {
        Part[] parts = sInt.findParts(1, 1, cell.id);
        String[] names = new String[parts.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = parts[i].name;
        }

        final JDialog frame = new JDialog(mainWindow, "Add Reactions", true);
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));
        JLabel l1 = new JLabel("Choose reactions to add:");
        UI.addTo(jp, l1);

        JList rList = new JList();
        rList.setModel(new DefaultComboBoxModel(names));
        JScrollPane ecPane = new JScrollPane();
        ecPane.setMaximumSize(new Dimension(300, 150));
        ecPane.setPreferredSize(new Dimension(200, 150));
        ecPane.setViewportView(rList);

        ecPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        UI.addTo(jp, ecPane);
        JButton b1 = new JButton("Done");
        UI.addToRight(jp, b1, false);
        b1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Part[] p = new Part[rList.getSelectedIndices().length];
                for (int i = 0; i < p.length; i++) {
                    p[i] = parts[rList.getSelectedIndices()[i]];
                }
                addParts(p);
                frame.setVisible(false);
                frame.dispose();
                gm.updateGraph();
            }
        });
        frame.getContentPane().add(jp);
        frame.pack();
        frame.setLocationRelativeTo(mainWindow);
        frame.setVisible(true);
    }

    public void setEC(Reaction r) {
        final JDialog frame = new JDialog(mainWindow, "Choose EC number", true);
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));
        JLabel l1 = new JLabel("Choose EC number for reaction:");
        UI.addTo(jp, l1);
        String[] lm = new String[r.ec.size()];
        for (int i = 0; i < lm.length; i++) {
            lm[i] = r.ec.get(i).ecNumber;
        }
        JList ecList = new JList(lm);
        JScrollPane ecPane = new JScrollPane();
        ecPane.setMaximumSize(new Dimension(300, 150));
        ecPane.setPreferredSize(new Dimension(200, 150));
        ecPane.setViewportView(ecList);
        ecList.setSelectedIndex(r.pickedEC);
        ecPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        UI.addTo(jp, ecPane);
        JButton b1 = new JButton("Done");
        UI.addToRight(jp, b1, false);
        b1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                r.pickedEC = ecList.getSelectedIndex();
                frame.setVisible(false);
                frame.dispose();
                gm.updateGraph();
            }
        });
        frame.getContentPane().add(jp);
        frame.pack();
        frame.setLocationRelativeTo(mainWindow);
        frame.setVisible(true);
    }

    public void cellClicked(mxCell cell, int x, int y) {
        Part p = s.graphNodes.get(cell);
        if (p instanceof Reaction) {
            ReactionCellPopUp pop = new ReactionCellPopUp((Reaction) p);
            pop.show(mainWindow, x, y);
        } else if (p instanceof Compound) {
            CompoundCellPopUp pop = new CompoundCellPopUp((Compound) p);
            pop.show(mainWindow, x, y);
        }
    }

    public void chooseEnzyme(Reaction r) {
        final JDialog frame = new JDialog(mainWindow, "Choose Enzyme", true);
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));
        JLabel l1 = new JLabel("Choose enzyme for reaction:");
        UI.addTo(jp, l1);
        Protein[] p = sInt.getProteins(r.ec.get(r.pickedEC).ecNumber);
        String[] lm = new String[p.length];
        int pick = -1;
        for (int i = 0; i < lm.length; i++) {
            lm[i] = p[i].id;
            if (r.enzyme != null && r.enzyme.id.equals(p[i].id)) {
                pick = i;
            }
        }
        JList ecList = new JList(lm);
        JScrollPane ecPane = new JScrollPane();
        ecPane.setMaximumSize(new Dimension(300, 150));
        ecPane.setPreferredSize(new Dimension(200, 150));
        ecPane.setViewportView(ecList);
        if (pick != -1) {
            ecList.setSelectedIndex(pick);
        }
        ecPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        UI.addTo(jp, ecPane);
        JButton b1 = new JButton("Done");
        UI.addToRight(jp, b1, false);
        b1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Protein ps = p[ecList.getSelectedIndex()];
                if (!s.proteins.contains(ps)) {
                    s.proteins.add(ps);
                    saveXML(ps);
                }
                r.enzyme = ps;
                frame.setVisible(false);
                frame.dispose();
                gm.updateGraph();
            }
        });
        frame.getContentPane().add(jp);
        frame.pack();
        frame.setLocationRelativeTo(mainWindow);
        frame.setVisible(true);
    }


    public void competingReactions() {
        ArrayList<Reaction> reactions = new ArrayList<>();
        for (Compound c : s.compounds) {
            Reaction[] r = sInt.findCompetingReactions(s.organism, c.id);
            reactions.addAll(Arrays.asList(r));
        }
        addParts(reactions.toArray(new Reaction[0]));
        mainWindow.setStatusLabel("Competing reactions added.");
    }

    public void setTarget(Compound cell) {
        s.target = cell;
        gm.updateGraph();
    }

    public void edgeAdded(mxCell edge, mxCell source, mxCell target) {
        Reaction r = null;
        Compound c = null;
        Part p1 = s.graphNodes.get(source);
        Part p2 = s.graphNodes.get(target);
        boolean product = false;
        if (p1 instanceof Reaction) {
            product = true;
            r = (Reaction) p1;
        } else if (p1 instanceof Compound) {
            c = (Compound) p1;
        }
        if (p2 instanceof Reaction) {
            r = (Reaction) p2;
        } else if (p2 instanceof Compound) {
            c = (Compound) p2;
        }
        if (r != null && c != null && r.local) {
            r.compounds.add(c);
            if (product) {
                r.products.add(c);
            } else {
                r.reactants.add(c);
            }
        } else {
            mxGraph graph = mainWindow.workSpacePanel.graph;
            graph.getModel().beginUpdate();
            graph.removeCells(new Object[]{edge});
            graph.refresh();
            graph.getModel().endUpdate();
            graph.refresh();
        }
    }

    public void delete(Reaction r) {
        mxCell key = null;
        for (Map.Entry entry : s.graphNodes.entrySet()) {
            if (r == entry.getValue()) {
                key = (mxCell) entry.getKey();
                break;
            }
        }
        if (key != null) {
            s.reactions.remove(r);
            for (Part c : r.compounds) {
                boolean remove = true;
                for (Reaction or : s.reactions) {
                    if (or.compounds.contains(c)) {
                        remove = false;
                        break;
                    }
                }
                if (remove) {
                    delete((Compound) c);
                }
            }
            mxGraph graph = mainWindow.workSpacePanel.graph;
            graph.getModel().beginUpdate();
            graph.removeCells(new Object[]{key});
            graph.refresh();
            graph.getModel().endUpdate();
            graph.refresh();

        }

    }

    public void delete(Compound c) {
        mxCell key = null;
        for (Map.Entry entry : s.graphNodes.entrySet()) {
            if (c == entry.getValue()) {
                key = (mxCell) entry.getKey();
                break;
            }
        }
        if (key != null) {
            for (Reaction r : s.reactions) {
                if (r.compounds.contains(c) && !r.local) {
                    JOptionPane.showMessageDialog(null, "Cannot remove compound because it is used in reaction.");
                    return;
                }
            }
            s.compounds.remove(c);
            mxGraph graph = mainWindow.workSpacePanel.graph;
            graph.getModel().beginUpdate();
            graph.removeCells(new Object[]{key});
            graph.refresh();
            graph.getModel().endUpdate();
            graph.refresh();
        }
    }

    public void commonReaction() {
        Part c1 = s.graphNodes.get(gm.getSelected()[0]);
        Part c2 = s.graphNodes.get(gm.getSelected()[1]);
        if (c1 instanceof Compound && c2 instanceof Compound) {
            final JDialog frame = new JDialog(mainWindow, "Common Reactions", true);
            JPanel jp = new JPanel();
            jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));
            JLabel l1 = new JLabel("Common Reactions");
            UI.addTo(jp, l1);
            Reaction[] p = sInt.commonReactions(c1.id, c2.id);
            String[] lm = new String[p.length];
            for (int i = 0; i < lm.length; i++) {
                lm[i] = p[i].name;
            }
            JList rList = new JList(lm);
            JScrollPane rPane = new JScrollPane();
            rPane.setMaximumSize(new Dimension(300, 150));
            rPane.setPreferredSize(new Dimension(200, 150));
            rPane.setViewportView(rList);

            rPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            UI.addTo(jp, rPane);
            JButton b1 = new JButton("Done");
            UI.addToRight(jp, b1, false);
            b1.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    Reaction r = p[rList.getSelectedIndex()];
                    addParts(new Part[]{r});
                    frame.setVisible(false);
                    frame.dispose();
                    gm.updateGraph();
                }
            });
            frame.getContentPane().add(jp);
            frame.pack();
            frame.setLocationRelativeTo(mainWindow);
            frame.setVisible(true);
        }

    }

    public void structSimilarity(Reaction r) {
        try {
            final JDialog frame = new JDialog(mainWindow, "Structural Similarity", true);
            Object columnNames[] = {"Reactant", "Product", "Tanimoto"};
            ArrayList<String[]> rows = new ArrayList<>();
            for (Compound reactant : r.reactants) {
                for (Compound product : r.products) {
                    if(reactant.smiles==null || product.smiles == null){
                        continue;
                    }
                    String[] row = new String[3];
                    row[0] = reactant.name;
                    row[1] = product.name;
                    IChemObjectBuilder bldr
                            = SilentChemObjectBuilder.getInstance();
                    SmilesParser smilesParser = new SmilesParser(bldr);
                    IAtomContainer mol1 = smilesParser.parseSmiles(reactant.smiles);
                    IAtomContainer mol2 = smilesParser.parseSmiles(product.smiles);
                    HybridizationFingerprinter fingerprinter = new HybridizationFingerprinter();
                    IBitFingerprint bitset1 = fingerprinter.getBitFingerprint(mol1);
                    IBitFingerprint bitset2 = fingerprinter.getBitFingerprint(mol2);
                    row[2] = Double.toString(Tanimoto.calculate(bitset1, bitset2));
                    rows.add(row);
                }
            }
            Object rowData[][] = new Object[rows.size()][3];
            for(int i =0; i<rows.size(); i++){
                rowData[i] = rows.get(i);
            }
            JTable table = new JTable(rowData, columnNames);
            //table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            //new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED)
            frame.setMinimumSize(new Dimension(640, 360));
            frame.getContentPane().add(table);
            frame.pack();
            frame.setLocationRelativeTo(mainWindow);
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteSelected() {
        Object[] cells = gm.getSelected();
        mxGraph graph = mainWindow.workSpacePanel.graph;
        graph.getModel().beginUpdate();
        //remove reactions
        for (Object cell : cells) {
            if (s.graphNodes.get(cell) instanceof Reaction) {
                Reaction r = (Reaction) s.graphNodes.get(cell);
                s.reactions.remove(r);
                for (Part c : r.compounds) {
                    boolean remove = true;
                    for (Reaction or : s.reactions) {
                        if (or.compounds.contains(c)) {
                            remove = false;
                            break;
                        }
                    }
                    if (remove) {
                        delete((Compound) c);
                    }
                }

                graph.removeCells(new Object[]{cell});
            }
        }
        //remove compounds
        cells = gm.getSelected();
        for (Object cell : cells) {
            if (s.graphNodes.get(cell) instanceof Compound) {
                delete((Compound) s.graphNodes.get(cell));
            }
        }
        graph.refresh();
        graph.getModel().endUpdate();
        graph.refresh();
    }

    public void viewSelected() {
        if (gm.getSelected().length > 0) {
            showInfo((Compound) s.graphNodes.get(gm.getSelected()[0]));
        }
    }

    public void editSelected() {
        mxCell cell = (mxCell) gm.getSelected()[0];
        Part p = (Part) s.graphNodes.get(cell);
        if (p.local) {
            if (p instanceof Compound) {
                NewParts.editCompound(mainWindow, (Compound) p);
            } else if (p instanceof Reaction) {
                NewParts.editReaction(mainWindow, (Reaction) p);
            } else if (p instanceof ECNumber) {
                NewParts.editECNumber(mainWindow, (ECNumber) p);
            } else if (p instanceof Protein) {
                NewParts.editEnzyme(mainWindow, (Protein) p);
            }
        }
        gm.updateGraph();
    }
}
