package pekase3.panels.imagepanel.spritesheetpanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SpriteSheetMouseAdapter extends MouseAdapter {
    private SpriteSheetModel model;
    private SpriteSheetPanel spriteSheetPanel;
    
    private int offsetX;
    private int offsetY;
    
    // Constructor is package private because this class should only be used with SpriteSheetPanel.
    SpriteSheetMouseAdapter(SpriteSheetModel spm, SpriteSheetPanel sp) {
        this.model = spm;
        
        this.spriteSheetPanel = sp;
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        
        spriteSheetPanel.getFrameEditMode().mousePressed(e);
        spriteSheetPanel.repaint();
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        
        spriteSheetPanel.getFrameEditMode().mouseReleased(e);
        spriteSheetPanel.repaint();
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        
        spriteSheetPanel.getFrameEditMode().mouseDragged(e);
        spriteSheetPanel.repaint();
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        
        spriteSheetPanel.getFrameEditMode().mouseMoved(e);
        spriteSheetPanel.repaint();
    }
}
