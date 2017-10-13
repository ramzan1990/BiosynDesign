package biosyndesign.core.ui.popups;

import biosyndesign.core.managers.PartsManager;
import biosyndesign.core.sbol.parts.Reaction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TablePopUp extends JPopupMenu {
    JMenuItem item1;

    public TablePopUp(PartsManager pm, Reaction r){
        item1 = new JMenuItem("Edit");
        item1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                pm.chooseEnzyme(r);
            }
        });

        add(item1);
    }
}
