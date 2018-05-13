package biosyndesign.core.sbol.parts;

public class ReactionCompound {
    public Compound c;
    public int s;

    public ReactionCompound(Compound c, int s){
        this.c = c;
        this.s = s;
    }

    public String toString(){
        return c.toString();
    }
}
