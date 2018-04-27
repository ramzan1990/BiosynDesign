package biosyndesign.core.managers;

import biosyndesign.core.Main;
import biosyndesign.core.sbol.*;
import biosyndesign.core.sbol.local.LocalRepo;
import biosyndesign.core.sbol.parts.*;
import biosyndesign.core.ui.MainWindow;
import biosyndesign.core.graphics.ImageComponent;
import biosyndesign.core.ui.popups.*;
import biosyndesign.core.utils.Common;
import biosyndesign.core.utils.UI;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import org.apache.commons.io.FileUtils;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLWriter;

/**
 * Created by Umarov on 2/21/2017.
 */
public class PartsManager {
    private MainWindow mainWindow;
    private ProjectState s;
    private GraphManager gm;
    public CDNAManager cm;
    Part[] searchParts;
    private Protein[] prots;
    private static SBOLInterface cInt;
    private static SBOLInterface sInt;
    private static SBOLInterface lInt;


    public PartsManager(ProjectState s, MainWindow mainWindow, GraphManager gm, LocalRepo lp) {
        sInt = new SBOLme(s.prefix);
        lInt = lp;
        cInt = sInt;
        this.s = s;
        this.mainWindow = mainWindow;
        this.gm = gm;
        cm = new CDNAManager(this, mainWindow);
    }

    private void saveXML(Part p) {
        try {
            //System.out.println("saving...");
            URL website = new URL(s.prefix + "/" + p.url);
            //ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            //FileOutputStream fos = new FileOutputStream(s.projectPath + s.projectName + File.separator + "parts" + File.separator + p.id);
            //fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            File f = new File(s.projectPath + s.projectName + File.separator + "parts" + File.separator + p.id);
            FileUtils.copyURLToFile(website, f, 5*1000, 5*1000);
            //System.out.println("saving done");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Connection to the server failed please try again.");
        }
    }

    private  ArrayList<String> saveReaction(String reaction, String ec){
        ArrayList<String> zp = new ArrayList<>();
        try {
            long startTime = System.currentTimeMillis();
            zp = cInt.getZip(reaction, s.organism, ec, s.projectPath + s.projectName + File.separator + "parts" + File.separator);
            long finishTime = System.currentTimeMillis();
            System.out.println("That took: " + (finishTime - startTime) + " ms");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Connection to the server failed please try again.");
        }
        return zp;
    }

    public void addParts(Part[] p, boolean update) {
        System.out.println("Adding Parts");
        new Thread() {
            public void run() {
                mainWindow.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                addPartsS(p, update, true);
                mainWindow.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

            }
        }.start();
    }

