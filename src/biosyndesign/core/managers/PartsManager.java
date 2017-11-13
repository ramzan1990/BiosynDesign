package biosyndesign.core.managers;

import biosyndesign.core.Main;
import biosyndesign.core.sbol.*;
import biosyndesign.core.sbol.local.LocalRepo;
import biosyndesign.core.sbol.parts.*;
import biosyndesign.core.ui.MainWindow;
import biosyndesign.core.ui.ImageComponent;
import biosyndesign.core.ui.popups.CompoundCellPopUp;
import biosyndesign.core.ui.popups.EnzymeCellPopUp;
import biosyndesign.core.ui.popups.ReactionCellPopUp;
import biosyndesign.core.ui.popups.TablePopUp;
import biosyndesign.core.utils.Common;
import biosyndesign.core.utils.UI;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.*;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.similarity.Tanimoto;
import org.openscience.cdk.smiles.SmilesParser;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

/**
 * Created by Umarov on 2/21/2017.
 */
public class PartsManager {
    private MainWindow mainWindow;
    private ProjectState s;
    private GraphManager gm;
    Part[] searchParts;
    private Protein[] prots;
    private static SBOLInterface cInt;
    private static SBOLInterface sInt;
    private static SBOLInterface lInt;


    public PartsManager(ProjectState s, MainWindow mainWindow, GraphManager gm) {
        sInt = new SBOLme(s.prefix);
        lInt = new LocalRepo();
        cInt = sInt;
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

    public void addParts(Part[] p, boolean update) {
        System.out.println("Adding Parts");
        new Thread() {
            public void run() {
                addPartsS(p, update);
            }
        }.start();
    }

    private void addPartsS(Part[] p, boolean update){
        for (int i = 0; i < p.length; i++) {
            try {
                if (!p[i].local) {
                    saveXML(p[i]);
                } else {
                    Common.copy(p[i].url, s.projectPath + s.projectName + File.separator + "parts" + File.separator + p[i].id);
                }
                String xml = new String(Files.readAllBytes(Paths.get(s.projectPath + s.projectName + File.separator + "parts" + File.separator + p[i].id)));

                if (p[i] instanceof Reaction) {
                    if (s.reactions.contains(p[i])) {
                        continue;
                    }
                    xml = xml.replaceAll(":", "");
                    SAXBuilder jdomBuilder = new SAXBuilder();
                    Document jdomDocument = jdomBuilder.build(new StringReader(xml));
                    XPathFactory xFactory = XPathFactory.instance();
                    Reaction r = (Reaction) p[i];
                    s.reactions.add(r);
                    //adding reactions compounds
                    XPathExpression<Element> expr = xFactory.compile("//sbolParticipation", Filters.element());
                    List<Element> links = expr.evaluate(jdomDocument);
                    for (Element e : links) {
                        String id = e.getChildText("sboldisplayId");
                        if (!(id.contains("product") || id.contains("reactant"))) {
                            continue;
                        }
                        boolean prod = id.contains("product");
                        id = id.substring(0, id.lastIndexOf("_"));
                        Compound op = null;
                        for (Compound c : s.compounds) {
                            if (c.id.equals(id)) {
                                op = c;
                                break;
                            }
                        }
                        if (op == null) {
                            System.out.println("need to add compound");
                            op = (Compound) cInt.findParts(0, 0, id)[0];
                            System.out.println("compound added");
                            addPartsS(new Part[]{op}, false);
                        }
                        r.compounds.add(op);
                        int s = Integer.parseInt(e.getChildText("reactionstoichiometry"));
                        if (prod) {
                            r.products.add(new CompoundStoichiometry(op, s));
                        } else {
                            r.reactants.add(new CompoundStoichiometry(op, s));
                        }
                    }
                    //adding reactions ec numbers
                    expr = xFactory.compile("//ecnumid", Filters.element());
                    links = expr.evaluate(jdomDocument);
                    for (Element e : links) {
                        String id = e.getText();
                        ECNumber op = null;
                        for (ECNumber c : s.ecNumbers) {
                            if (c.id.equals(id)) {
                                op = c;
                                break;
                            }
                        }
                        if (op == null) {
                            Part[] temp = cInt.findParts(2, 0, id);
                            if(temp.length==0){
                            }else {
                                op = (ECNumber) temp[0];
                                if (op != null) {
                                    s.ecNumbers.add(op);
                                    saveXML(op);
                                }
                            }
                        }
                        if (op != null) {
                            r.ec.add(op);
                        } else {
                            r.partialEC = id;
                        }
                    }
                    r.nat = cInt.isNative(r.id, s.organism);
                    if(r.ec.size()>0) {
                        Protein prots[] = cInt.getProteins(r.ec.get(r.pickedEC).ecNumber, s.organism);
                        if (prots.length > 0) {
                            r.enzyme = prots[0];
                            r.enzymeType = "Native";
                            addPartsS(new Part[]{prots[0]}, false);
                        } else {
                            r.enzymeType = "Foreign";
                        }
                    }
                } else if (p[i] instanceof Compound) {
                    if (s.compounds.contains(p[i])) {
                        continue;
                    }
                    p[i].name = Common.between(xml, "<dcterms:title>", "</dcterms:title>");
                    try {
                        ((Compound) p[i]).smiles = Common.between(xml, "<sbol:elements>", "</sbol:elements>");
                    } catch (Exception e) {
                    }
                    s.compounds.add((Compound) p[i]);
                    String smiles = ((Compound) p[i]).smiles;
                    if (smiles != null) {
                        try {
                            IChemObjectBuilder bldr
                                    = SilentChemObjectBuilder.getInstance();
                            SmilesParser smipar = new SmilesParser(bldr);
                            IAtomContainer mol = smipar.parseSmiles(smiles);
                            ImageComponent ic = new ImageComponent(mol);
                            ic.setText(p[i].name);
                            Main.projectIO.saveComponentImage(ic, p[i].id);
                        } catch (Exception ex) {
                        }
                    }
                } else if (p[i] instanceof Protein) {
                    Protein pr = (Protein) p[i];
                    pr.sequence = Common.between(xml, "<sbol:elements>", "</sbol:elements>");
                    pr.name = Common.between(xml, "<dcterms:title>", "</dcterms:title>");
                    pr.nat = pr.organism.equals(s.organism);
                    s.proteins.add(pr);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.out.println("Part Added");
        }
        if (update) {
            gm.updateGraph();
            updateTable();
            mainWindow.setStatusLabel("Parts Added");
        }
    }

    public void showInfo(Part c) {
        try {
            ImageComponent ic = null;
            TransformerFactory factory = TransformerFactory.newInstance();
            Templates template = null;
            if (c instanceof Compound) {
                template = factory.newTemplates(new StreamSource(
                        new FileInputStream("xsl" + File.separator + "compound.xsl")));
                String smiles = ((Compound) c).smiles;
                if (smiles != null) {
                    try {
                        IChemObjectBuilder bldr
                                = SilentChemObjectBuilder.getInstance();
                        SmilesParser smipar = new SmilesParser(bldr);
                        IAtomContainer mol = smipar.parseSmiles(smiles);
                        ic = new ImageComponent(mol);
                    } catch (Exception ex) {
                    }
                }
            } else if (c instanceof Reaction) {
                template = factory.newTemplates(new StreamSource(
                        new FileInputStream("xsl" + File.separator + "reaction.xsl")));
            } else if (c instanceof ECNumber) {
                template = factory.newTemplates(new StreamSource(
                        new FileInputStream("xsl" + File.separator + "enzyme.xsl")));
            } else if (c instanceof Protein) {
                template = factory.newTemplates(new StreamSource(
                        new FileInputStream("xsl" + File.separator + "protein.xsl")));
            }
            Transformer xformer = template.newTransformer();
            Source source = new StreamSource(new FileInputStream(s.projectPath + s.projectName + File.separator + "parts" + File.separator + c.id));
            Result result = new StreamResult(new FileOutputStream("temp.html"));
            xformer.transform(source, result);
            String html = new String(Files.readAllBytes(Paths.get("temp.html")));
            JEditorPane edit1 = new JEditorPane("text/html", html);
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
            frame.setSize(new Dimension(1024, 768));
            frame.setLocationRelativeTo(mainWindow);
            frame.setVisible(true);
        } catch (Exception e) {
        }

    }

    public void findReactions(Compound cell) {
        Part[] parts = cInt.findParts(1, 1, cell.id);
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
                addParts(p, true);
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
        } else if (p instanceof Protein) {
            EnzymeCellPopUp pop = new EnzymeCellPopUp((Protein) p);
            pop.show(mainWindow, x, y);
        }
    }

    public void chooseEnzyme(Reaction r) {
        final JDialog frame = new JDialog(mainWindow, "Choose Enzyme", true);
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));
        UI.addTo(jp, new JLabel("Reaction " + r.getEName()));

        UI.addTo(jp, new JLabel("Enzyme Origin "));
        JComboBox cmb1 = new JComboBox();
        cmb1.addItem("Native");
        cmb1.addItem("Foreign");
        cmb1.addItem("De novo");
        UI.addTo(jp, cmb1);

        UI.addTo(jp, new JLabel("Organism Filter "));
        String[] options = cInt.getOrganisms(r.ec.get(r.pickedEC).ecNumber);
        JComboBox cmb2 = new JComboBox(options);
        AutoCompleteDecorator.decorate(cmb2);
        UI.addTo(jp, cmb2);

        UI.addTo(jp, new JLabel("Enzyme "));
        DefaultListModel model = new DefaultListModel();
        JList partsList = new JList(model);
        JScrollPane partsPane = new JScrollPane();
        partsPane.setViewportView(partsList);
        partsPane.setPreferredSize(new Dimension(400, 200));
        partsPane.setMaximumSize(new Dimension(400, 200));
        partsPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        UI.addTo(jp, partsPane);

        //UI.addTo(jp, new JLabel("Primary structure "));
        //JTextArea ta = new JTextArea();
        //ta.setPreferredSize(new Dimension(400, 140));
        //ta.setMaximumSize(new Dimension(400, 140));
        //UI.addTo(jp, ta);

        prepareEnzymeDialog(r, cmb1, cmb2, partsList);
        cmb1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                r.enzymeType = cmb1.getSelectedItem().toString();
                prepareEnzymeDialog(r, null, cmb2, partsList);
            }
        });
        JButton b1 = new JButton("Done");
        UI.addToRight(jp, b1, false);
        b1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                r.enzyme = prots[partsList.getSelectedIndex()];
                addParts(new Part[]{r.enzyme}, true);
                frame.setVisible(false);
                frame.dispose();
            }
        });
        if(r.enzyme!=null){
            cmb2.setSelectedItem(r.enzyme.organism);
        }
        frame.getContentPane().add(jp);
        frame.pack();
        frame.setLocationRelativeTo(mainWindow);
        frame.setVisible(true);
    }

    private void prepareEnzymeDialog(Reaction r, JComboBox cmb1, JComboBox cmb2, JList partsList) {
        ArrayList<String> names = new ArrayList<>();
        if (r.enzymeType.equals("Native")) {
            if(cmb1!=null){
                cmb1.setSelectedIndex(0);
            }
            prots = cInt.getProteins(r.ec.get(r.pickedEC).ecNumber, s.organism);
            int pick = -1;
            for(int i =0; i< prots.length;i++){
                names.add(prots[i].id);
                if(r.enzyme!=null && prots[i].id.equals(r.enzyme.id)){
                    pick = i;
                }
            }
            partsList.setModel(new DefaultComboBoxModel(names.toArray()));
            if(pick!=-1){
                partsList.setSelectedIndex(pick);
            }
            cmb2.setEnabled(false);
            cmb2.setSelectedItem(s.organism);
        } else if (r.enzymeType.equals("Foreign")) {
            if(cmb1!=null){
                cmb1.setSelectedIndex(1);
            }
            cmb2.setSelectedIndex(-1);
            cmb2.setEnabled(true);
            cmb2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    names.clear();
                    if(cmb2.getSelectedItem()!=null && Common.isOrganism(cmb2.getSelectedItem().toString())){
                        prots = cInt.getProteins(r.ec.get(r.pickedEC).ecNumber, cmb2.getSelectedItem().toString());
                        int pick = -1;
                        for(int i =0; i< prots.length;i++){
                            names.add(prots[i].id);
                            if(r.enzyme!=null && prots[i].id.equals(r.enzyme.id)){
                                pick = i;
                            }
                        }
                        partsList.setModel(new DefaultComboBoxModel(names.toArray()));
                        if(pick!=-1){
                            partsList.setSelectedIndex(pick);
                        }
                    }
                }
            });
        } else {
            if(cmb1!=null){
                cmb1.setSelectedIndex(2);
            }
            cmb2.setEnabled(false);
            cmb2.setSelectedIndex(-1);
            prots = lInt.getProteins(r.ec.get(r.pickedEC).ecNumber, "");

            int pick = -1;
            for(int i =0; i< prots.length;i++){
                names.add(prots[i].id);
                if(r.enzyme!=null && prots[i].id.equals(r.enzyme.id)){
                    pick = i;
                }
            }

            partsList.setModel(new DefaultComboBoxModel(names.toArray()));
            if(pick!=-1){
                partsList.setSelectedIndex(pick);
            }
        }
    }

    public void updateTable() {
        DefaultTableModel dtm = (DefaultTableModel) (mainWindow.enzymeTable.getModel());
        dtm.setRowCount(0);
        for (Reaction r : s.reactions) {
            if (r.enzyme != null) {
                dtm.addRow(new Object[]{r.getEName(), r.enzymeType, r.enzyme.name, r.enzyme.sequence});
            } else {
                dtm.addRow(new Object[]{r.getEName(), r.enzymeType, "", ""});
            }
        }
        dtm.fireTableDataChanged();
        mainWindow.enzymeTable.repaint();
    }

    public void rowClicked(int row, int x, int y) {
        TablePopUp pop = new TablePopUp(Main.pm, s.reactions.get(row));
        pop.show(mainWindow, x, y+50);
    }

    public void competingReactions() {
        ArrayList<Reaction> reactions = new ArrayList<>();
        for (Compound c : s.compounds) {
            Reaction[] r = cInt.findCompetingReactions(s.organism, c.id, s.maxCompeting);
            reactions.addAll(Arrays.asList(r));
        }
        final JDialog frame = new JDialog(mainWindow, "Choose Enzyme", true);
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));
        JLabel l1 = new JLabel("Choose reactions to add:");
        UI.addTo(jp, l1);
        String[] lm = new String[reactions.size()];
        int pick = -1;
        for (int i = 0; i < lm.length; i++) {
            lm[i] = reactions.get(i).name;
        }
        JList rList = new JList(lm);
        rList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane rPane = new JScrollPane();
        rPane.setMaximumSize(new Dimension(300, 150));
        rPane.setPreferredSize(new Dimension(200, 150));
        rPane.setViewportView(rList);
        if (pick != -1) {
            rList.setSelectedIndex(pick);
        }
        rPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        UI.addTo(jp, rPane);
        JButton b1 = new JButton("Done");
        UI.addToRight(jp, b1, false);
        b1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Reaction[] selectedReactions = new Reaction[rList.getSelectedIndices().length];
                for (int i = 0; i < selectedReactions.length; i++) {
                    selectedReactions[i] = reactions.get(rList.getSelectedIndices()[i]);
                }
                addParts(selectedReactions, true);
                frame.setVisible(false);
                frame.dispose();
            }
        });
        frame.getContentPane().add(jp);
        frame.pack();
        frame.setLocationRelativeTo(mainWindow);
        frame.setVisible(true);
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
            int s = Integer.parseInt(JOptionPane.showInputDialog("Stoichiometry:"));
            if (product) {
                r.products.add(new CompoundStoichiometry(c, s));
            } else {
                r.reactants.add(new CompoundStoichiometry(c, s));
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


    public void commonReaction() {
        Part c1 = s.graphNodes.get(gm.getSelected()[0]);
        Part c2 = s.graphNodes.get(gm.getSelected()[1]);
        if (c1 instanceof Compound && c2 instanceof Compound) {
            final JDialog frame = new JDialog(mainWindow, "Common Reactions", true);
            JPanel jp = new JPanel();
            jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));
            JLabel l1 = new JLabel("Common Reactions");
            UI.addTo(jp, l1);
            Reaction[] p = cInt.commonReactions(c1.id, c2.id);
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
                    addParts(new Part[]{r}, true);
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
            ArrayList<String> vals = getSim(r, 0);
            int cc = 0;
            for (CompoundStoichiometry reactant : r.reactants) {
                for (CompoundStoichiometry product : r.products) {
                    if (reactant.c.smiles == null || product.c.smiles == null) {
                        continue;
                    }
                    String[] row = new String[3];
                    row[0] = reactant.c.name;
                    row[1] = product.c.name;
                    row[2] = vals.get(cc++);
                    rows.add(row);
                }
            }
            Object rowData[][] = new Object[rows.size()][3];
            for (int i = 0; i < rows.size(); i++) {
                rowData[i] = rows.get(i);
            }
            JTable table = new JTable(rowData, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            frame.setMinimumSize(new Dimension(640, 360));
            JPanel p = new JPanel();
            UI.addTo(p, new JLabel("Select Fingerprinter"));
            String[] fList = {"PubchemFingerprinter", "EStateFingerprinter", "ExtendedFingerprinter", "GraphOnlyFingerprinter", "HybridizationFingerprinter",
                    "ShortestPathFingerprinter", "KlekotaRothFingerprinter", "MACCSFingerprinter", "SubstructureFingerprinter"};
            JComboBox fComboBox = new JComboBox(fList);
            fComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        ArrayList<String> newVals = getSim(r, fComboBox.getSelectedIndex());
                        for (int i = 0; i < table.getModel().getRowCount(); i++) {
                            table.getModel().setValueAt(newVals.get(i), i, 2);
                        }
                    } catch (Exception ex) {
                    }
                }
            });
            p.add(fComboBox);
            frame.getContentPane().add(p, BorderLayout.NORTH);
            frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(mainWindow);
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> getSim(Reaction r, int f) throws CDKException {
        ArrayList<String> list = new ArrayList<>();
        for (CompoundStoichiometry reactant : r.reactants) {
            for (CompoundStoichiometry product : r.products) {
                if (reactant.c.smiles == null || product.c.smiles == null) {
                    continue;
                }
                IChemObjectBuilder bldr
                        = SilentChemObjectBuilder.getInstance();
                SmilesParser smilesParser = new SmilesParser(bldr);
                IAtomContainer mol1 = smilesParser.parseSmiles(reactant.c.smiles);
                for (int i = 0; i < reactant.s - 1; i++) {
                    mol1.addAtom(mol1.getAtom(0));
                }
                IAtomContainer mol2 = smilesParser.parseSmiles(product.c.smiles);
                for (int i = 0; i < product.s - 1; i++) {
                    mol2.addAtom(mol2.getAtom(0));
                }
                IBitFingerprint bitset1 = null, bitset2 = null;
                IFingerprinter fingerprinter = null;
                if (f == 0) {
                    fingerprinter = new PubchemFingerprinter(DefaultChemObjectBuilder.getInstance());
                } else if (f == 1) {
                    fingerprinter = new EStateFingerprinter();
                } else if (f == 2) {
                    fingerprinter = new ExtendedFingerprinter();
                } else if (f == 3) {
                    fingerprinter = new GraphOnlyFingerprinter();
                } else if (f == 4) {
                    fingerprinter = new HybridizationFingerprinter();
                } else if (f == 5) {
                    fingerprinter = new ShortestPathFingerprinter();
                } else if (f == 6) {
                    fingerprinter = new KlekotaRothFingerprinter();
                } else if (f == 7) {
                    fingerprinter = new MACCSFingerprinter();
                } else if (f == 8) {
                    fingerprinter = new SubstructureFingerprinter();
                }
                bitset1 = fingerprinter.getBitFingerprint(mol1);
                bitset2 = fingerprinter.getBitFingerprint(mol2);
                list.add(Double.toString(Tanimoto.calculate(bitset1, bitset2)));
            }
        }
        return list;
    }

    public void deleteSelected() {
        Object[] cells = gm.getSelected();
        delete(cells);
    }

    public void delete(Object[] cells) {
        mxGraph graph = mainWindow.workSpacePanel.graph;
        graph.getModel().beginUpdate();
        //remove reactions
        for (Object cell : cells) {
            if (s.graphNodes.get(cell) instanceof Reaction) {
                delete((Reaction) s.graphNodes.get(cell));
            } else if (s.graphNodes.get(cell) instanceof Compound) {
                delete((Compound) s.graphNodes.get(cell));
            } else if (s.graphNodes.get(cell) instanceof Protein) {
                delete((Protein) s.graphNodes.get(cell));
            }
        }
        graph.refresh();
        graph.getModel().endUpdate();
        graph.refresh();
        gm.updateGraph();
        updateTable();
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
            for (ECNumber ec : r.ec) {
                boolean remove = true;
                for (Reaction or : s.reactions) {
                    if (or.ec.contains(ec)) {
                        remove = false;
                        break;
                    }
                }
                if (remove) {
                    delete(ec);
                }
            }
            if (r.enzyme != null) {
                delete(r.enzyme);
            }
            mxGraph graph = mainWindow.workSpacePanel.graph;
            graph.removeCells(new Object[]{key});
        }
        try {
            new File(s.projectPath + s.projectName + File.separator + "parts" + File.separator + r.id).delete();
        } catch (Exception e) {

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
            graph.removeCells(new Object[]{key});
        }
        try {
            new File(s.projectPath + s.projectName + File.separator + "parts" + File.separator + c.id).delete();
        } catch (Exception e) {

        }
    }

    public void delete(ECNumber ec) {
        s.ecNumbers.remove(ec);
        try {
            new File(s.projectPath + s.projectName + File.separator + "parts" + File.separator + ec.id).delete();
        } catch (Exception e) {

        }
    }

    public void delete(Protein p) {
        s.proteins.remove(p);
        for (Reaction or : s.reactions) {
            if (or.enzyme == p) {
                or.enzyme = null;
            }
        }
        try {
            new File(s.projectPath + s.projectName + File.separator + "parts" + File.separator + p.id).delete();
        } catch (Exception e) {

        }
    }

    public void viewSelected() {
        if (gm.getSelected().length > 0) {
            showInfo(s.graphNodes.get(gm.getSelected()[0]));
        }
    }

    public void editSelected() {
        mxCell cell = (mxCell) gm.getSelected()[0];
        Part p = (Part) s.graphNodes.get(cell);
        if (p.local) {
            if (p instanceof Compound) {
                Main.lpm.editCompound((Compound) p);
            } else if (p instanceof Reaction) {
                Main.lpm.editReaction((Reaction) p);
            } else if (p instanceof ECNumber) {
                Main.lpm.editECNumber((ECNumber) p);
            } else if (p instanceof Protein) {
                Main.lpm.editEnzyme((Protein) p);
            }
        }
        gm.updateGraph();
    }

    public void search(int c1, int c2, String value) {
        new Thread() {
            public void run() {
                try {
                    searchParts = cInt.findParts(c1, c2, value);
                    String[] names = new String[searchParts.length];
                    for (int i = 0; i < names.length; i++) {
                        names[i] = searchParts[i].name;
                    }
                    mainWindow.setResults(names);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.start();
    }

    public void addPartsSelected(int[] selectedIndices) {
        Part[] p = new Part[selectedIndices.length];
        for (int i = 0; i < p.length; i++) {
            p[i] = searchParts[selectedIndices[i]];
        }
        addParts(p, true);
    }

    public void similarityClicked() {
        if (gm.getSelected().length > 1) {
            try {
                Compound c1 = (Compound) s.graphNodes.get(gm.getSelected()[0]);
                Compound c2 = (Compound) s.graphNodes.get(gm.getSelected()[1]);
                Reaction r = new Reaction("", "", "", 0);
                r.products.add(new CompoundStoichiometry(c1, 1));
                r.reactants.add(new CompoundStoichiometry(c2, 1));
                structSimilarity(r);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Select two compounds!");
            }
        }
    }

    public void setPrefix(String p) {
        ((SBOLme) cInt).prefix = p;
    }

    public void setRepo(boolean local) {
        if (local) {
            cInt = lInt;
        } else {
            cInt = sInt;
        }
    }

    public void reverse(Reaction r) {
        r.reverse = !r.reverse;
        gm.updateGraph();
    }
}
