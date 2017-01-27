package biosyndesign.core.ui;

import biosyndesign.core.sbol.Reaction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class cellPopUp extends JPopupMenu {
    JMenuItem item1, item2, item3;

    public cellPopUp(Reaction cell){
        item1 = new JMenuItem("Chooze EC number");
        item1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Main.setEC(cell);
            }
        });
        item2 = new JMenuItem("Choose enzyme");
        item2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Main.chooseEnzyme(cell);
            }
        });
        add(item1);
        add(item2);
    }
}