    public Part[] addPartsS(Part[] p, boolean update, boolean save) {
        Part[] np = new Part[p.length];
        for (int i = 0; i < p.length; i++) {
            try {
                if (!p[i].local) {
                    if(save) {
                        saveXML(p[i]);
                    }
                } else {
                    Common.copy(p[i].url, s.projectPath + s.projectName + File.separator + "parts" + File.separator + p[i].id);
                }
                String xml = new String(Files.readAllBytes(Paths.get(s.projectPath + s.projectName + File.separator + "parts" + File.separator + p[i].id)));
                xml = xml.replaceAll(":", "");
                SAXBuilder jdomBuilder = new SAXBuilder();
                Document jdomDocument = jdomBuilder.build(new StringReader(xml));
                XPathFactory xFactory = XPathFactory.instance();
                XPathExpression<Element> expr;
                List<Element> links;
                if (p[i] instanceof Reaction) {
                    if (s.reactions.contains(p[i])) {
                        continue;
                    }

                    Reaction r = (Reaction) p[i];
                    s.reactions.add(r);

                    //adding reactions ec numbers
                    expr = xFactory.compile("//enzyme_classid", Filters.element());
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
                            if (temp.length == 0) {
                            } else {
                                op = (ECNumber) temp[0];
                                if (op != null) {
                                    s.ecNumbers.add(op);
                                    //saveXML(op);
                                }
                            }
                        }
                        if (op != null) {
                            r.ec.add(op);
                        } else {
                            r.partialEC = id;
                        }
                    }
                    //Downloading ZIPed Reaction XMLs
                    //-----------------------------------------------------
                    String ecToSend = "no_ec";
                    if (r.ec.size() > 0) {
                        ecToSend = r.ec.get(r.pickedEC).ecNumber;
                    }
                    ArrayList<String> zp = saveReaction(p[i].id, ecToSend);
                    if (zp.size() > 0) {
                        r.nat = true;
                        r.enzyme =(Protein)addPartsS(new Part[]{new Protein(zp.get(0), "", "", "")}, false, false)[0];
                        r.enzymeType = "Native";
                        r.nativeEnzyme = true;
                        r.cDNA = cInt.getCDNA(r.enzyme.sequence, r.enzyme.organism);
                        r.baseCDNA = r.cDNA;
                    } else {
                        r.enzymeType = "Foreign";
                    }
                    //-----------------------------------------------------

                    //adding reactions compounds
                    expr = xFactory.compile("//sbolParticipation", Filters.element());
                    links = expr.evaluate(jdomDocument);
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
                            //op = (Compound) cInt.findParts(0, 0, id)[0];
                            op = (Compound) addPartsS(new Part[]{new Compound(id, "", "")}, false, false)[0];
                        }
                        r.compounds.add(op);
                        int s = Integer.parseInt(e.getChildText("reactionstoichiometry"));
                        if (prod) {
                            r.products.add(op);
                        } else {
                            r.reactants.add(op);
                        }
                        r.stoichiometry.put(op, s);
                    }

                } else if (p[i] instanceof Compound) {
                    int index = s.compounds.indexOf(p[i]);
                    if (index != -1) {
                        np[i] = s.compounds.get(index);
                        continue;
                    }
                    p[i].name =  xFactory.compile("//dctermstitle", Filters.element()).evaluate(jdomDocument).get(0).getValue();
                    p[i].url = xFactory.compile("//sbolComponentDefinition", Filters.element()).evaluate(jdomDocument).get(0).getAttributeValue("rdfabout");
                    try {
                        ((Compound) p[i]).smiles = xFactory.compile("//sbolelements", Filters.element()).evaluate(jdomDocument).get(0).getValue();
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
                    np[i] = p[i];
                } else if (p[i] instanceof Protein) {
                    int index = s.proteins.indexOf(p[i]);
                    if (index != -1) {
                        np[i] = s.proteins.get(index);
                        continue;
                    }
                    Protein pr = (Protein) p[i];
                    pr.sequence = xFactory.compile("//sbolelements", Filters.element()).evaluate(jdomDocument).get(0).getValue();
                    pr.name = xFactory.compile("//dctermstitle", Filters.element()).evaluate(jdomDocument).get(0).getValue();
                    pr.organism = xFactory.compile("//organismname", Filters.element()).evaluate(jdomDocument).get(0).getValue();
                    pr.url = xFactory.compile("//sbolComponentDefinition", Filters.element()).evaluate(jdomDocument).get(0).getAttributeValue("rdfabout");
                    pr.ecNumber = xFactory.compile("//enzyme_classid", Filters.element()).evaluate(jdomDocument).get(0).getValue();
                    pr.nat = pr.organism.equals(s.organism);
                    s.proteins.add(pr);
                    np[i] = p[i];
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
        return np;
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
        mxGraph graph = mainWindow.workSpacePanel.graph;
        Part p = s.graphNodes.get(cell);
        if (graph.getSelectionCells().length > 1) {
            boolean allCompound = true;
            Part[] pp = new Part[graph.getSelectionCells().length];
            for (int i = 0; i < graph.getSelectionCells().length; i++) {
                if (!(s.graphNodes.get(graph.getSelectionCells()[i]) instanceof Compound)) {
                    allCompound = false;
                }
                pp[i] = s.graphNodes.get(graph.getSelectionCells()[i]);
            }
            if (allCompound && graph.getSelectionCells().length == 2) {
                CompoundsCellPopUp pop = new CompoundsCellPopUp(pp);
                pop.show(mainWindow, x, y);
            } else {
                DeleteCellPopUp pop = new DeleteCellPopUp(pp);
                pop.show(mainWindow, x, y);
            }
        } else {
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
    }

    public void chooseEnzyme(Reaction r) {
        mainWindow.setCursor(new Cursor(Cursor.WAIT_CURSOR));
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
        int hh = (int) cmb2.getPreferredSize().getHeight();
        cmb2.setPreferredSize(new Dimension(450, hh));
        AutoCompleteDecorator.decorate(cmb2);
        UI.addTo(jp, cmb2);


        UI.addTo(jp, new JLabel("Enzyme "));
        DefaultListModel model = new DefaultListModel();
        JList partsList = new JList(model);
        JScrollPane partsPane = new JScrollPane();
        partsPane.setViewportView(partsList);
        partsPane.setPreferredSize(new Dimension(450, 200));
        partsPane.setMaximumSize(new Dimension(450, 200));
        partsPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        UI.addTo(jp, partsPane);

        //UI.addTo(jp, new JLabel("Primary structure "));
        //JTextArea ta = new JTextArea();
        //ta.setPreferredSize(new Dimension(400, 140));
        //ta.setMaximumSize(new Dimension(400, 140));
        //UI.addTo(jp, ta);
        cmb1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cmb1.getSelectedIndex() == 1 && cmb2.getItemCount() < 1) {
                    JOptionPane.showMessageDialog(null, "Foreign enzyme for this enzyme class doesn't exist!");
                }
                if (!r.nativeEnzyme && cmb1.getSelectedIndex() == 0) {
                    JOptionPane.showMessageDialog(null, "Native enzyme for this enzyme class doesn't exist!");
                    cmb1.setSelectedIndex(1);
                } else {
                    r.enzymeType = cmb1.getSelectedItem().toString();
                    prepareEnzymeDialog(r, cmb2, partsList);
                }
            }
        });

        cmb1.setSelectedItem(r.enzymeType);

        JButton b1 = new JButton("Done");
        UI.addToRight(jp, b1, false);
        b1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                r.enzyme = prots[partsList.getSelectedIndex()];
                addPartsS(new Part[]{r.enzyme}, true, true);
                r.cDNA = getCDNA(r);
                r.baseCDNA = r.cDNA;
                frame.setVisible(false);
                frame.dispose();
                updateTable();
            }
        });
        if (r.enzyme != null) {
            cmb2.setSelectedItem(r.enzyme.organism);
        }
        frame.getContentPane().add(jp);
        //frame.pack();
        frame.setSize(new Dimension(480, 480));
        frame.setLocationRelativeTo(mainWindow);
        frame.setVisible(true);
        mainWindow.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    public String getCDNA(Reaction r) {
        return cInt.getCDNA(r.enzyme.sequence, r.enzyme.organism);
    }

    private void prepareEnzymeDialog(Reaction r, JComboBox cmb2, JList partsList) {
        ArrayList<String> names = new ArrayList<>();
        if (r.enzymeType.equals("Native")) {
            prots = cInt.getProteins(r.ec.get(r.pickedEC).ecNumber, s.organism);
            int pick = -1;
            for (int i = 0; i < prots.length; i++) {
                names.add(prots[i].id);
                if (r.enzyme != null && prots[i].id.equals(r.enzyme.id)) {
                    pick = i;
                }
            }
            partsList.setModel(new DefaultComboBoxModel(names.toArray()));
            if (pick != -1) {
                partsList.setSelectedIndex(pick);
            }
            cmb2.setEnabled(false);
            cmb2.setSelectedItem(s.organism);
        } else if (r.enzymeType.equals("Foreign")) {
            cmb2.setSelectedIndex(-1);
            cmb2.setEnabled(true);
            cmb2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    names.clear();
                    if (cmb2.getSelectedItem() != null && Common.isOrganism(cmb2.getSelectedItem().toString())) {
                        prots = cInt.getProteins(r.ec.get(r.pickedEC).ecNumber, cmb2.getSelectedItem().toString());
                        int pick = -1;
                        for (int i = 0; i < prots.length; i++) {
                            names.add(prots[i].id);
                            if (r.enzyme != null && prots[i].id.equals(r.enzyme.id)) {
                                pick = i;
                            }
                        }
                        partsList.setModel(new DefaultComboBoxModel(names.toArray()));
                        if (pick != -1) {
                            partsList.setSelectedIndex(pick);
                        }
                    }
                }
            });
        } else {
            cmb2.setEnabled(false);
            cmb2.setSelectedIndex(-1);
            prots = lInt.getProteins(r.ec.get(r.pickedEC).ecNumber);

            int pick = -1;
            for (int i = 0; i < prots.length; i++) {
                names.add(prots[i].id);
                if (r.enzyme != null && prots[i].id.equals(r.enzyme.id)) {
                    pick = i;
                }
            }

            partsList.setModel(new DefaultComboBoxModel(names.toArray()));
            if (pick != -1) {
                partsList.setSelectedIndex(pick);
            }
        }
    }

    public void updateTable() {
        DefaultTableModel dtm1 = (DefaultTableModel) (mainWindow.enzymeTable.getModel());
        DefaultTableModel dtm2 = (DefaultTableModel) (mainWindow.genesTable.getModel());
        dtm1.setRowCount(0);
        dtm2.setRowCount(0);
        for (Reaction r : s.reactions) {
            if (r.enzyme != null) {
                dtm1.addRow(new Object[]{r.getEName(), r.enzymeType, r.enzyme.name, r.enzyme.sequence});
                if (!r.enzymeType.equals("Native")) {
                    dtm2.addRow(new Object[]{r.enzyme.name, r.enzyme.sequence, r.cDNA});
                }
            } else {
                dtm1.addRow(new Object[]{r.getEName(), r.enzymeType, "", ""});
            }
        }
        dtm1.fireTableDataChanged();
        dtm2.fireTableDataChanged();
        mainWindow.enzymeTable.repaint();
        mainWindow.genesTable.repaint();
    }

    public void rowClicked(int row, int x, int y) {
        TablePopUp pop = new TablePopUp(Main.pm, s.reactions.get(row));
        pop.show(mainWindow, x, y + 50);
    }

    public void rowClickedCDNA(int row, int x, int y) {
        int c = 0;
        Reaction cr = null;
        for (Reaction r : s.reactions) {
            if (r.enzyme != null && !r.enzymeType.equals("Native")) {
                if (row == c) {
                    cr = r;
                    break;
                }
                c++;
            }
        }
        cDNAPopUp pop = new cDNAPopUp(Main.pm, cr);
        pop.show(mainWindow, x, y + 50);
    }

    public void competingReactions() {
        ArrayList<Reaction> reactions = new ArrayList<>();
        for (Compound c : s.compounds) {
            Reaction[] r = cInt.findCompetingReactions(s.organism, c.id, s.maxCompeting);
            reactions.addAll(Arrays.asList(r));
        }
        final JDialog frame = new JDialog(mainWindow, "Competing Reactions", true);
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
        if (s.source != cell) {
            s.target = cell;
            gm.updateGraph();
        } else {
            JOptionPane.showMessageDialog(null, "This compound is already marked as source!");
        }
    }

    public void setSource(Compound cell) {
        if (s.target != cell) {
            s.source = cell;
            gm.updateGraph();
        } else {
            JOptionPane.showMessageDialog(null, "This compound is already marked as target!");
        }
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
                r.products.add(c);
            } else {
                r.reactants.add(c);
            }
            r.stoichiometry.put(c, s);
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
        commonReaction(c1, c2);
    }

    public void commonReaction(Part c1, Part c2) {
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
            for (Compound reactant : r.reactants) {
                for (Compound product : r.products) {
                    if (reactant.smiles == null || product.smiles == null) {
                        continue;
                    }
                    String[] row = new String[3];
                    row[0] = reactant.name;
                    row[1] = product.name;
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
        for (Compound reactant : r.reactants) {
            for (Compound product : r.products) {
                if (reactant.smiles == null || product.smiles == null) {
                    continue;
                }
                IChemObjectBuilder bldr
                        = SilentChemObjectBuilder.getInstance();
                SmilesParser smilesParser = new SmilesParser(bldr);
                IAtomContainer mol1 = smilesParser.parseSmiles(reactant.smiles);
                for (int i = 0; i < r.stoichiometry.get(reactant) - 1; i++) {
                    mol1.addAtom(mol1.getAtom(0));
                }
                IAtomContainer mol2 = smilesParser.parseSmiles(product.smiles);
                for (int i = 0; i < r.stoichiometry.get(product) - 1; i++) {
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

    public void delete(Part[] parts) {
        mxGraph graph = mainWindow.workSpacePanel.graph;
        graph.getModel().beginUpdate();
        //remove reactions
        for (Object part : parts) {
            if (part instanceof Reaction) {
                delete((Reaction) part);
            } else if (part instanceof Compound) {
                delete((Compound) part);
            } else if (part instanceof Protein) {
                delete((Protein) part);
            }
        }
        graph.refresh();
        graph.getModel().endUpdate();
        graph.refresh();
        gm.updateGraph();
        updateTable();
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
        if (value.length() == 0) {
            JOptionPane.showMessageDialog(null, "Value cannot be empty!");
            return;
        }
        new Thread() {
            public void run() {
                try {
                    searchParts = cInt.findParts(c1, c2, value);
                    String[] names = new String[searchParts.length];
                    for (int i = 0; i < names.length; i++) {
                        names[i] = searchParts[i].name + " [" + searchParts[i].id + "]";
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
                r.products.add(c1);
                r.reactants.add(c2);
                r.stoichiometry.put(c1, 1);
                r.stoichiometry.put(c2, 1);
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

    public void align() {
        if (s.source != null) {
            align(s.source, new ArrayList<Reaction>(), s.reactions.size(), false);
        }
        if (s.target != null) {
            align(s.target, new ArrayList<Reaction>(), s.reactions.size(), true);
        }
        gm.updateGraph();
    }

    private void align(Compound start, ArrayList<Reaction> exclude, int n, boolean isTarget) {
        if (n <= 0) {
            return;
        }
        n--;
        ArrayList<Compound> needAlign = new ArrayList<>();
        for (Reaction r : s.reactions) {
            if (exclude.contains(r)) {
                continue;
            }
            boolean c = true;
            if (r.reactants.contains(start)) {
                r.reverse = isTarget;
            } else if (r.products.contains(start)) {
                r.reverse = !isTarget;
            } else {
                c = false;
            }
            if (c) {
                if (isTarget) {
                    needAlign.addAll(r.getReactants());
                } else {
                    needAlign.addAll(r.getProducts());
                }
                exclude.add(r);
            }
        }
        for (Compound c : needAlign) {
            align(c, exclude, n, isTarget);
        }
    }

    public void exportPathway() {
        String file = Main.projectIO.openFile();
        try {
            SBOLDocument doc = new SBOLDocument();
            //s.compounds;
            //s.reactions;
            //s.ecNumbers;
            //s.target;
            //s.source;
            //s.organism;
            //s.proteins;

            SBOLWriter.write(doc, file);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
