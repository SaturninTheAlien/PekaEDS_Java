package pekaeds.tool.tools;

import pekaeds.data.Layer;
import pekaeds.tool.Tool;
import pekaeds.tool.undomanager.ActionType;
import pekaeds.tool.undomanager.UndoAction;
import pekaeds.ui.listeners.CutToolListener;
import pk2.level.PK2TileArray;
import pk2.sprite.PK2Sprite;
import pk2.util.TileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/*
    TODO Decide how to handle resizing.
        -> Only allow it when the selection has not been moved?
        -> Only allow reducing dimensions?
            Keep data, so when the user increases size again it gets restored?
 */
public final class CutTool extends Tool {
    private static final Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    private static final Cursor moveCursor = new Cursor(Cursor.MOVE_CURSOR);

    private boolean cutForegroundLayer = true;
    private boolean cutBackgroundLayer = true;
    private boolean cutSpritesLayer = true;

    private PK2TileArray foregroundLayer = PK2TileArray.singleTile(0);
    private PK2TileArray backgroundLayer = PK2TileArray.singleTile(0);
    private PK2TileArray spritesLayer = PK2TileArray.singleTile(0);

    private Point selectionStart;
    private Rectangle selectionRect = new Rectangle();
    private boolean selecting = false;
    private boolean cutSelection = true;

    private CutToolListener selectionListener;

    public CutTool() {
        useRightMouseButton = true;
    }
    
    private void moveSelectionTo(Point position, int xOffset, int yOffset) {
        int mx = position.x / 32;
        int my = position.y / 32;
        
        int x = mx - xOffset;
        int y = my - yOffset;
        
        selectionRect.x = x;
        selectionRect.y = y;

        selectionListener.selectionUpdated(selectionRect);
    }

