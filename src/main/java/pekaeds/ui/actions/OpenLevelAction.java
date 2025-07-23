package pekaeds.ui.actions;

import javax.swing.*;

import pekaeds.ui.filefilters.FileFilters;
import pekaeds.ui.main.PekaEDSGUI;
import pekaeds.ui.misc.UnsavedChangesDialog;
import pk2.filesystem.PK2FileSystem;

import java.awt.event.ActionEvent;
import java.io.File;

public class OpenLevelAction extends AbstractAction {
    private final PekaEDSGUI gui;
    
    public OpenLevelAction(PekaEDSGUI ui) {
        this.gui = ui;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {

        File startDir = null;
        if(PK2FileSystem.isEpisodeSet()){
            startDir = PK2FileSystem.getEpisodeDir();
        }
        else{
            startDir = PK2FileSystem.getAssetsPath(PK2FileSystem.EPISODES_DIR);
        }

        JFileChooser fc = new JFileChooser(startDir);
    
        fc.setFileFilter(FileFilters.PK2_MAP_FILTER);
        
        if (gui.unsavedChangesPresent()) {
            int result = UnsavedChangesDialog.show(gui);
    
            if (result != JOptionPane.CANCEL_OPTION && result != JOptionPane.CLOSED_OPTION) {
                showFileChooser(fc);
            }
        } else {
            showFileChooser(fc);
        }
    }
    
    private void showFileChooser(JFileChooser fc) {
        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            gui.loadMap(fc.getSelectedFile());
        }
    }
}
