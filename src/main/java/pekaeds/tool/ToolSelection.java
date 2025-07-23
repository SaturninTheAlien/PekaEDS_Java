package pekaeds.tool;

import java.awt.*;

import pk2.level.PK2TileArray;

public final class ToolSelection {
    private Point start;
    private Point end;

    /**
     * List containing the tile layers.
     *
     * Foreground = 0
     * Background = 1
     */

    private PK2TileArray tileFGSelection;
    private PK2TileArray tileBGSelection;
    private PK2TileArray spritesSelection;

    private int width;
    private int height;

    private Rectangle rect = new Rectangle();
    
    public void reset() {
        tileFGSelection =  PK2TileArray.singleTile(0);
        tileBGSelection =  null;
        spritesSelection =  PK2TileArray.singleTile(0);

        width = 1;
        height = 1;
        
        rect.setRect(0, 0, 0, 0);
    }

    public void setTileFGSelection(PK2TileArray selection) {
        tileFGSelection = selection;
        tileBGSelection = selection;
        setDimensions(selection);
    }

    public PK2TileArray getTileFGSelection() {
        return tileFGSelection;
    }


    public void setTileBGSelection(PK2TileArray selection) {
        tileBGSelection = selection;
    }

    public PK2TileArray getTileBGSelection() {
        return tileBGSelection;
    }

    public void setSelectionSprites(PK2TileArray selection) {
        this.spritesSelection = selection;

        setDimensions(selection);
    }

    @Deprecated
    public PK2TileArray getTileSelection(int layer) {
        return tileFGSelection;
    }

    public int getFirstTile(){
        return  this.tileFGSelection.get(0, 0);
    }

    public int getFirstSprite(){
        return this.spritesSelection.get(0, 0);
    }

    public PK2TileArray getSelectionSprites() {
        return spritesSelection;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int w) { this.width = w; }
    public void setHeight(int h) { this.height = h;}

    public void setStart(Point p) {
        this.start = p;
    }

    public Point getStart() {
        return start;
    }

    public void setEnd(Point p) {
        this.end = p;
    }

    public Point getEnd() {
        return end;
    }

    private void setDimensions(PK2TileArray selection) {
        width = selection.getWidth();
        height = selection.getHeight();
    }
}
