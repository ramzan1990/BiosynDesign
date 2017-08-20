package biosyndesign.core.sbol;

import java.io.Serializable;

/**
 * Created by Umarov on 5/12/2017.
 */
public class CompoundStoichiometry implements Serializable{
    public Compound c;
    public int s;

    public CompoundStoichiometry(Compound c, int s){
        this.c = c;
        this.s= s;
    }
}
