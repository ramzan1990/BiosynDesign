package biosyndesign.core.sbol;

import java.util.ArrayList;

/**
 * Created by Umarov on 1/26/2017.
 */
public class Reaction extends Part{

    public ArrayList<Compound> compounds;
    public ArrayList<Compound> reactants;
    public ArrayList<Compound> products;
    public ArrayList<ECNumber> ec;
    public int pickedEC;
    public Protein enzyme;
    public String partialEC;
    public double energy;
    public boolean nat;

    public Reaction(String id, String name, String url, double energy) {
        super(id, name, url);
        compounds = new ArrayList();
        ec = new ArrayList();
        reactants=new ArrayList();
        products=new ArrayList();
        partialEC="";
        this.energy=energy;
    }
}
