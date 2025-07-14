package pekaeds.ui.actions;

import javax.swing.*;

import pekaeds.ui.main.PekaEDSGUI;

import java.awt.event.ActionEvent;



public class PlayLevelAction extends AbstractAction {
    private PekaEDSGUI gui;    
    public PlayLevelAction(PekaEDSGUI ui) {
        this.gui = ui;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        this.gui.testLevel();
    }
}
