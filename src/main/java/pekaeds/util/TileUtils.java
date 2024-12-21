package pekaeds.util;

import java.awt.*;

import pekaeds.pk2.map.PK2MapSector;

public final class TileUtils {
    private TileUtils() {}
    
    /**
     * Creates and returns an instance of the Rectangle class, which contains the start position and the width and height in tiles.
     * <p>
     * In tiles meaning the resulting value is something between 0 and MAP_WIDTH/HEIGHT.
     * @param selectionStart
     * @param selectionEnd
     * @return
     */
    public static Rectangle calculateSelectionRectangle(Point selectionStart, Point selectionEnd, final PK2MapSector sector) {
        int startX = selectionStart.x;
        int startY = selectionStart.y;
    
        int endX = selectionEnd.x;
        int endY = selectionEnd.y;
    
        // If selection goes in the negative direction, to the left, swap start and end values.
        if (selectionStart.x > selectionEnd.x) {
            startX = selectionEnd.x;
            endX = selectionStart.x;
        }
    
        if (selectionStart.y > selectionEnd.y) {
            startY = selectionEnd.y;
            endY = selectionStart.y;
        }
    
        startX /= 32;
        startY /= 32;
    
        endX /= 32;
        endY /= 32;

        // Keep selection within bounds
        if (startX < 0) startX = 0;
        if (startY < 0) startY = 0;

        if (endX >= sector.getWidth()) endX = sector.getWidth() - 1;
        if (endY >= sector.getHeight()) endY = sector.getHeight() - 1;

        int selectionWidth = endX - startX;
        int selectionHeight = endY - startY;

        return new Rectangle(startX, startY, selectionWidth + 1, selectionHeight + 1);
    }
    
    /**
     * Same as calculateSelectionRectangle, but instead of returning the "local" position in the level (in range of x: 0-256, y: 0-224), it gets the position of the actual game scene.
     * @param selectionStart
     * @param selectionEnd
     * @return
     */
    public static Rectangle calculateSelectionRectangleInScene(Point selectionStart, Point selectionEnd, PK2MapSector sector) {
        var r = calculateSelectionRectangle(selectionStart, selectionEnd, sector);
        
        r.x *= 32;
        r.y *= 32;
        
        return r;
    }
    
    public static Point findPlayerStartPosition(int[][] foregroundLayer) {
        for (int y = 0; y < foregroundLayer.length; y++) {
            for (int x = 0; x < foregroundLayer[0].length; x++) {
                if (foregroundLayer[y][x] == 148) {
                    return new Point(x, y);
                }
            }
        }
    
        return new Point(0, 0);
    }

    public static Rectangle calculateOffsets(final int[] layer, int layerWidth, int layerHeight) {
        int startX = layerWidth;
        int width = 0;
        int startY = layerHeight;
        int height = 0;

        for (int y = 0; y < layerHeight; y++) {
            for (int x = 0; x < layerWidth; x++) {
                if (layer[layerWidth * y + x] != 255) {
                    if (x < startX) {
                        startX = x;
                    }

                    if (y < startY) {
                        startY = y;
                    }

                    if (x > width) {
                        width = x;
                    }

                    if (y > height) {
                        height = y;
                    }
                }
            }
        }

        if (width < startX || height < startY) {
            startX = 0;
            startY = 0;

            height = 1;
            width = 1;
        }

        return new Rectangle(startX, startY, width - startX, height - startY);
    }

    /**
     * Calculate tile id from tile position in the tileset.
     * <p></p>
     * For example:
     *   tileId = 56
     *   x = 192
     *   y = 160
        
     *   x / 32 = 6
     *   y / 32 * 10 = 50
     * <p></p>
     *   x + y = 56
     */
    public static int getTileIdFromTilesetPosition(final Point pos) {
        return (pos.x / 32) + ((pos.y / 32) * 10);
    }
    
    
    /**
     * Convenience method that divides and multiplies the x and y of a Point object to align it with the tile grid.
     * @param point Point object to adjust.
     */
    public static void alignPointToGrid(Point point) {
        point.x = point.x / 32 * 32;
        point.y = point.y / 32 * 32;
    }

    public static void convertToMapCoordinates(Point point) {
        alignPointToGrid(point); // TODO is this necessary?
        
        point.x /= 32;
        point.y /= 32;
    }
}
