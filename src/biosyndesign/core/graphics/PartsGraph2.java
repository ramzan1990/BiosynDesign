package biosyndesign.core.graphics;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import javax.swing.*;

/**
 * Created by Umarov on 1/19/2017.
 */
public class PartsGraph2 extends JPanel{
    public mxGraph graph;
    public mxGraphComponent graphComponent;
    public PartsGraph2(){
        super();
        graph = new mxGraph() {
            // Overrides method to disallow edge label editing
            public boolean isCellEditable(Object cell) {
                return !getModel().isEdge(cell);
            }
        };
        Object parent = graph.getDefaultParent();

        graph.getModel().beginUpdate();
        try
        {
            //graph.insertEdge(parent, null, "Edge", v1, v2);
        }
        finally
        {
            graph.getModel().endUpdate();
        }

        graphComponent = new mxGraphComponent(graph);
        graph.setAllowDanglingEdges(false);
        this.add(graphComponent);
    }

}
