package biosyndesign.core.graphics;

import biosyndesign.core.utils.Common;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.depict.DepictionGenerator;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.color.IAtomColorer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

public class ImageComponent extends JComponent {
    IAtomContainer mol;
    String text = null;

    public ImageComponent(IAtomContainer mol){
        super();
        this.mol = mol;
    }

    protected void paintComponent(Graphics g) {
        Image im = null;
        int w = this.getWidth();
        int h = this.getHeight()-14;
        Color bg = new Color(223, 244, 255);
        g.setColor(bg);
        g.fillRect(0, 0, this.getWidth(),  this.getHeight());
        //mol.setProperty(CDKConstants.TITLE, text);.withMolTitle().withTitleColor(Color.BLACK)
        try {
            im = new DepictionGenerator().withBackgroundColor(bg).withAtomColors(new IAtomColorer() {
                @Override
                public Color getAtomColor(IAtom iAtom) {
                    return Color.lightGray;
                }

                @Override
                public Color getAtomColor(IAtom iAtom, Color color) {
                    return Color.lightGray;
                }
            })
                    .withSize(w, h)
                    .depict(mol).toImg();
        }catch(Exception e){
            e.printStackTrace();
        }
        g.drawImage(im, 0, 0, null);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, this.getWidth()-1,  this.getHeight()-1);
        g.drawLine(0, h, w, h);
        if(text!=null){
            g.drawString(text, 5, this.getHeight()-2);
        }
    }

    public void setText(String text){
        this.text = Common.restrict(text, 20);
    }

    public static Image makeColorTransparent
            (Image im, final Color color) {
        ImageFilter filter = new RGBImageFilter() {
            // the color we are looking for... Alpha bits are set to opaque
            public int markerRGB = color.getRGB() | 0xFF000000;

            public final int filterRGB(int x, int y, int rgb) {
                if ( ( rgb | 0xFF000000 ) == markerRGB ) {
                    // Mark the alpha bits as zero - transparent
                    return 0x00FFFFFF & rgb;
                }
                else {
                    // nothing to do
                    return rgb;
                }
            }
        };

        ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(ip);
    }
}
