package biosyndesign.core.sbol.parts;

public class Enzyme extends Part {
    public String classScheme;
    public String classID;

    public Enzyme(String classScheme, String classID, String name, String url) {
        super(getID(classScheme, classID), name, url);
        this.classScheme = classScheme;
        this.classID = classID;
    }

    private static String getID(String classScheme, String classID) {
        return classScheme + "_" + classID.replaceAll(".", "_");
    }

    public String toString() {
        return classScheme + ":" + classID;
    }
}