    // TODO Change from right click to left click?
    private int clickXOffset, clickYOffset;
    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        
        if (SwingUtilities.isRightMouseButton(e)) {
            if (isSelectionPresent()) {
                finalizeCut();
                
                resetCut();
            } else {
                selecting = true;
    
                selectionStart = e.getPoint();
            }

            selectionListener.selectionUpdated(selectionRect);
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            if (isSelectionPresent()) {
                int mx = e.getX() / 32;
                int my = e.getY() / 32;
                
                if (!selectionRect.contains(mx, my)) {
                    selectionRect.setLocation(mx - (selectionRect.width / 2), my - (selectionRect.height / 2));
                } else {
                    clickXOffset = mx - selectionRect.x;
                    clickYOffset = my - selectionRect.y;
                }

                selectionListener.selectionUpdated(selectionRect);
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            moveSelectionTo(e.getPoint(), clickXOffset, clickYOffset);
        } else if (SwingUtilities.isRightMouseButton(e)) {
            if (selecting) selectionRect = TileUtils.calculateSelectionRectangle(selectionStart, e.getPoint(), selectedSector); // TODO Sector
        }

        selectionListener.selectionUpdated(selectionRect);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            if (selecting) {
                getUndoManager().startBlock();
                
                if (cutForegroundLayer) {
                    foregroundLayer = layerHandler.getTilesFromRect(selectionRect, Layer.FOREGROUND);
        
                    getUndoManager().pushTilePlaced(this, ActionType.CUT_TOOL_CUT_FOREGROUND, selectionRect.x * 32, selectionRect.y * 32, foregroundLayer, layerHandler.getTilesFromRect(selectionRect, Layer.FOREGROUND), null, null, Layer.FOREGROUND);
        
                    layerHandler.removeTilesArea(selectionRect, Layer.FOREGROUND);
                }
    
                if (cutBackgroundLayer) {
                    backgroundLayer = layerHandler.getTilesFromRect(selectionRect, Layer.BACKGROUND);
        
                    getUndoManager().pushTilePlaced(this, ActionType.CUT_TOOL_CUT_BACKGROUND, selectionRect.x * 32, selectionRect.y * 32, backgroundLayer, layerHandler.getTilesFromRect(selectionRect, Layer.BACKGROUND), null, null, Layer.BACKGROUND);
        
                    layerHandler.removeTilesArea(selectionRect, Layer.BACKGROUND);
                }
    
                if (cutSpritesLayer) {
                    spritesLayer = layerHandler.getSpritesFromRect(selectionRect);
        
                    getUndoManager().pushSpritePlaced(this, ActionType.CUT_TOOL_CUT_SPRITES, selectionRect.x * 32, selectionRect.y * 32, spritesLayer, layerHandler.getSpritesFromRect(selectionRect));
        
                    layerHandler.removeSpritesArea(selectionRect);
                }
    
                getUndoManager().endBlock();

                selectionListener.selectionUpdated(selectionRect);

                selecting = false;
    
                if (isSelectionPresent()) {
                    getMapPanelPainter().setCursor(moveCursor);
                } else {
                    getMapPanelPainter().setCursor(defaultCursor);
                }
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (selecting) {
            // Check isSelectionPresent() in here because if I don't the selection rect still gets drawn at 0,0 with a width and height of 1, for whatever reason.
            if (isSelectionPresent()) drawSelectionRect(g, selectionRect.x * 32, selectionRect.y * 32, selectionRect.width * 32, selectionRect.height * 32);
        } else if (isSelectionPresent()) {
            if (cutBackgroundLayer) drawLayer(g, backgroundLayer, selectionRect.x * 32, selectionRect.y * 32);
            if (cutSpritesLayer) drawSelectedBackgroundSprites(g, selectionRect);
            if (cutForegroundLayer) drawLayer(g, foregroundLayer, selectionRect.x * 32, selectionRect.y * 32);
            if (cutSpritesLayer) drawSelectedSprites(g, selectionRect);

            drawSelectionRect(g, selectionRect.x * 32, selectionRect.y * 32, selectionRect.width * 32, selectionRect.height * 32);
        }
    }
    
    @Override
    public void onSelect() {
        if (isSelectionPresent()) {
            getMapPanelPainter().setCursor(moveCursor);
        }
    }
    
    @Override
    public void onDeselect(boolean ignorePrompts) {
        getMapPanelPainter().setCursor(defaultCursor);
        
        if (isSelectionPresent()) {
            if (!ignorePrompts) {
                int res = JOptionPane.showConfirmDialog(null, "Cut selection has not been placed. Do you want to place it?", "Selection hasn't been placed", JOptionPane.YES_NO_OPTION);
    
                if (res == JOptionPane.YES_OPTION) {
                    finalizeCut();
                } else {
                    getUndoManager().undoLastAction();
                }
            } else {
                getUndoManager().undoLastAction();
            }
            
            resetCut();
        }
    }
    
    private void finalizeCut() {
        getUndoManager().startBlock();
        
        if (cutForegroundLayer) {
            getUndoManager().pushTilePlaced(this, ActionType.CUT_TOOL_PLACE_FOREGROUND, selectionRect.x * 32, selectionRect.y * 32, foregroundLayer, layerHandler.getTilesFromRect(selectionRect, Layer.FOREGROUND), null, null, Layer.FOREGROUND);
            
            layerHandler.placeTilesScreen(selectionRect.x * 32, selectionRect.y * 32, Layer.FOREGROUND, foregroundLayer);
        }
    
        if (cutBackgroundLayer) {
            getUndoManager().pushTilePlaced(this, ActionType.CUT_TOOL_PLACE_BACKGROUND, selectionRect.x * 32, selectionRect.y * 32, backgroundLayer, layerHandler.getTilesFromRect(selectionRect, Layer.BACKGROUND),null, null, Layer.BACKGROUND);
    
            layerHandler.placeTilesScreen(selectionRect.x * 32, selectionRect.y * 32, Layer.BACKGROUND, backgroundLayer);
        }
    
        if (cutSpritesLayer) {
            getUndoManager().pushSpritePlaced(this, ActionType.CUT_TOOL_PLACE_SPRITES, selectionRect.x * 32, selectionRect.y * 32, spritesLayer, layerHandler.getSpritesFromRect(selectionRect));
            
            layerHandler.placeSprites(selectionRect.x, selectionRect.y, spritesLayer);
        }
        
        getUndoManager().endBlock();
    }
    
    private void resetCut() {
        selectionRect.setRect(0, 0, 0, 0);

        selectionListener.selectionUpdated(selectionRect);

        getMapPanelPainter().setCursor(defaultCursor);
    }
    
    /*@SuppressWarnings("incomplete-switch")
    @Override
    public void onUndo(UndoAction action) {
        super.onUndo(action);
        
        // If the user undoes the placement of the cut selection, restore the old tiles and restore the selection
        if (doesActionPlace(action)) {
            switch (action.getType()) {
                case CUT_TOOL_PLACE_FOREGROUND -> foregroundLayer = action.getNewTiles();
                case CUT_TOOL_PLACE_BACKGROUND -> backgroundLayer = action.getNewTiles();
                case CUT_TOOL_PLACE_SPRITES -> spritesLayer = action.getNewTiles();
            }
    
            selectionRect.setRect(action.getX() / 32, action.getY() / 32, action.getNewTiles()[0].length, action.getNewTiles().length);
    
            getMapPanelPainter().setCursor(moveCursor);
            getMapPanelPainter().repaint();
        } else { // Otherwise, if they have made a cut, only restore the cut tiles
            resetCut();
        }
    }
    
    private boolean doesActionPlace(UndoAction action) {
        return action.getType() == ActionType.CUT_TOOL_PLACE_FOREGROUND ||
                action.getType() == ActionType.CUT_TOOL_PLACE_BACKGROUND ||
                action.getType() == ActionType.CUT_TOOL_PLACE_SPRITES;
    }*/
    
    @Override
    public void onRedo(UndoAction action) {
        super.onRedo(action);
    }
    
    private void drawLayer(Graphics2D g, PK2TileArray layer, int startX, int startY) {
        for (int x = 0; x < layer.getWidth(); x++) {
            for (int y = 0; y < layer.getHeight(); y++) {
                getMapPanelPainter().drawTile(g, startX + (x * 32), startY + (y * 32), layer.get(x, y));
            }
        }
    }

    private void drawSelectedSprites(Graphics2D g, Rectangle selection) {
        for (int x = 0; x < selection.width; x++) {
            for (int y = 0; y < selection.height; y++) {
                if (spritesLayer.get(x, y) != 255) {
                    var spr = level.getSprite(spritesLayer.get(x, y));

                    if (spr != null && spr.getType() != PK2Sprite.TYPE_BACKGROUND) {
                        getMapPanelPainter().drawSprite(g, spr,(selection.x + x) * 32, (selection.y + y) * 32);
                    }
                }
            }
        }
    }

    private void drawSelectedBackgroundSprites(Graphics2D g, Rectangle selection) {
        for (int x = 0; x < selection.width; x++) {
            for (int y = 0; y < selection.height; y++) {
                if (spritesLayer.get(x, y) != 255) {
                    var spr = level.getSprite(spritesLayer.get(x, y));

                    if (spr != null && spr.getType() == PK2Sprite.TYPE_BACKGROUND) {
                        getMapPanelPainter().drawSprite(g, spr,(selection.x + x) * 32, (selection.y + y) * 32);
                    }
                }
            }
        }
    }

    public void setSelectionX(int newX) {
        selectionRect.x = newX;

        getMapPanelPainter().repaint();
    }

    public void setSelectionY(int newY) {
        selectionRect.y = newY;

        getMapPanelPainter().repaint();
    }

    // TODO Make changing dimensions work! How should it work after the selection has been moved?
    public void setSelectionWidth(int newWidth) {
        selectionRect.width = newWidth;

        getMapPanelPainter().repaint();
    }

    // TODO Make changing dimensions work! How should it work after the selection has been moved?
    public void setSelectionHeight(int newHeight) {
        selectionRect.height = newHeight;

        getMapPanelPainter().repaint();
    }

    public void setSelectionListener(CutToolListener listener) {
        selectionListener = listener;
    }

    private boolean isSelectionPresent() {
        return selectionRect.width > 0 && selectionRect.height > 0;
    }
    
    public boolean cutForegroundLayer() {
        return cutForegroundLayer;
    }

    public void setCutForegroundLayer(boolean cutForegroundLayer) {
        this.cutForegroundLayer = cutForegroundLayer;
    }

    public boolean cutBackgroundLayer() {
        return cutBackgroundLayer;
    }

    public void setCutBackgroundLayer(boolean cutBackgroundLayer) {
        this.cutBackgroundLayer = cutBackgroundLayer;
    }

    public boolean cutSpritesLayer() {
        return cutSpritesLayer;
    }

    public void setCutSpritesLayer(boolean cutSpritesLayer) {
        this.cutSpritesLayer = cutSpritesLayer;
    }

    public void setCutSelection(boolean cut) {
        this.cutSelection = cut;
    }

    public boolean cutSelection() {
        return cutSelection;
    }
}
