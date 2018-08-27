package biosyndesign.core.managers;

import java.awt.*;
import javax.swing.*;

public class JFrameExample implements Runnable
{
    public static void main(String[] args)
    {
        JFrameExample example = new JFrameExample();
        // schedule this for the event dispatch thread (edt)
        SwingUtilities.invokeLater(example);
    }

    public void run()
    {
        JFrame frame = new JFrame("My JFrame Example");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 600));
        JTextArea ta = new JTextArea();
        ta.setText("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        ta.setLineWrap(true);
        //ta.setEditable(false);
        JScrollPane tas = new JScrollPane(ta);
        tas.setPreferredSize(new Dimension(400, 200));
        tas.setMaximumSize(new Dimension(400, 200));
        tas.setBackground(Color.WHITE);
        frame.add(tas);
        frame.pack();
        frame.setVisible(true);
    }
}