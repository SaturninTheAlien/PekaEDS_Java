package pekaeds.tool.tools;

import java.awt.*;
import java.awt.event.MouseEvent;

import pekaeds.tool.Tool;

public class LineTool extends Tool {
    private Point start = null;
    private Point end = null;
    
    private int clickCount = 0;
    
    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        
        if (getMode() == MODE_TILE) {
            if (clickCount == 0) {
                getUndoManager().startBlock();
                
                start = e.getPoint();
        
                start.x /= 32;
                start.y /= 32;
        
                end = e.getPoint();
                end.x /= 32;
                end.y /= 32;
            }
        }
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        
        end = e.getPoint();
    
        end.x /= 32;
        end.y /= 32;
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        
        if (start != null) {
            plotLine(start.x, start.y, end.x, end.y);
    
            getUndoManager().endBlock();
            
            start = null;
            end = null;
        }
    }
    
    // https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm
    private void plotLine(Graphics2D g, int x0, int y0, int x1, int y1) {
        if (Math.abs(y1 - y0) < Math.abs(x1 - x0)) {
            if (x0 > x1) {
                plotLow(g, x1, y1, x0, y0);
            } else {
                plotLow(g, x0, y0, x1, y1);
            }
        } else {
            if (y0 > y1) {
                plotHigh(g, x1, y1, x0, y0);
            } else {
                plotHigh(g, x0, y0, x1, y1);
            }
        }
    }
    
    // TODO Draw selection rect
    private void plotLine(int x0, int y0, int x1, int y1) {
        plotLine(null, x0, y0, x1, y1);
    }
    
    private void plotLow(Graphics2D g, int x0, int y0, int x1, int y1) {
        int dx = x1 - x0;
        int dy = y1 - y0;
        int yi = 1;
        
        if (dy < 0) {
            yi = -1;
            dy = -dy;
        }
        
        int d = (2 * dy) - dx;
        int y = y0;
        
        for (int x = x0; x <= x1; x++) {
            if (g != null) {
                getMapPanelPainter().drawTile(g, x * 32, y * 32, selection.getFirstTile());
    
                drawSelectionRect(g, x * 32, y * 32, 32, 32);
            } else {
                getUndoManager().pushTilePlaced(this, x * 32, y * 32, selection.getFirstTile(), layerHandler.getTileAt(selectedLayer, x, y), selectedLayer);
                
                layerHandler.placeTileScreen(x * 32, y * 32, selection.getFirstTile(), selectedLayer);
            }
            
            if (d > 0) {
                y = y + yi;
                d = d + (2 * (dy - dx));
            } else {
                d = d + 2 * dy;
            }
        }
    }
    
    private void plotHigh(Graphics2D g, int x0, int y0, int x1, int y1) {
        int dx = x1 - x0;
        int dy = y1 - y0;
        
        int xi = 1;
        
        if (dx < 0) {
            xi = -1;
            dx = -dx;
        }
        
        int d = (2 * dx) - dy;
        int x = x0;
        
        for (int y = y0; y <= y1; y++) {
            if (g != null) {
                getMapPanelPainter().drawTile(g, x * 32, y * 32, selection.getFirstTile());
    
                drawSelectionRect(g, x * 32, y * 32, 32, 32);
            } else {
                getUndoManager().pushTilePlaced(this, x * 32, y * 32, selection.getFirstTile(), layerHandler.getTileAt(selectedLayer, x, y), selectedLayer);
                
                layerHandler.placeTileScreen(x * 32, y * 32, selection.getFirstTile(), selectedLayer);
            }
            
            if (d > 0) {
                x = x + xi;
                d = d + (2 * (dx - dy));
            } else {
                d = d + 2 * dx;
            }
        }
    }
    
    @Override
    public void draw(Graphics2D g) {
        if (getMode() == MODE_TILE) {
            if (start != null) {
                plotLine(g, start.x, start.y, end.x, end.y);
            } else {
                getMapPanelPainter().drawTile(g, getMousePosition().x, getMousePosition().y, selection.getFirstTile());
            }
        }
    }
    
    @Override
    public void onSelect() {
    
    }
    
    @Override
    public void onDeselect(boolean ignorePrompts) {
    
    }
}
