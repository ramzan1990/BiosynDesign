package biosyndesign.core.managers;

import biosyndesign.core.sbol.Compound;
import biosyndesign.core.sbol.Part;
import biosyndesign.core.sbol.Reaction;
import biosyndesign.core.ui.MainWindow;
import biosyndesign.core.utils.Common;
import biosyndesign.core.utils.Mover;
import com.mxgraph.view.mxGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

/**
 * Created by Umarov on 2/21/2017.
 */
public class GraphManager {
    private ProjectState s;
    private MainWindow mainWindow;

    public GraphManager(ProjectState s, MainWindow mainWindow) {
        this.s = s;
        this.mainWindow = mainWindow;
    }

    public void updateGraph() {
        s.graphNodes = new Hashtable<>();
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
        try {
            ArrayList<String> usedParts = new ArrayList<>();
            ArrayList<Object> objects = new ArrayList<>();
            Mover m = new Mover(300);
            int off = 200 + m.max(s.reactions.size()) * 170;
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
                if (r.nat) {
                    reactionStyle = "REACTION_NAT";
                } else {
                    reactionStyle = "REACTION";
                }
                Object v1 = graph.insertVertex(parent, null, rt, rx, ry, 80, 30, reactionStyle);
                s.graphNodes.put(v1, s.reactions.get(i));
                Mover ms = new Mover(90);

                if (r.enzyme != null) {
                    if (usedParts.contains(r.enzyme.id)) {
                        graph.insertEdge(parent, null, "", v1, objects.get(usedParts.indexOf(r.enzyme.id)), "ENZYME_EDGE");
                    } else {
                        if (r.enzyme.nat) {
                            enzymeStyle = "ENZYME_NAT";
                        } else {
                            enzymeStyle = "ENZYME";
                        }
                        cc++;
                        ms.move();
                        Object v2 = graph.insertVertex(parent, null, Common.restrict(r.enzyme.name.split(",")[0], 12), rx + ms.x(), ry + ms.y(), 80, 30, enzymeStyle);
                        s.graphNodes.put(v2, r.enzyme);
                        //, "shape=image;image=file:/c:/images/ME_C00022.png"
                        graph.insertEdge(parent, null, "", v1, v2, "ENZYME_EDGE");
                        usedParts.add(r.enzyme.id);
                        objects.add(v2);
                    }
                }

                for (int j = 0; j < s.reactions.get(i).compounds.size(); j++) {
                    Part c = s.reactions.get(i).compounds.get(j);
                    if (c == s.target) {
                        compoundStyle = "COMPOUND_TARGET";
                    } else {
                        compoundStyle = "COMPOUND";
                    }
                    boolean product = false;
                    if (r.products.contains(c)) {
                        product = true;
                    }
                    if (usedParts.contains(c.id)) {
                        if (product) {
                            graph.insertEdge(parent, null, "", v1, objects.get(usedParts.indexOf(c.id)));
                        } else {
                            graph.insertEdge(parent, null, "", objects.get(usedParts.indexOf(c.id)), v1);
                        }
                    } else {
                        cc++;
                        ms.move();
                        Object v2 = graph.insertVertex(parent, null, Common.restrict(c.name, 12), rx + ms.x(), ry + ms.y(), 80, 30, compoundStyle);
                        s.graphNodes.put(v2, c);
                        //, "shape=image;image=file:/c:/images/ME_C00022.png"
                        if (product) {
                            graph.insertEdge(parent, null, "", v1, v2);
                        } else {
                            graph.insertEdge(parent, null, "", v2, v1);
                        }
                        usedParts.add(c.id);
                        objects.add(v2);
                    }

                }
                m.move();
            }
            if (off == 0) {
                off = 170;
            }
            for (int i = 0; i < s.compounds.size(); i++) {
                int rx = m.x() + off;
                int ry = m.y() + off;
                Compound c = s.compounds.get(i);
                if (c == s.target) {
                    compoundStyle = "COMPOUND_TARGET";
                } else {
                    compoundStyle = "COMPOUND";
                }
                if (!usedParts.contains(c.id)) {
                    m.move();
                    Object v2 = graph.insertVertex(parent, null, Common.restrict(c.name.split(",")[0], 12), rx, ry, 80, 30, compoundStyle);
                    s.graphNodes.put(v2, c);
                    usedParts.add(c.id);
                    objects.add(v2);
                }
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


}
