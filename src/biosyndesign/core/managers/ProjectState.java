package biosyndesign.core.managers;

import biosyndesign.core.sbol.parts.*;

import java.io.Serializable;
import java.util.ArrayList;
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
    public ArrayList<Protein> proteins;
    public Hashtable<Object, Part> graphNodes;
    public String organism;
    public String prefix;
    public Compound target;
    public Compound source;
    public int maxCompeting = 5;

    public ProjectState(){
        compounds = new ArrayList<>();
        reactions = new ArrayList<>();
        ecNumbers = new ArrayList<>();
        proteins = new ArrayList<>();
        graphNodes = new Hashtable<Object, Part>();
    }
}
