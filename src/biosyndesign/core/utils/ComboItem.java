package biosyndesign.core.utils;

import biosyndesign.core.sbol.parts.Compound;

public class ComboItem {
    private Compound value;
    private String label;

    public ComboItem(Compound value, String label) {
        this.value = value;
        this.label = label;
    }

    public Compound getValue() {
        return this.value;
    }

    public String getLabel() {
        return this.label;
    }

    @Override
    public String toString() {
        return label;
    }
}