package pekase3.panels.imagepanel.spritesheetpanel.FrameEditMode;

import javax.swing.*;

import pekase3.panels.imagepanel.spritesheetpanel.FrameGFXUtils;

import java.awt.*;
import java.awt.event.MouseEvent;

public class DetectFrameMode extends FrameEditMode {
    @Override
    public void drawBehindSpriteSheet(Graphics2D g) {
    
    }
    
    @Override
    public void drawInFrontSpriteSheet(Graphics2D g) {
        if (model.getDetectedFrame().width > 0 && model.getDetectedFrame().height > 0) {
            g.setStroke(FrameGFXUtils.DEFAULT_STROKE);
            g.setColor(Color.black);
            
            g.drawRect(model.getDetectedFrame().x - 1, model.getDetectedFrame().y - 1, model.getDetectedFrame().width + 1, model.getDetectedFrame().height + 1);
        }
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            getModel().setFrameData(getModel().getDetectedFrame());
            
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
        detectFrame(e.getPoint());
    }
    
    private int lastColor;
    void detectFrame(Point pos) {
        if (pos.x < getModel().getImage().getWidth() && pos.y < getModel().getImage().getHeight()) {
            if (getModel().getImage().getRGB(pos.x, pos.y) != lastColor && getModel().getSeparatorColor() != null) {
                int startX = 0;
                int startY = 0;
                
                int width = 0;
                int height = 0;
                
                for (int x = pos.x; x > 1; x--) {
                    if (getModel().getImage().getRGB(x, pos.y) == getModel().getSeparatorColor().getRGB() &&
                            getModel().getImage().getRGB(x - 1, pos.y) == getModel().getImage().getColorModel().getRGB(255)) {
                        startX = x;
                        
                        break;
                    }
                }
                
                for (int x = pos.x; x < getModel().getImage().getWidth(); x++) {
                    if (getModel().getImage().getRGB(x, pos.y) == getModel().getSeparatorColor().getRGB() &&
                            getModel().getImage().getRGB(x + 1, pos.y) == getModel().getImage().getColorModel().getRGB(255)) {
                        width = x - startX;
                        
                        break;
                    }
                }
                
                for (int y = pos.y; y > 1; y--) {
                    if (getModel().getImage().getRGB(pos.x, y) == getModel().getSeparatorColor().getRGB() &&
                            getModel().getImage().getRGB(pos.x, y - 1) == getModel().getImage().getColorModel().getRGB(255)) {
                        startY = y;
                        
                        break;
                    }
                }
                
                for (int y = pos.y; y < getModel().getImage().getHeight(); y++) {
                    if (getModel().getImage().getRGB(pos.x, y) == getModel().getSeparatorColor().getRGB() &&
                            getModel().getImage().getRGB(pos.x, y + 1) == getModel().getImage().getColorModel().getRGB(255)) {
                        height = y - startY;
                        
                        break;
                    }
                }
                
                /*
                    Most sprites have a gap of 3 pixels between them. Border, empty, border. For a better user experience these gaps will be ignored.
                    
                    We also subtract 1 from width and height and add 1 to startX/startY to account for the separating frame around the actual sprite frame.
                 */
                if (width > 3 && height > 3) getModel().setDetectedFrame(startX + 1, startY + 1, width - 1, height - 1);
                
                lastColor = getModel().getImage().getRGB(pos.x, pos.y);
                
                getSheetPanel().repaint();
            }
        }
    }
}
