package pekase3.panels.imagepanel.spritesheetpanel.FrameEditMode;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import pekase3.panels.imagepanel.spritesheetpanel.SpriteSheetModel;
import pekase3.panels.imagepanel.spritesheetpanel.SpriteSheetPanel;
import pekase3.panels.spriteeditpane.SpriteEditPane;
import pekase3.panels.spriteeditpane.SpriteEditPaneModel;

public abstract class FrameEditMode extends MouseAdapter {
    protected SpriteSheetModel model;
    private SpriteSheetPanel sheetPanel;
    
    public void setModel(SpriteSheetModel model) {
        this.model = model;
    }
    public void setSheetPanel(SpriteSheetPanel panel) { this.sheetPanel = panel; }
    
    public abstract void drawBehindSpriteSheet(Graphics2D g);
    public abstract void drawInFrontSpriteSheet(Graphics2D g);
    
    public abstract void mousePressed(MouseEvent e);
    public abstract void mouseReleased(MouseEvent e);
    
    public abstract void mouseDragged(MouseEvent e);
    public abstract void mouseMoved(MouseEvent e);
    
    protected final SpriteSheetModel getModel() {
        return model;
    }
    
    protected final SpriteSheetPanel getSheetPanel() {
        return sheetPanel;
    }
}
