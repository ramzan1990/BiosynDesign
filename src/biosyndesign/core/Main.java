package biosyndesign.core;


import biosyndesign.core.managers.*;
import biosyndesign.core.sbol.local.LocalRepo;
import biosyndesign.core.ui.*;

import javax.swing.*;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Main {

    private static MainWindow mainWindow;
    private static ProjectState s;
    private static LocalRepo lp;
    public static ProjectIO projectIO;
    public static GraphManager gm;
    public static PartsManager pm;
    public static GUIManager guim;
    public static NewPartsManager lpm;

    public static void main(String[] args) {
        System.setProperty("org.apache.commons.logging.Log",
                "org.apache.commons.logging.impl.NoOpLog");
        s = new ProjectState();
        s.projectName = "DefaultProject";
        s.projectPath = "DefaultProject/";
        lp = new LocalRepo();
        lp.init();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        mainWindow = new MainWindow();
        projectIO = new ProjectIO(s, mainWindow);
        projectIO.showWelcome();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try
                {
                    DriverManager.getConnection("jdbc:derby:;shutdown=true");
                }
                catch (SQLException se)
                {
                }}});


        //lp.executeJSON("select * from compounds");
    }

    public static void initManagers(){
        gm = new GraphManager(s, mainWindow);
        pm = new PartsManager(s, mainWindow, gm, lp);
        lpm = new NewPartsManager(s, mainWindow);
        guim = new GUIManager(s, pm);

        gm.updateGraph();
        pm.updateTable();
    }


    public static void setState(ProjectState s) {
        Main.s = s;
    }

    public static LocalRepo getLocalRepo(){
        return lp;
    }
}
