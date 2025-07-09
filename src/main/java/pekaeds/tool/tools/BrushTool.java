package pekaeds.tool.tools;

import javax.swing.*;

import pekaeds.tool.Tool;
import pk2.settings.Settings;
import pk2.util.TileUtils;

import java.awt.*;
import java.awt.event.MouseEvent;

public class BrushTool extends Tool {
    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        
        getUndoManager().startBlock();
        
        placeSelection(e);
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        
        getUndoManager().endBlock();
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        
        placeSelection(e);
    }

    private void placeSelection(MouseEvent e) {
        // Have to use SwingUtilities, because for some reason checking for the mouse buttons like in mousePressed doesn't work.
        if (SwingUtilities.isLeftMouseButton(e)) {
            switch (getMode()) {
                case MODE_TILE -> {
                    // TODO Clean this stuff up, it's ugly.
                    int px = ((e.getX() / 32 * 32) - ((selection.getWidth() / 2) * 32) + (32 / 2));
                    int py = ((e.getY() / 32 * 32) - ((selection.getHeight() / 2) * 32) + (32 / 2));
                    
                    getUndoManager().pushTilePlaced(this, px, py, selection.getTileSelection(), layerHandler.getTilesFromArea(px, py, selection.getWidth(), selection.getHeight(), selectedLayer), selectedLayer);
                    
                    layerHandler.placeTilesScreen(px, py, selectedLayer, selection.getTileSelection());
                }
                
                case MODE_SPRITE -> {
                    getUndoManager().pushSpritePlaced(this, e.getX(), e.getY(), selection.getSelectionSprites(), layerHandler.getSpritesFromArea(e.getX(), e.getY(), 1, 1));
                    
                    layerHandler.placeSprite(e.getPoint());
                }
            }
        }
    }
    
    @Override
    public void draw(Graphics2D g) {
        switch (getMode()) {
            case MODE_TILE -> drawSelectedTiles(g);
            case MODE_SPRITE -> drawSelectedSprite(g);
        }
    }
    
    @Override
    public void onSelect() {
    }
    
    @Override
    public void onDeselect(boolean ignorePrompts) {
    }
    
    // TODO Draw 255/transparent tiles https://docs.oracle.com/javase/tutorial/2d/advanced/compositing.html
    private void drawSelectedTiles(Graphics2D g) {
        if (!selectingTiles) {
            // Make the selected tiles appear to be on a 32 * 32 grid.
            int xAdjusted = (getMousePosition().x / 32 * 32);
            int yAdjusted = (getMousePosition().y / 32 * 32);
            
            for (int y = 0; y < selection.getHeight(); y++) {
                for (int x = 0; x < selection.getWidth(); x++) {
                    // Offset the selections position by half its size (selection width and height), so it is centered at the position of the mouse cursor.
                    int offsetX = (x - (selection.getWidth() / 2)) * 32;
                    int offsetY = (y - (selection.getHeight() / 2)) * 32;
                    
                    getMapPanelPainter().drawTile(g, xAdjusted + offsetX, yAdjusted + offsetY, selection.getTileSelection()[y][x]);
                }
            }
            
            if (Settings.highlightSelection()) {
                int xPos = xAdjusted;
                int yPos = yAdjusted;
                int sWidth = selection.getWidth() * 32;
                int sHeight = selection.getHeight() * 32;
    
                xPos -= (sWidth / 2);
                yPos -= (sHeight / 2);
                
                if (selection.getWidth() % 2 != 0) {
                    xPos += 16;
                }
                
                if (selection.getHeight() % 2 != 0) {
                    yPos += 16;
                }
                
                drawSelectionRect(g, xPos, yPos, sWidth, sHeight);
            }
        }
    }
    
    private void drawSelectedSprite(Graphics2D g) {
        TileUtils.alignPointToGrid(getMousePosition());

        for (int x = 0; x < selection.getWidth(); x++) {
            for (int y = 0; y < selection.getHeight(); y++) {
                int selectedSprite = selection.getSelectionSprites()[y][x];

                if (selectedSprite != 255 && selectedSprite >= 0) {
                    getMapPanelPainter().drawSprite(g, level.getSprite(selectedSprite), getMousePosition().x, getMousePosition().y);
                }
            }
        }
    }
}
