package biosyndesign.core.ui;


import biosyndesign.core.graphics.PartsGraph2;
import biosyndesign.core.sbol.Part;
import biosyndesign.core.sbol.SBOLInterface;
import com.mxgraph.view.mxGraph;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static org.openscience.cdk.smiles.smarts.parser.SMARTSParserConstants.p;

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
                s.projectPath = f.getParentFile().toString() + "/";
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
    }

    static boolean saveProjectAs() {
        FileDialog fd = new FileDialog((Frame) null, "Save Project", FileDialog.SAVE);
        fd.setVisible(true);
        fd.setFilenameFilter(new FileFilter());

        if (fd.getFiles().length > 0) {
            File f = fd.getFiles()[0];
            s.projectName = f.getName();
            s.projectPath = f.getAbsolutePath();
            mainWindow.setTitle("BiosynDesign - " + s.projectName);
            isSaved = true;
            new File(s.projectPath).mkdir();
            s.projectPath += "/";
            new File(s.projectPath + s.projectName).mkdir();
            new File(s.projectPath + s.projectName + "/images").mkdir();
            new File(s.projectPath + s.projectName + "/parts").mkdir();
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
                        Part cp = sInt.findCompound(0, 0, id)[0];
                        p[i].compounds.add(cp);
                        s.parts.add(cp);
                        saveXML(cp);
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
        mxGraph graph = mainWindow.workSpacePanel.graph;
        graph.getModel().beginUpdate();
        Object parent = graph.getDefaultParent();
        try {
            ArrayList<String> usedParts = new ArrayList<>();
            ArrayList<Object> objects = new ArrayList<>();
            for (int i = 0; i < s.reactions.size(); i++) {
                int rx = 50+i*150;
                int ry = 50+i*150;
                Object v1 = graph.insertVertex(parent, null, s.reactions.get(i).id, rx, ry, 80, 30);
                int jj = 0;
                for(int j = 0; j<s.reactions.get(i).compounds.size(); j++){
                    Part c = s.reactions.get(i).compounds.get(j);
                    if(usedParts.contains(c.id)){
                        graph.insertEdge(parent, null, "", v1, objects.get(usedParts.indexOf(c.id)));
                    }else {
                        Object v2 = graph.insertVertex(parent, null, c.id, rx - 40 + jj++ * 90, ry + 40, 80, 30);
                        graph.insertEdge(parent, null, "", v1, v2);
                        usedParts.add(c.id);
                        objects.add(v2);
                    }

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally
        {
            graph.getModel().endUpdate();
        }
        mainWindow.workSpacePanel.repaint();
    }

    private static void saveXML(Part p) throws IOException {
        URL website = new URL("http://www.cbrc.kaust.edu.sa/sbolme/" + p.url);
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream(s.projectPath + s.projectName + "/parts/" + p.id + ".xml");
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    }
}
