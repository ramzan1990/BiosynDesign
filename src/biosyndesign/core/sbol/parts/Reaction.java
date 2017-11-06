package biosyndesign.core.sbol.parts;

import java.util.ArrayList;

/**
 * Created by Umarov on 1/26/2017.
 */
public class Reaction extends Part{

    public ArrayList<Compound> compounds;
    public ArrayList<CompoundStoichiometry> reactants;
    public ArrayList<CompoundStoichiometry> products;
    public ArrayList<ECNumber> ec;
    public int pickedEC;
    public Protein enzyme;
    public String partialEC;
    public double energy;
    public boolean nat;
    public String enzymeType;

    public Reaction(String id, String name, String url, double energy) {
        super(id, name, url);
        compounds = new ArrayList();
        ec = new ArrayList();
        reactants=new ArrayList();
        products=new ArrayList();
        partialEC="";
        this.energy=energy;
    }

    public String getEName() {
        if(pickedEC >=0 && ec.size()>pickedEC){
            return "EC:"+ec.get(pickedEC).ecNumber;
        }
        return "EC:Partial";
    }
}
