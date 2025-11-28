package pekaeds.ui.mappanel;

import java.awt.*;
import java.awt.image.BufferedImage;

import pekaeds.data.Layer;
import pk2.level.PK2Level;
import pk2.level.PK2LevelSector;
import pk2.settings.Settings;
import pk2.sprite.SpritePrototype;

// TODO Optimize drawing
public class MapPanelPainter {
    private MapPanel mapPanel;

    private final Composite compAlphaHalf = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
    private final Composite compAlphaFull = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);

    private Rectangle mapViewport = new Rectangle();

    public MapPanelPainter(MapPanel mp) {
        mapPanel = mp;
    }

    public void drawBackground(Graphics2D g) {
        if (mapPanel.sector().getBackgroundImage() != null) {
            for (int x = 0; x < mapPanel.getBgRepeatX(); x++) {
                for (int y = 0; y < mapPanel.getBgRepeatY(); y++) {
                    g.drawImage(mapPanel.sector().getBackgroundImage(),
                            x * mapPanel.sector().getBackgroundImage().getWidth(),
                            y * mapPanel.sector().getBackgroundImage().getHeight(),
                            null);
                }
            }
        }
    }

    public void drawLayers(Graphics2D g) {
        if (mapPanel.sector() != null) {
            int currentLayer = mapPanel.getSelectedLayer();

            final int[] foregroundLayer = mapPanel.sector().getForegroundLayer();
            BufferedImage foregroundTilesetImage = mapPanel.sector().getTilesetImage();

            switch (currentLayer) {
                case Layer.FOREGROUND -> {

                    if(Settings.showTransparentLayers){
                        g.setComposite(compAlphaHalf);
                        drawBackgroundLayer(g);
                    }

                    g.setComposite(compAlphaFull);
                    drawLayer(g, foregroundLayer, foregroundTilesetImage);
                }

                case Layer.BACKGROUND -> {
                    if(Settings.showTransparentLayers){
                        g.setComposite(compAlphaHalf);
                        drawLayer(g, foregroundLayer, foregroundTilesetImage);
                    }
                    g.setComposite(compAlphaFull);
                    drawBackgroundLayer(g);
                }

                case Layer.BOTH -> {
                    g.setComposite(compAlphaFull);
                    drawBackgroundLayer(g);

                    g.setComposite(compAlphaFull);
                    drawLayer(g, foregroundLayer, foregroundTilesetImage);
                }
            }
        }
    }

    private void drawBrokenSlope(Graphics2D g, int posX, int posY){

        int x = 32 * posX;
        int y = 32 * posY;
        int width = 32;
        int height = 32;

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
    
        g.setColor(Color.YELLOW);
        g.fillRect(x, y, 32, 32);
        
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));

        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height); // Draw outer black part
        g.drawRect(x + 2, y + 2, width - 4, height - 4); // Draw inner black part
        
        // Draw white middle part
        g.setColor(Color.YELLOW);
        g.drawRect(x + 1, y + 1, width - 2, height - 2);
    }



    public void drawBrokenSlopes(Graphics2D g){
        if(this.mapPanel.sector() != null){


            final PK2LevelSector sector = this.mapPanel.sector();
            final int width  = mapPanel.sector().getWidth();
            final int height = mapPanel.sector().getHeight();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if(sector.checkBrokenSlope(x, y)){
                        this.drawBrokenSlope(g, x, y);
                    }
                }
            }   
        }
    }

    private void drawBackgroundLayer(Graphics2D g) {
        final int[] layer = mapPanel.sector().getBackgroundLayer();

        if (mapPanel.sector().getBackgroundTilesetImage() != null) {
            drawLayer(g, layer, mapPanel.sector().getBackgroundTilesetImage());
        } else {
            drawLayer(g, layer, mapPanel.sector().getTilesetImage());
        }
    }

    public void drawLayer(Graphics2D g, final int[] layer, BufferedImage tileset) {
        int sectorWidth = mapPanel.sector().getWidth();
        int sectorHeight = mapPanel.sector().getHeight();

        for (int y = 0; y < sectorHeight; y++) {
            for (int x = 0; x < sectorWidth; x++) {
                /*
                     screenX and Y are the coordinates of the tiles on the mapPanel itself.
                     The mapPanel itself is only as big as the JPanel, what you see.

                     viewX and viewY are between 0 and sectorWidth/sectorHeight.
                     screenX and Y are between 0 and mapPanel.getWidth()/getHeight().

                     This is done for optimization purposes, in case the user has a huge sector (Like 1000 tiles by 1000 tiles).
                 */
                int screenX = (x - mapViewport.x) * 32;
                int screenY = (y - mapViewport.y) * 32;

                drawTile(g, screenX, screenY, layer[sectorWidth * y + x], tileset);
            }
        }
    }

    public void drawBackgroundSprites(Graphics2D g) {
        if (mapPanel.sector() != null) {

            PK2LevelSector sector = mapPanel.sector();
            PK2Level map = mapPanel.map();

            for (int x = 0; x < sector.getWidth(); x++) {
                for (int y = 0; y < sector.getHeight(); y++) {
                    int spriteTile = sector.getSpriteTile(x, y);

                    if (spriteTile != 255 && spriteTile < map.getSpriteList().size()) {
                        var spr = map.getSprite(spriteTile);

                        if (spr.getType() == SpritePrototype.TYPE_BACKGROUND) {
                            drawSprite(g, spr, (x - mapViewport.x) * 32, (y - mapViewport.y) * 32);
                        }
                    }
                }
            }
        }
    }

    public void drawForegroundSprites(Graphics2D g) {
        if (mapPanel.sector() != null) {
            PK2LevelSector sector = mapPanel.sector();
            PK2Level map = mapPanel.map();

            for (int x = 0; x < sector.getWidth(); x++) {
                for (int y = 0; y < sector.getHeight(); y++) {
                    int spriteTile = sector.getSpriteTile(x, y);

                    if (spriteTile != 255 && spriteTile < map.getSpriteList().size()) {
                        var spr = map.getSprite(spriteTile);

                        if (spr.getType() == SpritePrototype.TYPE_FOREGROUND) {
                            drawSprite(g, spr, (x - mapViewport.x) * 32, (y - mapViewport.y) * 32);
                        }
                    }
                }
            }
        }
    }

    public void drawRegularSprites(Graphics2D g) {
        PK2LevelSector sector = mapPanel.sector();
        PK2Level map = mapPanel.map();

        if (sector != null) {
            for (int x = 0; x < sector.getWidth(); x++) {
                for (int y = 0; y < sector.getHeight(); y++) {
                    int tile = sector.getSpriteTile(x, y);

                    if (tile != 255 && tile < map.getSpriteList().size()) {
                        var spr = map.getSprite(tile);

                        int type = spr.getType();
                        if (type != SpritePrototype.TYPE_BACKGROUND && type != SpritePrototype.TYPE_FOREGROUND) {
                            drawSprite(g, spr, (x - mapViewport.x) * 32, (y - mapViewport.y) * 32);
                        }
                    }
                }
            }
        }
    }
    /*
    Javadoc:
        https://docs.oracle.com/javase/7/docs/api/java/awt/Graphics.html#drawImage(java.awt.Image,%20int,%20int,%20int,%20int,%20int,%20int,%20int,%20int,%20java.awt.image.ImageObserver)

        img - the specified image to be drawn. This method does nothing if img is null.
        dx1 - the x coordinate of the first corner of the destination rectangle.
        dy1 - the y coordinate of the first corner of the destination rectangle.
        dx2 - the x coordinate of the second corner of the destination rectangle.
        dy2 - the y coordinate of the second corner of the destination rectangle.
        sx1 - the x coordinate of the first corner of the source rectangle.
        sy1 - the y coordinate of the first corner of the source rectangle.
        sx2 - the x coordinate of the second corner of the source rectangle.
        sy2 - the y coordinate of the second corner of the source rectangle.
        observer - object to be notified as more of the image is scaled and converted.
     */

    public void drawSprite(Graphics2D g, SpritePrototype spr, int x, int y) {
        int sprX = x - (spr.getFrameWidth() / 2) + 16;
        int sprY = y - (spr.getFrameHeight() - 32);
        g.drawImage(mapPanel.sector().getSpriteImage(spr.getImageFileIdentifier()),
                sprX,
                sprY,
                sprX + spr.getFrameWidth(),
                sprY + spr.getFrameHeight(),

                spr.getFrameX(), spr.getFrameY(),
                spr.getFrameX() + spr.getFrameWidth(),
                spr.getFrameY() + spr.getFrameHeight(),

                null);
    }

    public void drawSpriteHighlights(Graphics2D g) {
        PK2LevelSector sector = mapPanel.sector();

        for (int x = 0; x < sector.getWidth(); x++) {
            for (int y = 0; y < sector.getHeight(); y++) {
                if (sector.getSpriteTile(x, y) != 255) {
                    g.setColor(Color.WHITE);
                    g.drawRect((x - mapViewport.x) * 32, (y - mapViewport.y) * 32, 32, 32);
                }
            }
        }
    }

    /*
    Javadoc:
        https://docs.oracle.com/javase/7/docs/api/java/awt/Graphics.html#drawImage(java.awt.Image,%20int,%20int,%20int,%20int,%20int,%20int,%20int,%20int,%20java.awt.image.ImageObserver)
        
        img - the specified image to be drawn. This method does nothing if img is null.
        dx1 - the x coordinate of the first corner of the destination rectangle.
        dy1 - the y coordinate of the first corner of the destination rectangle.
        dx2 - the x coordinate of the second corner of the destination rectangle.
        dy2 - the y coordinate of the second corner of the destination rectangle.
        sx1 - the x coordinate of the first corner of the source rectangle.
        sy1 - the y coordinate of the first corner of the source rectangle.
        sx2 - the x coordinate of the second corner of the source rectangle.
        sy2 - the y coordinate of the second corner of the source rectangle.
        observer - object to be notified as more of the image is scaled and converted.
     */
    public void drawTile(Graphics2D g, int x, int y, int tile, BufferedImage tileset) {
        if (tileset != null && tile != 255) {
            int tileX = (tile % 10) * 32;
            int tileY = (tile / 10) * 32;

            g.drawImage(tileset, x, y, x + 32, y + 32, tileX, tileY, tileX + 32, tileY + 32, null);
        }
    }

    // Method used in the Tool subclasses, they don't care which tileset is supposed to be used.
    public void drawTile(Graphics2D g, int x, int y, int tile) {
        if (mapPanel.sector().getBackgroundTilesetImage() != null && mapPanel.getSelectedLayer() == Layer.BACKGROUND) {
            drawTile(g, x, y, tile, mapPanel.sector().getBackgroundTilesetImage());
        } else {
            drawTile(g, x, y, tile, mapPanel.sector().getTilesetImage());
        }
    }

    void setMapViewportSize(int newWidth, int newHeight) {
        mapViewport.setSize(newWidth, newHeight);
    }

    public void setCursor(Cursor cursor) {
        mapPanel.setCursor(cursor);
    }

    public void repaint() {
        mapPanel.repaint();
    }
}
