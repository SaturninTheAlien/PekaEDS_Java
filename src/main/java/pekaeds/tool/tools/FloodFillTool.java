package pekaeds.tool.tools;

import javax.swing.*;

import pekaeds.data.Layer;
import pekaeds.tool.Tool;
import pk2.level.PK2TileArray;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

// TODO Optimize this class
public final class FloodFillTool extends Tool {
    // https://lodev.org/cgtutor/floodfill.html#Scanline_Floodfill_Algorithm_With_Stack

    private final List<PK2TileArray> previewLayers = new ArrayList<>();
    
    private boolean fillEmptyTiles = false;
    
    @Override
    public void mouseMoved(MouseEvent e) {
        int mx = e.getX() / 32;
        int my = e.getY() / 32;
        int layer = selectedLayer == Layer.BOTH ? Layer.FOREGROUND : selectedLayer;

        resetPreview();
        fillPreview(mx, my, selection.getFirstTile(), previewLayers.get(layer).get(mx, my));
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        
        if (SwingUtilities.isLeftMouseButton(e)) {
            SwingUtilities.invokeLater(() -> {
                getUndoManager().startBlock();
                
                fill(e.getX() / 32, e.getY() / 32, selection.getFirstTile(), layerHandler.getTileAt(selectedLayer, e.getX() / 32, e.getY() / 32));
                
                getUndoManager().endBlock();
            });
        }
    }
    
    @Override
    public void draw(Graphics2D g) {
        if (!previewLayers.isEmpty()) {
            for (int y = (viewRect.y / 32); y < ((viewRect.y + viewRect.height) / 32) + 1; y++) {
                for (int x = viewRect.x / 32; x < ((viewRect.x + viewRect.width) / 32) + 1; x++) {
                    if (x < selectedSector.getWidth() && y < selectedSector.getHeight()) {
                        switch (selectedLayer) {
                            case Layer.BOTH, Layer.FOREGROUND -> {
                                if (previewLayers.get(Layer.FOREGROUND).get(x,y) != 255) getMapPanelPainter().drawTile(g, x * 32, y * 32, previewLayers.get(Layer.FOREGROUND).get(x,y));
                            }
        
                            case Layer.BACKGROUND -> {
                                if (previewLayers.get(Layer.BACKGROUND).get(x,y) != 255) getMapPanelPainter().drawTile(g, x * 32, y * 32, previewLayers.get(Layer.BACKGROUND).get(x,y));
                            }
                        }
                    }
                }
            }
        }
    }
    
    // TODO Optimize Undo/Redo is way too slow.
    //https://lodev.org/cgtutor/floodfill.html#Recursive_Scanline_Floodfill_Algorithm
    private void fill(int x, int y, int newTile, int oldTile) {
        if (oldTile == newTile) return;
        if (layerHandler.getTileAt(selectedLayer, x, y) != oldTile) return;

        int x1 = x;
        
        while (x1 < selectedSector.getWidth() && layerHandler.getTileAt(selectedLayer, x1, y) == oldTile) {
            getUndoManager().pushTilePlaced(this, x1 * 32, y * 32, newTile, layerHandler.getTileAt(selectedLayer, x1, y), selectedLayer);
            
            layerHandler.placeTileScreen(x1 * 32, y * 32, newTile, selectedLayer);
            
            x1++;
        }
        
        x1 = x - 1;
        while (x1 >= 0 && layerHandler.getTileAt(selectedLayer, x1, y) == oldTile) {
            getUndoManager().pushTilePlaced(this, x1 * 32, y * 32, newTile, layerHandler.getTileAt(selectedLayer, x1, y), selectedLayer);
    
            layerHandler.placeTileScreen(x1 * 32, y * 32, newTile, selectedLayer);
            
            x1--;
        }
        
        // test for scanlines above
        x1 = x;
        
        while (x1 < selectedSector.getWidth() && layerHandler.getTileAt(selectedLayer, x1, y) == newTile) {
            if (y > 0 && layerHandler.getTileAt(selectedLayer, x1, y - 1) == oldTile) {
                fill(x1, y - 1, newTile, oldTile);
            }
            
            x1++;
        }
        
        x1 = x - 1;
        while (x1 >= 0 && layerHandler.getTileAt(selectedLayer, x1, y) == newTile) {
            if (y > 0 && layerHandler.getTileAt(selectedLayer, x1, y - 1) == oldTile) {
                fill(x1, y - 1, newTile, oldTile);
            }
            
            x1--;
        }
        
        // test for new scanlines below
        x1 = x;
        while (x1 < selectedSector.getWidth() && layerHandler.getTileAt(selectedLayer, x1, y) == newTile) {
            if (y < selectedSector.getHeight() - 1 && layerHandler.getTileAt(selectedLayer, x1, y + 1) == oldTile) {
                fill(x1, y + 1, newTile, oldTile);
            }
            
            x1++;
        }
        
        x1 = x - 1;
        while (x1 >= 0 && layerHandler.getTileAt(selectedLayer, x1, y) == newTile) {
            if (y < selectedSector.getHeight() - 1 && layerHandler.getTileAt(selectedLayer, x1, y + 1) == oldTile) {
                fill(x1, y + 1, newTile, oldTile);
            }
            
            x1--;
        }
    }
    
