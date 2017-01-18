package biosyndesign.core.ui;

import org.openscience.cdk.depict.DepictionGenerator;
import org.openscience.cdk.interfaces.IAtomContainer;

import javax.swing.*;
import java.awt.*;

public class ImageComponent extends JComponent {
    IAtomContainer mol;

    public ImageComponent(IAtomContainer mol){
        super();
        this.mol = mol;
    }

    protected void paintComponent(Graphics g) {
        Image im = null;
        try {
            im = new DepictionGenerator()
                    .withSize(this.getWidth(), this.getHeight())
                    .depict(mol).toImg();
        }catch(Exception e){
            e.printStackTrace();
        }
        g.drawImage(im, 0, 0, null);
    }
}