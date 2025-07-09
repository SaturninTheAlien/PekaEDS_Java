package pekase3.panels.imagepanel.spritesheetpanel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pekase3.panels.imagepanel.spritesheetpanel.FrameEditMode.FrameEditMode;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public final class SpriteSheetModel {
    private final ChangeEvent changeEvent = new ChangeEvent(this);
    
    private Rectangle currentFrameData = new Rectangle(-1, -1, 0, 0);
    private Rectangle lastFrameData = new Rectangle();
    
    private int framesAmount;
    
    private Color separatorColor;
    private Color separatorColorPreview = new Color(0);
    
    private List<ChangeListener> listeners = new ArrayList<>();
    
    private Class<? extends FrameEditMode> mode;
    private Class<? extends FrameEditMode> lastMode;
    
    private boolean lockToFrame = true;
    
    private BufferedImage image;
    
    private Point mousePosition;
    private Point clickPosition;
    
    private Rectangle detectedFrame = new Rectangle(-1, -1, 0, 0);
    private Point firstClick;
    
    public void addListener(ChangeListener listener) {
        listeners.add(listener);
    }
    
    public void setFramePosition(int x, int y) {
        currentFrameData.setLocation(x, y);
    
        updateListeners();
    }
    
    public void setFrameDimension(int width, int height) {
        currentFrameData.setSize(width, height);
        
        updateListeners();
    }
    
    public int getFramesAmount() {
        return framesAmount;
    }
    
    public void setFramesAmount(int frames) {
        this.framesAmount = frames;
        
        updateListeners();
    }
    
    /**
     * Returns the frames data as a rectangle.
     *
     * Do NOT modify the frame's data by calling getCurrentFrameData().x,y,width,height. Only use this to read the frames' data.
     *
     * Use the setFrame... methods, because those update the listeners, which is important and the application won't function correctly if the listeners don't get updated.
     * @return
     */
    public Rectangle getCurrentFrameData() {
        return currentFrameData;
    }
    
    public Rectangle getLastFrameData() {
        return lastFrameData;
    }
    
    public void setFrameData(Rectangle frameData) {
        currentFrameData = frameData;
        
        updateListeners();
    }
    
    public void setSeparatorColor(Color color) {
        this.separatorColor = color;
        
        updateListeners();
    }
    
    public Color getSeparatorColor() {
        return separatorColor;
    }
    
    public void setLockToFrame(boolean lock) {
        this.lockToFrame = lock;
    }
    
    public boolean lockToFrame() {
        return lockToFrame;
    }
    
    public void setEditMode(Class<? extends FrameEditMode> mode) {
        lastMode = this.mode;
        
        this.mode = mode;
        
        updateListeners();
    }
    
    public void setImage(BufferedImage image) {
        this.image = image;
    }
    
    public BufferedImage getImage() {
        return image;
    }
    
    Class<? extends FrameEditMode> getEditMode() {
        return mode;
    }
    
    public void setMousePosition(Point pos) {
        this.mousePosition = pos;
    }
    
    public Point getMousePosition() {
        return mousePosition;
    }
    
    public void setSeparatorColorPreview(int col) {
        if (col != SpriteSheetPanel.COLOR_TRANSPARENT) {
            separatorColorPreview = new Color(col);
        } else {
            separatorColorPreview = null;
        }
        
        updateListeners();
    }
    
    public Color getSeparatorColorPreview() {
        return separatorColorPreview;
    }
    
    private void updateListeners() {
        for (var l : listeners) {
            l.stateChanged(changeEvent);
        }
    }
    
    public void setDetectedFrame(int startX, int startY, int width, int height) {
        detectedFrame.setBounds(startX, startY, width, height);
    }
    
    public Rectangle getDetectedFrame() {
        return detectedFrame;
    }
    
    public Class<? extends FrameEditMode> getLastMode() {
        return lastMode;
    }
    
    public Point getFirstClick() {
        return firstClick;
    }

    public void setFirstClick(Point p) {
        this.firstClick = p;
    }
    
    public void setClickPosition(Point p) {
        this.clickPosition = p;
    }
    
    public Point getClickPosition() {
        return clickPosition;
    }
}
