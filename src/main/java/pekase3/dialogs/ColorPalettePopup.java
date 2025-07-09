package pekase3.dialogs;

import javax.swing.*;

import pekase3.panels.imagepanel.ColorPalettePanel;

import java.awt.*;
import java.awt.image.IndexColorModel;

public class ColorPalettePopup extends JDialog {
    private ColorPalettePanel colorPalettePanel;
    
    public ColorPalettePopup() {
        setup();
    }
    
    private void setup() {
        colorPalettePanel = new ColorPalettePanel();
        
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        setPreferredSize(new Dimension(256, 256));
        setMinimumSize(new Dimension(256, 256));
        setMaximumSize(new Dimension(256, 256));
        setUndecorated(true);
        getRootPane().setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        add(colorPalettePanel);
    }
    
    public void setColorPalette(IndexColorModel colorPalette) {
        colorPalettePanel.setPalette(colorPalette);
    }
}
