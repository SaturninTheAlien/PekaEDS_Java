package pekaeds.tool.tools;

import javax.swing.*;

import pekaeds.tool.Tool;
import pk2.settings.Settings;
import pk2.util.TileUtils;

import java.awt.*;
import java.awt.event.MouseEvent;

// TODO Add sprite selection
public class SelectionTool extends Tool {
    private Rectangle selectionRect = new Rectangle(-1, -1, 0, 0);
    
    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        
        if (SwingUtilities.isRightMouseButton(e)) {
            selection.setStart(e.getPoint());
            selection.setEnd(e.getPoint());

            selectionRect = TileUtils.calculateSelectionRectangle(e.getPoint(), e.getPoint(), selectedSector);
            
            if (getMode() == Tool.MODE_TILE) selectingTiles = true;
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        if (layerHandler.getSpriteAt(e.getPoint()) == 255 || !Settings.showSprites) {
            setMode(MODE_TILE);
    
            doTileSelection();
        } else {
            setMode(MODE_SPRITE);
    
            doSpriteSelection();
        }
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        
        if (SwingUtilities.isRightMouseButton(e)) {
            selection.setEnd(e.getPoint());
            
            if (selection.getEnd().x < 0) selection.getEnd().x = 0;
            if (selection.getEnd().y < 0) selection.getEnd().y = 0;
            
            if (selection.getEnd().x >= selectedSector.getWidth() * 32) selection.getEnd().x = (selectedSector.getWidth() * 32) - 32;
            if (selection.getEnd().y >= selectedSector.getHeight() * 32) selection.getEnd().y = (selectedSector.getHeight() * 32) - 32;
            
            selectionRect = TileUtils.calculateSelectionRectangle(selection.getStart(), selection.getEnd(), selectedSector);
        }
    }
    
    private void doTileSelection() {
        selectionRect = TileUtils.calculateSelectionRectangle(selection.getStart(), selection.getEnd(), selectedSector);

        selection.setTileSelection(layerHandler.getTilesFromRect(selectionRect, selectedLayer));

        selectionRect.x = -1;
        selectionRect.y = -1;
        selectionRect.width = 0;
        selectionRect.height = 0;

        selectingTiles = false;
    }
    
    private void doSpriteSelection() {
        selectionRect = TileUtils.calculateSelectionRectangle(selection.getStart(), selection.getEnd(), selectedSector);

        selection.setSelectionSprites(new int[][]{{ layerHandler.getSpriteAt(selection.getStart().x, selection.getStart().y) }}); // TODO Fix multiselection of sprites
    }

    @Override
    public void draw(Graphics2D g) {
        if (selectionRect.x != -1) {
            int x = selectionRect.x * 32;
            int y = selectionRect.y * 32;
            
            drawSelectionRect(g, x, y, selectionRect.width * 32, selectionRect.height * 32);
        }
    }
    
    @Override
    public void onSelect() {
    
    }
    
    @Override
    public void onDeselect(boolean ignorePrompts) {
    
    }
}
