package pekaeds.tool;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pekaeds.data.Layer;
import pekaeds.tool.undomanager.ToolUndoManager;
import pekaeds.tool.undomanager.UndoAction;
import pekaeds.ui.listeners.SpritePlacementListener;
import pekaeds.ui.listeners.TileChangeListener;
import pekaeds.ui.mappanel.MapPanelPainter;
import pk2.level.PK2Level;
import pk2.level.PK2LevelSector;
import pk2.sprite.SpritePrototype;
import pk2.util.TileUtils;

import java.awt.*;
import java.awt.event.MouseEvent;


/**
 * Parent class for all Tools.
 *
 * To create a new tool extend this class.
 *
 * To place a single tile use the placeTile() method. To place a whole tile selection use placeTiles().
 * Put these methods in mousePressed() or mouseDragged() after overriding them. When overriding them make sure to call super.mouseMethodYouHaveOverriden(), to keep the mousePosition updated in this parent class updated.
 *
 * Undo and redo gets handled in placeTile() and placeTiles().
 *
 * Then register it with the application by adding a Tools.addTool(yourTool.class) in PekaEDSGUI.registerTools().
 *
 * You should also add a Keyboard shortcut for it. Look at the description of the ShortcutUtils class to see how that is done.
 *
 * If you want to add a button to the main UI, look at the ToolsToolBar class.
 */
public abstract class Tool {
    public static final int MODE_TILE = 0;
    public static final int MODE_SPRITE = 1;
    
    protected static PK2LevelSector selectedSector;
    protected static PK2Level level;

    private MapPanelPainter mapPainter;
    
    protected static int selectedLayer;

    protected final static ToolSelection selection = new ToolSelection();
    protected static boolean selectingTiles;
    
    private static Point mousePosition = new Point(-1, -1);

    private static int mode = MODE_TILE;
    
    private static final ToolInformation toolInformation = new ToolInformation();
    private static ChangeListener toolInformationListener;
    private static ToolModeListener toolModeListener;

    protected static final LayerHandler layerHandler = new LayerHandler(selection);
    
    protected static Rectangle viewRect;

    private static final ToolUndoManager undoManager = new ToolUndoManager();
    
    public static void setSelection(int[][] tileSelection) {
        selection.setTileSelection(tileSelection);
    }

    protected boolean useRightMouseButton = false;
    
    /**
     * Tools must call the super.mouse... methods so that undo/redo works!
     * @param e
     */
    public void mousePressed(MouseEvent e) {
        mousePosition = e.getPoint();
        
        TileUtils.alignPointToGrid(mousePosition);
        
        ToolUndoManager.clearRedoStack();
    }
    public void mouseReleased(MouseEvent e) {
        mousePosition = e.getPoint();
        
        TileUtils.alignPointToGrid(mousePosition);
    }
    
    /*
        NOTE: Child classes need to call the following two classes when overriding them, so the mouse position keeps being updated.
     */
    public void mouseMoved(MouseEvent e) {
        mousePosition = e.getPoint();
        
        TileUtils.alignPointToGrid(mousePosition);
        
        updateToolInformation(e.getPoint());
    }
    public void mouseDragged(MouseEvent e) {
        mousePosition = e.getPoint();
        
        TileUtils.alignPointToGrid(mousePosition);
        
        updateToolInformation(e.getPoint());
    }
    
    public Point getMousePosition() {
        return mousePosition;
    }

    
    public abstract void draw(Graphics2D g);
    
    public final void drawSelectionRect(Graphics2D g, int x, int y, int width, int height) {
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height); // Draw outer black part
        g.drawRect(x + 2, y + 2, width - 4, height - 4); // Draw inner black part
        
