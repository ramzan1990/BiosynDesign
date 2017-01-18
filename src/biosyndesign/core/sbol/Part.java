package biosyndesign.core.sbol;

/**
 * Created by Umarov on 1/18/2017.
 */
public class Part {
    public String url;
    public String name;
    public String id;

    public Part(String id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }
}
