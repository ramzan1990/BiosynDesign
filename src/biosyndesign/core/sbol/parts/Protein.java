package biosyndesign.core.sbol.parts;

/**
 * Created by Umarov on 1/26/2017.
 */
public class Protein extends Part{

    public String ecNumber;
    public String sequence;
    public String organism;
    public String cDNA;
    public boolean nat;

    public Protein(String id, String organism, String url,String ecNumber) {
        super(id, "", url);
        this.organism=organism;
        this.ecNumber=ecNumber;
    }
}
