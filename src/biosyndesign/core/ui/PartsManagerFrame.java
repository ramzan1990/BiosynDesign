package biosyndesign.core.ui;

import biosyndesign.core.Main;
import biosyndesign.core.sbol.local.LocalRepo;
import biosyndesign.core.sbol.parts.Part;
import biosyndesign.core.sbol.parts.Protein;
import biosyndesign.core.utils.UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Umarov on 1/23/2017.
 */
public class PartsManagerFrame extends BDFrame {
    private LocalRepo lr;
    private JTable table;
    private PartsManagerFrame thisFrame;
    private JLabel statusLabel;
    private JLabel maxPageLabel;
    private int page = 0;

    public PartsManagerFrame(JFrame parent) {
        super();
        thisFrame = this;
        lr = Main.getLocalRepo();
        int w = 800;
        this.setSize(new Dimension(w, 600));
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
        this.setLayout(new BorderLayout());
        JPanel rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(220, 500));
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
        rightPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        int dpcw = rightPanel.getPreferredSize().width - 15;
        JComboBox cmb1, cmb2, cmb0;
        JLabel l0 = new JLabel("Data set:");
        l0.setMaximumSize(new Dimension(dpcw, l0.getPreferredSize().height));
        UI.addTo(rightPanel, l0);
        cmb0 = new JComboBox();
        cmb0.setPreferredSize(new Dimension(dpcw, cmb0.getPreferredSize().height));
        cmb0.setMaximumSize(new Dimension(500, cmb0.getPreferredSize().height));
        String[] datasets = lr.getDatasets();
        for (String b : datasets) {
            cmb0.addItem(b);
        }
        UI.addTo(rightPanel, cmb0);

