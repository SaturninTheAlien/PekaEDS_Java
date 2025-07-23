package pekaeds.tool.undomanager;


import pekaeds.tool.Tool;
import pk2.level.PK2TileArray;

public class UndoAction {
    private Tool tool;
    private int x, y;
    private PK2TileArray newTiles;
    private PK2TileArray oldTiles;

    private PK2TileArray newTilesBG;
    private PK2TileArray oldTilesBG;

    private int layer;
    private ActionType type;
    private ActionType undoType;
    
    public UndoAction(Tool tool, ActionType undoType, ActionType actionType, int x, int y,
    
            PK2TileArray newTile,
            PK2TileArray oldTile,

            PK2TileArray newTilesBG,
            PK2TileArray oldTilesBG,

            int layer) {


        this.tool = tool;
        
        this.undoType = undoType;
        this.type = actionType;
        
        this.x = x;
        this.y = y;
        
        this.newTiles = newTile;
        this.oldTiles = oldTile;

        this.newTilesBG = newTilesBG;
        this.oldTilesBG = oldTilesBG;
        
        this.layer = layer;
    }
    
    // Constructor for sprites
    public UndoAction(Tool tool, ActionType undoType, ActionType actionType, int x, int y, PK2TileArray newTile, PK2TileArray oldTile) {
        this.tool = tool;
        
        this.undoType = undoType;
        this.type = actionType;
        
        this.x = x;
        this.y = y;
        
        this.newTiles = newTile;
        this.oldTiles = oldTile;


        this.newTilesBG = null;
        this.oldTilesBG = null;
    }
    
    public void changeIntoRedo() {
        undoType = ActionType.REDO;
    }
    
    public void changeIntoUndo() {
        undoType = ActionType.UNDO;
    }
    
    @Override
    public String toString() {
        return "UndoAction{" +
                "tool=" + tool +
                ", x=" + x +
                ", y=" + y +
                ", newTiles=" + newTiles.toString() +
                ", oldTiles=" + oldTiles.toString() +
                ", layer=" + layer +
                ", type=" + type +
                '}';
    }
    
    public Tool getTool() {
        return tool;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public PK2TileArray getNewTiles() {
        return newTiles;
    }
    
    public PK2TileArray getOldTiles() {
        return oldTiles;
    }

    public PK2TileArray getNewTilesBG(){
        return this.newTilesBG;
    }

    public PK2TileArray getOldTilesBG(){
        return this.oldTilesBG;
    }
    
    public int getLayer() {
        return layer;
    }
    
    public ActionType getType() {
        return type;
    }
    
    public ActionType getUndoType() { return undoType; }
}
