package biosyndesign.core.managers;

import biosyndesign.core.sbol.parts.Reaction;
import biosyndesign.core.ui.MainWindow;
import biosyndesign.core.utils.Comment;
import biosyndesign.core.utils.UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Umarov on 1/10/2018.
 */
public class CDNAManager {

    private MainWindow mainWindow;
    private PartsManager pm;

    private Comment cc;
    private JTextArea comTA = null, cDNATA = null;
    private JTextArea seqTA = null;
    private boolean update = true;
    private JLabel count;
    private HashMap<String, String> name;
    private HashMap<String, String> codons;
    private ArrayList<Comment> comments;
    private JDialog thisFrame;
    private String newCDNA;
    JScrollPane seqTASC, cDNATASC;

    public CDNAManager(PartsManager pm, MainWindow mainWindow) {
        this.pm = pm;
        this.mainWindow = mainWindow;
        name = new HashMap<>();
        name.put("A", "Ala");
        name.put("R", "Arg");
        name.put("N", "Asn");
        name.put("D", "Asp");
        name.put("C", "Cys");
        name.put("E", "Glu");
        name.put("Q", "Gln");
        name.put("G", "Gly");
        name.put("H", "His");
        name.put("I", "Ile");
        name.put("L", "Leu");
        name.put("K", "Lys");
        name.put("M", "Met");
        name.put("F", "Phe");
        name.put("P", "Pro");
        name.put("S", "Ser");
        name.put("T", "Thr");
        name.put("W", "Trp");
        name.put("Y", "Tyr");
        name.put("V", "Val");
        codons = new HashMap<>();
        codons.put("TTT", "Phe");
        codons.put("TTC", "Phe");
        codons.put("TTA", "Leu");
        codons.put("TTG", "Leu");
        codons.put("CTT", "Leu");
        codons.put("CTC", "Leu");
        codons.put("CTA", "Leu");
        codons.put("CTG", "Leu");
        codons.put("ATT", "Ile");
        codons.put("ATC", "Ile");
        codons.put("ATA", "Ile");
        codons.put("ATG", "Met");
        codons.put("GTT", "Val");
        codons.put("GTC", "Val");
        codons.put("GTA", "Val");
        codons.put("GTG", "Val");
        codons.put("TCT", "Ser");
        codons.put("TCC", "Ser");
        codons.put("TCA", "Ser");
        codons.put("TCG", "Ser");
        codons.put("CCT", "Pro");
        codons.put("CCC", "Pro");
        codons.put("CCA", "Pro");
        codons.put("CCG", "Pro");
        codons.put("ACT", "Thr");
        codons.put("ACC", "Thr");
        codons.put("ACA", "Thr");
        codons.put("ACG", "Thr");
        codons.put("GCT", "Ala");
        codons.put("GCC", "Ala");
        codons.put("GCA", "Ala");
        codons.put("GCG", "Ala");
        codons.put("TAT", "Tyr");
        codons.put("TAC", "Tyr");
        codons.put("TAA", "STOP");
        codons.put("TAG", "STOP");
        codons.put("CAT", "His");
        codons.put("CAC", "His");
        codons.put("CAA", "Gln");
        codons.put("CAG", "Gln");
        codons.put("AAT", "Asn");
        codons.put("AAC", "Asn");
        codons.put("AAA", "Lys");
        codons.put("AAG", "Lys");
        codons.put("GAT", "Asp");
        codons.put("GAC", "Asp");
        codons.put("GAA", "Glu");
        codons.put("GAG", "Glu");
        codons.put("TGT", "Cys");
        codons.put("TGC", "Cys");
        codons.put("TGA", "STOP");
        codons.put("TGG", "Trp");
        codons.put("CGT", "Arg");
        codons.put("CGC", "Arg");
        codons.put("CGA", "Arg");
        codons.put("CGG", "Arg");
        codons.put("AGT", "Ser");
        codons.put("AGC", "Ser");
        codons.put("AGA", "Arg");
        codons.put("AGG", "Arg");
        codons.put("GGT", "Gly");
        codons.put("GGC", "Gly");
        codons.put("GGA", "Gly");
        codons.put("GGG", "Gly");
    }

