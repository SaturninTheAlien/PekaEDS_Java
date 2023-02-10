package pk.pekaeds.ui.actions;

import pk.pekaeds.ui.main.PekaEDSGUI;

import javax.swing.*;
import java.awt.event.ActionEvent;

// TODO Fix: This doesn't work with JCombobox, because the layer is passed in the constructor(?)
public class SwitchLayerAction extends AbstractAction {
    private PekaEDSGUI gui;
    private int newLayer;
    
    public SwitchLayerAction(PekaEDSGUI ui, int layer) {
        this.gui = ui;
        
        this.newLayer = layer;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        gui.setLayer(newLayer);
    }
}
