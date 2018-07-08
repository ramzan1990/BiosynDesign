package biosyndesign.core.ui;

import biosyndesign.core.sbol.parts.Part;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Pagination extends JPanel {
    public JButton prevPage;
    public JButton nextPage;
    public JButton gotoPage;
    public JLabel maxPageLabel;
    public JTextField pageTF;
    public int page;

    public Pagination(){
        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        this.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        pageTF = new JTextField("", 3);
        pageTF.setHorizontalAlignment(SwingConstants.RIGHT);
        prevPage = new JButton("Previous");
        nextPage = new JButton("Next");
        gotoPage = new JButton("Go");
        this.add(prevPage);
        this.add(Box.createRigidArea(new Dimension(10, 0)));
        this.add(nextPage);
        this.add(Box.createRigidArea(new Dimension(10, 0)));
        this.add(gotoPage);
        this.add(Box.createRigidArea(new Dimension(10, 0)));
        this.add(pageTF);
        maxPageLabel = new JLabel();
        this.add(maxPageLabel);
    }
}
