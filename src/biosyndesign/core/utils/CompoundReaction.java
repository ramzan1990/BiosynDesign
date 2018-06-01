package biosyndesign.core.utils;

import biosyndesign.core.sbol.parts.Compound;
import biosyndesign.core.sbol.parts.Reaction;

public class CompoundReaction {
    public Compound c;
    public Reaction r;
    public CompoundReaction(Compound c, Reaction r){
        this.c =c ;
        this.r = r;

    }
}
