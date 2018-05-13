package biosyndesign.core.sbol.parts;

import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * Created by Umarov on 1/26/2017.
 */
public class Compound extends Part {

    public String smiles;

    //private String id;
    public Compound(String id, String name, String url) {
        super(id, name, url);
    }

    public boolean equals(Object obj) {
        if (obj instanceof Compound) {
            Compound otherCompound = (Compound) obj;
            if (this.smiles != null && otherCompound.smiles != null) {
                try {
                    IChemObjectBuilder bldr
                            = SilentChemObjectBuilder.getInstance();
                    SmilesParser smipar = new SmilesParser(bldr);
                    SmilesGenerator sg = new SmilesGenerator();
                    String smiles1 = sg.create(smipar.parseSmiles(this.smiles));
                    String smiles2 = sg.create(smipar.parseSmiles(otherCompound.smiles));
                    return smiles1.equals(smiles2);
                } catch (Exception e) {

                }
            }
            return otherCompound.id.equals(this.id);
        } else {
            return false;
        }
    }

    public String toString(){
        return name;
    }

}
