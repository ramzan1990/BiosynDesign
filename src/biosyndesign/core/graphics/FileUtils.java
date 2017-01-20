package biosyndesign.core.graphics;

/**
 * Created by Umarov on 1/20/2017.
 */


        import java.lang.reflect.Field;

        import javax.swing.JOptionPane;


        import org.w3c.dom.Document;

        import com.mxgraph.io.mxCodec;
        import com.mxgraph.io.mxCodecRegistry;
        import com.mxgraph.io.mxModelCodec;
        import com.mxgraph.io.mxObjectCodec;
        import com.mxgraph.swing.mxGraphComponent;
        import com.mxgraph.util.mxUtils;
        import com.mxgraph.util.mxXmlUtils;
        import com.mxgraph.view.mxGraph;

public class FileUtils {

    static boolean registered = false;


    public static void loadGraph( mxGraphComponent graphComponent, String fileName) {
        try {

            mxGraph graph = graphComponent.getGraph();

            // taken from EditorActions class
            Document document = mxXmlUtils.parseXml( mxUtils.readFile( fileName));
            mxCodec codec = new mxCodec( document);
            codec.decode( document.getDocumentElement(), graph.getModel());

        } catch( Exception ex) {
            throw new RuntimeException( ex);
        }
    }

    public static void saveGraph( mxGraphComponent graphComponent, String fileName) {

        try {

            mxGraph graph = graphComponent.getGraph();

            // taken from EditorActions class
            mxCodec codec = new mxCodec();
            String xml = mxXmlUtils.getXml(codec.encode(graph.getModel()));
            mxUtils.writeFile(xml, fileName);

            //JOptionPane.showMessageDialog( graphComponent, "File saved to: " + fileName);

        } catch( Exception ex) {
            throw new RuntimeException( ex);
        }

    }

}