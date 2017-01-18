package biosyndesign.core.ui;

import biosyndesign.core.graphics.PartsGraph;
import biosyndesign.core.sbol.Part;
import biosyndesign.core.sbol.SBOLInterface;
import com.google.gson.JsonArray;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class GUI extends JFrame {

    private static final int panelMargin = 6;
    private JMenu File, HelpM, Options, Window;
    private JMenuItem ClearConsole, Exit, Save, Help, About, NewProject, SaveAs, OpenProject;
    private JCheckBoxMenuItem HideDataPanel, HideTools, HideConsole;
    ButtonGroup transformGroup;
    private JPanel dataSelectPanel, consolePanel, dataTransformPanel;
    private JPanel dataPanel;
    private JToolBar toolsPanel;
    private JPanel workSpacePanel;
    private JMenuBar menu;
    public JTextArea consoleArea;
    private JScrollPane consoleScroll;
    private JLabel newProject, openProject, saveProject;
    JTextField tf, tf2;
    JComboBox cmb1, cmb2;
    JTextField qValueTF;
    Part[] parts;
    JList partsList;

    public GUI() {
        // <editor-fold defaultstate="collapsed" desc="menu">
        ClearConsole = new JMenuItem("Clear console");
        HideDataPanel = new JCheckBoxMenuItem("Data Panel");
        HideTools = new JCheckBoxMenuItem("Tools Panel");
        HideConsole = new JCheckBoxMenuItem("Console");
        menu = new JMenuBar();
        File = new JMenu("File");
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

        NewProject.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Main.newProject();
            }
        });

        File.add(NewProject);
        File.add(OpenProject);
        File.addSeparator();
        File.add(Save);
        File.add(SaveAs);
        File.addSeparator();
        File.add(Exit);
        OpenProject.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Main.openProject();
            }
        });
        SaveAs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Main.saveProjectAs();
            }
        });
        Exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                System.exit(0);
            }
        });

        Save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Main.saveProject();
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
        Window.add(HideDataPanel);
        Window.add(HideTools);
        Window.add(HideConsole);
        Window.addSeparator();
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
Main.newProject();
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                newProject.setIcon(new ImageIcon(Main.class.getResource("images/newRollover.png")));
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
                Main.openProject();
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                openProject.setIcon(new ImageIcon(Main.class.getResource("images/openRollover.png")));
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
                Main.saveProject();
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                saveProject.setIcon(new ImageIcon(Main.class.getResource("images/saveRollover.png")));
            }

            public void mouseExited(MouseEvent e) {
                saveProject.setIcon(new ImageIcon(Main.class.getResource("images/save.png")));
            }
        });


        toolsPanel = new JToolBar();

        toolsPanel.setLayout(new BoxLayout(toolsPanel, BoxLayout.X_AXIS));
        toolsPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        toolsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        toolsPanel.add(newProject);
        toolsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        toolsPanel.add(openProject);
        toolsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        toolsPanel.add(saveProject);
        toolsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        toolsPanel.addSeparator();
        //</editor-fold>

        dataPanel = new JPanel();
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.PAGE_AXIS));
        dataPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        dataPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        tf = new JTextField();
        tf.setPreferredSize(new Dimension(250, 20));
        tf.setMaximumSize(new Dimension(500, 20));
        tf.setText("c1cc(CC=CC#N)ccn1");
        dataPanel.add(tf);
        dataPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        JButton b1 = new JButton("Render");
        dataPanel.add(b1);
        b1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String smiles = tf.getText();
                try {
                    IChemObjectBuilder bldr
                            = SilentChemObjectBuilder.getInstance();
                    SmilesParser smipar = new SmilesParser(bldr);
                    IAtomContainer mol = smipar.parseSmiles(smiles);

                    //createFrame("Test", new ImageComponent(mol));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });
        dataPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel l1 = new JLabel("Search for");
        JLabel l2 = new JLabel("Filter By");
        JLabel l3 = new JLabel("Value");
        dataPanel.add(l1);
        cmb1 = new JComboBox();
        cmb1.setPreferredSize(new Dimension(250, 20));
        cmb1.setMaximumSize(new Dimension(500, 20));
        cmb2 = new JComboBox();
        cmb2.setPreferredSize(new Dimension(250, 20));
        cmb2.setMaximumSize(new Dimension(500, 20));
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
        dataPanel.add(cmb1);
        dataPanel.add(l2);
        dataPanel.add(cmb2);
        cmb1.setSelectedIndex(0);
        dataPanel.add(l3);
        qValueTF = new JTextField();
        qValueTF.setPreferredSize(new Dimension(250, 20));
        qValueTF.setMaximumSize(new Dimension(500, 20));
        dataPanel.add(qValueTF);

        JButton b2 = new JButton("Search");
        dataPanel.add(b2);
        b2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    SBOLInterface sInt = new SBOLInterface();
                    parts = sInt.findCompound(cmb1.getSelectedIndex(), cmb2.getSelectedIndex(), qValueTF.getText());
                    String[] names = new String[parts.length];
                    for(int i=0; i<names.length;i++){
                        names[i] = parts[i].name;
                    }
                    partsList.setModel(new DefaultComboBoxModel(names));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });

        partsList = new JList();
        JScrollPane partsPane = new JScrollPane();
        partsPane.setViewportView(partsList);
        partsPane.setPreferredSize(new Dimension(130, 200));
        partsPane.setBorder(BorderFactory.createEmptyBorder(0, panelMargin, 0, panelMargin));
        dataPanel.add(partsPane);
        JButton b3 = new JButton("Add");
        dataPanel.add(b3);
        b3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Part[] p = new Part[partsList.getSelectedIndices().length];
                for(int i =0; i<p.length;i++){
                    p[i] = parts[partsList.getSelectedIndices()[i]];
                }
                Main.addParts(p);
            }
        });

        workSpacePanel = new JPanel();
        workSpacePanel.add(new PartsGraph());
        // </editor-fold>
        // <editor-fold desc="console">
        consoleArea = new JTextArea();
        consoleArea.setEditable(false);
        consoleArea.setLineWrap(true);
        consoleArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        consoleScroll = new JScrollPane();
        consoleScroll.setPreferredSize(new Dimension(250, 110));
        consoleScroll.setViewportView(consoleArea);
        consoleScroll.setBorder(BorderFactory.createEmptyBorder());

        consolePanel = new JPanel();
        consolePanel.setLayout(new BorderLayout());
        consolePanel.add(consoleScroll, BorderLayout.CENTER);
        consolePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));// </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="prepairing frame">
        setTitle("VISAN");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setJMenuBar(menu);
        add(toolsPanel, BorderLayout.NORTH);
        add(dataPanel, BorderLayout.EAST);
        add(consolePanel, BorderLayout.SOUTH);
        add(workSpacePanel, BorderLayout.CENTER);
        // </editor-fold>
    }





    void writeToConsole(String text) {
        consoleArea.append(text);
    }



}