        JTextField qValueTF;
        JLabel l1 = new JLabel("Search for");
        l1.setMaximumSize(new Dimension(dpcw, l1.getPreferredSize().height));
        JLabel l2 = new JLabel("Filter By");
        l2.setMaximumSize(new Dimension(dpcw, l2.getPreferredSize().height));
        JLabel l3 = new JLabel("Value");
        l3.setMaximumSize(new Dimension(dpcw, l3.getPreferredSize().height));
        UI.addTo(rightPanel, l1);
        cmb1 = new JComboBox();
        cmb1.setPreferredSize(new Dimension(dpcw, cmb1.getPreferredSize().height));
        cmb1.setMaximumSize(new Dimension(500, cmb1.getPreferredSize().height));
        cmb2 = new JComboBox();
        cmb2.setPreferredSize(new Dimension(dpcw, cmb2.getPreferredSize().height));
        cmb2.setMaximumSize(new Dimension(500, cmb2.getPreferredSize().height));
        cmb1.addItem("Compound");
        cmb1.addItem("Reaction");
        cmb1.addItem("Enzyme");
        cmb1.addItem("Protein");
        cmb1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                page = 0;
                cmb2.removeAllItems();
                if (cmb1.getSelectedIndex() == 0) {
                    cmb2.addItem("Compound name or ID");
                    cmb2.addItem("Drug ID");
                    cmb2.addItem("Reaction participation");
                    cmb2.addItem("Associated compound");
                    cmb2.addItem("Transforming enzyme");
                    cmb2.addItem("SMILES");
                    showParts(lr.catalog("compounds"));
                } else if (cmb1.getSelectedIndex() == 1) {
                    cmb2.addItem("Reaction ID");
                    cmb2.addItem("Participating compound");
                    cmb2.addItem("Catalyzing enzyme class");
                    showParts(lr.catalog("reactions"));
                } else if (cmb1.getSelectedIndex() == 2) {
                    cmb2.addItem("Enzyme class ID");
                    cmb2.addItem("Transformed compound");
                    cmb2.addItem("Catalyzing reaction");
                    showParts(lr.catalog("enzymes"));
                } else if (cmb1.getSelectedIndex() == 3) {
                    cmb2.addItem("Protein ID");
                    cmb2.addItem("Enzyme class ID");
                    cmb2.addItem("Organism");
                    showParts(lr.catalog("proteins"));
                }
            }
        });
        UI.addTo(rightPanel, cmb1);
        //dataPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        UI.addTo(rightPanel, l2);
        UI.addTo(rightPanel, cmb2);
        //dataPanel.add(Box.createRigidArea(new Dimension(0, 10)))
        UI.addTo(rightPanel, l3);
        qValueTF = new JTextField("Pyruvate");
        qValueTF.setPreferredSize(new Dimension(dpcw, qValueTF.getPreferredSize().height));
        UI.addTo(rightPanel, qValueTF);
        //dataPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        //dataPanel.setBackground(Color.RED);
        JButton b2 = new JButton(" Search");
        b2.setIcon(new ImageIcon(Main.class.getResource("ui/images/search.png")));
        int bw = b2.getPreferredSize().width;
        b2.setPreferredSize(new Dimension(bw, b2.getPreferredSize().height));
        UI.addToRight(rightPanel, b2);
        b2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Part[] p = lr.findParts(cmb1.getSelectedIndex(), cmb2.getSelectedIndex(), qValueTF.getText(), 0);
                showParts(p);
            }
        });
        b2.setEnabled(false);
        cmb0.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                page = 0;
                if (cmb0.getSelectedIndex() > -1) {
                    lr.setCurrentDataset(cmb0.getSelectedItem().toString());
                    b2.setEnabled(true);
                    cmb1.setSelectedIndex(0);
                } else {
                    DefaultTableModel dtm = (DefaultTableModel) table.getModel();
                    dtm.setRowCount(0);
                }
            }
        });
        //rightPanel.add(Box.createRigidArea(new Dimension(0, 300)));
        JPanel topPanel = new JPanel();
        //topPanel.setSize(new Dimension(400, 250));
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.PAGE_AXIS));

        String[][] rowData = new String[][]{{"", ""}};
        String[] columnNames = new String[]{"ID", "Name"};
        table = new JTable(rowData, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        topPanel.add(scrollPane);
        JPanel paginationPanel = new JPanel();
        paginationPanel.setLayout(new BoxLayout(paginationPanel, BoxLayout.LINE_AXIS));
        paginationPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        JTextField pageTF = new JTextField();
        pageTF.setText(page + "");
        JButton prevPage = new JButton("Previous");
        prevPage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(page>0) {
                    Part[] p = lr.getPage(--page, cmb1.getSelectedIndex());
                    showParts(p);
                    pageTF.setText(page + "");
                }
            }
        });
        JButton nextPage = new JButton("Next");
        nextPage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(page + 1 < Math.ceil((double) lr.totalRows / lr.maxRowsPage)) {
                    Part[] p = lr.getPage(++page, cmb1.getSelectedIndex());
                    showParts(p);
                    pageTF.setText(page + "");
                }
            }
        });

        JButton gotoPage = new JButton("Go to");
        gotoPage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int pn = Integer.parseInt(pageTF.getText());
                    Part[] p = lr.getPage(pn, cmb1.getSelectedIndex());
                    showParts(p);
                    page = pn;
                } catch (Exception ex) {
                }
                pageTF.setText(page + "");
            }
        });
        paginationPanel.add(prevPage);
        paginationPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        paginationPanel.add(nextPage);
        paginationPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        paginationPanel.add(gotoPage);
        paginationPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        pageTF.setPreferredSize(gotoPage.getPreferredSize());
        pageTF.setMaximumSize(gotoPage.getPreferredSize());
        paginationPanel.add(pageTF);
        maxPageLabel = new JLabel();
        paginationPanel.add(maxPageLabel);
        topPanel.add(paginationPanel);
        JPanel lowerPanel = new JPanel(new BorderLayout());
        JPanel jp1 = new JPanel();
        JPanel jp2 = new JPanel();
        jp2.setLayout(new BoxLayout(jp2, BoxLayout.LINE_AXIS));
        jp2.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        JButton bc = new JButton("Create Dataset");
        jp2.add(bc);
        jp2.add(Box.createRigidArea(new Dimension(10, 0)));
        bc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                page = 0;
                String name = JOptionPane.showInputDialog("Choose name for the dataset:");
                if (name == null || name.length() == 0) {
                    return;
                }
                lr.createDB(name);
                cmb0.addItem(name);
                cmb0.setSelectedItem(name);
            }
        });

        JButton b3 = new JButton("Import Parts");
        jp2.add(b3);
        jp2.add(Box.createRigidArea(new Dimension(10, 0)));
        b3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                if (cmb0.getSelectedItem() != null) {
                    new Thread(() -> {
                        lr.importParts(cmb0.getSelectedItem().toString(), statusLabel);
                        thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    }).start();
                }

            }
        });
        JButton b4 = new JButton("Delete Dataset");
        b4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                page = 0;
                if (cmb0.getSelectedIndex() != -1) {
                    String name = cmb0.getSelectedItem().toString();
                    lr.deleteDataset(name);
                    cmb0.removeItem(name);
                    cmb0.setSelectedIndex(-1);
                    b2.setEnabled(false);
                }

            }
        });
        JButton b5 = new JButton("Reset");
        b5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to reset your collection? All data will be lost.", "Warning", JOptionPane.YES_NO_OPTION);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    //lr.resetDB(); ??
                    cmb0.removeAllItems();
                    DefaultTableModel dtm = (DefaultTableModel) table.getModel();
                    dtm.setRowCount(0);
                }
            }
        });
        jp2.add(b4);
        //jp2.add(Box.createHorizontalGlue());
        statusLabel = new JLabel("Ready");
        jp1.add(statusLabel);
        //jp2.add(b5);
        lowerPanel.add(jp1, BorderLayout.EAST);
        lowerPanel.add(jp2, BorderLayout.WEST);
        lowerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        this.setLayout(new BorderLayout());
        this.add(rightPanel, BorderLayout.EAST);
        this.add(lowerPanel, BorderLayout.SOUTH);
        this.add(topPanel, BorderLayout.CENTER);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (parent != null) {
                    parent.setVisible(true);
                }
            }
        });

        this.setTitle("Local Parts Manager");
        //this.pack();
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        if (datasets.length > 0) {
            cmb0.setSelectedIndex(0);
        }
        cmb1.setSelectedIndex(0);

    }

    private void showParts(Part[] p) {
        String[] columnNames;
        String[][] rowData;
        if (p.length > 0 && p[0] instanceof Protein) {
            rowData = new String[p.length][3];
            columnNames = new String[]{"ID", "Enzyme class ID", "Organism"};
            for (int i = 0; i < p.length; i++) {
                Protein prot = (Protein) p[i];
                rowData[i][0] = prot.id;
                rowData[i][1] = prot.enzymeID;
                rowData[i][2] = prot.organism.name;
            }
        } else {
            columnNames = new String[]{"ID", "Name"};
            rowData = new String[p.length][2];
            for (int i = 0; i < p.length; i++) {
                rowData[i][0] = p[i].id;
                rowData[i][1] = p[i].name;
            }
        }
        table.setModel(new DefaultTableModel(rowData, columnNames));
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                Main.pm.showInfo(p[row]);
            }
        });
        maxPageLabel.setText(" " + (int) Math.ceil((double) lr.totalRows / lr.maxRowsPage) + " total pages");
    }

}
