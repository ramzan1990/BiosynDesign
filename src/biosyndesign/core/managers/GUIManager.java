package biosyndesign.core.managers;


import biosyndesign.core.Main;
import biosyndesign.core.sbol.parts.Compound;
import biosyndesign.core.sbol.parts.Reaction;
import biosyndesign.core.ui.MainWindow;
import biosyndesign.core.utils.CompoundReaction;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLWriter;

import javax.swing.*;
import java.util.ArrayList;

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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Could not export SVG!");
        }
    }

    public void exportPathway() {
        String file = Main.projectIO.saveFile();
        try {
            SBOLDocument doc = new SBOLDocument();
            //s.compounds;
            //s.reactions;
            //s.enzymes;
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
            //s.enzymes;
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
            //s.enzymes;
            //s.target;
            //s.source;
            //s.organism;
            //s.proteins;

            SBOLWriter.write(doc, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showPathway(boolean selected) {
        Main.gm.showPathway = selected;
        ArrayList<CompoundReaction> path = new ArrayList<>();
        path.add(new CompoundReaction(s.source, null));
        findPath(s.source, new ArrayList<Reaction>(), s.reactions.size(), path);
        Main.gm.updateGraph();
    }

    private void findPath(Compound start, ArrayList<Reaction> exclude, int n, ArrayList<CompoundReaction> path) {
        if (n <= 0) {
            return;
        }
        n--;
        ArrayList<CompoundReaction> nextToInvestigate = new ArrayList<>();
        for (Reaction r : s.reactions) {
            if (exclude.contains(r)) {
                continue;
            }
            if (r.getReactants().contains(start)) {
                for(Compound p : r.getProducts()) {
                    nextToInvestigate.add(new CompoundReaction(p, r));
                }
                exclude.add(r);
            }
        }
        for (CompoundReaction c : nextToInvestigate) {
            if (c.c.equals(s.target)) {
                ArrayList<CompoundReaction> newPath = new ArrayList<>();
                newPath.addAll(path);
                newPath.add(c);
                Main.gm.finalPath = newPath;
                return;
            }
        }
        for (CompoundReaction c : nextToInvestigate) {
            ArrayList<CompoundReaction> newPath = new ArrayList<>();
            newPath.addAll(path);
            newPath.add(c);
            findPath(c.c, exclude, n, newPath);
        }
    }
}
