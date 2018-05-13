package biosyndesign.core.sbol.parts;

public class Annotation {
    public String annotationPrefixURI;
    public String annotationPrefix;
    public String annotationKey;
    public String annotationValue;

    public Annotation(String annotationPrefixURI, String annotationPrefix, String annotationKey, String annotationValue) {
        this.annotationPrefixURI = annotationPrefixURI;
        this.annotationPrefix = annotationPrefix;
        this.annotationKey = annotationKey;
        this.annotationValue = annotationValue;
    }

    public String toString() {
        return annotationKey + ":" + annotationValue;
    }
}
