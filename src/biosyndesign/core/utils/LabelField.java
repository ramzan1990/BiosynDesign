package biosyndesign.core.utils;

import javax.swing.*;
import java.awt.*;

public class LabelField extends JPanel {

    public JTextField field;

    public LabelField(String label){
        super();
        init(label);
    }

    public LabelField(String label, String s) {
        super();
        init(label);
        field.setText(s);
    }

    private void init(String label) {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel1.add(new JLabel(label));
        this.add(panel1);
        JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        field = new JTextField();
        field.setPreferredSize(new Dimension(300, field.getPreferredSize().height));
        field.setMaximumSize(new Dimension(300, field.getPreferredSize().height));
        field.setMinimumSize(new Dimension(300, field.getPreferredSize().height));
        panel2.add(field);
        this.add(panel2);
    }
}
