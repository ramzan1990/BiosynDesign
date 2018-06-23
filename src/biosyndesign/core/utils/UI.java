package biosyndesign.core.utils;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Umarov on 1/23/2017.
 */
public class UI {
    public static void addTo(JToolBar dataPanel, Component b1) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(b1);

        panel.setPreferredSize(new Dimension(250, b1.getPreferredSize().height+10));
        panel.setMaximumSize(new Dimension(250, b1.getPreferredSize().height+10));

        dataPanel.add(panel);
    }

    public static void addToRight(JToolBar dataPanel, Component b1) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(b1);

        panel.setPreferredSize(new Dimension(250, b1.getPreferredSize().height+10));
        panel.setMaximumSize(new Dimension(250, b1.getPreferredSize().height+10));

        dataPanel.add(panel);
    }
    public static void addToRight(JPanel dataPanel, Component b1) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(b1);

        panel.setPreferredSize(new Dimension(250, b1.getPreferredSize().height+10));
        panel.setMaximumSize(new Dimension(250, b1.getPreferredSize().height+10));

        dataPanel.add(panel);
    }
    public static void addToRight(JPanel dataPanel, Component b1, boolean s) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(b1);

        if(s) {
            panel.setPreferredSize(new Dimension(250, b1.getPreferredSize().height + 10));
            panel.setMaximumSize(new Dimension(250, b1.getPreferredSize().height + 10));
        }
        dataPanel.add(panel);
    }
    public static void addTo(JPanel dataPanel, Component b1) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(b1);
        panel.setMaximumSize(new Dimension(panel.getMaximumSize().width, b1.getPreferredSize().height+4));
        //panel.setPreferredSize(new Dimension(250, 30));
        //panel.setMaximumSize(new Dimension(250, 30));

        dataPanel.add(panel);
    }

    public static void addTo2(JPanel dataPanel, Component b1) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(b1);

        b1.setPreferredSize(new Dimension(600, 20));
        panel.setBackground(Color.blue);
        //panel.setMaximumSize(new Dimension(250, 30));

        dataPanel.add(panel);
    }

    public static void addTFTo(JPanel jp, JTextField c, int i, int m) {
        c.setPreferredSize(new Dimension(i, m));
        c.setMaximumSize(new Dimension(i, m));
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(c);
        jp.add(panel);
    }

    public static void addTATo(JPanel jp, JTextArea ta, int i, int m) {
        ta.setPreferredSize(new Dimension(i, m));
        ta.setMaximumSize(new Dimension(i, m));
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(ta);
        jp.add(panel);
    }

    public static void addCBTo(JPanel jp, JComboBox c, int i, int m) {
        c.setPreferredSize(new Dimension(i, m));
        c.setMaximumSize(new Dimension(i, m));
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(c);
        jp.add(panel);
    }

    public static void addJLTo(JPanel jp, JList<String> l, int i, int m) {
        l.setPreferredSize(new Dimension(i, m));
        l.setMaximumSize(new Dimension(i, m));
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(l);
        jp.add(panel);
    }

    public static void addTo(JPanel jp, JComponent l, int i, int m) {
        l.setPreferredSize(new Dimension(i, m));
        l.setMaximumSize(new Dimension(i, m));
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(l);
        jp.add(panel);
    }

    public static void addTo(JPanel jp, JComponent l) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setMaximumSize(new Dimension(panel.getMaximumSize().width, l.getPreferredSize().height+10));
        panel.add(l);
        jp.add(panel);
    }
}
