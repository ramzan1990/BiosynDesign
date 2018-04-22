package biosyndesign.core.ui.popups;

import biosyndesign.core.Main;
import biosyndesign.core.sbol.parts.Compound;
import biosyndesign.core.sbol.parts.Part;
import biosyndesign.core.sbol.parts.Reaction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DeleteCellPopUp extends JPopupMenu {
    JMenuItem item1;

    public DeleteCellPopUp(Part[] parts){
        item1= new JMenuItem("Delete");
        item1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Main.pm.delete(parts);
            }
        });
        add(item1);
    }
}
