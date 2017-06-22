package biosyndesign.core;


import biosyndesign.core.managers.*;
import biosyndesign.core.ui.*;

import javax.swing.*;
import java.io.File;


public class Main {

    private static MainWindow mainWindow;
    private static ProjectState s;

    public static ProjectIO projectIO;
    public static GraphManager gm;
    public static PartsManager pm;
    public static LocalPartsManager lpm;

    public static void main(String[] args) {
        System.setProperty("org.apache.commons.logging.Log",
                "org.apache.commons.logging.impl.NoOpLog");
        s = new ProjectState();
        s.projectName = "DefaultProject";
        s.projectPath = "DefaultProject/";
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        mainWindow = new MainWindow();
        projectIO = new ProjectIO(s, mainWindow);
        projectIO.showWelcome();
    }

    public static void initManagers(){
        gm = new GraphManager(s, mainWindow);
        pm = new PartsManager(s, mainWindow, gm);
        lpm = new LocalPartsManager(s, mainWindow);
    }


    public static void setState(ProjectState s) {
        Main.s = s;
    }

    public static void setFOption(int i) {
        s.fOption = i;
    }
}
