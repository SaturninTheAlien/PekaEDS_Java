package pekaeds.tool.tools;

import java.awt.*;
import java.awt.event.MouseEvent;

import pekaeds.data.Layer;
import pekaeds.tool.Tool;
import pk2.level.PK2TileArray;

public class EraserTool extends Tool {
    private static final PK2TileArray EMPTY_TILE = PK2TileArray.singleTile(255);
    
    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        
        getUndoManager().startBlock();
        
        doPlacement(e.getPoint());
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        
        doPlacement(e.getPoint());
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        getUndoManager().endBlock();
    }
    
    private void doPlacement(Point position) {
        switch (getMode()) {
            case MODE_TILE -> {
                if(selectedLayer==Layer.BOTH){
                    getUndoManager().pushTilePlaced(this, position.x, position.y,
                    EMPTY_TILE, layerHandler.getTilesFromArea(position.x, position.y, 1, 1, Layer.FOREGROUND),
                    EMPTY_TILE, layerHandler.getTilesFromArea(position.x, position.y, 1, 1, Layer.BACKGROUND), selectedLayer);
                    
                    layerHandler.placeTileScreen(position.x, position.y, 255, Layer.FOREGROUND);
                    layerHandler.placeTileScreen(position.x, position.y, 255, Layer.BACKGROUND);
                }
                else{
                    getUndoManager().pushTilePlaced(this, position.x, position.y, EMPTY_TILE, layerHandler.getTilesFromArea(position.x, position.y, 1, 1, selectedLayer), null, null, selectedLayer);
                    layerHandler.placeTileScreen(position.x, position.y, 255, selectedLayer);
                }
            }
            
            case MODE_SPRITE -> {
                getUndoManager().pushSpritePlaced(this, position.x, position.y, EMPTY_TILE, layerHandler.getSpritesFromArea(position.x, position.y, 1, 1));
                
                layerHandler.placeSprite(position, 255);
            }
        }
    }
    
    @Override
    public void draw(Graphics2D g) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
    
        g.setColor(Color.red);
        g.fillRect(getMousePosition().x, getMousePosition().y, 32, 32);
        
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
        
        drawSelectionRect(g, getMousePosition().x, getMousePosition().y, 32, 32);
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
