package pekaep.ui;

import java.io.File;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

public class LevelListPage extends JPanel {

    private DefaultListModel<String> levelListModel;
    private JList<String> jLevelList;

    public LevelListPage(){
        JLabel title = new JLabel("<html> <span style=\"font-weight:bold; font-size: 25px\"> Found Levels </span> </html>");
        this.setLayout(new MigLayout("fill", "[grow]", "[pref!][grow]"));        
        this.add(title, "cell 0 0");

        this.levelListModel = new DefaultListModel<>();        
        this.jLevelList = new JList<>(levelListModel);

        JScrollPane levelListScrollPane = new JScrollPane(this.jLevelList);
        //levelListScrollPane.setPreferredSize(preferredListSize);


        this.add(levelListScrollPane, "cell 0 1, grow, push");        
    }

    public void update(List<File> levelFiles){
        this.levelListModel.clear();
        for(File levelFile: levelFiles){
            this.levelListModel.addElement(levelFile.getName());
        }
    }    
}
