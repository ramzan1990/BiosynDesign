package biosyndesign.core.ui.popups;

import biosyndesign.core.Main;
import biosyndesign.core.sbol.parts.Compound;
import biosyndesign.core.sbol.parts.Part;
import biosyndesign.core.sbol.parts.Reaction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CompoundsCellPopUp extends JPopupMenu {
    JMenuItem item1, item2, item3;

    public CompoundsCellPopUp(Part[] parts){
        item1= new JMenuItem("Structural Similarity");
        item1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Compound c1 = (Compound) parts[0];
                Compound c2 = (Compound) parts[1];
                Reaction r = new Reaction("", "", "", 0);
                r.products.add(c1);
                r.reactants.add(c2);
                r.stoichiometry.put(c1, 1);
                r.stoichiometry.put(c2, 1);
                Main.pm.structSimilarity(r);
            }
        });
        item2= new JMenuItem("Find Common Reactions");
        item2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Main.pm.commonReaction(parts[0], parts[1]);
            }
        });
        item3= new JMenuItem("Delete");
        item3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Main.pm.delete(parts);
            }
        });
        add(item1);
        add(item2);
        add(item3);
    }
}
