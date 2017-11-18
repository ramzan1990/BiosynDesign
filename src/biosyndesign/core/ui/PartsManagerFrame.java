package biosyndesign.core.ui;

import biosyndesign.core.Main;
import biosyndesign.core.sbol.local.LocalRepo;
import biosyndesign.core.sbol.parts.Part;
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
    private String[] columnNames = new String[]{"ID", "Name"};

    public PartsManagerFrame(JFrame parent) {
        super();
        lr = Main.getLocalRepo();
        int w = 800;
        this.setSize(new Dimension(w, 600));
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
        this.setLayout(new BorderLayout());
        JPanel  rightPanel = new JPanel();
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
        String[] batches = lr.getDatasets();
        for(String b:batches){
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
        cmb1.addItem("EC Number");
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
                    showParts(lr.catalog("compounds"));
                } else if (cmb1.getSelectedIndex() == 1) {
                    cmb2.addItem("Reaction ID");
                    cmb2.addItem("Participating compound");
                    cmb2.addItem("Catalyzing enzyme class");
                    showParts(lr.catalog("reactions"));
                } else if (cmb1.getSelectedIndex() == 2) {
                    cmb2.addItem("EC number");
                    cmb2.addItem("Transformed compound");
                    cmb2.addItem("Catalyzing reaction");
                    showParts(lr.catalog("ecnum"));
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
                Part[] p = lr.findParts(cmb1.getSelectedIndex(), cmb2.getSelectedIndex(), qValueTF.getText());
                showParts(p);
            }
        });
        b2.setEnabled(false);
        cmb0.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(cmb0.getSelectedIndex()>-1) {
                    lr.setCurrentDataset(cmb0.getSelectedItem().toString());
                    b2.setEnabled(true);
                    cmb1.setSelectedIndex(0);
                }else{
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
        table = new JTable(rowData, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        topPanel.add(scrollPane);
        JPanel lowerPanel = new JPanel(new BorderLayout());
        JPanel jp1 = new JPanel();
        JPanel jp2 = new JPanel();
        JButton b3 = new JButton("Import Parts");
        jp2.add(b3);
        b3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog("Choose name for the dataset OR leave empty to import into current dataset:");
                lr.importParts(name);
                if(name.length() != 0){
                    cmb0.addItem(name);
                    cmb0.setSelectedItem(name);
                }else{
                    cmb1.setSelectedIndex(cmb1.getSelectedIndex());
                }
            }
        });
        JButton b4 = new JButton("Delete Parts");
        b4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(cmb0.getSelectedIndex()!=-1) {
                    String name= cmb0.getSelectedItem().toString();
                    lr.deleteDataset(name);
                    cmb0.removeItem(name);
                    cmb0.setSelectedIndex(-1);
                    b2.setEnabled(false);
                }

            }
        });
        JButton b5 = new JButton("Reset DB");
        b5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lr.resetDB();
            }
        });
        jp2.add(b4);
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
                parent.setVisible(true);
            }
        });

        this.setTitle("Local Parts Manager");
        //this.pack();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        cmb1.setSelectedIndex(0);
    }

    private void showParts(Part[] p){
        String[][] rowData = new String[p.length][2];
        for(int i =0; i<p.length; i++){
            rowData[i][0] = p[i].id;
            rowData[i][1] = p[i].name;
        }
        table.setModel(new DefaultTableModel(rowData, columnNames));
    }

}
