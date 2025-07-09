package pekaeds.ui.main;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pekaeds.ui.listeners.PK2MapConsumer;
import pekaeds.ui.listeners.PK2SectorConsumer;
import pekaeds.ui.listeners.RepaintListener;
import pk2.filesystem.PK2FileSystem;
import pk2.level.PK2Level;
import pk2.level.PK2LevelSector;

public class PekaEDSGUIModel {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    private PK2Level currentLevel;
    private PK2LevelSector currentSector;

    private File currentMapFile = null;
    
    private int currentLayer;
    private int currentMode;
    
    private final List<PK2MapConsumer> mapConsumers = new ArrayList<>();
    private final List<PK2SectorConsumer> sectorConsumers = new ArrayList<>();
    private final List<RepaintListener> repaintListeners = new ArrayList<>();
    
    public PK2Level getCurrentLevel() {
        return this.currentLevel;
    }

    public PK2LevelSector getCurrentLevelSector(){
        return this.currentSector;
    }

    public void setCurrentMap(PK2Level level) {
        this.currentLevel = level;
        if(level.sectors.size()>0){
            this.currentSector = level.sectors.get(0);
        }
    }
    
    public void setCurrentMapFile(File file)
    {
        this.currentMapFile = file;
        if(file!=null){
            PK2FileSystem.setEpisodeDir(file.getParentFile());
        }
        
    }
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


    public void addSectorConsumer(PK2SectorConsumer sectorHolder){
        if (!sectorConsumers.contains(sectorHolder)) sectorConsumers.add(sectorHolder);
    }

    List<PK2SectorConsumer> getSectorConsumers(){
        return sectorConsumers;
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
