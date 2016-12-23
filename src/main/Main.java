package main;


import javax.swing.*;
import java.util.ArrayList;

public class Main {

    private static GUI mainWindow;
    private static ArrayList<Object> componentList;
    static Object selectedComponent;
    private static JFileChooser fc;

    public static void main(String[] args){
        componentList = new ArrayList<Object>();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        mainWindow = new GUI();
        mainWindow.setTitle("BiosynDesign");
        mainWindow.setVisible(true);
        fc = new JFileChooser();
    }

    static void removeFromComponentList(Object c) {
        componentList.remove(c);
    }

    static void setSelectedComponent(Object c) {
        selectedComponent = c;
    }

    public static void writeToConsole(String text) {
        mainWindow.writeToConsole(text);
    }

}
