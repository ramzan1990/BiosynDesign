package biosyndesign.core.ui;

import biosyndesign.core.graphics.PartsGraph2;
import biosyndesign.core.sbol.Part;
import biosyndesign.core.sbol.SBOLInterface;
import biosyndesign.core.sbol.SBOLme;
import biosyndesign.core.utils.UI;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class GUI extends JFrame {

    private static final int panelMargin = 0;
    private JMenu File, Parts, HelpM, Options, Window;
    private JMenuItem ClearConsole, Exit, Save, Help, About, NewProject, SaveAs, OpenProject, addCompound, addReaction, addECNumber, addEnzyme, competingReactions;
    private JCheckBoxMenuItem HideDataPanel, HideTools, HideConsole;
    ButtonGroup transformGroup;
    private JPanel dataSelectPanel, consolePanel, dataTransformPanel;
    private JToolBar dataPanel;
    private JToolBar toolsPanel;
    private JMenuBar menu;
    public JTextArea consoleArea;
    private JScrollPane consoleScroll;
    private JLabel newProject, openProject, saveProject, snapShotLabel, update;
    JTextField tf, tf2;
    JComboBox cmb1, cmb2;
    JTextField qValueTF;
    Part[] parts;
    JList partsList;
    PartsGraph2 workSpacePanel;
    private JLabel statusLabel, infoLabel;
    private JButton b3;
    private JScrollPane partsPane;


    public GUI(ProjectIO io) {
        // <editor-fold defaultstate="collapsed" desc="menu">
        GUI ref = this;
        ClearConsole = new JMenuItem("Clear console");
        HideDataPanel = new JCheckBoxMenuItem("Data Panel");
        HideTools = new JCheckBoxMenuItem("Tools Panel");
        HideConsole = new JCheckBoxMenuItem("Console");
        menu = new JMenuBar();
        File = new JMenu("File");
        Parts = new JMenu("Parts");
        HelpM = new JMenu("Help");
        Options = new JMenu("Options");
        Window = new JMenu("Window");
        Exit = new JMenuItem("Exit");
        Save = new JMenuItem("Save");
        Help = new JMenuItem("Help");
        About = new JMenuItem("About");
        NewProject = new JMenuItem("New Project");
        SaveAs = new JMenuItem("Save As");
        OpenProject = new JMenuItem("Open Project");
        addCompound= new JMenuItem("Add Compound");
        addReaction= new JMenuItem("Add Reaction");
        addECNumber= new JMenuItem("Add EC Number");
        addEnzyme= new JMenuItem("Add Enzyme");
        competingReactions =  new JMenuItem("Find Competing Native Reactions");

        NewProject.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                io.newProject();
            }
        });

        File.add(NewProject);
        File.add(OpenProject);
        File.addSeparator();
        File.add(Save);
        File.add(SaveAs);
        File.addSeparator();
        File.add(Exit);

        Parts.add(addCompound);
        Parts.add(addReaction);
        Parts.add(addECNumber);
        Parts.add(addEnzyme);
        Parts.addSeparator();
        Parts.add(competingReactions);

        OpenProject.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                io.openProject();
            }
        });
        SaveAs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                io.saveProjectAs();
            }
        });
        Exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                System.exit(0);
            }
        });

        Save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                io.saveProject();
            }
        });


        HideConsole.setSelected(true);
        HideConsole.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (HideConsole.isSelected()) {
                    consolePanel.setVisible(true);
                } else {
                    consolePanel.setVisible(false);
                }
                repaint();
            }
        });
        HideTools.setSelected(true);
        HideTools.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (HideTools.isSelected()) {
                    toolsPanel.setVisible(true);
                } else {
                    toolsPanel.setVisible(false);
                }
                repaint();
            }
        });
        HideDataPanel.setSelected(true);
        HideDataPanel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (HideDataPanel.isSelected()) {
                    dataPanel.setVisible(true);
                } else {
                    dataPanel.setVisible(false);
                }
                repaint();
            }
        });

        addCompound.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                NewParts.addCompound(ref);
            }
        });
        addReaction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                NewParts.addReaction(ref);
            }
        });
        addECNumber.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                NewParts.addECNumber(ref);
            }
        });
        addEnzyme.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                NewParts.addEnzyme(ref);
            }
        });
        competingReactions.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Main.pm.competingReactions();
            }
        });
        Window.add(HideDataPanel);
        Window.add(HideTools);
        Window.add(HideConsole);
        Window.addSeparator();
        Window.add(ClearConsole);
        ClearConsole.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                consoleArea.setText("");
            }
        });


        HelpM.add(Help);
        HelpM.addSeparator();
        HelpM.add(About);
        Help.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {


            }
        });
        About.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                final JEditorPane editorPane = new JEditorPane();

                // Enable use of custom set fonts
                editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
                editorPane.setFont(new Font("Verdana", Font.PLAIN, 14));

                editorPane.setPreferredSize(new Dimension(470, 100));
                editorPane.setEditable(false);
                editorPane.setContentType("text/html");
                editorPane.setText(
                        "<html>"
                                + "<body link=\"#009aff\" vlink=\"#009aff\" alink=\"#009aff\">"
                                + "BiosynDesign - Best thing on Earth,  (c) SFB 2016"
                                + "<br>Authors: Hiroyuki Kuwahara & Ramzan Umarov<br>"
                                + "<a href=\"mailto:hkuwahara@gmail.com?Subject=BiosynDesign\" target=\"_top\"><br>"
                                + "<font color=\"009aff\">Send Mail</font></a>"
                                + "</body>"
                                + "</html>");

                editorPane.setBorder(BorderFactory.createEmptyBorder());
                editorPane.setBackground(new Color(0, 0, 0, 0));
                // TIP: Add Hyperlink listener to process hyperlinks
                editorPane.addHyperlinkListener(new HyperlinkListener() {
                    public void hyperlinkUpdate(final HyperlinkEvent e) {
                        if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) {
                            EventQueue.invokeLater(new Runnable() {
                                public void run() {
                                    // TIP: Show hand cursor
                                    SwingUtilities.getWindowAncestor(editorPane).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                                    // TIP: Show URL as the tooltip
                                    editorPane.setToolTipText(e.getURL().toExternalForm());
                                }
                            });
                        } else if (e.getEventType() == HyperlinkEvent.EventType.EXITED) {
                            EventQueue.invokeLater(new Runnable() {
                                public void run() {
                                    // Show default cursor
                                    SwingUtilities.getWindowAncestor(editorPane).setCursor(Cursor.getDefaultCursor());

                                    // Reset tooltip
                                    editorPane.setToolTipText(null);
                                }
                            });
                        } else if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                            // TIP: Starting with JDK6 you can show the URL in desktop browser
                            if (Desktop.isDesktopSupported()) {
                                try {
                                    Desktop.getDesktop().browse(e.getURL().toURI());
                                } catch (Exception ex) {
                                }
                            }
                            //System.out.println("Go to URL: " + e.getURL());
                        }
                    }
                });

                JOptionPane.showMessageDialog(null,
                        editorPane,
                        "About",
                        JOptionPane.INFORMATION_MESSAGE);

            }
        });


        menu.add(File);
        menu.add(Parts);
        menu.add(Options);
        menu.add(Window);
        menu.add(HelpM);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="panels">

        dataSelectPanel = new JPanel();
        dataSelectPanel.setBorder(BorderFactory.createEmptyBorder(0, panelMargin, 0, panelMargin));
        GridLayout blayout = new GridLayout(3, 2);
        blayout.setVgap(10);
        blayout.setHgap(10);
        dataSelectPanel.setLayout(blayout);

        dataTransformPanel = new JPanel();

        GridLayout dtpl = new GridLayout(6, 1);
        dtpl.setVgap(10);
        dtpl.setHgap(10);
        dataTransformPanel.setLayout(dtpl);

        // <editor-fold defaultstate="collapsed" desc="labels">
        newProject = new JLabel();
        newProject.setIcon(new ImageIcon(Main.class.getResource("images/new.png")));
        newProject.setToolTipText("New Project");
        newProject.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
