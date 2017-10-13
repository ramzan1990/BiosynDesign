package biosyndesign.core.utils;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Scanner;

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

    public static String restrict(String s, int m){
        if(s.length()>m){
            s = s.substring(0, m/2)+ "..."+s.substring(s.length() - (m/2 - 3), s.length());
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

    public static JComboBox organismsBox(){
        String options[] = {"Photorhabdus luminescens"};
        try {
            Scanner scan = new Scanner(new File("names.txt"));
            ArrayList<String> names = new ArrayList<>();
            while(scan.hasNextLine()){
                names.add(scan.nextLine().trim());
            }
            options = names.toArray(new String[0]);
        }catch(Exception e){

        }
        JComboBox cb = new JComboBox(options);
        AutoCompleteDecorator.decorate(cb);
        return cb;
    }

    public static boolean isOrganism(String s) {
        try {
            Scanner scan = new Scanner(new File("names.txt"));
            while(scan.hasNextLine()){
                if(scan.nextLine().trim().equals(s)){
                    return true;
                }
            }
        }catch(Exception e){

        }
        return false;
    }
}
