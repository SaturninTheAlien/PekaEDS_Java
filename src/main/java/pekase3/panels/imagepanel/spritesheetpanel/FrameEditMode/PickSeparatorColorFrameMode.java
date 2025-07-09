package pekase3.panels.imagepanel.spritesheetpanel.FrameEditMode;

import javax.swing.*;

import pekase3.panels.imagepanel.spritesheetpanel.SpriteSheetPanel;

import java.awt.*;
import java.awt.event.MouseEvent;

public class PickSeparatorColorFrameMode extends FrameEditMode {
    @Override
    public void drawBehindSpriteSheet(Graphics2D g) {
    
    }
    
    @Override
    public void drawInFrontSpriteSheet(Graphics2D g) {
        if (model.getSeparatorColorPreview() != null) {
            g.setColor(model.getSeparatorColorPreview());
            g.fillRect(model.getMousePosition().x + 8, model.getMousePosition().y + 16, 16, 16);
            
            g.setColor(Color.black);
            g.drawRect(model.getMousePosition().x + 8, model.getMousePosition().y + 16, 16, 16);
        }
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            getModel().setSeparatorColor(getModel().getSeparatorColorPreview());
            
            getSheetPanel().setEditMode(MoveOrResizeMode.class);
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
    
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
    
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        if (e.getX() >= 0 && e.getX() < getModel().getImage().getWidth() && e.getY() >= 0 && e.getY() < getModel().getImage().getHeight()) {
            getModel().setMousePosition(e.getPoint());
            
            var color = getModel().getImage().getRGB(e.getX(), e.getY());
            if (color == getModel().getImage().getColorModel().getRGB(255)) {
                color = SpriteSheetPanel.COLOR_TRANSPARENT;
            }
            
            getModel().setSeparatorColorPreview(color);
            
            getSheetPanel().repaint();
        }
    }
}