    public void editCDNA(Reaction r) {
        final JDialog frame = new JDialog(mainWindow, "Choose Enzyme", true);
        thisFrame = frame;
        int wd = 845;
        this.comments = cloneList(r.comments);
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));
        UI.addTo(jp, new JLabel("Sequence "));
        seqTA = new JTextArea();
        seqTA.setText(extendSeq(r.enzyme.sequence));
        seqTA.setLineWrap(true);
        Font font = new Font(Font.MONOSPACED, Font.PLAIN, 16);
        Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
        //attributes.put(TextAttribute.TRACKING, 0.01);
        Font font2 = font.deriveFont(attributes);
        seqTA.setFont(font2);
        seqTA.setEditable(false);
        //seqTA.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        seqTASC = new JScrollPane(seqTA);
        //sc.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.BLACK));
        JLabel columnheader = new JLabel() {

            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                //          g.drawLine(0, 0, this.getWidth(), 0);
                double step = 40;
                for (int i = 0; i < 20; i++) {
                    int v = (int) Math.round(i * step) + 15;
                    g.drawLine(v, 0, v, 3);
                    int a = 0;
                    if (i + 1 > 9) {
                        a = 5;
                    }
                    g.drawString("" + (i + 1), v - a, 16);
                }
            }

            public Dimension getPreferredSize() {
                return new Dimension((int) seqTA.getPreferredSize().getWidth(), 20);
            }
        };
        columnheader.setBackground(Color.white);
        columnheader.setOpaque(true);
        JLabel rowheader = new JLabel() {

            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                //          g.drawLine(0, 0, this.getWidth(), 0);
                double step = 22;
                int c = r.enzyme.sequence.length()/20 + 10;
                for (int i = 0; i < c; i++) {
                    int v = (int) Math.round(i * step) + 14;
                    g.drawLine(0, v, 3, v);
                    g.drawString("" + (i + 1), 10, v+5);
                }
            }

            public Dimension getPreferredSize() {
                return new Dimension( 30, (int)seqTA.getPreferredSize().getHeight());
            }
        };
        rowheader.setBackground(Color.white);
        rowheader.setOpaque(true);
        seqTASC.setColumnHeaderView(columnheader);
        seqTASC.setRowHeaderView(rowheader);
        seqTASC.setPreferredSize(new Dimension(wd, 200));
        seqTASC.setBackground(Color.WHITE);
        UI.addTo(jp, seqTASC);
        UI.addTo(jp, new JLabel("CDS "));
        cDNATA = new JTextArea();
        cDNATA.setText(breakString(r.CDS, 60));
        cDNATA.setLineWrap(true);
        font = new Font(Font.MONOSPACED, Font.PLAIN, 22);
        attributes = new HashMap<TextAttribute, Object>();
        //attributes.put(TextAttribute.TRACKING, 0.038);
        font2 = font.deriveFont(attributes);
        cDNATA.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 22));
        JButton b1 = new JButton("Annotate");
        JPanel bp = new JPanel();
        bp.setLayout(new BoxLayout(bp, BoxLayout.X_AXIS));
        b1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    for (Comment c : comments) {
                        if (c.start <= cDNATA.getSelectionEnd() && cDNATA.getSelectionStart() <= c.end) {
                            JOptionPane.showMessageDialog(null, "Comments are not allowed to overlap!");
                            return;
                        }
                    }
                    Color c = showDialog();
                    cDNATA.getHighlighter().addHighlight(cDNATA.getSelectionStart(), cDNATA.getSelectionEnd(),
                            new DefaultHighlighter.DefaultHighlightPainter(c));
                    comments.add(new Comment(cDNATA.getSelectionStart(), cDNATA.getSelectionEnd(), newComment, c));
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        });
        JButton b2 = new JButton("Deannotate");
        b2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    for (Comment c : comments) {
                        if (cDNATA.getCaretPosition() > c.start && cDNATA.getCaretPosition() < c.end) {
                            for (Highlighter.Highlight h : cDNATA.getHighlighter().getHighlights()) {
                                if (h.getStartOffset() == c.start && h.getEndOffset() == c.end) {
                                    cDNATA.getHighlighter().removeHighlight(h);
                                    break;
                                }
                            }
                            comments.remove(c);
                            disableCom();
                            break;
                        }
                    }
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        });
        JButton b3 = new JButton("Default CDS");
        b3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                r.baseCDS = r.enzyme.CDS;
                cDNATA.setText(r.baseCDS);
                seqTA.setCaretPosition(0);
                cDNATA.setCaretPosition(0);
            }
        });
        JButton b4 = new JButton("Optimize");
        b4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                try {
                    File directory = new File("temp");
                    if (!directory.exists()) {
                        directory.mkdir();
                    }
                    try (PrintWriter out = new PrintWriter("temp" + File.separator + "opt.txt")) {
                        out.println(cDNATA.getText().replaceAll("t", "u"));
                    }
                    File exe = new File("mRNA" + File.separator + "mRNAOptimiser.jar");
                    //ProcessBuilder builder = new ProcessBuilder(exe.getAbsolutePath(), "-f", "temp" + File.separator + "opt.txt", "-q");
                    //builder.redirectErrorStream(true);
                    //Process process = builder.start();
                    Process proc = Runtime.getRuntime().exec("java -jar " + exe.getAbsolutePath() + " -f temp" + File.separator + "opt.txt -q");
                    InputStream is = proc.getInputStream();
                    //InputStream is = process.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("Creating")) {
                            continue;
                        }
                        line = line.toLowerCase();
                        line = line.replaceAll("u", "t");
                        r.baseCDS = line;
                        cDNATA.setText(line);
                        seqTA.setCaretPosition(0);
                        cDNATA.setCaretPosition(0);
                        paintComments();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        JButton b5 = new JButton("Check");
        b5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkCDNA();
            }
        });
        JButton b6 = new JButton("Base CDS");
        b6.addMouseListener(new MouseListener() {
            int cp;

            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                newCDNA = cDNATA.getText();
                cp = cDNATASC.getVerticalScrollBar().getValue();
                cDNATA.setText(r.baseCDS);
                setScroll();
            }

            private void setScroll() {
                final Runnable run1 = new Runnable(){
                    public void run(){
                        cDNATASC.getVerticalScrollBar().setValue(cp);
                    }
                };
                SwingUtilities.invokeLater(run1);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                cp = cDNATASC.getVerticalScrollBar().getValue();
                cDNATA.setText(newCDNA);
                setScroll();
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        count = new JLabel();
        bp.add(b1);
        bp.add(Box.createRigidArea(new Dimension(10, 0)));
        bp.add(b2);
        bp.add(Box.createRigidArea(new Dimension(10, 0)));
        bp.add(b3);
        bp.add(Box.createRigidArea(new Dimension(10, 0)));
        bp.add(b4);
        bp.add(Box.createRigidArea(new Dimension(10, 0)));
        bp.add(b5);
        bp.add(Box.createRigidArea(new Dimension(10, 0)));
        bp.add(b6);
        bp.add(Box.createRigidArea(new Dimension(10, 0)));
        bp.add(count);
        paintComments();

        ((AbstractDocument) cDNATA.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String str, AttributeSet attr)
                    throws BadLocationException {
                super.replace(fb, offset, length, str, attr);
                check(cDNATA);
            }

            @Override
            public void insertString(FilterBypass fb, int offset, String string,
                                     AttributeSet attr) throws BadLocationException {
                super.insertString(fb, offset, string, attr);
                check(cDNATA);
            }

            @Override
            public void remove(final FilterBypass fb, final int offset, final int length) throws BadLocationException
            {
                super.remove(fb, offset, length);
                check(cDNATA);
            }

        });
        cDNATA.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (c != 'a' && c != 't' && c != 'g' && c != 'c') {
                    e.consume();  // ignore event
                }
            }
        });
        cDNATASC = new JScrollPane(cDNATA);
        //sc.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
        columnheader = new JLabel() {

            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                // g.drawLine(0, 0, this.getWidth(), 0);
                double step = 13.0;
                for (int i = 0; i < 60; i++) {
                    int v = (int) Math.round(i * step) + 7;
                    g.drawLine(v, 0, v, 3);
                    if (i == 0 || (i + 1) % 4 == 0) {
                        int a = 0;
                        if (i + 1 > 9) {
                            a = 5;
                        }
                        g.drawString("" + (i + 1), v - a, 16);
                    }
                }
            }

            public Dimension getPreferredSize() {
                return new Dimension((int) cDNATA.getPreferredSize().getWidth(), 20);
            }
        };
        columnheader.setBackground(Color.white);
        columnheader.setOpaque(true);
        rowheader = new JLabel() {

            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                //          g.drawLine(0, 0, this.getWidth(), 0);
                double step = 30;
                int c = r.enzyme.sequence.length()/20 + 10;
                for (int i = 0; i < c; i++) {
                    int v = (int) Math.round(i * step) + 18;
                    g.drawLine(0, v, 3, v);
                    g.drawString("" + (i + 1), 10, v+5);
                }
            }

            public Dimension getPreferredSize() {
                return new Dimension( 30, (int)cDNATA.getPreferredSize().getHeight());
            }
        };
        rowheader.setBackground(Color.white);
        rowheader.setOpaque(true);
        cDNATASC.setColumnHeaderView(columnheader);
        cDNATASC.setRowHeaderView(rowheader);
        cDNATASC.setPreferredSize(new Dimension(wd, 200));
        cDNATASC.setBackground(Color.WHITE);
        UI.addTo(jp, cDNATASC);
        UI.addTo(jp, bp);
        UI.addTo(jp, new JLabel("Annotation "));
        comTA = new JTextArea();
        comTA.setLineWrap(true);
        comTA.setEnabled(false);
        comTA.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateComment();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateComment();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateComment();
            }
        });
        JScrollPane sc = new JScrollPane(comTA);
        sc.setPreferredSize(new Dimension(wd, 120));
        UI.addTo(jp, sc);

        cDNATA.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                int dot = e.getDot();
                int mark = e.getMark();
                int start = Math.min(dot, mark);
                int end = Math.max(dot, mark);
                if (start != end) {
                    try {
                        int startC = change(start, 60, true);
                        int endC = change(end, 60, true);
                        if (endC % 3 == 1) {
                            endC += 2;
                        } else if (endC % 3 == 2) {
                            endC += 1;
                        }
                        if (startC % 3 == 1) {
                            startC -= 1;
                        } else if (startC % 3 == 2) {
                            startC -= 2;
                        }
                        seqTA.getHighlighter().removeAllHighlights();
                        int endS = change(endC, 20, false);
                        int startS = change(startC, 20, false);
                        seqTA.getHighlighter().addHighlight(startS, endS, new DefaultHighlighter.DefaultHighlightPainter(new Color(155, 225, 255)));
                        seqTA.repaint();
                        count.setText("Selected " + (endC - startC));
                    } catch (Exception ex) {

                    }
                } else {
                    seqTA.getHighlighter().removeAllHighlights();
                    count.setText("");
                }
                for (Comment c : comments) {
                    if (start >= c.start && end <= c.end) {
                        update = false;
                        comTA.setText(c.message);
                        comTA.setEnabled(true);
                        update = true;
                        cc = c;
                        return;
                    }
                }
                disableCom();
            }
        });

        seqTA.setCaretPosition(0);
        cDNATA.setCaretPosition(0);

        JPanel bp2 = new JPanel();
        bp2.setLayout(new BoxLayout(bp2, BoxLayout.X_AXIS));
        JButton ok = new JButton("Ok");
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                r.CDS = cDNATA.getText();
                r.comments = cloneList(comments);
                frame.setVisible(false);
                frame.dispose();
                pm.updateTable();
            }
        });
        JButton apply = new JButton("Apply");
        apply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                r.CDS = cDNATA.getText();
                r.comments = cloneList(comments);
                pm.updateTable();
            }
        });
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                frame.dispose();
            }
        });
        jp.add(new JSeparator());
        bp2.add(Box.createHorizontalGlue());
        bp2.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        bp2.add(ok);
        bp2.add(Box.createRigidArea(new Dimension(10, 0)));
        bp2.add(apply);
        bp2.add(Box.createRigidArea(new Dimension(10, 0)));
        bp2.add(cancel);
        bp2.add(Box.createRigidArea(new Dimension(10, 40)));
        jp.add(bp2);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                pm.updateTable();
            }
        });
        //frame.setMinimumSize(new Dimension(600, 600));
        frame.setResizable(false);
        frame.getContentPane().add(jp);
        frame.pack();
        frame.setLocationRelativeTo(mainWindow);
        frame.setVisible(true);
    }

    private void checkCDNA() {
        try {
            String[] s = seqTA.getText().split("\\s+");
            String text = cDNATA.getText();
            text = text.replaceAll("\\s+", "");
            ArrayList<String> c = new ArrayList<String>();
            int index = 0;
            while (index < text.length()) {
                c.add(text.substring(index, Math.min(index + 3, text.length())));
                index += 3;
            }
            int i = 0;
            for (String m : s) {
                if (!codons.get(c.get(i++).toUpperCase()).equals(m)) {
                    JOptionPane.showMessageDialog(null, "CDS sequence is wrong!");
                    return;
                }
            }
            do {
                if (!codons.get(c.get(i++).toUpperCase()).equals("STOP")) {
                    JOptionPane.showMessageDialog(null, "CDS sequence is wrong!");
                    return;
                }
            } while (i < c.size());
            JOptionPane.showMessageDialog(null, "CDS sequence is correct!");
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, "CDS sequence is wrong!");
        }
    }

    private String extendSeq(String sequence) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sequence.length(); i++) {
            sb.append(name.get(String.valueOf(sequence.charAt(i))));
            if ((i + 1) % 20 == 0) {
                sb.append("\n");
            } else {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private void paintComments() {
        cDNATA.getHighlighter().removeAllHighlights();
        for (Comment c : comments) {
            try {
                cDNATA.getHighlighter().addHighlight(c.start, c.end,
                        new DefaultHighlighter.DefaultHighlightPainter(c.c));
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    boolean updatecDNATA = true;
    int caretPosition;

    private void check(JTextArea cDNATA) {
        if (!updatecDNATA) {
            return;
        }
        updatecDNATA = false;
        String text = cDNATA.getText().replaceAll("\\s", "");

        caretPosition = cDNATA.getCaretPosition();
        cDNATA.setText(breakString(text, 60));
        try {
            cDNATA.setCaretPosition(caretPosition);
        } catch (Exception e) {

        }
        paintComments();
        updatecDNATA = true;

    }

    private String breakString(String sequence, int c) {
        sequence = sequence.replaceAll("\\s", "");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sequence.length(); i++) {
            if (i != 0 && i % c == 0) {
                sb.append("\n");
            }
            sb.append(sequence.charAt(i));
        }
        return sb.toString();
    }

    private int change(int n, int i, boolean r) {
        if (r) {
            return n - (n / (i + 1));
        } else {
            return n + (n / 3);
        }
    }

    private void disableCom() {
        update = false;
        comTA.setText("");
        comTA.setEnabled(false);
        update = true;
    }

    private void updateComment() {
        if (cc != null && update) {
            cc.message = comTA.getText();
        }
    }

    String newComment;

    private Color showDialog() {
        newComment = "";
        JColorChooser chooser = new JColorChooser(Color.RED);
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));
        JTextArea ta = new JTextArea();
        ta.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                newComment = ta.getText();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                newComment = ta.getText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                newComment = ta.getText();
            }
        });
        JScrollPane sc = new JScrollPane(ta);
        sc.setPreferredSize(new Dimension(590, 120));
        UI.addTo(jp, new JLabel("Enter the comment here:"));
        UI.addTo(jp, sc);

        chooser.setPreviewPanel(jp);
        JDialog dialog = JColorChooser.createDialog(
                null,
                "Choose a color for the comment",
                true,
                chooser,
                null,
                null);
        try {
            JPanel p = (JPanel) jp.getParent();
            p.setBorder(new EmptyBorder(0, 0, 0, 0));
        } catch (Exception e) {

        }
        dialog.setVisible(true);
        return chooser.getColor();
    }

    public static ArrayList<Comment> cloneList(ArrayList<Comment> list) {
        ArrayList<Comment> clone = new ArrayList<>(list.size());
        for (Comment item : list) clone.add(item.clone());
        return clone;
    }
}
