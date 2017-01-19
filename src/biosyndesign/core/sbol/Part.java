package biosyndesign.core.sbol;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Umarov on 1/18/2017.
 */
public class Part implements Serializable {
    public String url;
    public String name;
    public String id;
    public ArrayList<Part> compounds;

    public Part(String id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
        compounds = new ArrayList();
    }
}
