package pekaeds.tool.tools;

import javax.swing.*;

import pekaeds.settings.Settings;
import pekaeds.tool.Tool;
import pekaeds.util.TileUtils;

import java.awt.*;
import java.awt.event.MouseEvent;

public class RectangleTool extends Tool {
    private boolean fill = false;
    private boolean placing = false;

    private Rectangle rect;

    private Point selectionStart = new Point(), selectionEnd = new Point();

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        
        if (getMode() == MODE_TILE) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                getUndoManager().startBlock();
                
                selectionStart = e.getPoint();
                selectionEnd = e.getPoint();

                placing = true;
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);

        if (!placing) {
            selectionStart = e.getPoint();
            selectionEnd = e.getPoint();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        
        if (SwingUtilities.isLeftMouseButton(e)) {
            selectionEnd = e.getPoint();
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        if (getMode() == MODE_TILE) {
            placeRectangle();
    
            getUndoManager().endBlock();
            
            selectionStart.setLocation(-1, -1);
            selectionEnd.setLocation(-1, -1);

            placing = false;
        }
    }
    
    private void placeRectangle() {
        rect = TileUtils.calculateSelectionRectangle(selectionStart, selectionEnd, selectedSector);
        
        for (int x = rect.x; x < rect.x + rect.width; x++) {
            for (int y = rect.y; y < rect.y + rect.height; y++) {
                // TODO support a tile selection greater than one
                if (fill) {
                    getUndoManager().pushTilePlaced(this, x * 32, y * 32, selection.getTileSelection()[0][0], layerHandler.getTileAt(selectedLayer, x, y), selectedLayer);
                    
                    layerHandler.placeTileScreen(x * 32, y * 32, selection.getTileSelection()[0][0], selectedLayer);
                } else {
                    if (x == rect.x || x == rect.x + (rect.width - 1) ||
                            y == rect.y || y == rect.y + (rect.height - 1)) {
                        getUndoManager().pushTilePlaced(this, x * 32, y * 32, selection.getTileSelection()[0][0], layerHandler.getTileAt(selectedLayer, x, y), selectedLayer);

                        layerHandler.placeTileScreen(x * 32, y * 32, selection.getTileSelection()[0][0], selectedLayer);
                    }
                }
            }
        }
    }
    
    @Override
    public void draw(Graphics2D g) {
        if (getMode() == MODE_TILE) {
            if (selectionStart.x != -1 && selectionStart.y != -1) {
                rect = TileUtils.calculateSelectionRectangle(selectionStart, selectionEnd, selectedSector);
        
                for (int x = rect.x; x < rect.x + rect.width; x++) {
                    for (int y = rect.y; y < rect.y + rect.height; y++) {
                        // TODO support a tile selection greater than one
                        if (fill) {
                            getMapPanelPainter().drawTile(g, x * 32, y * 32, selection.getTileSelection(selectedLayer)[0][0]);
                        } else {
                            if (x == rect.x || x == rect.x + (rect.width - 1) ||
                                    y == rect.y || y == rect.y + (rect.height - 1)) {
                                getMapPanelPainter().drawTile(g, x * 32, y * 32, selection.getTileSelection(selectedLayer)[0][0]);
                            }
                        }
                    }
                }
                
                if (Settings.highlightSelection()) {
                    drawSelectionRect(g, rect.x * 32, rect.y * 32, rect.width * 32, rect.height * 32);
                    
                    // Draw inner outlines when not filled
                    if (!fill) {
                        // The dimensions get adjusted, so that they will be drawn inside the rectangle, instead of the outside.
                        int widthAdjusted = rect.width;
                        int heightAdjusted = rect.height;
                        
                        // Only need to adjust the values when they're greater 1.
                        if (rect.width > 1) {
                            widthAdjusted = (widthAdjusted - 2) * 32;
                        }
    
                        if (rect.height > 1) {
                            heightAdjusted = (heightAdjusted - 2) * 32;
                        }
                        
                        drawSelectionRect(g, (rect.x + 1) * 32, (rect.y + 1) * 32, widthAdjusted, heightAdjusted);
                    }
                }
            } else {
                getMapPanelPainter().drawTile(g, getMousePosition().x, getMousePosition().y, selection.getTileSelection(selectedLayer)[0][0]);
    
                if (Settings.highlightSelection()) {
                    drawSelectionRect(g, getMousePosition().x, getMousePosition().y, 32, 32);
                }
            }
        }
    }
    
    @Override
    public void onSelect() {
    
    }
    
    @Override
    public void onDeselect(boolean ignorePrompts) {
    
    }
    
    public void setFilled(boolean filled) {
        this.fill = filled;
    }
    
    public boolean isFilled() {
        return fill;
    }
}
