package biosyndesign.core.ui.popups;

import biosyndesign.core.managers.PartsManager;
import biosyndesign.core.sbol.parts.Reaction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class cDNAPopUp extends JPopupMenu {
    JMenuItem item1;

    public cDNAPopUp(PartsManager pm, Reaction r){
        item1 = new JMenuItem("Edit");
        item1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                pm.editCDNA(r);
            }
        });

        add(item1);
    }
}
