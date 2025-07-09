package pk2.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.tinylog.Logger;

import pk2.filesystem.PK2FileSystem;
import pk2.sprite.PK2Sprite;
import pk2.sprite.SpritePrototype;

public final class GFXUtils {
    private GFXUtils() {
    }

    public static BufferedImage setPaletteToBackgrounds(BufferedImage targetImage, final BufferedImage backgroundImage) {
        if (backgroundImage != null) {
            var palette = (IndexColorModel) backgroundImage.getColorModel();

            var rs = new byte[256];
            var gs = new byte[256];
            var bs = new byte[256];
            palette.getReds(rs);
            palette.getGreens(gs);
            palette.getBlues(bs);

            // Make the last color in the palette transparent, like in the game.
            var cm = new IndexColorModel(8, 256, rs, gs, bs, 255);

            var raster = targetImage.getRaster();
            targetImage = new BufferedImage(targetImage.getWidth(), targetImage.getHeight(), BufferedImage.TYPE_BYTE_INDEXED, cm);
            targetImage.setData(raster);
        }

        return targetImage;
    }

    public static BufferedImage makeTransparent(BufferedImage image) {

        if(! (image.getColorModel() instanceof IndexColorModel)){
            return image;
        }

        var palette = (IndexColorModel) image.getColorModel();

        var rs = new byte[256];
        var gs = new byte[256];
        var bs = new byte[256];
        palette.getReds(rs);
        palette.getGreens(gs);
        palette.getBlues(bs);

        var colorModel = new IndexColorModel(8, 256, rs, gs, bs, 255);

        var tmpData = image.getRaster();
        var newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_INDEXED, colorModel);
        newImage.setData(tmpData);

        return newImage;
    }

    public static BufferedImage getFirstSpriteFrame(SpritePrototype sprite) {
        return getFirstSpriteFrame(sprite, sprite.getImage());
    }

    public static BufferedImage getFirstSpriteFrame(SpritePrototype spr, BufferedImage spriteSheet) {
        return spriteSheet.getSubimage(spr.getFrameX(), spr.getFrameY(), spr.getFrameWidth(), spr.getFrameHeight());
    }

    public static void adjustSpriteColor(BufferedImage spriteSheet, int paletteIndex) {
        if (paletteIndex != 255) {
            var data = ((DataBufferByte) spriteSheet.getRaster().getDataBuffer()).getData();

            for (int i = 0; i < spriteSheet.getWidth() * spriteSheet.getHeight(); i++) {
                int color = data[i] & 0xFF;

                if (color != 255) {

                    color %= 32;
                    color += paletteIndex;

                    data[i] = (byte) color;
                }
            }
        }
    }

    
    public static List<BufferedImage> cutFrames(BufferedImage spriteSheet, int framesAmount, int frameStartX, int frameStartY, int frameWidth, int frameHeight) {
        var subImages = new ArrayList<BufferedImage>();
        
        int x = frameStartX;
        int y = frameStartY;
        for (int i = 0; i < framesAmount; i++) {
            if (x + frameWidth > spriteSheet.getWidth()) {
                y += frameHeight + 3;
                x = frameStartX;
            }
            
            if (x + frameWidth < spriteSheet.getWidth()) {
                if (y + frameHeight < spriteSheet.getHeight()) {
                    subImages.add(spriteSheet.getSubimage(x, y, frameWidth, frameHeight));
                }
            }
            
            x += frameWidth + 3;
        }
        
        return subImages;
    }
    
    /**
     * Loads the first frame from the sprite sheet, recolors it if necessary, and sets it to the sprites image via sprite.setImage(). Can be retrieved by sprite.getImage().
     * 
     */
    public static void loadFirstFrame(PK2Sprite sprite) throws IOException {
        File file = PK2FileSystem.findAsset(sprite.getImageFile(), PK2FileSystem.SPRITES_DIR);
        var img = ImageIO.read(file);
            
        img = makeTransparent(img);
        adjustSpriteColor(img, sprite.getColor());
        
        img = img.getSubimage(sprite.getFrameX(), sprite.getFrameY(), sprite.getFrameWidth(), sprite.getFrameHeight());
        
        sprite.setImage(img);
    }
    
    public static void loadSpriteImageSheet(PK2Sprite sprite) throws IOException {

        try {

            File file = PK2FileSystem.findAsset(sprite.getImageFile(), PK2FileSystem.SPRITES_DIR);
            var spriteImageSheet = ImageIO.read(file);
            adjustSpriteColor(spriteImageSheet, sprite.getColor());
            spriteImageSheet = makeTransparent(spriteImageSheet);
            sprite.setImage(spriteImageSheet);
        }
        catch (IOException e) {
            Logger.error(e, "Unable to load first frame for sprite. Image file: '" + sprite.getImageFile() + "'");
        }
    }
}
