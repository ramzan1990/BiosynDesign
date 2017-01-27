package biosyndesign.core.ui;

import biosyndesign.core.utils.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Umarov on 1/27/2017.
 */
public class NewParts {

    public static void addCompound(GUI mainWindow) {
        final JDialog frame = new JDialog(mainWindow, "New Compound", true);
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));
        JLabel l1 = new JLabel("Input the data");
        UI.addTo(jp, l1);
        JTextArea ta = new JTextArea();
        ta.setPreferredSize(new Dimension(300, 300));
        UI.addTo(jp, ta);
        JButton b1 = new JButton("Add");
        UI.addToRight(jp, b1, false);
        b1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //libsbolj stuff
                //Main.addParts();
                frame.setVisible(false);
                frame.dispose();
            }
        });
        frame.getContentPane().add(jp);
        frame.pack();
        frame.setLocationRelativeTo(mainWindow);
        frame.setVisible(true);
    }
    public static void addReaction(GUI mainWindow) {
        final JDialog frame = new JDialog(mainWindow, "New Reaction", true);
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));
        JLabel l1 = new JLabel("Input the data");
        UI.addTo(jp, l1);
        JTextArea ta = new JTextArea();
        ta.setPreferredSize(new Dimension(300, 300));
        UI.addTo(jp, ta);
        JButton b1 = new JButton("Add");
        UI.addToRight(jp, b1, false);
        b1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //libsbolj stuff
                //Main.addParts();
                frame.setVisible(false);
                frame.dispose();
            }
        });
        frame.getContentPane().add(jp);
        frame.pack();
        frame.setLocationRelativeTo(mainWindow);
        frame.setVisible(true);
    }
    public static void addECNumber(GUI mainWindow) {
        final JDialog frame = new JDialog(mainWindow, "New EC Number", true);
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));
        JLabel l1 = new JLabel("Input the data");
        UI.addTo(jp, l1);
        JTextArea ta = new JTextArea();
        ta.setPreferredSize(new Dimension(300, 300));
        UI.addTo(jp, ta);
        JButton b1 = new JButton("Add");
        UI.addToRight(jp, b1, false);
        b1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //libsbolj stuff
                //Main.addParts();
                frame.setVisible(false);
                frame.dispose();
            }
        });
        frame.getContentPane().add(jp);
        frame.pack();
        frame.setLocationRelativeTo(mainWindow);
        frame.setVisible(true);
    }
    public static void addEnzyme(GUI mainWindow) {
        final JDialog frame = new JDialog(mainWindow, "New Enzyme", true);
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));
        JLabel l1 = new JLabel("Input the data");
        UI.addTo(jp, l1);
        JTextArea ta = new JTextArea();
        ta.setPreferredSize(new Dimension(300, 300));
        UI.addTo(jp, ta);
        JButton b1 = new JButton("Add");
        UI.addToRight(jp, b1, false);
        b1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //libsbolj stuff
                //Main.addParts();
                frame.setVisible(false);
                frame.dispose();
            }
        });
        frame.getContentPane().add(jp);
        frame.pack();
        frame.setLocationRelativeTo(mainWindow);
        frame.setVisible(true);
    }
}
