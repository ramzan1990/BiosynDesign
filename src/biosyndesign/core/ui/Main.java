package biosyndesign.core.ui;


import biosyndesign.core.sbol.*;
import biosyndesign.core.utils.Mover;
import biosyndesign.core.utils.UI;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {

    private static GUI mainWindow;
    private static JFileChooser fc;
    public static File fcDir;
    public static ProjectState s;
    private static SBOLInterface sInt;
    public static ProjectIO projectIO;


    public static void main(String[] args) {
        s = new ProjectState();
        s.projectName = "DefaultProject";
        s.projectPath = "DefaultProject/";
        fcDir = new File(System.getProperty("user.dir"));

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        fc = new JFileChooser();
        sInt = new SBOLInterface();

        projectIO = new ProjectIO(s);
        mainWindow = new GUI(projectIO);
        projectIO.setMainWindow(mainWindow);
        projectIO.showWelcome();
    }


    public static void writeToConsole(String text) {
        mainWindow.writeToConsole(text);
    }

    public static void addParts(Part[] p) {
        projectIO.checkSaved();
        for (int i = 0; i < p.length; i++) {
            try {
                saveXML(p[i]);
                if (p[i] instanceof Reaction) {
                    Reaction r = (Reaction) p[i];
                    s.reactions.add(r);
                    //adding reactions compounds
                    String xml = new String(Files.readAllBytes(Paths.get(s.projectPath + s.projectName + File.separator +"parts"+ File.separator + p[i].id + ".xml")));
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
                            op = (Compound) sInt.findCompound(0, 0, id)[0];
                            s.compounds.add(op);
                            saveXML(op);
                        }
                        r.compounds.add(op);
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
                            s.ecNumbers.add(op);
                            saveXML(op);
                        }
                        r.ec.add(op);
                    }
                } else {
                    s.compounds.add((Compound) p[i]);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        updateGraph();

    }

    static void updateGraph() {
        s.graphNodes = new Hashtable<>();
        int cc = 0;
        int[] nb = new int[s.reactions.size()];
        for (int i = 0; i < nb.length; i++) {
            Reaction r1 = s.reactions.get(i);
            int sum = 0;
            for (int j = 0; j < nb.length; j++) {
                Reaction r2 = s.reactions.get(j);
                if (r1 == r2) {
                    continue;
                }
                ArrayList<Part> common = new ArrayList<>(r1.compounds);
                common.retainAll(r2.compounds);
                sum += common.size();
            }
            nb[i] = sum;
        }

        Collections.sort(s.reactions, new Comparator<Part>() {
            public int compare(Part left, Part right) {
                return Integer.compare(nb[s.reactions.indexOf(left)], nb[s.reactions.indexOf(right)]);
            }
        });
        Collections.reverse(s.reactions);
        mxGraph graph = mainWindow.workSpacePanel.graph;
        graph.getModel().beginUpdate();
        graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));
        Object parent = graph.getDefaultParent();
        try {
            ArrayList<String> usedParts = new ArrayList<>();
            ArrayList<Object> objects = new ArrayList<>();
            Mover m = new Mover(300);
            for (int i = 0; i < s.reactions.size(); i++) {
                System.out.println(s.reactions.get(i).id);
                int rx = m.x() + 390;
                int ry = m.y() + 390;
                String rt = "";
                String sep = "";
                for (ECNumber ec : s.reactions.get(i).ec) {
                    rt += sep + ec.ecNumber;
                    sep = "\n";
                }
                Object v1 = graph.insertVertex(parent, null, rt, rx, ry, 80, 30, "REACTION");
                s.graphNodes.put(v1, s.reactions.get(i));
                Mover ms = new Mover(90);
                for (int j = 0; j < s.reactions.get(i).compounds.size(); j++) {
                    Part c = s.reactions.get(i).compounds.get(j);
                    if (usedParts.contains(c.id)) {
                        graph.insertEdge(parent, null, "", v1, objects.get(usedParts.indexOf(c.id)));
                    } else {
                        cc++;
                        ms.move();
                        Object v2 = graph.insertVertex(parent, null, c.id, rx + ms.x(), ry + ms.y(), 80, 30, "COMPOUND");
                        s.graphNodes.put(v2, s.reactions.get(i).compounds.get(j));
                        //, "shape=image;image=file:/c:/images/ME_C00022.png"
                        graph.insertEdge(parent, null, "", v1, v2);
                        usedParts.add(c.id);
                        objects.add(v2);
                    }

                }
                m.move();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            graph.refresh();
            graph.getModel().endUpdate();
            graph.refresh();
            mainWindow.setInfoLabel(cc, s.reactions.size());
        }
    }

    private static void saveXML(Part p) {
        try {
            URL website = new URL("http://www.cbrc.kaust.edu.sa/sbolme/" + p.url);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(s.projectPath + s.projectName +  File.separator +"parts"+ File.separator  + p.id + ".xml");
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (Exception e) {

        }
    }

    public static void saveImage() {
        try {
            BufferedImage image = mxCellRenderer.createBufferedImage(mainWindow.workSpacePanel.graph, null, 1, Color.WHITE, true, null);
            String name = "graph";
            File f = new File(s.projectPath + s.projectName + File.separator + "images" + File.separator + name + ".png");
            for (int i = 0; f.exists(); i++) {
                f = new File(s.projectPath + s.projectName + File.separator + "images" + File.separator + name + i + ".png");
            }
            ImageIO.write(image, "PNG", f);
            mainWindow.setStatusLabel("Image saved");
        } catch (Exception e) {

        }
    }


    public static void setEC(Object o) {
        Part p = s.graphNodes.get(o);
        if(p instanceof Reaction){
            Reaction r = (Reaction) p;
            final JDialog frame = new JDialog(mainWindow, "Choose EC number", true);
            JPanel jp = new JPanel();
            jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));
            JLabel l1 = new JLabel("Choose EC number for reaction:");
            UI.addTo(jp, l1);
            JTextField tf = new JTextField();
            tf.setPreferredSize(new Dimension(l1.getPreferredSize().width, tf.getPreferredSize().height));
            String c = "";
            for(ECNumber e:r.ec){
                c+=e.ecNumber + "\n";
            }
            tf.setText(c);
            UI.addTo(jp, tf);
            JButton b1 = new JButton("Done");
            UI.addToRight(jp, b1, false);
            b1.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    //addECNumber(RIF, tf.getText());
                    frame.setVisible(false);
                    frame.dispose();

                }
            });
            //frame.setPreferredSize(new Dimension(640, 480));
            frame.getContentPane().add(jp);
            frame.pack();
            frame.setLocationRelativeTo(mainWindow);
            frame.setVisible(true);
        }
    }

    public static void addECNumber(String RIF, String ecn) {
        ECNumber ec;
        if (s.ecNumbers.contains(new ECNumber(ecn))) {
            ec = s.ecNumbers.get(s.ecNumbers.indexOf(new ECNumber(ecn)));
        } else {
            ec = sInt.findECNumber(ecn);
        }
        saveXML(ec);
        s.ecNumbers.add(ec);
        s.reactions.get(s.reactions.indexOf(new Part(RIF))).ec.add(ec);
        updateGraph();
    }
}
