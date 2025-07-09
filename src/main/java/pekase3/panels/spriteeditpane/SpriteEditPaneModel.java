package pekase3.panels.spriteeditpane;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import pk2.sprite.PK2Sprite;
import pk2.util.GFXUtils;

public final class SpriteEditPaneModel {
    private List<SpriteFramesChangeListener> spriteFramesListeners = new ArrayList<>();
    
    private PK2Sprite sprite = new PK2Sprite();
    
    private boolean unsavedChanges = false;
    
    public void setSprite(PK2Sprite spr) {
        this.sprite = spr;
    }
    
    public PK2Sprite getSprite() {
        return sprite;
    }
    
    public void setUnsavedChanges(boolean changesPresent) {
        this.unsavedChanges = changesPresent;
    }
    
    public boolean unsavedChangesPresent() {
        return unsavedChanges;
    }
    
    public void setSpriteImage(BufferedImage image) {
        sprite.setImage(image);
        
        sprite.setFramesList(GFXUtils.cutFrames(image, sprite.getFramesAmount(), sprite.getFrameX(), sprite.getFrameY(), sprite.getFrameWidth(), sprite.getFrameHeight()));
        
        updateFrames();
    }
    
    public void setSpriteFrames(List<BufferedImage> frames) {
        sprite.setFramesList(frames);
        
        updateFrames();
    }
    
    private void updateFrames() {
        for (var l : spriteFramesListeners) {
            l.framesChanged(sprite.getFramesList());
        }
    }
    
    public void addSpriteFramesChangeListener(SpriteFramesChangeListener listener) {
        spriteFramesListeners.add(listener);
    }
}
