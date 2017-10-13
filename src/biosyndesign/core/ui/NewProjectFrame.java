package biosyndesign.core.ui;

import biosyndesign.core.Main;
import biosyndesign.core.managers.ProjectIO;
import biosyndesign.core.ui.popups.repoPopUp;
import biosyndesign.core.utils.Common;
import biosyndesign.core.utils.PopClickListener;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Umarov on 1/23/2017.
 */
public class NewProjectFrame extends BDFrame {

    File f;

    public NewProjectFrame(ProjectIO io) {
        super();
        int w = 600;
        this.setSize(new Dimension(w, 400));
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
        this.setLayout(new BorderLayout());


        JPanel topPanel = new JPanel();
        //topPanel.setSize(new Dimension(400, 250));
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.PAGE_AXIS));

        JTextField locationTF = new JTextField();
        int h = locationTF.getPreferredSize().height;
        h = Math.max(h, 25);
        //locationTF.setPreferredSize(new Dimension(300, 20));
        JPanel browsePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        browsePanel.setMaximumSize(new Dimension(w, 100));
        locationTF.setEditable(false);
        locationTF.setPreferredSize(new Dimension(340, h));
        JButton browse = new JButton("...");
        browse.setPreferredSize(new Dimension(30, h));
        //browse.setBorder(BorderFactory.createEtchedBorder(1));
        browse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                FileDialog fd = new FileDialog((Frame) null, "Save Project", FileDialog.SAVE);
                fd.setVisible(true);
                fd.setFilenameFilter(new FileFilter());

                if (fd.getFiles().length > 0) {
                    f = fd.getFiles()[0];
                    locationTF.setText(f.getAbsolutePath());
                }


            }
        });
        JLabel l1 = new JLabel("Location:");

        browsePanel.add(l1);
        browsePanel.add(locationTF);
        browsePanel.add(browse);
       // UI.addTo(topPanel, picLabel);
        topPanel.add(browsePanel);

        JComboBox textField = Common.organismsBox();
        textField.setPreferredSize(new Dimension(340, h));
        JPanel organismPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        organismPanel.setMaximumSize(new Dimension(w, 100));
        JLabel l2 = new JLabel("Organism:");
        int m = l2.getPreferredSize().width;
        if(m<50){
            m = 50;
        }
        l1.setPreferredSize(new Dimension(m, h));
        l2.setPreferredSize(new Dimension(m, h));
        organismPanel.add(l2);
        organismPanel.add(textField);
        topPanel.add(organismPanel);

        JPanel prefixPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        prefixPanel.setMaximumSize(new Dimension(w, 100));
        JLabel l3 = new JLabel("Prefix:");
        l3.setPreferredSize(new Dimension(m, h));
        prefixPanel.add(l3);
        JTextField prefixField = new JTextField("http://www.cbrc.kaust.edu.sa/sbolme/dev");
        prefixField.setPreferredSize(new Dimension(340, h));
        prefixPanel.add(prefixField);
        topPanel.add(prefixPanel);


        this.setLayout(new BorderLayout());
        JPanel lowerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel lpl1 = new JLabel("cbrc.kaust.edu.sa/sbolme");
        lpl1.setPreferredSize(new Dimension(320, Math.max(20, lpl1.getPreferredSize().height)));
        lpl1.addMouseListener(new PopClickListener(new repoPopUp(lpl1)));
        lowerPanel.add(lpl1);

        JButton b2 = new JButton("Create Project");
        b2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (locationTF.getText().trim().length() != 0){
                    io.newProjectSelected(textField.getSelectedItem().toString(), prefixField.getText(), f);
                }
            }
        });
        lowerPanel.add(b2);
        lowerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        this.add(lowerPanel, BorderLayout.SOUTH);
        this.add(topPanel, BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.setTitle("Create New Project");
        this.pack();
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                io.creationCanceled();
            }
        });
    }
}
