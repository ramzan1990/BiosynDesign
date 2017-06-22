package biosyndesign.core.sbol;

public interface SBOLInterface {

    public Part[] findParts(int type, int data1, String data2);

    public ECNumber findECNumber(String ECNumber);

    public Protein[] getProteins(String ecNumber, String organism);

    public Reaction[] findCompetingReactions(String organism, String compound, int maxCompeting);

    public Reaction[] commonReactions(String id, String id1);
}
