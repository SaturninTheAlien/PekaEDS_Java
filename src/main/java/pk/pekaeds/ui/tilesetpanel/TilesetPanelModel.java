package pk.pekaeds.ui.tilesetpanel;

import pk.pekaeds.pk2.map.PK2Map;

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

// TODO This can probably be deleted
public class TilesetPanelModel {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    private BufferedImage tilesetImage;
    
    private PK2Map map;
    
    private int[][] selection;
    
    public void setSelection(int[][] sel) {
        pcs.firePropertyChange("tileSelection", this.selection, sel);
        
        this.selection = sel;
    }
    
    public void setMap(PK2Map m) {
        this.map = m;
    }
    
    public PK2Map getMap() {
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
