package biosyndesign.core.managers;


import javax.swing.*;

public class GUIManager {
    private ProjectState s;
    private PartsManager pm;

    public GUIManager(ProjectState s, PartsManager pm) {
        this.s = s;
        this.pm = pm;
    }

    public void chooseRepository() {
        String p = JOptionPane.showInputDialog(null, "Enter repository URL:", s.prefix);
        if(p.length()>0){
            s.prefix = p;
            pm.setPrefix(p);
        }
    }

    public String getOrganism() {
        return s.organism;
    }

    public void useLocalRepo(boolean b) {
       pm.setRepo(b);
    }
}
