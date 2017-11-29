package biosyndesign.core.sbol.parts;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Umarov on 1/26/2017.
 */
public class Reaction extends Part{

    public ArrayList<Compound> compounds;
    public ArrayList<Compound> reactants;
    public ArrayList<Compound> products;
    public ArrayList<ECNumber> ec;
    public HashMap<Compound, Integer> stoichiometry = new HashMap<Compound, Integer>();
    public int pickedEC;
    public Protein enzyme;
    public String partialEC;
    public double energy;
    public boolean nat;
    public String enzymeType;
    public boolean reverse;

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

    public ArrayList<Compound> getReactants() {
        if(reverse){
            return products;
        }else{
            return reactants;
        }
    }

    public ArrayList<Compound> getProducts() {
        if(reverse){
            return reactants;
        }else{
            return products;
        }
    }
}
