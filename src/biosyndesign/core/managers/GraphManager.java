package biosyndesign.core.managers;

import biosyndesign.core.Main;
import biosyndesign.core.sbol.parts.Compound;
import biosyndesign.core.sbol.parts.Part;
import biosyndesign.core.sbol.parts.Reaction;
import biosyndesign.core.ui.MainWindow;
import biosyndesign.core.utils.Common;
import biosyndesign.core.utils.CompoundReaction;
import biosyndesign.core.utils.Mover;
import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.canvas.mxGraphicsCanvas2D;
import com.mxgraph.shape.mxStencil;
import com.mxgraph.shape.mxStencilRegistry;
import com.mxgraph.util.mxImage;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.view.mxGraph;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Umarov on 2/21/2017.
 */
public class GraphManager {
    private ProjectState s;
    private MainWindow mainWindow;
    public int scale = 2;
    public boolean showPathway;
    public ArrayList<CompoundReaction> finalPath = new ArrayList<>();

    public GraphManager(ProjectState s, MainWindow mainWindow) {
        this.s = s;
        this.mainWindow = mainWindow;
    }

    public void updateGraph() {
        s.graphNodes = new Hashtable<>();
        ArrayList<Object> toRemove = new ArrayList<>();
        Collections.sort(s.reactions, new Comparator<Part>() {
            public int compare(Part left, Part right) {
                return left.id.compareTo(right.id);
            }
        });
        Collections.sort(s.compounds, new Comparator<Part>() {
            public int compare(Part left, Part right) {
                return left.id.compareTo(right.id);
            }
        });
        int cc = 0;
        int[] nb = new int[s.reactions.size()];
        for (int i = 0; i < nb.length; i++) {
            Reaction r1 = s.reactions.get(i);
            int sum = 0;
            for (int j = 0; j < nb.length; j++) {
                Reaction r2 = s.reactions.get(j);
                if (r1 == r2) {
                    continue;
                }
                ArrayList<Part> common = new ArrayList<>(r1.compounds);
                common.retainAll(r2.compounds);
                sum += common.size();
            }
            nb[i] = sum;
        }

        Collections.sort(s.reactions, new Comparator<Part>() {
            public int compare(Part left, Part right) {
                int i1 = nb[s.reactions.indexOf(left)];
                int i2 = nb[s.reactions.indexOf(right)];
                if (i1 > i2) {
                    return 1;
                } else if (i1 < i2) {
                    return -1;
                } else {
                    return left.id.compareTo(right.id);
                }
            }
        });
        Collections.reverse(s.reactions);
        mxGraph graph = mainWindow.workSpacePanel.graph;
        graph.getModel().beginUpdate();
        graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));
        Object parent = graph.getDefaultParent();
        //mainWindow.workSpacePanel.graphComponent.getViewport().setOpaque(true);
        //mainWindow.workSpacePanel.graphComponent.setBackgroundImage(new ImageIcon("C:\\Users\\Jumee\\Desktop\\e.jpg"));

        try {
            File sFile = new File(s.projectPath + s.projectName + File.separator + "stencils.xml");
            if (sFile.exists()) {
                Document doc = mxXmlUtils.parseXml(mxUtils.readFile(s.projectPath + s.projectName + File.separator + "stencils.xml"));
                Element shapes = (Element) doc.getDocumentElement();
                NodeList list = shapes.getElementsByTagName("shape");
                for (int ii = 0; ii < list.getLength(); ii++)
                {
                    Element shape = (Element) list.item(ii);
                    mxStencilRegistry.addStencil(shape.getAttribute("name"),
                            new mxStencil(shape)
                            {
                                protected mxGraphicsCanvas2D createCanvas(
                                        final mxGraphics2DCanvas gc)
                                {
                                    // Redirects image loading to graphics canvas
                                    return new mxGraphicsCanvas2D(gc.getGraphics())
                                    {
                                        protected Image loadImage(String src)
                                        {
                                            // Adds image base path to relative image URLs
                                            if (!src.startsWith("/")
                                                    && !src.startsWith("http://")
                                                    && !src.startsWith("https://")
                                                    && !src.startsWith("file:"))
                                            {
                                                src = gc.getImageBasePath() + src;
                                            }

                                            // Call is cached
                                            return gc.loadImage(src);
                                        }
                                    };
                                }
                            });
                }
            }
            ArrayList<String> usedParts = new ArrayList<>();
            ArrayList<Object> objects = new ArrayList<>();
            if(showPathway){
                int i = 1;
                Object prev = null;
                for(CompoundReaction cr: finalPath) {
                    Compound c = cr.c;
                    int rx = 200;
                    int ry = 200*i++;
                    File f = new File(s.projectPath + s.projectName + File.separator + "ci" + File.separator + c.id + ".svg");
                    Object v2;
                    if (f.exists()) {
                        v2 = graph.insertVertex(parent, null, "", rx, ry, 50 * scale, 25 * scale, "COMPOUND;fontSize=" + (5 * scale) + ";shape=" + cr.c.id);
                    } else {
                        v2 = graph.insertVertex(parent, null, Common.restrict(c.name, 20), rx, ry, 50 * scale, 25 * scale, "COMPOUND");
                    }
                    //s.graphNodes.put(v2, c);
                    if(prev != null) {
                        graph.insertEdge(parent, null, cr.r.name, prev, v2, "EDGE_SMALL");
                    }
                    prev = v2;
                }
            }else {
                Mover m = new Mover(180 * scale);
                int off = 100 + 80 * scale + m.max(s.reactions.size()) * (120 * scale);
                String compoundStyle;
                String reactionStyle;
                String enzymeStyle;
                for (int i = 0; i < s.reactions.size(); i++) {
                    int rx = m.x() + off;
                    int ry = m.y() + off;
                    String rt;
                    Reaction r = s.reactions.get(i);
                    if (r.ec.size() == 0) {
                        rt = r.partialEC;
                    } else {
                        rt = r.ec.get(r.pickedEC).ecNumber + " [" + r.ec.size() + "]";
                    }
                    if (r.enzyme != null) {
                        rt += "\n" + Common.restrict(r.enzyme.name, 14);
                    }
                    if (r.nat) {
                        reactionStyle = "REACTION_NAT";
                    } else {
                        reactionStyle = "REACTION";
                    }
                    Object v1 = graph.insertVertex(parent, null, rt, rx, ry, 50 * scale, 25 * scale, reactionStyle);
                    s.graphNodes.put(v1, s.reactions.get(i));
                    Mover ms = new Mover(63 * scale);

//                if (r.enzyme != null) {
//                    if (usedParts.contains(r.enzyme.id)) {
//                        graph.insertEdge(parent, null, "", v1, objects.get(usedParts.indexOf(r.enzyme.id)), "ENZYME_EDGE");
//                    } else {
//                        if (r.enzyme.nat) {
//                            enzymeStyle = "ENZYME_NAT";
//                        } else {
//                            enzymeStyle = "ENZYME";
//                        }
//                        cc++;
//                        ms.move();
//                        Object v2 = graph.insertVertex(parent, null, Common.restrict(r.enzyme.name.split(",")[0], 12), rx + ms.x(), ry + ms.y(), 80, 30, enzymeStyle);
//                        s.graphNodes.put(v2, r.enzyme);
//                        //, "shape=image;image=file:/c:/images/ME_C00022.png"
//                        graph.insertEdge(parent, null, "", v1, v2, "ENZYME_EDGE");
//                        usedParts.add(r.enzyme.id);
//                        objects.add(v2);
//                    }
//                }

                    for (int j = 0; j < s.reactions.get(i).compounds.size(); j++) {
                        Part c = s.reactions.get(i).compounds.get(j);
                        String stoichiometry = "";
                        compoundStyle = "";
                        if (c == s.target) {
                            compoundStyle = "TARGET";
                        } else if (c == s.source) {
                            compoundStyle = "SOURCE";
                        }
                        boolean product = r.reverse;
                        for (Compound cs : r.products) {
                            if (cs.id.equals(c.id)) {
                                product = !r.reverse;
                                stoichiometry = r.stoichiometry.get(cs) + "";
                            }
                        }
                        for (Compound cs : r.reactants) {
                            if (cs.id.equals(c.id)) {
                                stoichiometry = r.stoichiometry.get(cs) + "";
                            }
                        }
                        if (usedParts.contains(c.id)) {
                            if (product) {
                                graph.insertEdge(parent, null, stoichiometry, v1, objects.get(usedParts.indexOf(c.id)), "EDGE");
                            } else {
                                graph.insertEdge(parent, null, stoichiometry, objects.get(usedParts.indexOf(c.id)), v1, "EDGE");
                            }
                            toRemove.remove(objects.get(usedParts.indexOf(c.id)));
                        } else {
                            cc++;
                            ms.move();
                            File f = new File(s.projectPath + s.projectName + File.separator + "ci" + File.separator + c.id + ".svg");
                            Object v2;
                            if (f.exists()) {
                                v2 = graph.insertVertex(parent, null, compoundStyle, rx + ms.x(), ry + ms.y(), 50 * scale, 25 * scale, "COMPOUND;fontSize=" + (11 * scale) + ";shape=" + c.id);
                            } else {
                                v2 = graph.insertVertex(parent, null, Common.restrict(c.name, 12) + "\n" + compoundStyle, rx + ms.x(), ry + ms.y(), 50 * scale, 25 * scale, "COMPOUND");
                            }
                            s.graphNodes.put(v2, c);
                            if (product) {
                                graph.insertEdge(parent, null, stoichiometry, v1, v2, "EDGE");
                            } else {
                                graph.insertEdge(parent, null, stoichiometry, v2, v1, "EDGE");
                            }
                            usedParts.add(c.id);
                            objects.add(v2);
                            toRemove.add(v2);
                        }

                    }
                    m.move();
                }
                if (off == 0) {
                    off = 43 * scale;
                }
                for (int i = 0; i < s.compounds.size(); i++) {
                    int rx = m.x() + off;
                    int ry = m.y() + off;
                    Compound c = s.compounds.get(i);
                    compoundStyle ="";
                    if (c == s.target) {
                        compoundStyle = "TARGET";
                    } else if (c == s.source) {
                        compoundStyle = "SOURCE";
                    }
                    if (!usedParts.contains(c.id)) {
                        m.move();
                        //String im = Paths.get(s.projectPath + s.projectName + File.separator + "ci" + File.separator + c.id + ".png").toUri().toURL().toString();
                        //String im = Paths.get("C:\\Users\\Jumee\\Desktop\\1234.xml").toUri().toURL().toString();

                        File f = new File(s.projectPath + s.projectName + File.separator + "ci" + File.separator + c.id + ".svg");
                        Object v2;
                        if (f.exists()) {
                            v2 = graph.insertVertex(parent, null, compoundStyle, rx, ry, 50 * scale, 25 * scale, "COMPOUND;fontSize=" + (4 * scale) + ";shape=" +c.id);
                        } else {
                            v2 = graph.insertVertex(parent, null, Common.restrict(c.name, 12) + "\n" + compoundStyle, rx, ry, 50 * scale, 25 * scale, "COMPOUND");
                        }
                        s.graphNodes.put(v2, c);
                        usedParts.add(c.id);
                        objects.add(v2);
                    }
                }
//                if (showPathway) {
//                    try {
//                        if (usedParts.contains(s.target.id)) {
//                            toRemove.showPathway(objects.get(usedParts.indexOf(s.target.id)));
//                        }
//                        if (usedParts.contains(s.source.id)) {
//                            toRemove.showPathway(objects.get(usedParts.indexOf(s.source.id)));
//                        }
//                    } catch (Exception e) {
//                    }
//                    graph.removeCells(toRemove.toArray());
//                    for (Object o : toRemove) {
//                        s.graphNodes.showPathway(o);
//                    }
//                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            graph.refresh();
            graph.getModel().endUpdate();
            graph.refresh();
            mainWindow.setInfoLabel(cc, s.reactions.size());
        }
    }




    public Object[] getSelected() {
        mxGraph graph = mainWindow.workSpacePanel.graph;
        return graph.getSelectionCells();
    }


    public void zoom(boolean b) {
        if(b && scale<4){
            scale++;
        }else if(!b && scale>1){
            scale--;
        }
        Main.pm.updateStencils();
        updateGraph();
    }
}
