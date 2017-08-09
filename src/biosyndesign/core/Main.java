package biosyndesign.core;


import biosyndesign.core.managers.*;
import biosyndesign.core.sbol.LocalRepo;
import biosyndesign.core.ui.*;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.smiles.SmilesParser;

import javax.swing.*;
import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Main {

    private static MainWindow mainWindow;
    private static ProjectState s;
    private static LocalRepo lp;
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


        //lp.execute("select * from compounds");
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
