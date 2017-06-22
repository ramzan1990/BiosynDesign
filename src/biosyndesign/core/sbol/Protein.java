package biosyndesign.core.sbol;

/**
 * Created by Umarov on 1/26/2017.
 */
public class Protein extends Part{

    public String ecNumber;
    public String sequence;

    public Protein(String id, String name, String url,String ecNumber) {
        super(id, name, url);
        this.ecNumber=ecNumber;
    }
}
