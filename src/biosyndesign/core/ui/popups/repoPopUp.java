package biosyndesign.core.ui.popups;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class repoPopUp extends JPopupMenu {
    JMenuItem item1, item2, item3;
    JLabel label;
    public repoPopUp(JLabel label){
        this.label = label;
        item1 = new JMenuItem("cbrc.kaust.edu.sa/sbolme");
        item1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                label.setText("cbrc.kaust.edu.sa/sbolme");
            }
        });
        item2 = new JMenuItem("Option 2");
        item2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                label.setText("Option 2");
            }
        });
        item3 = new JMenuItem("Option 3");
        item3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                label.setText("Option 3");
            }
        });
        add(item1);
        add(item2);
        add(item3);
    }
}
