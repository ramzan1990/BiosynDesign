package biosyndesign.core.utils;

import biosyndesign.core.Main;
import biosyndesign.core.utils.svg2xml.Svg2XmlGui;
import org.openscience.cdk.depict.DepictionGenerator;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.renderer.color.IAtomColorer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import java.awt.*;
import java.io.File;

public class Bio {
    public static String getSVG(String smiles, String name, int scale) throws CDKException {
        int fontSize = scale*3;
        int maxLetters = scale*9;
        IChemObjectBuilder bldr
                = SilentChemObjectBuilder.getInstance();
        SmilesParser smipar = new SmilesParser(bldr);
        IAtomContainer mol = smipar.parseSmiles(smiles);
        //mol.setProperty(CDKConstants.TITLE, Common.restrict(p[i].name, 14));
        String svg = new DepictionGenerator().withBackgroundColor(new Color(223, 244, 255)).withAtomColors(new IAtomColorer() {
            @Override
            public Color getAtomColor(IAtom iAtom) {
                return Color.black;
            }

            @Override
            public Color getAtomColor(IAtom iAtom, Color color) {
                return Color.black;
            }


        })
                //.withMolTitle().withTitleColor(Color.BLACK).withTitleScale(2)
                .withMargin(1).withZoom(3).depict(mol).toSvgStr();
        String s = Common.between(svg, "viewBox='0 0 ", "'>");
        double w = Double.parseDouble(s.split(" ")[0]);
        double h = Double.parseDouble(s.split(" ")[1]);
        double wNew = 0;
        double hNew = 0;
        double wb = 0;
        double hb = 0;
        if(w>h*2.5) {
            wNew = 50;
            hNew = (50 * h) / w;
        }else{
            hNew = 20;
            wNew = (20 * w) / h;
        }
        wb = (50 - wNew)/2;
        hb = (20 - hNew)/2;
        svg = new DepictionGenerator().withBackgroundColor(new Color(223, 244, 255)).withAtomColors(new IAtomColorer() {
            @Override
            public Color getAtomColor(IAtom iAtom) {
                return Color.black;
            }

            @Override
            public Color getAtomColor(IAtom iAtom, Color color) {
                return Color.black;
            }


        })
                .withSize(wNew, hNew).withMargin(1).withZoom(3)
                .depict(mol).toSvgStr();
        svg = svg.replaceAll("<svg version.*",
                "<svg version='1.2' xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' width='50.0mm' height='25.0mm' viewBox='0 0 50.0 25.0'>");
        svg = svg.replaceAll("<rect.*",
                "<rect x='.0' y='.0' width='50' height='25' rx='2' ry='2' style='fill:#e0ecff;stroke:black;stroke-width:0.3;opacity:1.0'/>"
                        + System.lineSeparator() +  "<g transform='matrix(1 0 0 1 "+(wb)+" "+(hb)+")'>"
        );

        svg = svg.substring(0, svg.lastIndexOf("</g>")) + System.lineSeparator() +
               "  </g>"+
                "<text x='1' y='24' style='font: "+fontSize+"px sans-serif;'>" + Common.restrict(name, maxLetters) + "</text>" + System.lineSeparator() +
                svg.substring(svg.lastIndexOf("</g>"), svg.length());


        return svg;

    }
}