    private void fillPreview(int x, int y, int newTile, int oldTile) {
        int layer = selectedLayer == Layer.BOTH ? Layer.FOREGROUND : selectedLayer;
        
        if (oldTile == newTile) return;
        if (previewLayers.get(layer).get(x,y) != oldTile) return;
        if (!fillEmptyTiles && oldTile == 255) return;
        
        int x1 = x;
        
        while (x1 < selectedSector.getWidth() && previewLayers.get(layer).get(x1,y) == oldTile) {
            previewLayers.get(layer).set(x1,y, newTile);
            
            x1++;
        }
        
        x1 = x - 1;
        while (x1 >= 0 && previewLayers.get(layer).get(x1,y) == oldTile) {
            previewLayers.get(layer).set(x1,y, newTile);
            
            x1--;
        }
        
        // test for scanlines above
        x1 = x;
        
        while (x1 < selectedSector.getWidth() && previewLayers.get(layer).get(x1,y) == newTile) {
            if (y > 0 && previewLayers.get(layer).get(x1, y - 1) == oldTile) {
                fillPreview(x1, y - 1, newTile, oldTile);
            }
            
            x1++;
        }
        
        x1 = x - 1;
        while (x1 >= 0 && previewLayers.get(layer).get(x1,y) == newTile) {
            if (y > 0 && previewLayers.get(layer).get(x1, y - 1) == oldTile) {
                fillPreview(x1, y - 1, newTile, oldTile);
            }
            
            x1--;
        }
        
        // test for new scanlines below
        x1 = x;
        while (x1 < selectedSector.getWidth() && previewLayers.get(layer).get(x1,y) == newTile) {
            if (y < selectedSector.getHeight() - 1 && previewLayers.get(layer).get(x1, y + 1) == oldTile) {
                fillPreview(x1, y + 1, newTile, oldTile);
            }
            
            x1++;
        }
        
        x1 = x - 1;
        while (x1 >= 0 && previewLayers.get(layer).get(x1,y) == newTile) {
            if (y < selectedSector.getHeight() - 1 && previewLayers.get(layer).get(x1, y + 1)  == oldTile) {
                fillPreview(x1, y + 1, newTile, oldTile);
            }
            
            x1--;
        }
    }
    
    private void resetPreview() {
        for (int y = 0; y < selectedSector.getHeight(); y++) {
            for (int x = 0; x < selectedSector.getWidth(); x++) {
                previewLayers.get(Layer.FOREGROUND).set(x, y, selectedSector.getFgTile(x, y));
                previewLayers.get(Layer.BACKGROUND).set(x, y, selectedSector.getBgTile(x, y));
            }
        }
    }
    
    @Override
    public void onSelect() {
        if (previewLayers.isEmpty()) {
            previewLayers.add(new PK2TileArray(selectedSector.getWidth(), selectedSector.getHeight()));
            previewLayers.add(new PK2TileArray(selectedSector.getWidth(), selectedSector.getHeight()));
        }
        
        for (int y = 0; y < selectedSector.getHeight(); y++) {
            for (int x = 0; x < selectedSector.getWidth(); x++) {
                previewLayers.get(Layer.FOREGROUND).set(x,y, selectedSector.getFgTile(x, y));
                previewLayers.get(Layer.BACKGROUND).set(x,y, selectedSector.getBgTile(x, y));
            }
        }
    }
    
    @Override
    public void onDeselect(boolean ignorePrompts) {
        // Assign null to layer arrays, so that they get cleaned up?
        // OR create new arrays size [0][0]?
    }
    
    public void setFillEmptyTiles(boolean fill) {
        this.fillEmptyTiles = fill;
    }
    
    public boolean fillEmptyTiles() {
        return fillEmptyTiles;
    }
}
