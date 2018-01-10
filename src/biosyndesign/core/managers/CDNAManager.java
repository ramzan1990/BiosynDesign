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
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
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

    public CDNAManager(PartsManager pm, MainWindow mainWindow){
        this.pm = pm;
        this.mainWindow = mainWindow;
    }



    public void editCDNA(Reaction r) {
        final JDialog frame = new JDialog(mainWindow, "Choose Enzyme", true);
        int wd = 800;
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));
        UI.addTo(jp, new JLabel("Sequence "));
        seqTA = new JTextArea();
        seqTA.setText(breakString(r.enzyme.sequence, 20));
        seqTA.setLineWrap(true);
        Font font = new Font(Font.MONOSPACED, Font.PLAIN, 22);
        Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
        attributes.put(TextAttribute.TRACKING, 1.20);
        Font font2 = font.deriveFont(attributes);
        seqTA.setFont(font2);
        seqTA.setEditable(false);
        //seqTA.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JScrollPane sc = new JScrollPane(seqTA);
        JLabel columnheader = new JLabel() {

            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                double step = 39.5;
                for (int i = 0; i < 20; i++) {
                    int v = (int)Math.round(i*step);
                    g.drawLine(v, 0, v, 3);
                    g.drawString("" + (i+1), v, 16);
                }
            }

            public Dimension getPreferredSize() {
                return new Dimension((int) seqTA.getPreferredSize().getWidth(), 20);
            }
        };
        columnheader.setBackground(Color.white);
        columnheader.setOpaque(true);
        sc.setColumnHeaderView(columnheader);
        sc.setPreferredSize(new Dimension(wd, 200));
        jp.add(sc);
        UI.addTo(jp, new JLabel("cDNA "));
        cDNATA = new JTextArea();
        cDNATA.setText(breakString(r.cDNA, 60));
        cDNATA.setLineWrap(true);
        font = new Font(Font.MONOSPACED, Font.PLAIN, 21);
        attributes = new HashMap<TextAttribute, Object>();
        //attributes.put(TextAttribute.TRACKING, 0.038);
        font2 = font.deriveFont(attributes);
        cDNATA.setFont(font2);
        JButton b1 = new JButton("Comment");
        JPanel bp = new JPanel();
        bp.setLayout(new BoxLayout(bp, BoxLayout.X_AXIS));
        b1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    for (Comment c : r.comments) {
                        if (c.start <= cDNATA.getSelectionEnd() && cDNATA.getSelectionStart() <= c.end) {
                            JOptionPane.showMessageDialog(null, "Comments are not allowed to overlap!");
                            return;
                        }
                    }
                    Color c = showDialog();
                    cDNATA.getHighlighter().addHighlight(cDNATA.getSelectionStart(), cDNATA.getSelectionEnd(),
                            new DefaultHighlighter.DefaultHighlightPainter(c));
                    r.comments.add(new Comment(cDNATA.getSelectionStart(), cDNATA.getSelectionEnd(), newComment, c));
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        });
        JButton b2 = new JButton("Uncomment");
        b2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    for (Comment c : r.comments) {
                        if (cDNATA.getCaretPosition() > c.start && cDNATA.getCaretPosition() < c.end) {
                            for (Highlighter.Highlight h : cDNATA.getHighlighter().getHighlights()) {
                                if (h.getStartOffset() == c.start && h.getEndOffset() == c.end) {
                                    cDNATA.getHighlighter().removeHighlight(h);
                                    break;
                                }
                            }
                            r.comments.remove(c);
                            disableCom();
                            break;
                        }
                    }
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        });
        count = new JLabel();
        bp.add(b1);
        bp.add(Box.createRigidArea(new Dimension(10, 0)));
        bp.add(b2);
        bp.add(Box.createRigidArea(new Dimension(10, 0)));
        bp.add(count);
        for (Comment c : r.comments) {
            try {
                cDNATA.getHighlighter().addHighlight(c.start, c.end,
                        new DefaultHighlighter.DefaultHighlightPainter(c.c));
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }

        cDNATA.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                r.cDNA = cDNATA.getText();
                check(cDNATA);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                r.cDNA = cDNATA.getText();
                check(cDNATA);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                r.cDNA = cDNATA.getText();
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
        sc = new JScrollPane(cDNATA);
        columnheader = new JLabel() {

            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                double step = 13.1;
                for (int i = 0; i < 60; i++) {
                    int v = (int)Math.round(i*step);
                    g.drawLine(v, 0, v, 3);
                    if(i==0 || (i+1)%4==0) {
                        g.drawString("" + (i + 1), v, 16);
                    }
                }
            }

            public Dimension getPreferredSize() {
                return new Dimension((int) cDNATA.getPreferredSize().getWidth(), 20);
            }
        };
        columnheader.setBackground(Color.white);
        columnheader.setOpaque(true);
        sc.setColumnHeaderView(columnheader);
        sc.setPreferredSize(new Dimension(wd, 200));
        jp.add(sc);
        UI.addTo(jp, bp);
        UI.addTo(jp, new JLabel("Comment "));
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
        sc = new JScrollPane(comTA);
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
                        seqTA.getHighlighter().removeAllHighlights();
                        int ep = 0;
                        if(endC % 3==1){
                            ep = 2;
                        }else if(endC % 3==2){
                            ep = 1;
                        }
                        int endS = change((endC + ep) / 3, 20, false);
                        int sp = 0;
                        if(startC % 3==1){
                            sp = 1;
                        }else if(startC % 3==2){
                            sp = 2;
                        }
                        int startS = change((startC - sp) / 3, 20, false);
                        seqTA.getHighlighter().addHighlight(startS, endS, new DefaultHighlighter.DefaultHighlightPainter(new Color(155, 225, 255)));
                        count.setText("Selected " + (endC - startC));
                    } catch (Exception ex) {

                    }
                }else{
                    seqTA.getHighlighter().removeAllHighlights();
                    count.setText("");
                }
                for (Comment c : r.comments) {
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

    boolean updatecDNATA = true;
    int caretPosition;
    private void check(JTextArea cDNATA) {
        if(!updatecDNATA){
            return;
        }
        updatecDNATA = false;
        String text = cDNATA.getText().replaceAll("\\s","");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                caretPosition = cDNATA.getCaretPosition();
                cDNATA.setText(breakString(text, 60));
                try {
                    cDNATA.setCaretPosition(caretPosition);
                }catch (Exception e){

                }
                updatecDNATA = true;
            }
        });
    }

    private String breakString(String sequence, int c) {
        sequence = sequence.replaceAll("\\s","");
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
            return n - (n /  (i+1));
        } else {
            return n + (n / i);
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
}
