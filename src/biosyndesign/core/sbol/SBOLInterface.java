package biosyndesign.core.sbol;

import biosyndesign.core.sbol.parts.Part;
import biosyndesign.core.sbol.parts.Protein;
import biosyndesign.core.sbol.parts.Reaction;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public interface SBOLInterface {

    Part[] findParts(int type, int filter, String value, int page);

    Protein[] getProteins(String enzyme);

    Protein[] getProteins(String enzyme, String organism);

    Reaction[] findCompetingReactions(String organism, String compound, int maxCompeting);

    Reaction[] commonReactions(String id1, String id2);

    boolean isNative(String reaction, String organism);

    String[] getOrganisms(String enzyme);

    //String getCDNA(String sequence, String organism);

    ArrayList<String> getZipAndReturnProteins(String reaction, String organism, String enzyme, String output);

    JsonObject getQueryInfo();
}
