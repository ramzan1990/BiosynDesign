package biosyndesign.core.graphics;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Umarov on 1/19/2017.
 */
public class PartsGraph2 extends JPanel implements Serializable {
    public mxGraph graph;
    public mxGraphComponent graphComponent;

    public PartsGraph2(){
        super();
        this.setLayout(new BorderLayout());
        graph = new mxGraph() {
            // Overrides method to disallow edge label editing
            public boolean isCellEditable(Object cell) {
                return !getModel().isEdge(cell);
            }
        };
        Object parent = graph.getDefaultParent();

        mxConstants.STYLE_ENDARROW = "none";


        //Object v1 = graph.insertVertex(parent, null, "Hello",  20,  20, 80, 30);



        graphComponent = new mxGraphComponent(graph);
        graph.setAllowDanglingEdges(false);

        this.add(graphComponent, BorderLayout.CENTER);
    }



}
