package biosyndesign.core.ui;

import biosyndesign.core.sbol.Compound;
import biosyndesign.core.sbol.ECNumber;
import biosyndesign.core.sbol.Part;
import biosyndesign.core.sbol.Reaction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by Umarov on 1/18/2017.
 */
public class ProjectState implements Serializable {
    public String projectName;
    public String projectPath;
    public ArrayList<Compound> compounds;
    public ArrayList<Reaction> reactions;
    public ArrayList<ECNumber> ecNumbers;
    public Hashtable<Object, Part> graphNodes;

    public ProjectState(){
        compounds = new ArrayList<>();
        reactions = new ArrayList<>();
        ecNumbers = new ArrayList<>();
        graphNodes = new Hashtable<Object, Part>();
    }
}
