package biosyndesign.core.ui.popups;

import biosyndesign.core.sbol.parts.Compound;
import biosyndesign.core.Main;
import biosyndesign.core.sbol.parts.Part;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CompoundCellPopUp extends JPopupMenu {
    JMenuItem item0, item1, item2, item3, item4;

    public CompoundCellPopUp(Compound cell){
        item0 = new JMenuItem("Show info");
        item0.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Main.pm.showInfo(cell);
            }
        });
        item1 = new JMenuItem("Set as Source");
        item1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Main.pm.setSource(cell);
            }
        });
        item4 = new JMenuItem("Set as Target");
        item4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Main.pm.setTarget(cell);
            }
        });
        item2= new JMenuItem("Delete");
        item2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Main.pm.delete(new Part[]{cell});
            }
        });
        item3= new JMenuItem("Find Reactions");
        item3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                    Main.pm.findReactions(cell);
            }
        });
        add(item0);
        add(item1);
        add(item4);
        add(item3);
        add(item2);
    }
}
