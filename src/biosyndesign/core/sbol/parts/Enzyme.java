package biosyndesign.core.sbol.parts;

/**
 * Created by Umarov on 1/25/2017.
 */
public class Enzyme extends Part{
    public String ecNumber;
    public String classScheme = "ec";
    public String classID;


    public Enzyme(String id, String name, String url, String ecNumber) {
        super(id, name, url);
        this.ecNumber = ecNumber;
    }

    public Enzyme(String id, String name, String url, String classScheme, String classID) {
        super(id, name, url);
        this.ecNumber = classID;
        this.classScheme = classScheme;
    }

    public Enzyme(String ecNumber) {
        super("", "", "");
        this.ecNumber = ecNumber;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Enzyme) {
            Enzyme otherEnzyme = (Enzyme) obj;
            return otherEnzyme.ecNumber.equals(this.ecNumber);
        } else {
            return false;
        }
    }

    public static String getID(String classScheme, String classID) {
        return classScheme + "_" + classID.replaceAll(".", "_");
    }

    public String toString() {
        return classScheme + ":" + classID;
    }
}
