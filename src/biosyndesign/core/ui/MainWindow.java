package biosyndesign.core.ui;

import biosyndesign.core.Main;
import biosyndesign.core.graphics.PartsGraph;
import biosyndesign.core.sbol.parts.Part;
import biosyndesign.core.utils.UI;
import com.google.gson.JsonObject;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.table.DefaultTableModel;

public class MainWindow extends BDFrame {

    private static final int panelMargin = 0;
    private JMenu File, Parts, options, export, HelpM, repoOptions, Window;
    private JMenuItem ClearConsole, Exit, Save, Help, About, NewProject, SaveAs, OpenProject, addCompound, addReaction, addReactionClass, addEnzyme, competingReactions, chooseRepository,
            exportCDS, exportAA, exportPathway, exportGraph, showLocalParts;
    private JCheckBoxMenuItem HideDataPanel, HideTools, HideConsole, useLocalRepo, showPathway;
    private JPanel dataSelectPanel, consolePanel, dataTransformPanel;
    private JToolBar dataPanel;
    private JToolBar toolsPanel;
    private JMenuBar menu;
    public JTextArea consoleArea;
    private JScrollPane consoleScroll;
    private JLabel newProject, openProject, saveProject, snapShotLabel, update, delete, view, edit, similarity, zoomIn, zoomOut, alignArrows, commonReaction;
    JComboBox cmb1, cmb2;
    JTextField qValueTF;
    Part[] parts;
    JList partsList;
    private JTabbedPane tbp;
    public PartsGraph workSpacePanel;
    public JTable enzymeTable, genesTable;
    private JLabel statusLabel, infoLabel;
    private JButton b3;
    private JScrollPane partsPane;
    private Pagination p;
    private JLabel partsListLabel;
    private JPanel partsPanel;

