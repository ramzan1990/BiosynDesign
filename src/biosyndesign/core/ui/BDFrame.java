package biosyndesign.core.ui;

import biosyndesign.core.Main;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by Umarov on 3/18/2017.
 */
public class BDFrame extends  JFrame{
    public BDFrame(){
        try {
            ArrayList localArrayList = new ArrayList();
            localArrayList.add(new ImageIcon(Main.class.getResource("ui/images/ic16.png")).getImage());
            localArrayList.add(new ImageIcon(Main.class.getResource("ui/images/ic32.png")).getImage());
            localArrayList.add(new ImageIcon(Main.class.getResource("ui/images/ic64.png")).getImage());
            localArrayList.add(new ImageIcon(Main.class.getResource("ui/images/ic128.png")).getImage());
            this.setIconImages(localArrayList);
        } catch (Exception localException2) {
        }
    }
}
