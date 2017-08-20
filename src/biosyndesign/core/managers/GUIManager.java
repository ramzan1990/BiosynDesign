package biosyndesign.core.managers;


import javax.swing.*;

public class GUIManager {
    private ProjectState s;

    public GUIManager(ProjectState s) {
        this.s = s;
    }

    public void chooseRepository() {
        s.prefix = JOptionPane.showInputDialog(null, "Enter repository URL:", s.prefix);
    }

    public String getOrganism() {
        return s.organism;
    }
}
