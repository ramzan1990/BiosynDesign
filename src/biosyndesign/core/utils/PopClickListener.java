package biosyndesign.core.utils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Umarov on 1/25/2017.
 */
public class PopClickListener extends MouseAdapter {
    repoPopUp menu;

    public PopClickListener(repoPopUp menu){
        this.menu = menu;
    }
    public void mousePressed(MouseEvent e){
        if (e.isPopupTrigger())
            doPop(e);
    }

    public void mouseReleased(MouseEvent e){
        if (e.isPopupTrigger())
            doPop(e);
    }

    private void doPop(MouseEvent e){
        menu.show(e.getComponent(), e.getX(), e.getY());
    }
}

