package biosyndesign.core.sbol;

public interface SBOLInterface {

    public Part[] findParts(int type, int filter, String value);

    public ECNumber findECNumber(String ECNumber);

    public Protein[] getProteins(String ecNumber);

    public Reaction[] findCompetingReactions(String organism, String compound, int maxCompeting);

    public Reaction[] commonReactions(String id1, String id2);

    public boolean isNative(String reaction, String organism);
}
