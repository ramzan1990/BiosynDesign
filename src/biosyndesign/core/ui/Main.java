package biosyndesign.core.ui;


import biosyndesign.core.sbol.Part;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
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

    public static void main(String[] args){
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
        if(!isSaved){
            saveProjectAs();
        }
        for(int i =0; i<p.length;i++){
            try {
                s.parts.add(p[i]);
                URL website = new URL("http://www.cbrc.kaust.edu.sa/sbolme/"+p[i].url);
                ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                FileOutputStream fos = new FileOutputStream(s.projectPath + s.projectName + "/parts/" + p[i].id  + ".xml");
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }
}
