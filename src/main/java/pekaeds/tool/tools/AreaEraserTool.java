package pekaeds.tool.tools;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import pekaeds.data.Layer;
import pekaeds.tool.Tool;
import pk2.level.PK2TileArray;
import pk2.util.TileUtils;

public class AreaEraserTool extends Tool {

    private boolean placing = false;
    private Rectangle rect;
    private Point selectionStart = new Point(), selectionEnd = new Point();


    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        if (SwingUtilities.isLeftMouseButton(e)) {
            getUndoManager().startBlock();
            
            selectionStart = e.getPoint();
            selectionEnd = e.getPoint();

            placing = true;
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
        if (SwingUtilities.isLeftMouseButton(e)) {
            doPlacement();
    
            getUndoManager().endBlock();
            
            selectionStart.setLocation(-1, -1);
            selectionEnd.setLocation(-1, -1);

            placing = false;
        }
    }


    public void doPlacement(){
        rect = TileUtils.calculateSelectionRectangle(selectionStart, selectionEnd, selectedSector);

        PK2TileArray empty = new PK2TileArray(rect.width, rect.height);

        int px = rect.x * 32;
        int py = rect.y * 32;

        if(getMode() == Tool.MODE_SPRITE){
            var oldSprites = layerHandler.getSpritesFromRect(rect);

            getUndoManager().pushSpritePlaced(this, px, py, empty, oldSprites);
            layerHandler.placeSpritesScreen(px, py, empty);

        }
        else if(selectedLayer == Layer.BOTH){
            
            var oldFGTiles = layerHandler.getTilesFromRect(rect, Layer.FOREGROUND);
            var oldBGTiles = layerHandler.getTilesFromRect(rect, Layer.BACKGROUND);
            
            getUndoManager().pushTilePlaced(this, px, py, empty, oldFGTiles, empty, oldBGTiles, Layer.BOTH);

            layerHandler.placeTilesScreen(px, py, Layer.BACKGROUND, empty);
            layerHandler.placeTilesScreen(px, py, Layer.FOREGROUND, empty);

        }
        else{
            var oldFGTiles = layerHandler.getTilesFromRect(rect, selectedLayer);
            getUndoManager().pushTilePlaced(this, px, py, empty, oldFGTiles, null, null, selectedLayer);

            layerHandler.placeTilesScreen(px, py, selectedLayer, empty);
        }
    }
    

    @Override
    public void draw(Graphics2D g) {

        if(this.placing){
            rect = TileUtils.calculateSelectionRectangle(selectionStart, selectionEnd, selectedSector);
            int px = rect.x * 32;
            int py = rect.y * 32;
            int width = rect.width * 32;
            int height = rect.height * 32;

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
            g.setColor(Color.red);
            g.fillRect(px, py, width, height);

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));


            drawSelectionRect(g, px, py, width, height);

        }
        else{
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g.setColor(Color.red);
            g.fillRect(getMousePosition().x, getMousePosition().y, 32, 32);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
            drawSelectionRect(g, getMousePosition().x, getMousePosition().y, 32, 32);
        }
    }

    @Override
    public void onSelect() {
    }

    @Override
    public void onDeselect(boolean ignorePrompts) {

    }


    @Override
    public boolean isEraser(){
        return true;
    }
}
