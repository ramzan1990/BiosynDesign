package biosyndesign.core.sbol.parts;

/**
 * Created by Umarov on 1/25/2017.
 */
public class ECNumber extends Part{
    public String ecNumber;


    public ECNumber(String id, String name, String url, String ecNumber) {
        super(id, name, url);
        this.ecNumber = ecNumber;
    }

    public ECNumber(String ecNumber) {
        super("", "", "");
        this.ecNumber = ecNumber;
    }

    public boolean equals(Object obj) {
        if (obj instanceof ECNumber) {
            ECNumber otherECNumber = (ECNumber) obj;
            return otherECNumber.ecNumber.equals(this.ecNumber);
        } else {
            return false;
        }
    }
}
