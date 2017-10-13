package biosyndesign.core.ui.popups;

import biosyndesign.core.sbol.parts.Reaction;
import biosyndesign.core.Main;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ReactionCellPopUp extends JPopupMenu {
    JMenuItem item0, item1, item2, item3, item4;

    public ReactionCellPopUp(Reaction cell){
        item0 = new JMenuItem("Show info");
        item0.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Main.pm.showInfo(cell);
            }
        });
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
        item3= new JMenuItem("Structural Similarity");
        item3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Main.pm.structSimilarity(cell);
            }
        });
        item4= new JMenuItem("Delete");
        item4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Main.pm.delete(new Object[]{cell});
            }
        });
        add(item0);
        add(item1);
        //add(item2);
        add(item3);
        add(item4);
    }
}
