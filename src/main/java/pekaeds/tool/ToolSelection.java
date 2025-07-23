package pekaeds.tool;

import java.awt.*;

public final class ToolSelection {
    private Point start;
    private Point end;

    /**
     * List containing the tile layers.
     *
     * Foreground = 0
     * Background = 1
     */
    private int[][] tileFGSelection;
    private int[][] tileBGSelection;

    private int[][] spritesSelection;

    private int width;
    private int height;

    private Rectangle rect = new Rectangle();
    
    public void reset() {
        tileFGSelection = new int[1][1];
        tileBGSelection = new int[1][1];
        spritesSelection = new int[1][1];

        width = 1;
        height = 1;
        
        rect.setRect(0, 0, 0, 0);
    }

    public void setTileFGSelection(int[][] selection) {
        tileFGSelection = selection;
        tileBGSelection = selection;
        setDimensions(selection);
    }

    public int[][] getTileFGSelection() {
        return tileFGSelection;
    }


    public void setTileBGSelection(int[][] selection) {
        tileBGSelection = selection;
    }

    public int[][] getTileBGSelection() {
        return tileBGSelection;
    }

    public void setSelectionSprites(int[][] selection) {
        this.spritesSelection = selection;

        setDimensions(selection);
    }

    @Deprecated
    public int[][] getTileSelection(int layer) {
        return tileFGSelection;
    }


    public int getFirstTile(){
        return this.tileFGSelection[0][0];
    }

    public int[][] getSelectionSprites() {
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

    private void setDimensions(int[][] selection) {
        width = selection[0].length;
        height = selection.length;
    }
}
