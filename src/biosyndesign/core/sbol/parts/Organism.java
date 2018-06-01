package biosyndesign.core.sbol.parts;

public class Organism {
    public String ID;
    public String name;
    public String URL;

    public Organism(String ID, String name, String URL){
        this.ID = ID;
        this.name = name;
        this.URL = URL;
    }

    public Organism(String name) {
        this.name = name;
    }


}
