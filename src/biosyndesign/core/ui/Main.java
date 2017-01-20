package biosyndesign.core.ui;


import biosyndesign.core.graphics.FileUtils;
import biosyndesign.core.graphics.PartsGraph2;
import biosyndesign.core.sbol.Part;
import biosyndesign.core.sbol.SBOLInterface;
import biosyndesign.core.utils.Common;
import biosyndesign.core.utils.Mover;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class Main {

    private static GUI mainWindow;
    private static ArrayList<Object> componentList;
    static Object selectedComponent;
    private static JFileChooser fc;
    public static File fcDir;
    public static Project s;
    private static boolean isSaved;
    private static SBOLInterface sInt;

    public static void main(String[] args) {
        s = new Project();
        s.projectName = "DefaultProject";
        s.projectPath = "DefaultProject/";
        fcDir = new File(System.getProperty("user.dir"));

        componentList = new ArrayList<Object>();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        mainWindow = new GUI();
        mainWindow.setTitle("BiosynDesign - " + s.projectName);
        mainWindow.setVisible(true);
        fc = new JFileChooser();
        sInt = new SBOLInterface();
    }

    static void newProject() {
        saveProjectAs();
    }

    static void openProject() {
        FileDialog fd = new FileDialog((Frame) null, "Open Project", FileDialog.LOAD);
        fd.setVisible(true);
        fd.setFilenameFilter(new FileFilter());
        if (fd.getFiles().length > 0) {
            ObjectInputStream ois = null;
            try {
                File f = fd.getFiles()[0];
                FileInputStream fin = new FileInputStream(f.getAbsolutePath());
                GZIPInputStream gis = new GZIPInputStream(fin);
                ois = new ObjectInputStream(gis);
                s = (Project) ois.readObject();
                mainWindow.setTitle("BiosynDesign - " + s.projectName);
                s.projectPath = f.getParentFile().toString() + "\\";
                FileUtils.loadGraph(mainWindow.workSpacePanel.graphComponent, s.projectPath + s.projectName + "\\graph.xml");
                isSaved = true;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Cannot open the project!", "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                if (ois != null) {
                    try {
                        ois.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        mainWindow.workSpacePanel.graph.refresh();
        mainWindow.workSpacePanel.repaint();
    }

    static boolean saveProjectAs() {
        FileDialog fd = new FileDialog((Frame) null, "Save Project", FileDialog.SAVE);
        fd.setVisible(true);
        fd.setFilenameFilter(new FileFilter());

        if (fd.getFiles().length > 0) {
            File f = fd.getFiles()[0];
            String oldPath = s.projectPath + s.projectName;
            s.projectName = f.getName();
            s.projectPath = f.getAbsolutePath();
            mainWindow.setTitle("BiosynDesign - " + s.projectName);
            new File(s.projectPath).mkdir();
            s.projectPath += "\\";
            new File(s.projectPath + s.projectName).mkdir();
            if(isSaved) {
                try {
                    Common.copy(oldPath + "\\images\\", s.projectPath + s.projectName + "\\images\\");
                    Common.copy(oldPath + "\\parts\\", s.projectPath + s.projectName + "\\parts\\");
                } catch (Exception e) {
                    new File(s.projectPath + s.projectName + "\\images").mkdir();
                    new File(s.projectPath + s.projectName + "\\parts").mkdir();
                }
            }else {
                new File(s.projectPath + s.projectName + "\\images").mkdir();
                new File(s.projectPath + s.projectName + "\\parts").mkdir();
            }
            isSaved = true;
            saveProject();
            return true;
        }
        return false;
    }

    static void saveProject() {
        if (!isSaved) {
            saveProjectAs();
            return;
        }
        try {
            FileOutputStream fout = new FileOutputStream(s.projectPath + s.projectName + ".bdp");
            GZIPOutputStream gz = new GZIPOutputStream(fout);
            ObjectOutputStream oos = new ObjectOutputStream(gz);
            oos.writeObject(s);
            oos.close();
            FileUtils.saveGraph(mainWindow.workSpacePanel.graphComponent, s.projectPath + s.projectName + "\\graph.xml");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error While Saving!", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    static void removeFromComponentList(Object c) {
        componentList.remove(c);
    }

    static void setSelectedComponent(Object c) {
        selectedComponent = c;
    }

    public static void writeToConsole(String text) {
        mainWindow.writeToConsole(text);
    }

    public static void addParts(Part[] p) {
        if (!isSaved) {
            saveProjectAs();
        }
        for (int i = 0; i < p.length; i++) {
            try {
                saveXML(p[i]);
                if (p[i].id.contains("R")) {
                    s.reactions.add(p[i]);
                    String xml = new String(Files.readAllBytes(Paths.get(s.projectPath + s.projectName + "/parts/" + p[i].id + ".xml")));
                    String pattern1 = "<sbol:definition rdf:resource=\"http://www.cbrc.kaust.edu.sa/sbolme/parts/compound/";
                    String pattern2 = "\"/>";

                    Pattern pat = Pattern.compile(Pattern.quote(pattern1) + "(.*?)" + Pattern.quote(pattern2));
                    Matcher m = pat.matcher(xml);
                    while (m.find()) {
                        String id = m.group(1);
                        Part op = null;
                        for(Part c:s.parts){
                            if(c.id.equals(id)){
                                op = c;
                                break;
                            }
                        }
                        if(op==null) {
                            op = sInt.findCompound(0, 0, id)[0];
                            s.parts.add(op);
                            saveXML(op);
                        }
                        p[i].compounds.add(op);
                    }

                } else {
                    s.parts.add(p[i]);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        updateGraph();

    }

    private static void updateGraph() {
        int[] nb = new int[s.reactions.size()];
        for(int i =0; i<nb.length;i++){
            Part r1 = s.reactions.get(i);
            int sum = 0;
            for(int j =0;j<nb.length;j++){
                Part r2 = s.reactions.get(j);
                if(r1==r2){
                    continue;
                }
                ArrayList<Part> common = new ArrayList<>(r1.compounds);
                common.retainAll(r2.compounds);
                sum+=common.size();
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
        graph.removeCells();
        Object parent = graph.getDefaultParent();
        try {
            ArrayList<String> usedParts = new ArrayList<>();
            ArrayList<Object> objects = new ArrayList<>();
            Mover m = new Mover(300);
            for (int i = 0; i < s.reactions.size(); i++) {
                System.out.println(s.reactions.get(i).id);
                int rx =  m.x()+390;
                int ry =  m.y()+390;
                Object v1 = graph.insertVertex(parent, null, s.reactions.get(i).id,  rx,  ry, 80, 30);
                Mover ms = new Mover(90);
                for (int j = 0; j < s.reactions.get(i).compounds.size(); j++) {
                    Part c = s.reactions.get(i).compounds.get(j);
                    if (usedParts.contains(c.id)) {
                        graph.insertEdge(parent, null, "", v1, objects.get(usedParts.indexOf(c.id)));
                    } else {
                        ms.move();
                        Object v2 = graph.insertVertex(parent, null, c.id,  rx + ms.x(), ry + ms.y(), 80, 30);
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
        }
    }

    private static void saveXML(Part p) throws IOException {
        URL website = new URL("http://www.cbrc.kaust.edu.sa/sbolme/" + p.url);
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream(s.projectPath + s.projectName + "/parts/" + p.id + ".xml");
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    }
    public static void saveImage(){
        try {
            BufferedImage image = mxCellRenderer.createBufferedImage(mainWindow.workSpacePanel.graph, null, 1, Color.WHITE, true, null);
            String name = "graph";
            File f = new File(s.projectPath + s.projectName + "\\images\\" + name + ".png");
            for (int i = 0; f.exists(); i++) {
                f = new File(s.projectPath + s.projectName + "\\images\\" + name + i + ".png");
            }
            ImageIO.write(image, "PNG", f);
            JOptionPane.showMessageDialog(null, "Done");
        }catch (Exception e){

        }
    }
}
