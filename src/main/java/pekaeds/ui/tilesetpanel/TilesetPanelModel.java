package pekaeds.ui.tilesetpanel;

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import pekaeds.pk2.level.PK2LevelSector;

// TODO This can probably be deleted
public class TilesetPanelModel {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    private BufferedImage tilesetImage;
    
    private PK2LevelSector map;
    
    private int[][] selection;
    
    public void setSelection(int[][] sel) {
        pcs.firePropertyChange("tileSelection", this.selection, sel);
        
        this.selection = sel;
    }
    
    public void setMap(PK2LevelSector m) {
        this.map = m;
    }
    
    public PK2LevelSector getMap() {
        return map;
    }
    
    public int[][] getSelection() {
        return selection;
    }
    
    public void setTilesetImage(BufferedImage image) {
        this.tilesetImage = image;
    }
    
    BufferedImage getTilesetImage() {
        return tilesetImage;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
}
