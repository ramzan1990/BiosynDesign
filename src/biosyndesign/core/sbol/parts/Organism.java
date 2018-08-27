package biosyndesign.core.sbol.parts;

import java.io.Serializable;

public class Organism implements Serializable {
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
