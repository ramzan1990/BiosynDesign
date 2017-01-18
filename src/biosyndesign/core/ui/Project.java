package biosyndesign.core.ui;

import biosyndesign.core.sbol.Part;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Umarov on 1/18/2017.
 */
public class Project implements Serializable {
    public String projectName;
    public String projectPath;
    public ArrayList<Part> parts;

    public Project(){
        parts = new ArrayList<>();
    }
}
