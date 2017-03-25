package biosyndesign.core.graphics;

import biosyndesign.core.ui.Main;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by Umarov on 1/19/2017.
 */
public class PartsGraph2 extends JPanel implements Serializable {
    public mxGraph graph;
    public mxGraphComponent graphComponent;

    public PartsGraph2() {
        super();
        this.setLayout(new BorderLayout());
        graph = new mxGraph() {
            // Overrides method to disallow edge label editing
            public boolean isCellEditable(Object cell) {
                return !getModel().isEdge(cell);
            }
        };
        Object parent = graph.getDefaultParent();




        mxStylesheet stylesheet = graph.getStylesheet();
        Hashtable<String, Object> style = new Hashtable<String, Object>();
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        //style.put(mxConstants.STYLE_OPACITY, 50);
        style.put(mxConstants.STYLE_FONTCOLOR, "#774400");
        style.put(mxConstants.STYLE_FILLCOLOR, "#addeed");
        style.put(mxConstants.STYLE_ROUNDED, "1");
        style.put(mxConstants.STYLE_EDITABLE, "0");
        stylesheet.putCellStyle("COMPOUND", style);

        Hashtable<String, Object> style1 = new Hashtable<String, Object>();
        style1.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        //style.put(mxConstants.STYLE_OPACITY, 50);
        style1.put(mxConstants.STYLE_FONTCOLOR, "#774400");
        style1.put(mxConstants.STYLE_FILLCOLOR, "#36d84e");
        style1.put(mxConstants.STYLE_ROUNDED, "1");
        style1.put(mxConstants.STYLE_EDITABLE, "0");
        stylesheet.putCellStyle("COMPOUND_TARGET", style1);

        Hashtable<String, Object> style2 = new Hashtable<String, Object>();
        style2.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        //style.put(mxConstants.STYLE_OPACITY, 50);
        style2.put(mxConstants.STYLE_FONTCOLOR, "#774400");
        style2.put(mxConstants.STYLE_FILLCOLOR, "#ed9393");
        style2.put(mxConstants.STYLE_ROUNDED, "1");
        style2.put(mxConstants.STYLE_EDITABLE, "0");
        stylesheet.putCellStyle("REACTION", style2);

        Hashtable<String, Object> style3 = new Hashtable<String, Object>();
        style3.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        //style.put(mxConstants.STYLE_OPACITY, 50);
        style3.put(mxConstants.STYLE_FONTCOLOR, "#774400");
        style3.put(mxConstants.STYLE_FILLCOLOR, "#ffa500");
        style3.put(mxConstants.STYLE_ROUNDED, "1");
        style3.put(mxConstants.STYLE_EDITABLE, "0");
        stylesheet.putCellStyle("ENZYME", style3);

        //Object v1 = graph.insertVertex(parent, null, "Hello",  20,  20, 80, 30);


        graph.setAllowDanglingEdges(false);
        //graph.setCellsEditable(false);
        graphComponent = new mxGraphComponent(graph);
        graphComponent.getViewport().setOpaque(true);
        //graphComponent.getViewport().setBackground(new Color(172, 172, 172));
        graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if(SwingUtilities.isRightMouseButton(e)){
                    mxCell cell = (mxCell) graphComponent.getCellAt(e.getX(), e.getY());
                    if (cell != null) {
                        Main.pm.cellClicked(cell, e.getX(), e.getY());
                    }
                }
            }
        });
        graphComponent.getConnectionHandler().addListener(mxEvent.CONNECT, new mxEventSource.mxIEventListener() {
            public void invoke(Object sender, mxEventObject evt) {
                mxCell edge = (mxCell)evt.getProperty("cell");
                mxCell source = (mxCell) edge.getSource();
                mxCell target = (mxCell) edge.getTarget();
                Main.pm.edgeAdded(edge, source, target);
            }
        });

        this.add(graphComponent, BorderLayout.CENTER);
        this.setBorder(BorderFactory.createEmptyBorder(1, 1, 1,1));
    }


}
