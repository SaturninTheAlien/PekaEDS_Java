package pekase3.panels.imagepanel.spritesheetpanel.FrameEditMode;

import javax.swing.*;

import pekase3.panels.imagepanel.spritesheetpanel.FrameGFXUtils;

import java.awt.*;
import java.awt.event.MouseEvent;

public class MoveOrResizeMode extends FrameEditMode {
    private int offsetX;
    private int offsetY;
    
    @Override
    public void drawBehindSpriteSheet(Graphics2D g2) {
        var frame = getModel().getCurrentFrameData();
        
        if (frame.x >= 0 && frame.y >= 0 && frame.x + frame.width <= getModel().getImage().getWidth() && frame.y + frame.height <= getModel().getImage().getHeight()) {
            // Draw the background behind the highlighted frame
            g2.setColor(FrameGFXUtils.colorFirstFrameBG);
            g2.fillRect(frame.x, frame.y, frame.width, frame.height);
            
            g2.setColor(FrameGFXUtils.colorFramesBG);
            int x = frame.x + frame.width + 3; // Start after the first frame
            int y = frame.y;
            int frameCount = 0;
            while (frameCount < getModel().getFramesAmount() - 1) { // - 1 because we start after the first frame
                if (x + frame.width > 640) {
                    y += frame.height + 3;
                    
                    x = 1;
                }
                
                //drawDashedFrameRect(g2, x, y, frame.width, frame.height);
                g2.fillRect(x, y, frame.width, frame.height);
                
                x += frame.width + 3;
                
                frameCount++;
            }
        }
    }
    
    @Override
    public void drawInFrontSpriteSheet(Graphics2D g2) {
        var frame = getModel().getCurrentFrameData();
        g2.setColor(Color.black);
        g2.drawRect(frame.x - 1, frame.y - 1, frame.width + 1, frame.height + 1);
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (getModel().getCurrentFrameData().contains(e.getPoint())) {
                offsetX = e.getX() - getModel().getCurrentFrameData().x;
                offsetY = e.getY() - getModel().getCurrentFrameData().y;
            } else {
                offsetX = -1;
                offsetY = -1;
            }
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
    
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (getSheetPanel().getCursor().getType() == Cursor.getDefaultCursor().getType()) {
                if (offsetX > -1 && offsetY > -1) {
                    moveFrame(e.getPoint());
                }
            } else {
                switch (getSheetPanel().getCursor().getType()) {
                    case Cursor.SE_RESIZE_CURSOR -> {
                        int width = e.getX() - getModel().getCurrentFrameData().x;
                        int height = e.getY() - getModel().getCurrentFrameData().y;
                        
                        if (width > 0 && height > 0) {
                            getModel().setFrameDimension(width, height);
                        }
                    }
                    
                    case Cursor.SW_RESIZE_CURSOR -> {
                        int rightSide = getModel().getCurrentFrameData().x + getModel().getCurrentFrameData().width; // Calculate the position of the right side of the frame.
                        int width = rightSide - e.getX(); // Then subtract the current mouse x position to get the width.
                        
                        int bottomSide = getModel().getCurrentFrameData().y;
                        int height = e.getY() - bottomSide;
                        
                        if (width > 0 && height > 0) {
                            getModel().setFramePosition(e.getX(), getModel().getCurrentFrameData().y);
                            getModel().setFrameDimension(width, height);
                        }
                    }
                    
                    case Cursor.NW_RESIZE_CURSOR -> {
                        int rightSide = getModel().getCurrentFrameData().x + getModel().getCurrentFrameData().width; // Calculate the position of the right side of the frame.
                        int width = rightSide - e.getX(); // Then subtract the current mouse x position to get the width.
                        
                        int bottomSide = getModel().getCurrentFrameData().y + getModel().getCurrentFrameData().height;
                        int height = bottomSide - e.getY();
                        
                        if (width > 0 && height > 0) {
                            getModel().setFramePosition(e.getX(), e.getY());
                            getModel().setFrameDimension(width, height);
                        }
                    }
                    
                    case Cursor.NE_RESIZE_CURSOR -> {
                        int width = e.getX() - getModel().getCurrentFrameData().x;
                        int height = (getModel().getCurrentFrameData().y + getModel().getCurrentFrameData().height) - e.getY();
                        
                        if (width > 0 && height > 0) {
                            getModel().setFramePosition(getModel().getCurrentFrameData().x, e.getY());
                            getModel().setFrameDimension(width, height);
                        }
                    }
                }
            }
        }
        
        getSheetPanel().repaint();
    }
    
    private final int RESIZE_BOX_SIZE = 16;
    private final int RESIZE_BOX_SIZE_HALF = RESIZE_BOX_SIZE / 2;
    @Override
    public void mouseMoved(MouseEvent e) {
        var frame = getModel().getCurrentFrameData();
        var edgeNorthWest = new Rectangle(frame.x - RESIZE_BOX_SIZE_HALF, frame.y - RESIZE_BOX_SIZE_HALF, RESIZE_BOX_SIZE, RESIZE_BOX_SIZE);
        var edgeNorthEast = new Rectangle(frame.x + frame.width - RESIZE_BOX_SIZE_HALF, frame.y - RESIZE_BOX_SIZE_HALF, RESIZE_BOX_SIZE, RESIZE_BOX_SIZE);
        var edgeSouthWest = new Rectangle(frame.x - RESIZE_BOX_SIZE_HALF, frame.y + frame.height - RESIZE_BOX_SIZE_HALF, RESIZE_BOX_SIZE, RESIZE_BOX_SIZE);
        var edgeSouthEast = new Rectangle(frame.x - RESIZE_BOX_SIZE_HALF + frame.width, frame.y + frame.height - RESIZE_BOX_SIZE_HALF, RESIZE_BOX_SIZE, RESIZE_BOX_SIZE);
        
        if (edgeNorthEast.contains(e.getPoint())) {
            getSheetPanel().setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
        } else if (edgeNorthWest.contains(e.getPoint())) {
            getSheetPanel().setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
        } else if (edgeSouthEast.contains(e.getPoint())) {
            getSheetPanel().setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
        } else if (edgeSouthWest.contains(e.getPoint())) {
            getSheetPanel().setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
        } else {
            getSheetPanel().setCursor(Cursor.getDefaultCursor());
        }
    }
    
    private void moveFrame(Point point) {
        int posX = point.x;
        int posY = point.y;
        
        if (getModel().lockToFrame()) {
            posX /= getModel().getCurrentFrameData().width + 3;
            posX *= getModel().getCurrentFrameData().width + 3;
            
            posY /= getModel().getCurrentFrameData().height + 3;
            posY *= getModel().getCurrentFrameData().height + 3;
        } else {
            posX = point.x - offsetX;
            posY = point.y - offsetY;
        }
        
        if (posX < 0) {
            posX = 0;
        } else if (posX + getModel().getCurrentFrameData().width >= getModel().getImage().getWidth()) {
            posX = getModel().getImage().getWidth() - getModel().getCurrentFrameData().width;
        }
        
        if (posY < 0) {
            posY = 0;
        } else if (posY + getModel().getCurrentFrameData().height >= getModel().getImage().getHeight()) {
            posY = getModel().getImage().getHeight() - getModel().getCurrentFrameData().height;
        }
        
        getModel().setFramePosition(posX + 1, posY + 1);
    }
}
