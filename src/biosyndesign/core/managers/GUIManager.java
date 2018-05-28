package biosyndesign.core.managers;


import biosyndesign.core.Main;
import biosyndesign.core.ui.MainWindow;
import com.mxgraph.view.mxGraph;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLWriter;

import javax.swing.*;
import java.io.File;

public class GUIManager {
    private ProjectState s;
    private PartsManager pm;
    private MainWindow mainWindow;

    public GUIManager(ProjectState s, PartsManager pm, MainWindow mainWindow) {
        this.s = s;
        this.pm = pm;
        this.mainWindow = mainWindow;
    }

    public void chooseRepository() {
        String p = JOptionPane.showInputDialog(null, "Enter repository URL:", s.prefix);
        if (p.length() > 0) {
            s.prefix = p;
            pm.setPrefix(p);
        }
    }

    public String getOrganism() {
        return s.organism;
    }

    public void useLocalRepo(boolean b) {
        pm.setRepo(b);
    }

    public void exportGraph() {
        try {
            String file = Main.projectIO.saveFile();
            mainWindow.workSpacePanel.generateSVGGraphImage(file);
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, "Could not export SVG!");
        }
    }

    public void exportPathway() {
        String file = Main.projectIO.saveFile();
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


    public void exportAA() {
        String file = Main.projectIO.saveFile();
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

    public void exportCDNA() {
        String file = Main.projectIO.saveFile();
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
