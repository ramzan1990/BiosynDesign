package biosyndesign.core.ui.popups;

import biosyndesign.core.Main;
import biosyndesign.core.sbol.parts.Protein;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EnzymeCellPopUp extends JPopupMenu {
    JMenuItem item0, item1;

    public EnzymeCellPopUp(Protein cell){
        item0 = new JMenuItem("Show info");
        item0.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Main.pm.showInfo(cell);
            }
        });
        item1= new JMenuItem("Delete");
        item1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Main.pm.delete(new Object[]{cell});
            }
        });
        add(item0);
        add(item1);
    }
}
