package pekase3.panels.imagepanel.spritesheetpanel.FrameEditMode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class DrawFrameMode extends FrameEditMode {
    @Override
    public void drawBehindSpriteSheet(Graphics2D g) {
    
    }
    
    @Override
    public void drawInFrontSpriteSheet(Graphics2D g) {
        g.setColor(Color.black);
        g.drawRect(model.getCurrentFrameData().x, model.getCurrentFrameData().y, model.getCurrentFrameData().width, model.getCurrentFrameData().height);
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            getModel().setClickPosition(e.getPoint());
            
            getModel().setFramePosition(e.getX(), e.getY());
            getModel().setFrameDimension(1, 1);
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            getSheetPanel().setEditMode(MoveOrResizeMode.class);
        }
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            int startX = getModel().getClickPosition().x;
            int startY = getModel().getClickPosition().y;
            
            int endX = e.getX();
            int endY = e.getY();
            
            if (startX > endX) {
                endX = getModel().getClickPosition().x;
                startX = e.getX();
                
                getModel().setFramePosition(startX, getModel().getCurrentFrameData().y);
            }
            
            if (startY > endY) {
                endY = getModel().getClickPosition().y;
                startY = e.getY();
                
                getModel().setFramePosition(getModel().getCurrentFrameData().x, startY);
            }
            
            int width = startX > endX ? startX - endX : endX - startX;
            int height = startY > endY ? startY - endY : endY - startY;
            getModel().setFrameDimension(width, height);
            
            getSheetPanel().repaint();
        }
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
    
    }
}
