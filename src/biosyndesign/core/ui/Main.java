package biosyndesign.core.ui;


import com.mxgraph.util.mxCellRenderer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;


public class Main {

    private static GUI mainWindow;
    private static JFileChooser fc;
    public static File fcDir;
    public static ProjectState s;
    public static ProjectIO projectIO;
    public static GraphManager gm;
    public static PartsManager pm;

    public static void main(String[] args) {
        System.setProperty("org.apache.commons.logging.Log",
                "org.apache.commons.logging.impl.NoOpLog");
        s = new ProjectState();
        s.projectName = "DefaultProject";
        s.projectPath = "DefaultProject/";
        fcDir = new File(System.getProperty("user.dir"));

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        fc = new JFileChooser();


        projectIO = new ProjectIO(s);
        mainWindow = new GUI(projectIO);
        projectIO.setMainWindow(mainWindow);
        projectIO.showWelcome();
        gm = new GraphManager(s, mainWindow);
        pm = new PartsManager(s, mainWindow, gm);
    }


    public static void writeToConsole(String text) {
        mainWindow.writeToConsole(text);
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


}
