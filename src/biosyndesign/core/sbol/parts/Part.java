package biosyndesign.core.sbol.parts;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Umarov on 1/18/2017.
 */
public class Part implements Serializable {
    public String url;
    public String name;
    public String id;
    public boolean local;
    public ArrayList<String> info;
    public ArrayList<Annotation> annotations;

    public Part(String id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
        info = new ArrayList<>();
        annotations = new ArrayList<>();
    }

    public Part(String id) {
        this.id = id;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Part) {
            Part otherPart = (Part) obj;
            return otherPart.id.equals(this.id);
        } else {
            return false;
        }
    }

    public Part setLocal(boolean b) {
        this.local = b;
        return this;
    }
}
