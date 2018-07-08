package biosyndesign.core.sbol.parts;

/**
 * Created by Umarov on 1/25/2017.
 */
public class Enzyme extends Part{
    public String classID;
    public String classScheme = "ec";


    public Enzyme(String id, String name, String url, String enzymeClassID) {
        super(id, name, url);
        this.classID = enzymeClassID;
    }

    public Enzyme(String id, String name, String url, String classScheme, String classID) {
        super(id, name, url);
        this.classID = classID;
        this.classScheme = classScheme;
    }

    public Enzyme(String enzymeClassID) {
        super("", "", "");
        this.classID = enzymeClassID;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Enzyme) {
            Enzyme otherEnzyme = (Enzyme) obj;
            return otherEnzyme.classID.equals(this.classID);
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
