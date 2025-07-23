package pekaeds.tool.undomanager;

import java.util.Stack;

import pekaeds.tool.Tool;
import pk2.level.PK2TileArray;

public class ToolUndoManager {
    private static final Stack<UndoAction> undoActionStack = new Stack<>();
    private static final Stack<UndoAction> redoActionStack = new Stack<>();
    
    private static final Stack<UndoBlock> undoBlockStack = new Stack<>();
    private static final Stack<UndoBlock> redoBlockStack = new Stack<>();
    
    //private PK2Map map;
    
    private int blockStartSize;
    public void startBlock() {
        blockStartSize = undoActionStack.size();
    }
    public void endBlock() {
        undoBlockStack.push(new UndoBlock(blockStartSize, undoActionStack.size()));
    }
    
    public void pushTilePlaced(Tool tool, ActionType actionType, int x, int y,
        
        PK2TileArray newTiles,
        PK2TileArray oldTiles,
        PK2TileArray newTilesBG,
        PK2TileArray oldTilesBG,

        int layer) {
        undoActionStack.push(new UndoAction(tool, ActionType.UNDO, actionType, x, y, newTiles, oldTiles, newTilesBG, oldTilesBG, layer));
    }
    
    public void pushTilePlaced(Tool tool, int x, int y,

        PK2TileArray newTiles,
        PK2TileArray oldTiles,
        PK2TileArray newTilesBG,
        PK2TileArray oldTilesBG,
        int layer) {
        pushTilePlaced(tool, ActionType.PLACE_TILE, x, y, newTiles, oldTiles, newTilesBG, oldTilesBG, layer);
    }
    
    public void pushTilePlaced(Tool tool, int x, int y, int newTile, int oldTile, int layer) {
        pushTilePlaced(tool, x, y, PK2TileArray.singleTile(newTile), PK2TileArray.singleTile(oldTile), null, null, layer);
    }
    
    public void pushSpritePlaced(Tool tool, int x, int y, PK2TileArray newSprite, PK2TileArray oldSprite) {
        pushSpritePlaced(tool, ActionType.PLACE_SPRITE, x, y, newSprite, oldSprite);
    }
    
    public void pushSpritePlaced(Tool tool, ActionType actionType, int x, int y, PK2TileArray newSprite, PK2TileArray oldSprite) {
        undoActionStack.push(new UndoAction(tool, ActionType.UNDO, actionType, x, y, newSprite, oldSprite));
    }
    
    public void undoLastAction() {
        if (!undoActionStack.empty() && !undoBlockStack.empty()) {
            var lastUndoBlock = undoBlockStack.pop();
            
            int redoStart = redoActionStack.size();
            
            for (int start = lastUndoBlock.start; start < lastUndoBlock.end; start++) {
                var lastUndo = undoActionStack.pop();
                
                lastUndo.getTool().onUndo(lastUndo);
                
                lastUndo.changeIntoRedo();
                redoActionStack.push(lastUndo);
            }
            
            redoBlockStack.push(new UndoBlock(redoStart, redoActionStack.size()));
        }
    }
    
    public void redoLastAction() {
        if (!redoActionStack.empty() && !redoBlockStack.empty()) {
            var lastRedoBlock = redoBlockStack.pop();
            
            int undoStart = undoActionStack.size();
            
            for (int start = lastRedoBlock.start; start < lastRedoBlock.end; start++) {
                var lastRedo = redoActionStack.pop();
                
                lastRedo.getTool().onRedo(lastRedo);
                
                lastRedo.changeIntoUndo();
                undoActionStack.push(lastRedo);
            }
            
            undoBlockStack.push(new UndoBlock(undoStart, undoActionStack.size()));
        }
    }
    
    public static void clearRedoStack() {
        redoActionStack.clear();
        redoBlockStack.clear();
    }
    
    /*public void setMap(PK2Map map) {
        this.map = map;
    }*/
    
    private static class UndoBlock {
        int start;
        int end;
        
        public UndoBlock(int s, int e) {
            start = s;
            end = e;
        }
    }
}