        // Draw white middle part
        g.setColor(Color.WHITE);
        g.drawRect(x + 1, y + 1, width - 2, height - 2);
    }
    
    public static void setSelectionSize(int width, int height) {
        selection.setWidth(width);
        selection.setHeight(height);
    }
    
    public void setMapPanelPainter(MapPanelPainter painter) {
        this.mapPainter = painter;
    }
    
    public MapPanelPainter getMapPanelPainter() {
        return mapPainter;
    }

    public static void setLevel(PK2Level m){
        level = m;
        layerHandler.setLevel(m);
    }

    public static void setSector(PK2LevelSector m) {
        selectedSector = m;

        layerHandler.setSector(selectedSector);
        //undoManager.setMap(m);

        //reset();
    }
    
    public static void reset() {
        setMode(MODE_TILE);

        selection.reset();
    }
    
    public static void setSelectedLayer(int layer) {
        selectedLayer = layer == Layer.BOTH ? Layer.FOREGROUND : layer;

        //layerHandler.setCurrentLayer(selectedLayer);
        
        onLayerChange();
    }
    
    private static void onLayerChange() {
    
    }
    
    public static void setSelectedSprite(SpritePrototype newSprite) {
        // TODO Change grid size to size of selected sprite?

        for (int i = 0; i < level.getSpriteList().size(); i++) {
            if (level.getSpriteList().get(i) == newSprite) {
                selection.setSelectionSprites(new int[][]{{ i }});

                break;
            }
        }
    }
    
    public static void setToolInformationListener(ChangeListener listener) {
        toolInformationListener = listener;
    }
    
    private void updateToolInformation(Point mousePosition) {
        toolInformation.setX(mousePosition.x);
        toolInformation.setY(mousePosition.y);
        
        toolInformation.setForegroundTile(layerHandler.getTileAt(Layer.FOREGROUND, mousePosition));
        toolInformation.setBackgroundTile(layerHandler.getTileAt(Layer.BACKGROUND, mousePosition));
        
        int sprId = layerHandler.getSpriteAt(mousePosition);
        toolInformation.setSpriteId(sprId);
        
        String sprFilename = "none";
        if (sprId != 255 && sprId < level.getSpriteList().size()) {
            if (selectedSector != null) {
                sprFilename = level.getSpriteList().get(sprId).getFilename();
            }
        }
        
        toolInformation.setSpriteFilename(sprFilename);
        
        toolInformationListener.stateChanged(new ChangeEvent(this));
    }
    
    public static ToolInformation getToolInformation() {
        return toolInformation;
    }

    public static int getMode() {
        return mode;
    }
    
    public static void setMode(int m) {
        mode = m;
        
        toolModeListener.changeMode(mode);
    }
    
    public static void setToolModeListener(ToolModeListener listener) {
        toolModeListener = listener;
    }

    public static void setTileChangeListener(TileChangeListener listener) {
        layerHandler.setTileChangeListener(listener);
    }

    public static void setChangeListener(ChangeListener listener){
        layerHandler.setChangeListener(listener);
    }

    public static void setSpritePlacementListener(SpritePlacementListener listener) {
        layerHandler.setSpritePlacementListener(listener);
    }

    public static void setViewRect(Rectangle rect) {
        viewRect = rect;
    }
    
    public boolean useRightMouseButton() {
        return useRightMouseButton;
    }
    
    public static ToolUndoManager getUndoManager() {
        return undoManager;
    }
    
    public abstract void onSelect();
    public abstract void onDeselect(boolean ignorePrompts);
    
    public void onUndo(UndoAction action) {
        switch (action.getType()) {
            case PLACE_TILE,
                    CUT_TOOL_PLACE_FOREGROUND,
                    CUT_TOOL_PLACE_BACKGROUND,
                    CUT_TOOL_CUT_FOREGROUND,
                    CUT_TOOL_CUT_BACKGROUND -> layerHandler.placeTilesScreen(action.getX(), action.getY(), action.getLayer(), action.getOldTiles());
            
            case PLACE_SPRITE,
                    CUT_TOOL_PLACE_SPRITES,
                    CUT_TOOL_CUT_SPRITES -> layerHandler.placeSpritesScreen(action.getX(), action.getY(), action.getOldTiles());
        }
    
        action.changeIntoRedo();
    }
    
    public void onRedo(UndoAction action) {
        switch (action.getType()) {
            case PLACE_TILE,
                    CUT_TOOL_PLACE_FOREGROUND,
                    CUT_TOOL_PLACE_BACKGROUND,
                    CUT_TOOL_CUT_FOREGROUND,
                    CUT_TOOL_CUT_BACKGROUND -> layerHandler.placeTilesScreen(action.getX(), action.getY(), action.getLayer(), action.getNewTiles());
            
            case PLACE_SPRITE,
                    CUT_TOOL_PLACE_SPRITES,
                    CUT_TOOL_CUT_SPRITES -> layerHandler.placeSpritesScreen(action.getX(), action.getY(), action.getNewTiles());
        }
        
        action.changeIntoUndo();
    }
}
