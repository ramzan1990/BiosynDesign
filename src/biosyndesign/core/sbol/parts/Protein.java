package biosyndesign.core.sbol.parts;

/**
 * Created by Umarov on 1/26/2017.
 */
public class Protein extends Part {

    public String enzymeID;
    public String enzymeClassScheme = "ec";
    public String sequence;
    public String CDS;
    public Organism organism;
    public boolean nat;

    public Protein(String id, String organism, String url, String enzymeID) {
        super(id, "", url);
        this.organism = new Organism(organism);
        this.enzymeID = enzymeID;
    }
}
