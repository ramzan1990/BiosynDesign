package biosyndesign.core.ui.popups;

import biosyndesign.core.managers.ProjectIO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RecentProjectPopUp extends JPopupMenu {
    JMenuItem item1;

    public RecentProjectPopUp(JList projectsList, int selectedIndex, ProjectIO io){
        item1 = new JMenuItem("Remove");
        item1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                ((DefaultListModel) projectsList.getModel()).remove(selectedIndex);
                io.remove(selectedIndex);
            }
        });
        add(item1);
    }


}
