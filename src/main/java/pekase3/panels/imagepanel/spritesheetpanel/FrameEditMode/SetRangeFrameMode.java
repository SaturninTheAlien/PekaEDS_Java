package pekase3.panels.imagepanel.spritesheetpanel.FrameEditMode;

import javax.swing.*;

import pekase3.panels.imagepanel.spritesheetpanel.FrameGFXUtils;

import java.awt.*;
import java.awt.event.MouseEvent;

public class SetRangeFrameMode extends FrameEditMode {
    @Override
    public void drawBehindSpriteSheet(Graphics2D g) {
    
    }
    
    @Override
    public void drawInFrontSpriteSheet(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.setStroke(FrameGFXUtils.DEFAULT_STROKE);
        if (model.getFirstClick() != null) g.drawRect(model.getFirstClick().x, model.getFirstClick().y, model.getCurrentFrameData().width + 1, model.getCurrentFrameData().height + 1);
        
        //FrameGFXUtils.drawDashedFrameRect(g, model.getCurrentFrameData().x, model.getCurrentFrameData().y, model.getCurrentFrameData().width, model.getCurrentFrameData().height);
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            int gridX = getModel().getCurrentFrameData().width + 3;
            int gridY = getModel().getCurrentFrameData().height + 3;
            
            if (getModel().getFirstClick() == null) {
                getModel().setFirstClick(new Point((e.getX() / gridX) * gridX, (e.getY() / gridY) * gridY));
            } else {
                int frameAmount = 0;
                
                // First click position adjusted to lock to the frames
                int xPos = getModel().getFirstClick().x;
                int yPos = getModel().getFirstClick().y;
                
                // Last click position, again adjusted
                int xPosLast = ((e.getX() + gridX) / gridX) * gridX;
                int yPosLast = (e.getY() / gridY) * gridY;
                int columns = (yPosLast - yPos) / gridY;
                
                // Calculate the first row
                int framesFirstRow = (xPosLast - xPos) / gridX; // If there's only one column use the first and last click position to calculate how many frames there are.
                if (columns > 0) { // If there's more than one column start at the click position and count to the end of the image's width.
                    framesFirstRow = (getModel().getImage().getWidth() - xPos) / gridX;
                }
                
                frameAmount = framesFirstRow;
                
                // Calculate the last row
                int framesLastRow = xPosLast / gridX;
                if (columns > 0) {
                    frameAmount += framesLastRow;
                }
                
                // Calculate the rows between the first and the last one
                int maxFramesPerRow = getModel().getImage().getWidth() / gridX;
                for (int i = 0; i < columns - 1; i++) { // Disregard the last column, because that one has already been calculated.
                    frameAmount += maxFramesPerRow;
                }

                getModel().setFramePosition(xPos + 1, yPos + 1); // Plus 1 to account for the frame that's around the sprite frame.
                getModel().setFramesAmount(frameAmount);
                
                getModel().setFirstClick(null);
                getSheetPanel().setEditMode(MoveOrResizeMode.class);
            }
        }
        
        getSheetPanel().repaint();
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
    
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
    
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
    
    }
}
