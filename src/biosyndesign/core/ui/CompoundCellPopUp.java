package biosyndesign.core.ui;

import biosyndesign.core.sbol.Compound;
import biosyndesign.core.sbol.Reaction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CompoundCellPopUp extends JPopupMenu {
    JMenuItem item1, item2;

    public CompoundCellPopUp(Compound cell){
        item1 = new JMenuItem("Set as Target");
        item1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Main.setTarget(cell);
            }
        });
        item2= new JMenuItem("Delete");
        item2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Main.delete(cell);
            }
        });
        add(item1);
        add(item2);
    }
}
