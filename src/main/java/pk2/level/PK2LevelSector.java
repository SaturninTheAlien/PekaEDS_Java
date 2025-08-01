package pk2.level;

import pk2.sprite.SpritePrototype;
import pk2.sprite.io.SpriteMissing;
import pk2.util.GFXUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class PK2LevelSector {

    public static final int CLASSIC_WIDTH = 256;
    public static final int CLASSIC_HEIGHT = 224;

    public BufferedImage tilesetImage;
    public BufferedImage tilesetBgImage;
    private BufferedImage backgroundImage;

    public String name = "untitled";
    public String tilesetName;
    public String tilesetBgName;
    public String backgroundName;
    public String musicName;
    public String pk2stuffName;

    public int weather = 0;
    public int splash_color = -1; //based on the tileset
    public int fire_color_1 = 64;  //red
    public int fire_color_2 = 128; //orange

    public int rain_color = 40; //rain color

    public int background_scrolling = 0;

    private int[] backgroundLayer;
    private int[] foregroundLayer;
    private int[] spriteLayer;

    private int width;
    private int height;

    // Contains all the sprite sheets with their own palette, just adjusted for sprite color variants
    private static HashMap<String, BufferedImage> baseSpriteSheets = new HashMap<>();

    // Contains the sprite sheets that have the same palette as the background image
    private HashMap<String, BufferedImage> adjustedSpriteSheets = new HashMap<>();

    public PK2LevelSector(int sectorWidth, int sectorHeight) {
        width = sectorWidth;
        height = sectorHeight;

        backgroundLayer = new int[sectorWidth * sectorHeight];
        foregroundLayer = new int[sectorWidth * sectorHeight];
        spriteLayer = new int[sectorWidth * sectorHeight];

        Arrays.fill(backgroundLayer, 255);
        Arrays.fill(foregroundLayer, 255);
        Arrays.fill(spriteLayer, 255);
    }

    /**
     * Copy constructor
     */
    public PK2LevelSector(PK2LevelSector source){
        this.width = source.width;
        this.height = source.height;

        this.backgroundLayer = Arrays.copyOf(source.backgroundLayer, source.backgroundLayer.length);
        this.foregroundLayer = Arrays.copyOf(source.foregroundLayer, source.foregroundLayer.length);
        this.spriteLayer = Arrays.copyOf(source.spriteLayer, source.spriteLayer.length);

        this.name = source.name + " (copy)";

        this.tilesetImage = source.tilesetImage;
        this.tilesetBgImage = source.tilesetBgImage;
        this.backgroundImage = source.backgroundImage;

        this.tilesetName = source.tilesetName;
        this.tilesetBgName = source.tilesetBgName;
        this.backgroundName = source.backgroundName;
        this.musicName = source.musicName;
        
        this.weather = source.weather;
        this.splash_color = source.splash_color;
        this.fire_color_1 = source.fire_color_1;
        this.fire_color_2 = source.fire_color_2;

        this.background_scrolling = source.background_scrolling;
    }

    public void setSize(Rectangle rect) {
        setSize(rect.x, rect.y, rect.width, rect.height);
    }

    public void setSize(int startX, int startY, int newWidth, int newHeight) {
        if (width != newWidth || height != newHeight || startX != 0 || startY != 0) {
            this.foregroundLayer = this.resizeLayer(this.foregroundLayer, startX, startY, newWidth, newHeight);
            this.backgroundLayer = this.resizeLayer(this.backgroundLayer, startX, startY, newWidth, newHeight);
            this.spriteLayer = this.resizeLayer(this.spriteLayer, startX, startY, newWidth, newHeight);

            this.width = newWidth;
            this.height = newHeight;
        }
    }

    // I would use Arrays.copyOf here, but this method fills the empty parts of the array with 0 instead of 255. 0 is the first tile in the tileset, 255 is the "empty" tile
    private int[] resizeLayer(int[] layer, int startX, int startY, int newWidth, int newHeight) {

        int[] newLayer = new int[newWidth*newHeight];
        Arrays.fill(newLayer, 255);

        for(int y=0; y<this.height;++y){

            int new_y = y - startY;
            if(new_y>=0 && new_y < newHeight){

                for(int x=0; x<this.width; ++x){
                    int new_x = x - startX;
                    if(new_x>=0 && new_x < newWidth){
                        newLayer[new_y*newWidth + new_x] = layer[y*width + x];
                    }
                }
            }
        }

        return newLayer;
    }

    public void addSpriteSheet(SpritePrototype sprite) {
        BufferedImage spriteSheet = baseSpriteSheets.get(sprite.getImageFileIdentifier());

        BufferedImage image = new BufferedImage(spriteSheet.getColorModel(),
                spriteSheet.getRaster(),
                spriteSheet.isAlphaPremultiplied(),
                null);

        image = GFXUtils.setPaletteToBackgrounds(image, backgroundImage);
        image = GFXUtils.makeTransparent(image);

        //sprite.setImage(image);

        adjustedSpriteSheets.put(sprite.getImageFileIdentifier(), image);
    }

    public final BufferedImage getSpriteImage(String spriteImageIdentifier) {
        /**
         * Image identifiers starting with "!" are reserved for special purposes
         */
        if(spriteImageIdentifier!=null && spriteImageIdentifier.startsWith("!")){
            if(spriteImageIdentifier.equals("!missing")){
                return SpriteMissing.getMissingImage();
            }
            else if(spriteImageIdentifier.equals("!missing_texture")){
                return SpriteMissing.getMissingTextureImage();
            }
        }
        return adjustedSpriteSheets.get(spriteImageIdentifier);
    }


    public static void clearBaseSpriteSheets(){
        baseSpriteSheets.clear();
    }


    public static void registerSpriteSheet(String imageFileIdentifier, BufferedImage image) {

        if(imageFileIdentifier==null || imageFileIdentifier.startsWith("!")){
            return;
        }

        baseSpriteSheets.put(imageFileIdentifier, image);
    }

    public static boolean isSpriteSheetLoaded(String imageFileIdentifier) {
        return baseSpriteSheets.containsKey(imageFileIdentifier);
    }

    public void updateSpritePalettes(ArrayList<SpritePrototype> spriteList) {
        adjustedSpriteSheets.clear();

        for (SpritePrototype sprite : spriteList) {
            addSpriteSheet(sprite);
        }
    }

    public static BufferedImage getBaseSpriteSheet(String imageFileIdentifier) {
        return baseSpriteSheets.get(imageFileIdentifier);
    }

    public int getBGTile(int posX, int posY) {
        if (posX >= 0 && posX < width && posY >= 0 && posY < height) {
            return backgroundLayer[width * posY + posX];
        }

        return 255;
    }

    public int getFGTile(int posX, int posY) {
        if (posX >= 0 && posX < width && posY >= 0 && posY < height) {
            return foregroundLayer[width * posY + posX];
        }

        return 255;
    }

    public int getSpriteTile(int posX, int posY) {
        if (posX >= 0 && posX < width && posY >= 0 && posY < height) {
            return spriteLayer[width * posY + posX];
        }

        return 255;
    }

    public void removeSprite(int id) {
        for (int i = 0; i < spriteLayer.length; ++i) {
            if (spriteLayer[i] == id) {
                spriteLayer[i] = 255;
            } else if (spriteLayer[i] > id && spriteLayer[i] != 255) {
                spriteLayer[i] -= 1;
            }
        }
    }

    public void replaceSprite(int id, int newID){
        
        for (int i = 0; i < spriteLayer.length; ++i) {
            if (spriteLayer[i] == id) {
                spriteLayer[i] = newID;
            } else if (spriteLayer[i] > id && spriteLayer[i] != 255) {
                spriteLayer[i] -= 1;
            }
        }
    }

    public int countTiles(int id) {
        int result = 0;

        for (int i = 0; i < spriteLayer.length; ++i) {
            if (spriteLayer[i] == id) {
                ++result;
            }
        }

        return result;
    }

    public final int[] getForegroundLayer() {
        return foregroundLayer;
    }

    public final int[] getBackgroundLayer() {
        return backgroundLayer;
    }

    public final int[] getSpritesLayer() {
        return spriteLayer;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setBackgroundTile(int posX, int posY, int value) {
        backgroundLayer[width * posY + posX] = value;
    }

    public void setForegroundTile(int posX, int posY, int value) {
        foregroundLayer[width * posY + posX] = value;
    }

    public void setSpriteTile(int posX, int posY, int value) {
        spriteLayer[width * posY + posX] = value;
    }

    public void setBackgroundLayer(int[] newLayer) {
        backgroundLayer = newLayer;
    }

    public void setForegroundLayer(int[] newLayer) {
        foregroundLayer = newLayer;
    }

    public void setSpriteLayer(int[] newLayer) {
        spriteLayer = newLayer;
    }

    public String getTilesetName() {
        return tilesetName;
    }

    public String getBgTilesetName() {
        return tilesetBgName;
    }

    public void setBackgroundImage(BufferedImage image) {
        backgroundImage = image;
    }

    public String getBackgroundName() {
        return backgroundName;
    }

    public BufferedImage getTilesetImage() {
        return tilesetImage;
    }

    public BufferedImage getBackgroundTilesetImage() {
        return tilesetBgImage;
    }

    public BufferedImage getBackgroundImage() {
        return backgroundImage;
    }

    public BufferedImage getTilesetBgImage() {
        return tilesetBgImage;
    }
}