io.newProject();
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                newProject.setIcon(new ImageIcon(Main.class.getResource("images/new0.png")));
            }

            public void mouseExited(MouseEvent e) {
                newProject.setIcon(new ImageIcon(Main.class.getResource("images/new.png")));
            }
        });

        openProject = new JLabel();
        openProject.setIcon(new ImageIcon(Main.class.getResource("images/open.png")));
        openProject.setToolTipText("Open Project");
        openProject.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                io.openProject();
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                openProject.setIcon(new ImageIcon(Main.class.getResource("images/open0.png")));
            }

            public void mouseExited(MouseEvent e) {
                openProject.setIcon(new ImageIcon(Main.class.getResource("images/open.png")));
            }
        });

        saveProject = new JLabel();
        saveProject.setIcon(new ImageIcon(Main.class.getResource("images/save.png")));
        saveProject.setToolTipText("Save Project");
        saveProject.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                io.saveProject();
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                saveProject.setIcon(new ImageIcon(Main.class.getResource("images/save0.png")));
            }

            public void mouseExited(MouseEvent e) {
                saveProject.setIcon(new ImageIcon(Main.class.getResource("images/save.png")));
            }
        });

        snapShotLabel = new JLabel();
        snapShotLabel.setIcon(new ImageIcon(Main.class.getResource("images/camera.png")));
        snapShotLabel.setToolTipText("Take Snapshot");
        snapShotLabel.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                Main.saveImage();
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                snapShotLabel.setIcon(new ImageIcon(Main.class.getResource("images/camera0.png")));
            }

            public void mouseExited(MouseEvent e) {
                snapShotLabel.setIcon(new ImageIcon(Main.class.getResource("images/camera.png")));
            }
        });

        update = new JLabel();
        update.setIcon(new ImageIcon(Main.class.getResource("images/update.png")));
        update.setToolTipText("Take Snapshot");
        update.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                Main.gm.updateGraph();
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                update.setIcon(new ImageIcon(Main.class.getResource("images/update0.png")));
            }

            public void mouseExited(MouseEvent e) {
                update.setIcon(new ImageIcon(Main.class.getResource("images/update.png")));
            }
        });

        toolsPanel = new JToolBar();

        toolsPanel.setLayout(new BoxLayout(toolsPanel, BoxLayout.X_AXIS));
       // toolsPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        toolsPanel.add(newProject);
        toolsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        toolsPanel.add(openProject);
        toolsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        toolsPanel.add(saveProject);
        toolsPanel.add(Box.createRigidArea(new Dimension(2, 0)));
        toolsPanel.addSeparator();
        toolsPanel.add(Box.createRigidArea(new Dimension(2, 0)));
        toolsPanel.add(snapShotLabel);
        toolsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        toolsPanel.add(update);
        toolsPanel.add(Box.createHorizontalGlue());
        JLabel lt = new JLabel("Some interactable info");
        lt.setIcon(new ImageIcon(Main.class.getResource("images/v1.png")));
        toolsPanel.add(lt);
        toolsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        toolsPanel.addSeparator();
        toolsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        toolsPanel.add(new JLabel("Text Button1"));
        toolsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        toolsPanel.addSeparator();
        toolsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        toolsPanel.add(new JLabel("Text Button2"));
        toolsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        //</editor-fold>

        dataPanel = new JToolBar();
        dataPanel.setPreferredSize(new Dimension(250, 500));
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.PAGE_AXIS));
        dataPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        dataPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        int dpcw = dataPanel.getPreferredSize().width - 15;
        JLabel lp = new JLabel("Parts");
        Font labelFont = lp.getFont();
        lp.setFont(new Font(labelFont.getName(), Font.PLAIN, 28));
        lp.setForeground(new Color(61, 166, 255));
        UI.addTo(dataPanel, lp);
        JLabel l1 = new JLabel("Search for");
        l1.setMaximumSize(new Dimension(dpcw, l1.getPreferredSize().height) );
        JLabel l2 = new JLabel("Filter By");
        l2.setMaximumSize(new Dimension(dpcw, l2.getPreferredSize().height) );
        JLabel l3 = new JLabel("Value");
        l3.setMaximumSize(new Dimension(dpcw, l3.getPreferredSize().height) );
        UI.addTo(dataPanel, l1);
        cmb1 = new JComboBox();
        cmb1.setPreferredSize(new Dimension(dpcw, cmb1.getPreferredSize().height));
        cmb1.setMaximumSize(new Dimension(500, cmb1.getPreferredSize().height));
        cmb2 = new JComboBox();
        cmb2.setPreferredSize(new Dimension(dpcw, cmb2.getPreferredSize().height));
        cmb2.setMaximumSize(new Dimension(500, cmb2.getPreferredSize().height));
        cmb1.addItem("Compound");
        cmb1.addItem("Reaction");
        cmb1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cmb2.removeAllItems();
                if (cmb1.getSelectedIndex() == 0) {
                    cmb2.addItem("Compound name or ID");
                    cmb2.addItem("Drug ID");
                    cmb2.addItem("Reaction participation");
                    cmb2.addItem("Associated compound");
                    cmb2.addItem("Transforming enzyme");
                    cmb2.addItem("Compound substructure");
                } else if (cmb1.getSelectedIndex() == 1) {
                    cmb2.addItem("Reaction ID");
                    cmb2.addItem("Participating compound");
                    cmb2.addItem("Catalyzing enzyme class");
                }
            }
        });
        UI.addTo(dataPanel, cmb1);
        //dataPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        UI.addTo(dataPanel, l2);
        UI.addTo(dataPanel, cmb2);
        //dataPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        cmb1.setSelectedIndex(0);
        UI.addTo(dataPanel, l3);
        qValueTF = new JTextField("Pyruvate");
        qValueTF.setPreferredSize(new Dimension(dpcw, qValueTF.getPreferredSize().height) );
        UI.addTo(dataPanel, qValueTF);
        //dataPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        //dataPanel.setBackground(Color.RED);
        JButton b2 = new JButton("Search");
        UI.addToRight(dataPanel, b2);
        b2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread()
                {
                    public void run() {
                        try {
                            SBOLInterface sInt = Main.pm.sInt;
                            parts = sInt.findParts(cmb1.getSelectedIndex(), cmb2.getSelectedIndex(), qValueTF.getText());
                            String[] names = new String[parts.length];
                            for(int i=0; i<names.length;i++){
                                names[i] = parts[i].name;
                            }
                            partsList.setModel(new DefaultComboBoxModel(names));
                            b3.setVisible(true);
                            partsPane.setVisible(true);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }.start();


            }
        });

        partsList = new JList();
        partsPane = new JScrollPane();
        partsPane.setViewportView(partsList);
        partsPane.setPreferredSize(new Dimension(dpcw, 200));
        partsPane.setMaximumSize(new Dimension(dpcw, 200));
        partsPane.setBorder(BorderFactory.createEmptyBorder(0, panelMargin, 0, panelMargin));
        UI.addTo(dataPanel, partsPane);
        //dataPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        b3 = new JButton("Add");
        b3.setIcon(new ImageIcon(Main.class.getResource("images/load.png")));
        b3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Part[] p = new Part[partsList.getSelectedIndices().length];
                for(int i =0; i<p.length;i++){
                    p[i] = parts[partsList.getSelectedIndices()[i]];
                }
                Main.pm.addParts(p);
            }
        });
        UI.addToRight(dataPanel, b3);
        b3.setVisible(false);
        partsPane.setVisible(false);

        workSpacePanel = new PartsGraph2();

        // </editor-fold>
        // <editor-fold desc="console">
        consoleArea = new JTextArea();
        consoleArea.setEditable(false);
        consoleArea.setLineWrap(true);
        consoleArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        consoleScroll = new JScrollPane();
        consoleScroll.setPreferredSize(new Dimension(250, 110));
        consoleScroll.setViewportView(consoleArea);
        consoleScroll.setBorder(BorderFactory.createEmptyBorder());

        consolePanel = new JPanel();
        consolePanel.setLayout(new BorderLayout());
        consolePanel.add(consoleScroll, BorderLayout.CENTER);
        //consolePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        statusLabel = new JLabel("  Ready");
        statusPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, statusLabel.getPreferredSize().height + 10));
        statusPanel.add(statusLabel, BorderLayout.WEST);
        infoLabel = new JLabel("Compounds: 0   Reactions: 0  ");
        statusPanel.add(infoLabel, BorderLayout.EAST);
        consolePanel.add(statusPanel, BorderLayout.SOUTH);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="prepairing frame">
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setJMenuBar(menu);
        add(toolsPanel, BorderLayout.NORTH);
        add(dataPanel, BorderLayout.EAST);
        add(consolePanel, BorderLayout.SOUTH);
        add(workSpacePanel, BorderLayout.CENTER);
        this.pack();
        this.setLocationRelativeTo(null);
        // </editor-fold>
    }




    public void writeToConsole(String text) {
        consoleArea.append(text);
    }


    public void setStatusLabel(String status) {
        LocalDateTime currentTime = LocalDateTime.now();
        String time =  " ("+currentTime.getHour() + ":"+currentTime.getMinute()+")";
        statusLabel.setText("  "+status + time);
    }
    public void setInfoLabel(int i1, int i2) {
        infoLabel.setText("Compounds: "+i1+"   Reactions: "+i2+"  ");
    }
}
