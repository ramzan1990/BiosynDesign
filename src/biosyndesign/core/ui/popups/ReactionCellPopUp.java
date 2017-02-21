package biosyndesign.core.ui.popups;

import biosyndesign.core.sbol.Reaction;
import biosyndesign.core.ui.Main;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ReactionCellPopUp extends JPopupMenu {
    JMenuItem item1, item2, item3;

    public ReactionCellPopUp(Reaction cell){
        item1 = new JMenuItem("Choose EC Number");
        item1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Main.pm.setEC(cell);
            }
        });
        item2 = new JMenuItem("Choose Enzyme");
        item2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Main.pm.chooseEnzyme(cell);
            }
        });
        item3= new JMenuItem("Delete");
        item3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Main.pm.delete(cell);
            }
        });
        add(item1);
        add(item2);
        add(item3);
    }
}
