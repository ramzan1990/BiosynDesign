package biosyndesign.core.sbol;

/**
 * Created by Umarov on 5/12/2017.
 */
public class CompoundStoichiometry {
    public Compound c;
    public int s;

    public CompoundStoichiometry(Compound c, int s){
        this.c = c;
        this.s= s;
    }
}
