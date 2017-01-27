package biosyndesign.core.ui;

import biosyndesign.core.utils.PopClickListener;
import biosyndesign.core.utils.UI;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.jdesktop.swingx.autocomplete.ObjectToStringConverter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * Created by Umarov on 1/23/2017.
 */
public class FirstFrame extends JFrame {
    boolean empty;
    String po[];

    public FirstFrame(ProjectIO io) {
        int w = 600;
        this.setSize(new Dimension(w, 400));
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
        this.setLayout(new BorderLayout());
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setSize(new Dimension(200, 300));
        leftPanel.setMinimumSize(new Dimension(200, 300));
        //leftPanel.add(new JLabel("Where am I????----------------------"));

        po = io.getRecent();
        String[] poList;
        empty = false;
        if (po == null) {
            poList = new String[]{"  No recent projects"};
            empty = true;
        } else {
            poList = new String[po.length];
            for (int i = 0; i < po.length; i++) {
                int p1 = po[i].lastIndexOf("\\");
                String sv = po[i].substring(0, p1);
                if(sv.length()>30){
                    sv = sv.substring(0, 12)+ "..."+sv.substring(sv.length() - 12, sv.length());
                }
                poList[i] = "  " + sv;
            }
        }
        JList projectsList = new JList(poList);
        if (!empty) {
            projectsList.setSelectedIndex(0);
        }
        projectsList.setFixedCellHeight(60);
        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selectedItem = po[projectsList.getSelectedIndex()];
                    if (!empty) {
                        io.openRecentSelected(selectedItem.trim());
                    }
                }
            }
        };
        projectsList.addMouseListener(mouseListener);
        JScrollPane partsPane = new JScrollPane();
        partsPane.setMaximumSize(new Dimension(300, 900));
        partsPane.setPreferredSize(new Dimension(200, 300));
        partsPane.setViewportView(projectsList);
        //projectsList.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        partsPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        leftPanel.add(partsPane);


        //leftPanel.add(Box.createRigidArea(new Dimension(0, 300)));
        JPanel topPanel = new JPanel();
        //topPanel.setSize(new Dimension(400, 250));
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.PAGE_AXIS));

        JTextField locationTF = new JTextField("                                                                                                                  ");
        //locationTF.setPreferredSize(new Dimension(300, 20));
        JPanel browsePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        browsePanel.setMaximumSize(new Dimension(w, 100));
        locationTF.setEditable(false);
        locationTF.setPreferredSize(new Dimension(340, 25));
        JButton browse = new JButton("...");
        browse.setPreferredSize(new Dimension(30, 25));
        //browse.setBorder(BorderFactory.createEtchedBorder(1));
        browse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                FileDialog fd = new FileDialog((Frame) null, "Save Project", FileDialog.SAVE);
                fd.setVisible(true);
                fd.setFilenameFilter(new FileFilter());

                if (fd.getFiles().length > 0) {
                    File f = fd.getFiles()[0];
                    Main.s.projectName = f.getName();
                    Main.s.projectPath = f.getAbsolutePath();
                    String path = Main.s.projectPath;
                    locationTF.setText(path);
                }


            }
        });
        JLabel l1 = new JLabel("Location:");
        l1.setPreferredSize(new Dimension(50, 25));
        browsePanel.add(l1);
        browsePanel.add(locationTF);
        browsePanel.add(browse);
        JLabel picLabel = new JLabel(new ImageIcon(Main.class.getResource("images/logo.png")));
        UI.addTo(topPanel, picLabel);
        topPanel.add(browsePanel);
        String options[] = {"Photorhabdus luminescens"};
        JList<String> list = new JList<String>(options);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        JTextField textField = new JTextField("dddddddddddddddddddddd");
        textField.setPreferredSize(new Dimension(340, 25));
        JPanel organismPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        organismPanel.setMaximumSize(new Dimension(w, 100));
        JLabel l2 = new JLabel("Organism:");
        l2.setPreferredSize(new Dimension(50, 25));
        organismPanel.add(l2);
        organismPanel.add(textField);
        topPanel.add(organismPanel);
        topPanel.add(Box.createRigidArea(new Dimension(400, 10)));
        AutoCompleteDecorator.decorate(list, textField, ObjectToStringConverter.DEFAULT_IMPLEMENTATION);


        //topPanel.setBackground(Color.BLUE);


        this.setLayout(new BorderLayout());
        JPanel lowerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel lpl1 = new JLabel("cbrc.kaust.edu.sa/sbolme");
        lpl1.setPreferredSize(new Dimension(420, 20));
        lpl1.addMouseListener(new PopClickListener(new repoPopUp(lpl1)));
        lowerPanel.add(lpl1);
        JButton b1 = new JButton("Open Project");
        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                io.openProjectSelected();
            }
        });
        lowerPanel.add(b1);

        JButton b2 = new JButton("Create Project");
        b2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (locationTF.getText().trim().length() != 0){
                    io.newProjectSelected(textField.getText());
                }
            }
        });
        lowerPanel.add(b2);
        lowerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        this.add(leftPanel, BorderLayout.WEST);

        this.add(lowerPanel, BorderLayout.SOUTH);
        this.add(topPanel, BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setTitle("Welcome to BiosynDesign");
        this.pack();
        this.setLocationRelativeTo(null);

    }
}
