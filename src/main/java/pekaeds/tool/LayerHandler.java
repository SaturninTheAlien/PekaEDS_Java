package pekaeds.tool;

import java.awt.*;

import pekaeds.data.Layer;
import pekaeds.ui.listeners.SpritePlacementListener;
import pekaeds.ui.listeners.TileChangeListener;
import pk2.level.PK2Level;
import pk2.level.PK2LevelSector;
import pk2.level.PK2TileArray;
import pk2.util.TileUtils;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public final class LayerHandler {
    private final ToolSelection selection;
    private PK2LevelSector sector;
    private PK2Level level;

    private int gridX = 32, gridY = 32;
    //private int currentLayer;

    private ChangeEvent changeEvent = new ChangeEvent(this);
    private ChangeListener changeListener;

    private TileChangeListener tileChangeListener;
    private SpritePlacementListener spritePlacementListener;

    public LayerHandler(ToolSelection toolSelection) {
        this.selection = toolSelection;
    }

    public void setTileAt(int layer, int x, int y, int tileID) {

        switch (layer) {
            case Layer.BACKGROUND:
                sector.setBgTile(x, y, tileID);
                break;

            case Layer.FOREGROUND:
                sector.setFgTile(x, y, tileID);
                break;

            case Layer.SPRITES:
                sector.setSpriteTile(x, y, tileID);
                break;

            case Layer.BOTH:
                sector.setBgTile(x, y, tileID);
                sector.setFgTile(x, y, tileID);
                break;

            default:
                break;
        }

        changeListener.stateChanged(changeEvent);

    }

    /**
     * Like placeTileMap, but adjusts the x and y values by dividing them by the size of a single tile (32).
     * Use this when x and y are screen/mouse coordinates.
     *
     * @param x
     * @param y
     * @param tileId
     * @param layer
     */
    public void placeTileScreen(int x, int y, int tileId, int layer) {
        x /= gridX;
        y /= gridY;

        placeTile(x, y, tileId, layer);
    }

    public void placeTile(int x, int y, int tileId, int layer) {
        if (layer == Layer.BOTH) layer = Layer.FOREGROUND; // TODO I think this can go, but needs testing.

        if (x >= 0 && y >= 0 && x < sector.getWidth() && y < sector.getHeight()) {
            setTileAt(layer, x, y, tileId);

            tileChangeListener.tileChanged(x, y, tileId);
        }
    }

    public void placeTilesScreen(int x, int y, int layer, PK2TileArray tiles) {
        int selectionWidth = tiles.getWidth();
        int selectionHeight = tiles.getHeight();
        
        for (int sy = 0; sy < selectionHeight; sy++) {
            for (int sx = 0; sx < selectionWidth; sx++) {
                int xx = x + (sx * 32);
                int yy = y + (sy * 32);

                placeTileScreen(xx, yy, tiles.get(sx, sy), layer);
            }
        }
    }

    public int getTileAt(int layer, Point position) {
        return getTileAt(layer, position.x / 32, position.y / 32);
    }

    public int getTileAt(int layer, int x, int y) {
        int tile = 255;

        if (layer == Layer.BOTH) layer = Layer.FOREGROUND;

        if (sector != null) {
            if (x >= 0 && y >= 0 && x < sector.getWidth() && y < sector.getHeight()) {

                switch (layer) {
                    case Layer.BACKGROUND:
                        tile = sector.getBgTile(x, y);
                        break;

                    case Layer.BOTH:
                        tile = sector.getFgTile(x, y);
                        if (tile == 255) {
                            tile = sector.getBgTile(x, y);
                        }
                        break;
                    case Layer.FOREGROUND:
                        tile = sector.getFgTile(x, y);
                        break;

                    case Layer.SPRITES:
                        tile = sector.getSpriteTile(x, y);
                        break;

                    default:
                        break;
                }

                //tile = sector.getLayers().get(layer)[y][x];
            }
        }

        return tile;
    }

    /**
     * Gets tile from an area, should be used with ToolSelection.
     * <p>
     * selectionRect's values should be in sector coordinates. Meaning they should be x >= 0; x < MAP_WIDTH, y >= 0; y < MAP_HEIGHT
     */
    public PK2TileArray getTilesFromRect(Rectangle selectionRect, int layer) {
        // TODO FIX: For some reason this can throw: java.lang.NegativeArraySizeException: -2 even though it should never be negative to begin with?!
        PK2TileArray tempSelection = new PK2TileArray(selectionRect.width, selectionRect.height);

        for (int sx = 0; sx < selectionRect.width; sx++) {
            for (int sy = 0; sy < selectionRect.height; sy++) {
                tempSelection.set(sx, sy, getTileAt(layer, selectionRect.x + sx, selectionRect.y + sy));
            }
        }

        return tempSelection;
    }

    /**
     * Gets tiles from an area. x and y position need to be in screen coordinates. They will be divided by the tile size (32)
     *
     * @param x
     * @param y
     * @param width
     * @param height
     * @param layer
     * @return
     */

    public PK2TileArray getTilesFromArea(int x, int y, int width, int height, int layer){
        PK2TileArray tempSelection = new PK2TileArray(width, height);

        x /= 32;
        y /= 32;

        for (int sx = 0; sx < width; sx++) {
            for (int sy = 0; sy < height; sy++) {
                tempSelection.set(sx, sy, getTileAt(layer, x + sx, y + sy));
            }
        }

        return tempSelection;

    }

    public void removeTilesArea(Rectangle area, int layer) {
        for (int sx = 0; sx < area.width; sx++) {
            for (int sy = 0; sy < area.height; sy++) {
                // sector.getLayers().get(layer)[area.y + sy][area.x + sx] = 255;
                setTileAt(layer, area.x + sx, area.y + sy, 255);
            }
        }

        // TODO Handle UndoManager
    }

    public int getSpriteAt(Point position) {
        return getSpriteAt(position.x, position.y);
    }

    public int getSpriteAt(int x, int y) {
        int spr = selection.getFirstSprite();

        if (sector != null) {
            spr = sector.getSpriteTile(x / 32, y / 32);
        }

        return spr;
    }

    public PK2TileArray getSpritesFromRect(Rectangle selectionRect) {

        PK2TileArray tempSelection =  new PK2TileArray(selectionRect.width, selectionRect.height);

        for (int sx = 0; sx < selectionRect.width; sx++) {
            for (int sy = 0; sy < selectionRect.height; sy++) {
                tempSelection.set(sx, sy, getSpriteAt((selectionRect.x + sx) * 32, (selectionRect.y + sy) * 32));
            }
        }

        return tempSelection;
    }

    public void placeSprite(Point position, int newSpriteID) {
        TileUtils.convertToMapCoordinates(position);

        if (position.x >= 0 && position.x <= sector.getWidth() && position.y >= 0 && position.y <= sector.getHeight()) {
            int oldSpriteID = sector.getSpriteTile(position.x, position.y);

            if (newSpriteID != 255) {
                level.getSprite(newSpriteID).increasePlacedAmount();
            }

            if (oldSpriteID != 255) {
                level.getSprite(oldSpriteID).decreasePlacedAmount();
            }

            spritePlacementListener.placed(newSpriteID);
            sector.setSpriteTile(position.x, position.y, newSpriteID);

            changeListener.stateChanged(changeEvent);
        }
    }

    public void placeSprite(Point position) {
        placeSprite(position, selection.getFirstSprite());
    }

    public void placeSpritesScreen(int x, int y, PK2TileArray sprites) {
        placeSprites(x / 32, y / 32, sprites);
    }

    /**
     * Places sprites on the sector, coordinates are within sector bounds x: >=0, < 256, y: >=0, < 224
     *
     * @param x
     * @param y
     * @param spritesLayer
     */
    public void placeSprites(int x, int y, PK2TileArray spritesLayer) {
        for (int sy = 0; sy < spritesLayer.getHeight(); sy++) {
            for (int sx = 0; sx < spritesLayer.getWidth(); sx++) {
                int xAdjusted = x + sx;
                int yAdjusted = y + sy;

                sector.setSpriteTile(xAdjusted, yAdjusted, spritesLayer.get(sx, sy));
            }
        }
    }

    public PK2TileArray getSpritesFromArea(int x, int y, int width, int height){
        PK2TileArray sprites = new PK2TileArray(width, height);

        x /= 32;
        y /= 32;

        for (int yy = 0; yy < height; yy++) {
            for (int xx = 0; xx < width; xx++) {
                sprites.set(xx, yy, sector.getSpriteTile(x + xx, y + yy));
            }
        }

        return sprites;
    }

    public void removeSpritesArea(Rectangle area) {
        for (int sx = 0; sx < area.width; sx++) {
            for (int sy = 0; sy < area.height; sy++) {

                int xAdjusted = area.x + sx;
                int yAdjusted = area.y + sy;

                int oldSpriteID = sector.getSpriteTile(xAdjusted, yAdjusted);
                if (oldSpriteID != 255) {
                    level.getSprite(oldSpriteID).decreasePlacedAmount();
                }
                sector.setSpriteTile(area.x + sx, area.y + sy, 255); // TODO Handle undo
            }
        }

        // TODO Handle UndoManager
    }

    public void setSector(PK2LevelSector sector) {
        this.sector = sector;
    }

    public void setLevel(PK2Level level) {
        this.level = level;
    }

    public void setGrid(int x, int y) {
        this.gridX = x;
        this.gridY = y;
    }

    /*public void setCurrentLayer(int layer) {
        this.currentLayer = layer;
    }*/

    public void setTileChangeListener(TileChangeListener listener) {
        this.tileChangeListener = listener;
    }

    public void setChangeListener(ChangeListener listener) {
        this.changeListener = listener;
    }

    public void setSpritePlacementListener(SpritePlacementListener listener) {
        this.spritePlacementListener = listener;
    }
}
