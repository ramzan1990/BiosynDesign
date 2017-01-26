package biosyndesign.core.sbol;

import java.util.ArrayList;

/**
 * Created by Umarov on 1/26/2017.
 */
public class Reaction extends Part{

    public ArrayList<Part> compounds;
    public ArrayList<ECNumber> ec;

    public Reaction(String id, String name, String url) {
        super(id, name, url);
        compounds = new ArrayList();
        ec = new ArrayList();
    }
}
