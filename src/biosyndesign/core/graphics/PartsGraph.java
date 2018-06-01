package biosyndesign.core.graphics;

import biosyndesign.core.Main;
import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.*;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.svggen.*;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.Hashtable;

/**
 * Created by Umarov on 1/19/2017.
 */
public class PartsGraph extends JPanel implements Serializable {
    public mxGraph graph;
    public mxGraphComponent graphComponent;

    public PartsGraph() {
        super();
        this.setLayout(new BorderLayout());
        graph = new mxGraph() {
//            public void drawState(mxICanvas canvas, mxCellState state, boolean drawLabel) {
//                super.drawState(canvas, state, drawLabel);
//                if (canvas instanceof mxGraphics2DCanvas) //  drag&drop ignored              return;
//                    if (model.isVertex(state.getCell())) {
//                        Graphics2D g = ((mxGraphics2DCanvas) canvas).getGraphics();
//                        Object userValue = model.getValue(state.getCell());
//                        String textToDisplay = "testing";
//                        if (textToDisplay != null) {
//                            //Font scaledFont = mxUtils.getFont(state.getStyle(), canvas.getScale());
//                            //g.setFont(scaledFont);
//                        }
//                        g.setFont(new Font("Verdana", Font.PLAIN, 14));
//                        FontMetrics fm = g.getFontMetrics();
//                        int w = SwingUtilities.computeStringWidth(fm, textToDisplay);
//                        int h = fm.getAscent();
//                        Color fontColor = mxUtils.getColor(state.getStyle(), mxConstants.STYLE_FONTCOLOR, Color.black);
//                        g.setColor(fontColor);
//                        g.drawString(textToDisplay, (int) state.getX() + (int) state.getWidth() + (int) canvas.getTranslate().getX() - w, (int) state.getY() + (int) state.getHeight() + (int) canvas.getTranslate().getY() + h);
//                    }
//            }
            public boolean isCellEditable(Object cell) {
                return !getModel().isEdge(cell);
            }
        };

        Object parent = graph.getDefaultParent();


        mxStylesheet stylesheet = graph.getStylesheet();
        Hashtable<String, Object> style = new Hashtable<String, Object>();
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        //style.put(mxConstants.STYLE_OPACITY, 50);
        style.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_ITALIC);
        style.put(mxConstants.STYLE_FONTCOLOR, Color.red);
        style.put(mxConstants.STYLE_ROUNDED, "1");
        style.put(mxConstants.STYLE_EDITABLE, "0");
        style.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_LEFT);
        style.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_BOTTOM);
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
        style2.put(mxConstants.STYLE_STROKEWIDTH, "1.5");
        style2.put(mxConstants.STYLE_STROKECOLOR, Color.BLACK);
        stylesheet.putCellStyle("REACTION", style2);

        Hashtable<String, Object> styler2 = new Hashtable<String, Object>();
        styler2.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        styler2.put(mxConstants.STYLE_DASHED, true);
        styler2.put(mxConstants.STYLE_FONTCOLOR, "#774400");
        styler2.put(mxConstants.STYLE_FILLCOLOR, "#ed9393");
        styler2.put(mxConstants.STYLE_ROUNDED, "1");
        styler2.put(mxConstants.STYLE_EDITABLE, "0");
        styler2.put(mxConstants.STYLE_STROKEWIDTH, "1.5");
        styler2.put(mxConstants.STYLE_STROKECOLOR, Color.BLACK);
        stylesheet.putCellStyle("REACTION_NAT", styler2);

        Hashtable<String, Object> style3 = new Hashtable<String, Object>();
        style3.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        //style.put(mxConstants.STYLE_OPACITY, 50);
        style3.put(mxConstants.STYLE_FONTCOLOR, "#774400");
        style3.put(mxConstants.STYLE_FILLCOLOR, "#ffa500");
        style3.put(mxConstants.STYLE_ROUNDED, "1");
        style3.put(mxConstants.STYLE_EDITABLE, "0");
        stylesheet.putCellStyle("ENZYME", style3);

        Hashtable<String, Object> style4 = new Hashtable<String, Object>();
        style4.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        style4.put(mxConstants.STYLE_FONTCOLOR, "#774400");
        style4.put(mxConstants.STYLE_DASHED, true);
        style4.put(mxConstants.STYLE_FILLCOLOR, "#ffa500");
        style4.put(mxConstants.STYLE_ROUNDED, "1");
        style4.put(mxConstants.STYLE_EDITABLE, "0");
        stylesheet.putCellStyle("ENZYME_NAT", style4);

        Hashtable<String, Object> edge = new Hashtable<String, Object>();
        //edge.put(mxConstants.STYLE_ROUNDED, true);
        //edge.put(mxConstants.STYLE_ORTHOGONAL, false);
        //edge.put(mxConstants.STYLE_EDGE, "elbowEdgeStyle");
        //edge.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
        edge.put(mxConstants.STYLE_ENDARROW, mxConstants.NONE);
        //edge.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);
        //edge.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER);
        //edge.put(mxConstants.STYLE_STROKECOLOR, "#000000"); // default is #6482B9
        //edge.put(mxConstants.STYLE_FONTCOLOR, "#446299");
        stylesheet.putCellStyle("ENZYME_EDGE", edge);

        Hashtable<String, Object> edge1 = new Hashtable<String, Object>();
        edge1.put(mxConstants.STYLE_FONTSIZE, 20);
        edge1.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_RIGHT);
        edge1.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_TOP);
        edge1.put(mxConstants.STYLE_ENDSIZE, 10);
        edge1.put(mxConstants.STYLE_STARTSIZE, 10);
        stylesheet.putCellStyle("EDGE", edge1);
        //Object v1 = graph.insertVertex(parent, null, "Hello",  20,  20, 80, 30);

        Hashtable<String, Object> edge2 = new Hashtable<String, Object>();
        edge2.put(mxConstants.STYLE_FONTSIZE, 12);
        edge2.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER);
        edge2.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_TOP);
        edge2.put(mxConstants.STYLE_ENDSIZE, 10);
        edge2.put(mxConstants.STYLE_STARTSIZE, 10);
        stylesheet.putCellStyle("EDGE_SMALL", edge2);

        graph.setAllowDanglingEdges(false);
        //graph.setCellsEditable(false);
        graphComponent = new mxGraphComponent(graph);
        graphComponent.getViewport().setOpaque(true);
        //graphComponent.getViewport().setBackground(new Color(172, 172, 172));
        graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    mxCell cell = (mxCell) graphComponent.getCellAt(e.getX(), e.getY());
                    if (cell != null) {
                        Main.pm.cellClicked(cell, e.getXOnScreen(), e.getYOnScreen());
                    }
                }
            }
        });
        graphComponent.getConnectionHandler().addListener(mxEvent.CONNECT, new mxEventSource.mxIEventListener() {
            public void invoke(Object sender, mxEventObject evt) {
                mxCell edge = (mxCell) evt.getProperty("cell");
                mxCell source = (mxCell) edge.getSource();
                mxCell target = (mxCell) edge.getTarget();
                Main.pm.edgeAdded(edge, source, target);
            }
        });
        graphComponent.setPanning(true);

        this.add(graphComponent, BorderLayout.CENTER);
        this.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
    }

    public void generateSVGGraphImage(String path) throws IOException {
        //Get the Graph component from the mxGraph to create an image out of this Graph
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        //Create an instance of org.w3c.dom.Document
        String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
        DOMImplementation domImpl = SVGDOMImplementation.getDOMImplementation();
        Document document = domImpl.createDocument(svgNS, "svg", null);

        // Create an instance of the SVG Generator
        SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(document);

        // Reuse our embedded base64-encoded image data.
        GenericImageHandler ihandler = new CachedImageHandlerBase64Encoder();
        ctx.setGenericImageHandler(ihandler);

        //Create SVG graphics2d Generator similar to the Graphich2d
        SVGGraphics2D svgGenerator = new SVGGraphics2D(ctx, false);

        //First draw Graph to the SVGGrapgics2D object using graphcomponent objects draw method
        graphComponent.getGraphControl().drawGraph(svgGenerator, true);

        //Once every thing is drawn on graphics find root element and update this by adding additional values for the required fields.
        Element root = svgGenerator.getRoot();
        root.setAttributeNS(null, "width", graphComponent.getGraphControl().getPreferredSize().width + "");
        root.setAttributeNS(null, "height", graphComponent.getGraphControl().getPreferredSize().height + "");
        root.setAttributeNS(null, "viewBox", "0 0 " + graphComponent.getGraphControl().getPreferredSize().width + " " + graphComponent.getGraphControl().getPreferredSize().height);

        // Print to the SVG Graphics2D object
        boolean useCSS = true; // we want to use CSS style attributes
        if (!path.toLowerCase().endsWith(".svg")) {
            path += ".svg";
        }
        Writer out = new FileWriter(new File(path));
        try {
            svgGenerator.stream(root, out, useCSS, false);
        } catch (SVGGraphics2DIOException e) {
            e.printStackTrace();
        } finally {
            out.close();
        }
    }
}
