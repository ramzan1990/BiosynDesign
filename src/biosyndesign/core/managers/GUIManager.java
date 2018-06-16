package biosyndesign.core.managers;


import biosyndesign.core.Main;
import biosyndesign.core.sbol.parts.Compound;
import biosyndesign.core.sbol.parts.Reaction;
import biosyndesign.core.ui.MainWindow;
import biosyndesign.core.utils.CompoundReaction;
import org.openscience.cdk.exception.CDKException;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLWriter;

import javax.swing.*;
import java.util.ArrayList;

public class GUIManager {
    private ProjectState s;
    private PartsManager pm;
    private MainWindow mainWindow;
    ArrayList<Path> paths;

    public GUIManager(ProjectState s, PartsManager pm, MainWindow mainWindow) {
        this.s = s;
        this.pm = pm;
        this.mainWindow = mainWindow;
        paths = new ArrayList<>();
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
        try {
            Main.gm.showPathway = selected;
            ArrayList<CompoundReaction> path = new ArrayList<>();
            path.add(new CompoundReaction(s.source, null));
            findPath(s.source, new ArrayList<Reaction>(), s.reactions.size(), path, 0);
            double bestCost = Double.MAX_VALUE;
            ArrayList<CompoundReaction> bestPath = null;
            for(Path p: paths){
                if(p.cost<bestCost){
                    bestCost = p.cost;
                    bestPath = p.path;
                }
            }
            Main.gm.finalPath = bestPath;
            Main.gm.updateGraph();
        }catch (Exception e){

        }
    }

    private void findPath(Compound start, ArrayList<Reaction> exclude, int n, ArrayList<CompoundReaction> path, double pathCost) throws CDKException {
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
                double newCost = pathCost + 1.0/Main.pm.getSim(Main.pm.defaultFingerprinter, path.get(path.size() - 1).c, c.c,  c.r);
                paths.add(new Path(newPath, newCost));
                return;
            }
        }
        for (CompoundReaction c : nextToInvestigate) {
            ArrayList<CompoundReaction> newPath = new ArrayList<>();
            newPath.addAll(path);
            newPath.add(c);
            double newCost = pathCost + 1.0/Main.pm.getSim(Main.pm.defaultFingerprinter, path.get(path.size() - 1).c, c.c,  c.r);
            findPath(c.c, exclude, n, newPath, newCost);
        }
    }
}
class Path{
    public ArrayList<CompoundReaction> path;
    public double cost;

    public Path(ArrayList<CompoundReaction> path, double cost){
        this.path = path;
        this.cost = cost;
    }
}