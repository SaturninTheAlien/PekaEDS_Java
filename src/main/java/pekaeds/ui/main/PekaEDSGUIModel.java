package pekaeds.ui.main;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pekaeds.pk2.map.PK2Map;
import pekaeds.ui.listeners.PK2MapConsumer;
import pekaeds.ui.listeners.RepaintListener;

public class PekaEDSGUIModel {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    private PK2Map currentMap;
    private File currentMapFile = null;
    
    private int currentLayer;
    private int currentMode;
    
    private final List<PK2MapConsumer> mapConsumers = new ArrayList<>();
    private final List<RepaintListener> repaintListeners = new ArrayList<>();
    
    public PK2Map getCurrentMap() {
        return currentMap;
    }
    
    public void setCurrentMap(PK2Map map) {
        this.currentMap = map;
    }
    
    public void setCurrentMapFile(File file) { this.currentMapFile = file; }
    public File getCurrentMapFile() {
        return currentMapFile;
    }
    
    List<PK2MapConsumer> getMapConsumers() {
        return mapConsumers;
    }
    
    public void addMapConsumer(PK2MapConsumer mapHolder) {
        if (!mapConsumers.contains(mapHolder)) mapConsumers.add(mapHolder);
    }
    
    public void removeMapConsumer(PK2MapConsumer mapHolder) {
        mapConsumers.remove(mapHolder);
    }
    
    public void addRepaintListener(RepaintListener r) {
        if (!repaintListeners.contains(r)) repaintListeners.add(r);
    }
    
    public List<RepaintListener> getRepaintListeners() {
        return repaintListeners;
    }
    
    public void setCurrentLayer(int layer) {
        pcs.firePropertyChange("layer", currentLayer, layer);
        
        this.currentLayer = layer;
    }
    
    public int getCurrentLayer() {
        return currentLayer;
    }
    
    public void setCurrentMode(int mode) {
        pcs.firePropertyChange("currentMode", currentMode, mode);
        
        this.currentMode = mode;
    }
    
    public int getCurrentMode() {
        return currentMode;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
}
