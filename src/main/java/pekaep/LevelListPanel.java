package pekaep;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JPanel;

public class LevelListPanel extends JPanel {

    private ArrayList<File> levelFiles;
    public LevelListPanel(ArrayList<File> levelFiles){

        

        this.levelFiles = levelFiles;
    }


    public ArrayList<File> getLevelFiles(){
        return this.levelFiles;
    }
}
