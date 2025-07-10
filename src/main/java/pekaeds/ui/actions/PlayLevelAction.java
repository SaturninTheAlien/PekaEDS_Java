package pekaeds.ui.actions;

import java.io.File;
import javax.swing.*;

import pekaeds.ui.main.PekaEDSGUI;
import pekaeds.ui.misc.UnsavedChangesDialog;
import pk2.util.LevelTestingUtil;

import java.awt.event.ActionEvent;



public class PlayLevelAction extends AbstractAction {
    private PekaEDSGUI gui;    
    public PlayLevelAction(PekaEDSGUI ui) {
        this.gui = ui;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {

        if (gui.unsavedChangesPresent()) {
            int result = UnsavedChangesDialog.show(gui);
            
            if (result != JOptionPane.CANCEL_OPTION && result != JOptionPane.CLOSED_OPTION) {
                playLevel();
            }
        } else {
            playLevel();
        }
    }


    private void playLevel(){
        File levelFile = gui.getCurrentFile();
        LevelTestingUtil.playLevel(levelFile);
    }
}
