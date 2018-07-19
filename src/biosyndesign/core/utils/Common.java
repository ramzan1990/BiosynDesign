package biosyndesign.core.utils;

import biosyndesign.core.graphics.ImageComponent;
import biosyndesign.core.sbol.parts.*;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import javax.swing.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Umarov on 1/19/2017.
 */
public class Common {
    public static void copy(String src, String dest) {
        try {
            Path srcDir = new File(src).toPath();
            Path dstDir = new File(dest).toPath();
            Files.walkFileTree(srcDir, new SimpleFileVisitor<Path>() {
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    return copy(file);
                }

                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return copy(dir);
                }

                private FileVisitResult copy(Path fileOrDir) throws IOException {
                    Files.copy(fileOrDir, dstDir.resolve(srcDir.relativize(fileOrDir)));
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String restrict(String s, int m) {
        if (s.length() > m) {
            s = s.substring(0, m / 2) + "..." + s.substring(s.length() - (m / 2 - 3), s.length());
        }
        return s;
    }

    public static String between(String fc, String s1, String s2) {
        String name = fc.substring(fc.indexOf(s1) + s1.length(), fc.indexOf(s2));
        return name;
    }

    public static int countMatches(String line, String m) {
        return line.length() - line.replace(m, "").length();
    }

    public static JComboBox organismsBox() {
        String options[] = {"Photorhabdus luminescens"};
        try {
            Scanner scan = new Scanner(new File("names.txt"));
            ArrayList<String> names = new ArrayList<>();
            while (scan.hasNextLine()) {
                names.add(scan.nextLine().trim());
            }
            options = names.toArray(new String[0]);
        } catch (Exception e) {

        }
        JComboBox cb = new JComboBox(options);
        AutoCompleteDecorator.decorate(cb);
        return cb;
    }

    public static boolean isOrganism(String s) {
        try {
            Scanner scan = new Scanner(new File("names.txt"));
            while (scan.hasNextLine()) {
                if (scan.nextLine().trim().equals(s)) {
                    return true;
                }
            }
        } catch (Exception e) {

        }
        return false;
    }

    public static void extractFolder(String zipFile, String extractFolder) {
        ZipFile zip = null;
        try {
            int BUFFER = 2048;
            File file = new File(zipFile);


            zip = new ZipFile(file);

            String newPath = extractFolder;

            new File(newPath).mkdir();
            Enumeration zipFileEntries = zip.entries();

            // Process each entry
            while (zipFileEntries.hasMoreElements()) {
                // grab a zip file entry
                ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
                String currentEntry = entry.getName();

                File destFile = new File(newPath, currentEntry);
                //destFile = new File(newPath, destFile.getName());
                File destinationParent = destFile.getParentFile();

                // create the parent directory structure if needed
                destinationParent.mkdirs();

                if (!entry.isDirectory()) {
                    BufferedInputStream is = new BufferedInputStream(zip
                            .getInputStream(entry));
                    int currentByte;
                    // establish buffer for writing file
                    byte data[] = new byte[BUFFER];

                    // write the current file to disk
                    FileOutputStream fos = new FileOutputStream(destFile);
                    BufferedOutputStream dest = new BufferedOutputStream(fos,
                            BUFFER);

                    // read and write until last byte is encountered
                    while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, currentByte);
                    }
                    dest.flush();
                    dest.close();
                    is.close();
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (zip != null) {
            try {
                zip.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String ltrim(String p, String s) {
        if (s.startsWith(p)) {
            s = s.substring(p.length(), s.length());
        }
        return s;
    }

    public static void showInfo(Part c, String url, JFrame parent) {
        try {
            ImageComponent ic = null;
            TransformerFactory factory = TransformerFactory.newInstance();
            Templates template = null;
            if (c instanceof Compound) {
                template = factory.newTemplates(new StreamSource(
                        new FileInputStream("xsl" + File.separator + "compound.xsl")));
                String smiles = ((Compound) c).smiles;
                if (smiles != null) {
                    try {
                        IChemObjectBuilder bldr
                                = SilentChemObjectBuilder.getInstance();
                        SmilesParser smipar = new SmilesParser(bldr);
                        IAtomContainer mol = smipar.parseSmiles(smiles);
                        ic = new ImageComponent(mol, Color.WHITE);
                    } catch (Exception ex) {
                    }
                }
            } else if (c instanceof Reaction) {
                if(c.custom){
                    template = factory.newTemplates(new StreamSource(
                            new FileInputStream("xsl" + File.separator + "reaction2.xsl")));
                }else{
                    template = factory.newTemplates(new StreamSource(
                            new FileInputStream("xsl" + File.separator + "reaction.xsl")));
                }
            } else if (c instanceof Enzyme) {
                template = factory.newTemplates(new StreamSource(
                        new FileInputStream("xsl" + File.separator + "enzyme.xsl")));
            } else if (c instanceof Protein) {
                template = factory.newTemplates(new StreamSource(
                        new FileInputStream("xsl" + File.separator + "protein.xsl")));
            }
            Transformer xformer = template.newTransformer();
            Source source = new StreamSource(new FileInputStream(url));
            Result result = new StreamResult(new FileOutputStream("temp.html"));
            xformer.transform(source, result);
            String html = new String(Files.readAllBytes(Paths.get("temp.html")));
            JEditorPane edit1 = new JEditorPane("text/html", html);
            ScrollPane sp = new ScrollPane();
            sp.add(edit1);
            sp.setPreferredSize(new Dimension(edit1.getPreferredSize().width + 40, 600));
            final JDialog frame = new JDialog(parent, "", true);
            if (ic != null) {
                ic.setPreferredSize(new Dimension(200, 200));
                frame.getContentPane().add(ic, BorderLayout.NORTH);
            }
            frame.getContentPane().add(sp, BorderLayout.CENTER);
            frame.pack();
            frame.setSize(new Dimension(1024, 768));
            frame.setLocationRelativeTo(parent);
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