    public MainWindow() {
        super();
        // <editor-fold defaultstate="collapsed" desc="menu">
        MainWindow ref = this;
        ClearConsole = new JMenuItem("Clear console");
        HideDataPanel = new JCheckBoxMenuItem("Data Panel");
        HideTools = new JCheckBoxMenuItem("Tools Panel");
        HideConsole = new JCheckBoxMenuItem("Console");
        menu = new JMenuBar();
        File = new JMenu("File");
        Parts = new JMenu("Parts");
        HelpM = new JMenu("Help");
        repoOptions = new JMenu("Repository");
        options = new JMenu("Options");
        export = new JMenu("Export");
        chooseRepository = new JMenuItem("Choose Repository");
        useLocalRepo = new JCheckBoxMenuItem("Use Local Repository");
        showPathway = new JCheckBoxMenuItem("Show Pathway");
        Window = new JMenu("Window");
        Exit = new JMenuItem("Exit");
        Save = new JMenuItem("Save");
        Help = new JMenuItem("Help");
        About = new JMenuItem("About");
        NewProject = new JMenuItem("New Project");
        SaveAs = new JMenuItem("Save As");
        OpenProject = new JMenuItem("Open Project");
        addCompound = new JMenuItem("Add Compound");
        addReaction = new JMenuItem("Add Reaction");
        addReactionClass = new JMenuItem("Add Reaction Class");
        addEnzyme = new JMenuItem("Add Protein");
        exportCDS = new JMenuItem("Export CDS");
        exportAA = new JMenuItem("Export AA Sequences");
        exportPathway = new JMenuItem("Export pathway");
        competingReactions = new JMenuItem("Find Competing Native Reactions");
        exportGraph = new JMenuItem("Export graph as SVG");
        showLocalParts = new JMenuItem("Show Local Parts");

        NewProject.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Main.projectIO.newProject();
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
        Parts.add(addReactionClass);
        Parts.add(addEnzyme);
        Parts.addSeparator();
        Parts.add(competingReactions);

        OpenProject.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Main.projectIO.openProject();
            }
        });
        SaveAs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Main.projectIO.saveProjectAs();
            }
        });
        Exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                System.exit(0);
            }
        });

        Save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Main.projectIO.saveProject();
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
                Main.lpm.addCompound(null);
            }
        });
        addReaction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Main.lpm.addReaction(null);
            }
        });
        addReactionClass.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Main.lpm.addEnzyme(null);
            }
        });
        addEnzyme.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Main.lpm.addProtein(null);
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
        export.add(exportPathway);
        export.add(exportAA);
        export.add(exportCDS);
        exportPathway.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.guim.exportPathway();
            }
        });
        exportAA.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.guim.exportAA();
            }
        });
        exportCDS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.guim.exportCDS();
            }
        });
        export.addSeparator();
        export.add(exportGraph);
        exportGraph.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.guim.exportGraph();
            }
        });
        options.add(showPathway);
        options.add(showLocalParts);
        showLocalParts.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PartsManagerFrame pmf = new PartsManagerFrame(null);
                pmf.setVisible(true);
            }
        });
        showPathway.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.guim.showPathway(showPathway.isSelected());
            }
        });
        repoOptions.add(chooseRepository);
        repoOptions.add(useLocalRepo);
        chooseRepository.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.guim.chooseRepository();
            }
        });
        useLocalRepo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.guim.useLocalRepo(useLocalRepo.isSelected());
            }
        });
        menu.add(File);
        menu.add(Parts);
        menu.add(repoOptions);
        menu.add(export);
        menu.add(options);
        menu.add(Window);
        menu.add(HelpM);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="panels">

        tbp = new JTabbedPane();
        workSpacePanel = new PartsGraph();
        String[] columnNamesEnzyme = {"Reaction",
                "Enzyme Origin", "Enzyme Name", "Primary Structure"};
        DefaultTableModel enzymeModel = new DefaultTableModel(columnNamesEnzyme, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        enzymeTable = new JTable(enzymeModel);
        enzymeTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Main.pm.rowClicked(enzymeTable.getSelectedRow(), e.getX(), e.getY() + 50);
            }
        });
        String[] columnNamesGenes = {"Enzyme Name", "Primary Structure", "CDS"};
        DefaultTableModel genesModel = new DefaultTableModel(columnNamesGenes, 0);
        genesTable = new JTable(genesModel);
        genesTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Main.pm.rowClickedCDS(genesTable.getSelectedRow(), e.getX(), e.getY() + 50);
            }
        });


        tbp.addTab("Pathway", workSpacePanel);
        tbp.addTab("Enzyme", new JScrollPane(enzymeTable));
        tbp.addTab("Gene", new JScrollPane(genesTable));
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
        newProject.setIcon(new ImageIcon(Main.class.getResource("ui/images/new.png")));
        newProject.setToolTipText("New Project");
        newProject.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                Main.projectIO.newProject();
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                newProject.setIcon(new ImageIcon(Main.class.getResource("ui/images/new0.png")));
            }

            public void mouseExited(MouseEvent e) {
                newProject.setIcon(new ImageIcon(Main.class.getResource("ui/images/new.png")));
            }
        });

        openProject = new JLabel();
        openProject.setIcon(new ImageIcon(Main.class.getResource("ui/images/open.png")));
        openProject.setToolTipText("Open Project");
        openProject.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                Main.projectIO.openProject();
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                openProject.setIcon(new ImageIcon(Main.class.getResource("ui/images/open0.png")));
            }

            public void mouseExited(MouseEvent e) {
                openProject.setIcon(new ImageIcon(Main.class.getResource("ui/images/open.png")));
            }
        });

        saveProject = new JLabel();
        saveProject.setIcon(new ImageIcon(Main.class.getResource("ui/images/save.png")));
        saveProject.setToolTipText("Save Project");
        saveProject.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                Main.projectIO.saveProject();
                setStatusLabel("Project Saved");
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                saveProject.setIcon(new ImageIcon(Main.class.getResource("ui/images/save0.png")));
            }

            public void mouseExited(MouseEvent e) {
                saveProject.setIcon(new ImageIcon(Main.class.getResource("ui/images/save.png")));
            }
        });

        snapShotLabel = new JLabel();
        snapShotLabel.setIcon(new ImageIcon(Main.class.getResource("ui/images/camera.png")));
        snapShotLabel.setToolTipText("Take Snapshot");
        snapShotLabel.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                Main.projectIO.saveImage();
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                snapShotLabel.setIcon(new ImageIcon(Main.class.getResource("ui/images/camera0.png")));
            }

            public void mouseExited(MouseEvent e) {
                snapShotLabel.setIcon(new ImageIcon(Main.class.getResource("ui/images/camera.png")));
            }
        });

        zoomIn = new JLabel();
        zoomIn.setIcon(new ImageIcon(Main.class.getResource("ui/images/zoomin.png")));
        zoomIn.setToolTipText("Zoom In");
        zoomIn.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                Main.gm.zoom(true);
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                zoomIn.setIcon(new ImageIcon(Main.class.getResource("ui/images/zoomin0.png")));
            }

            public void mouseExited(MouseEvent e) {
                zoomIn.setIcon(new ImageIcon(Main.class.getResource("ui/images/zoomin.png")));
            }
        });

        zoomOut = new JLabel();
        zoomOut.setIcon(new ImageIcon(Main.class.getResource("ui/images/zoomout.png")));
        zoomOut.setToolTipText("Zoom Out");
        zoomOut.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                Main.gm.zoom(false);
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                zoomOut.setIcon(new ImageIcon(Main.class.getResource("ui/images/zoomout0.png")));
            }

            public void mouseExited(MouseEvent e) {
                zoomOut.setIcon(new ImageIcon(Main.class.getResource("ui/images/zoomout.png")));
            }
        });


        update = new JLabel();
        update.setIcon(new ImageIcon(Main.class.getResource("ui/images/update.png")));
        update.setToolTipText("Refresh");
        update.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                Main.gm.updateGraph();
                Main.pm.updateTable();
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                update.setIcon(new ImageIcon(Main.class.getResource("ui/images/update0.png")));
            }

            public void mouseExited(MouseEvent e) {
                update.setIcon(new ImageIcon(Main.class.getResource("ui/images/update.png")));
            }
        });

        alignArrows = new JLabel();
        alignArrows.setIcon(new ImageIcon(Main.class.getResource("ui/images/align.png")));
        alignArrows.setToolTipText("Align Arrows");
        alignArrows.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                Main.pm.align();
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                alignArrows.setIcon(new ImageIcon(Main.class.getResource("ui/images/align0.png")));
            }

            public void mouseExited(MouseEvent e) {
                alignArrows.setIcon(new ImageIcon(Main.class.getResource("ui/images/align.png")));
            }
        });

        similarity = new JLabel();
        similarity.setIcon(new ImageIcon(Main.class.getResource("ui/images/similar-1.png")));
        similarity.setToolTipText("Similarity");
        similarity.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                Main.pm.similarityClicked();
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                similarity.setIcon(new ImageIcon(Main.class.getResource("ui/images/similar-2.png")));
            }

            public void mouseExited(MouseEvent e) {
                similarity.setIcon(new ImageIcon(Main.class.getResource("ui/images/similar-1.png")));
            }
        });


        commonReaction = new JLabel();
        commonReaction.setIcon(new ImageIcon(Main.class.getResource("ui/images/common-1.png")));
        commonReaction.setToolTipText("Common Reaction");
        commonReaction.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                Main.pm.commonReaction();
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                commonReaction.setIcon(new ImageIcon(Main.class.getResource("ui/images/common-2.png")));
            }

            public void mouseExited(MouseEvent e) {
                commonReaction.setIcon(new ImageIcon(Main.class.getResource("ui/images/common-1.png")));
            }
        });


        delete = new JLabel();
        delete.setIcon(new ImageIcon(Main.class.getResource("ui/images/delete.png")));
        delete.setToolTipText("Delete cells");
        delete.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                Main.pm.deleteSelected();
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                delete.setIcon(new ImageIcon(Main.class.getResource("ui/images/delete0.png")));
            }

            public void mouseExited(MouseEvent e) {
                delete.setIcon(new ImageIcon(Main.class.getResource("ui/images/delete.png")));
            }
        });

        view = new JLabel();
        view.setIcon(new ImageIcon(Main.class.getResource("ui/images/view.png")));
        view.setToolTipText("View cells");
        view.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                Main.pm.viewSelected();
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                view.setIcon(new ImageIcon(Main.class.getResource("ui/images/view0.png")));
            }

            public void mouseExited(MouseEvent e) {
                view.setIcon(new ImageIcon(Main.class.getResource("ui/images/view.png")));
            }
        });

        edit = new JLabel();
        edit.setIcon(new ImageIcon(Main.class.getResource("ui/images/edit.png")));
        edit.setToolTipText("Edit cells");
        edit.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                Main.pm.editSelected();
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                edit.setIcon(new ImageIcon(Main.class.getResource("ui/images/edit0.png")));
            }

            public void mouseExited(MouseEvent e) {
                edit.setIcon(new ImageIcon(Main.class.getResource("ui/images/edit.png")));
            }
        });


        toolsPanel = new JToolBar();
        toolsPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY));
        toolsPanel.setLayout(new BoxLayout(toolsPanel, BoxLayout.X_AXIS));
        // toolsPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        toolsPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        toolsPanel.add(newProject);
        toolsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        toolsPanel.add(openProject);
        toolsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        toolsPanel.add(saveProject);
        //toolsPanel.add(Box.createRigidArea(new Dimension(2, 0)));
        toolsPanel.addSeparator();
        //toolsPanel.add(Box.createRigidArea(new Dimension(2, 0)));
        toolsPanel.add(snapShotLabel);
        toolsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        toolsPanel.add(zoomIn);
        toolsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        toolsPanel.add(zoomOut);
        toolsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        toolsPanel.add(view);
        toolsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        toolsPanel.add(edit);
        toolsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        toolsPanel.add(delete);
        toolsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        toolsPanel.add(update);
        toolsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        toolsPanel.add(alignArrows);
        toolsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        toolsPanel.add(similarity);
        toolsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        toolsPanel.add(commonReaction);

        toolsPanel.add(Box.createHorizontalGlue());
        JLabel lt = new JLabel("Project Info");
        lt.setIcon(new ImageIcon(Main.class.getResource("ui/images/project-info.png")));
        toolsPanel.add(lt);
        toolsPanel.add(Box.createRigidArea(new Dimension(5, 0)));

        lt.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(null, "Organism Name: " + Main.guim.getOrganism() + "\n" +
                        "More info later.");
            }

        });
        //</editor-fold>

        dataPanel = new JToolBar();
        dataPanel.setPreferredSize(new Dimension(250, 500));
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.PAGE_AXIS));
        dataPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));
        dataPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        int dpcw = dataPanel.getPreferredSize().width - 15;
        JLabel lp = new JLabel("Parts");
        Font labelFont = lp.getFont();
        lp.setFont(new Font(labelFont.getName(), Font.PLAIN, 28));
        lp.setForeground(new Color(61, 166, 255));
        UI.addTo(dataPanel, lp);
        JLabel l1 = new JLabel("Search for");
        l1.setMaximumSize(new Dimension(dpcw, l1.getPreferredSize().height));
        JLabel l2 = new JLabel("Filter By");
        l2.setMaximumSize(new Dimension(dpcw, l2.getPreferredSize().height));
        JLabel l3 = new JLabel("Value");
        l3.setMaximumSize(new Dimension(dpcw, l3.getPreferredSize().height));
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
                    cmb2.addItem("SMILES");
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
        qValueTF.setPreferredSize(new Dimension(dpcw, qValueTF.getPreferredSize().height));
        UI.addTo(dataPanel, qValueTF);
        //dataPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        //dataPanel.setBackground(Color.RED);
        JButton b2 = new JButton(" Search");
        b2.setIcon(new ImageIcon(Main.class.getResource("ui/images/search.png")));
        int bw = b2.getPreferredSize().width;
        b2.setPreferredSize(new Dimension(bw, b2.getPreferredSize().height));
        UI.addToRight(dataPanel, b2);
        b2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                p.page = 0;
                p.pageTF.setText("1");
                Main.pm.search(cmb1.getSelectedIndex(), cmb2.getSelectedIndex(), qValueTF.getText(), 0);
            }
        });
        cmb2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cmb1.getSelectedIndex() == 0) {
                    if (cmb2.getSelectedIndex() == 0) {
                        qValueTF.setText("Pyruvate");
                    } else if (cmb2.getSelectedIndex() == 1) {
                        qValueTF.setText("DB00119");
                    } else if (cmb2.getSelectedIndex() == 2) {
                        qValueTF.setText("ME_R00006");
                    } else if (cmb2.getSelectedIndex() == 3) {
                        qValueTF.setText("Pyruvate");
                    } else if (cmb2.getSelectedIndex() == 4) {
                        qValueTF.setText("2.3.1.74");
                    } else if (cmb2.getSelectedIndex() == 5) {
                        qValueTF.setText("CC(=O)C(=O)O");
                    }

                } else if (cmb1.getSelectedIndex() == 1) {
                    if (cmb2.getSelectedIndex() == 0) {
                        qValueTF.setText("ME_R00006");
                    } else if (cmb2.getSelectedIndex() == 1) {
                        qValueTF.setText("Pyruvate");
                    } else if (cmb2.getSelectedIndex() == 2) {
                        qValueTF.setText("2.3.1.74");
                    }
                }
            }
        });
        partsPanel = new JPanel();
        partsListLabel = new JLabel("0 results");
        partsListLabel.setForeground(Color.LIGHT_GRAY);
        UI.addToLeft(partsPanel, partsListLabel);
        partsList = new JList();
        partsPane = new JScrollPane();
        partsPane.setViewportView(partsList);
        partsPane.setPreferredSize(new Dimension(dpcw, 200));
        partsPane.setMaximumSize(new Dimension(dpcw, 200));
        partsPane.setBorder(BorderFactory.createEmptyBorder(0, panelMargin, 0, panelMargin));
        partsPanel.add(partsPane);
        p = new Pagination();
        p.nextPage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JsonObject ob = Main.pm.getQueryInfo();
                if(p.page < Main.pm.getQueryInfo().get("count").getAsInt()-1) {
                    Main.pm.search(cmb1.getSelectedIndex(), cmb2.getSelectedIndex(), qValueTF.getText(), ++p.page);
                }
            }
        });
        p.prevPage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JsonObject ob = Main.pm.getQueryInfo();
                if(p.page > 0) {
                    Main.pm.search(cmb1.getSelectedIndex(), cmb2.getSelectedIndex(), qValueTF.getText(), --p.page);
                }
            }
        });
        p.gotoPage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int np = Integer.parseInt(p.pageTF.getText()) - 1;
                if(np < Main.pm.getQueryInfo().get("count").getAsInt()-1 && np>=0) {
                    p.page = np;
                    Main.pm.search(cmb1.getSelectedIndex(), cmb2.getSelectedIndex(), qValueTF.getText(), p.page);
                }
            }
        });
        partsPanel.add(p);
        partsPanel.setVisible(false);

        //dataPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        b3 = new JButton(" Add");
        b3.setIcon(new ImageIcon(Main.class.getResource("ui/images/load.png")));
        b3.setPreferredSize(new Dimension(bw, b3.getPreferredSize().height));
        b3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.pm.addPartsSelected(partsList.getSelectedIndices());
            }
        });
        UI.addToRight(partsPanel, b3);

        dataPanel.add(partsPanel);
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
        consolePanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
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
        add(tbp, BorderLayout.CENTER);
        this.setMinimumSize(new Dimension(800, 600));
        this.pack();
        this.setLocationRelativeTo(null);
        // </editor-fold>
    }


    public void writeToConsole(String text) {
        consoleArea.append(text);
    }


    public void setStatusLabel(String status) {
        LocalDateTime currentTime = LocalDateTime.now();
        String m = "" + currentTime.getMinute();
        if (m.length() == 1) {
            m = "0" + m;
        }
        String h = "" + currentTime.getHour();
        if (h.length() == 1) {
            h = "0" + h;
        }
        String time = " (" + h + ":" + m + ")";
        statusLabel.setText("  " + status + time);
    }

    public void setInfoLabel(int i1, int i2) {
        infoLabel.setText("Compounds: " + i1 + "   Reactions: " + i2 + "  ");
    }

    public void setResults(String[] names, JsonObject queryInfo) {
        if(queryInfo.get("count").getAsInt()==0){
            partsListLabel.setText("0 results");
        }else {
            partsList.setModel(new DefaultComboBoxModel(names));
            partsListLabel.setText("Page " + (p.page + 1) + " out of " + queryInfo.get("count").getAsInt()
                    + " [" + Main.pm.getQueryInfo().get("total").getAsInt() + " results]");
            p.pageTF.setText((p.page + 1) + "");
            partsPanel.setVisible(true);
            partsPane.repaint();
        }
    }
}
