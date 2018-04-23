package biosyndesign.core.sbol;

import biosyndesign.core.sbol.parts.Part;
import biosyndesign.core.sbol.parts.Protein;
import biosyndesign.core.sbol.parts.Reaction;

import java.io.File;
import java.util.zip.ZipInputStream;

public interface SBOLInterface {

    public Part[] findParts(int type, int filter, String value);

    public Protein[] getProteins(String ecNumber);

    public Protein[] getProteins(String ecNumber, String organism);

    public Reaction[] findCompetingReactions(String organism, String compound, int maxCompeting);

    public Reaction[] commonReactions(String id1, String id2);

    public boolean isNative(String reaction, String organism);

    String[] getOrganisms(String ecNumber);

    String getCDNA(String sequence, String organism);

    void getZip(String reaction, String organism, String ecNumber, String output);
}
