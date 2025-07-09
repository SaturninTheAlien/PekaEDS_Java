package pk2.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.tinylog.Logger;

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

    public static void adjustSpriteColor(BufferedImage spriteSheet, int paletteIndex, int frameX, int frameY, int frameWidth, int frameHeight, int frameAmount) {
        if (paletteIndex != 255) {
            var data = ((DataBufferByte) spriteSheet.getRaster().getDataBuffer()).getData();
            int spriteSheetWidth = spriteSheet.getWidth();
            int col = 0;
            
            for (int j = 0; j < spriteSheet.getHeight(); j++) {
                for (int i = 0; i < spriteSheetWidth; i++) {
                    if ((col = data[i + j * spriteSheetWidth] & 0xFF) != 255) {
                        col &= 0xFF;
                        
                        col %= 32;
                        col += paletteIndex;
                        
                        data[i + j * spriteSheetWidth] = (byte) col;
                    }
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
     * @param sprite
     * @param pathToImage
     */
    public static void loadFirstFrame(PK2Sprite sprite, String pathToImage) {
        try {
            var img = ImageIO.read(new File(pathToImage + File.separatorChar + sprite.getImageFile()));
            
            img = makeTransparent(img);
            adjustSpriteColor(img, sprite.getColor(), sprite.getFrameX(), sprite.getFrameY(), sprite.getFrameWidth(), sprite.getFrameHeight(), sprite.getFramesAmount());
            
            img = img.getSubimage(sprite.getFrameX(), sprite.getFrameY(), sprite.getFrameWidth(), sprite.getFrameHeight());
            
            sprite.setImage(img);
        } catch (IOException e) {
            Logger.warn(e, "Unable to load first frame for sprite. Image file: '" + sprite.getImageFile() + "', provided path: '" + pathToImage + "'");
        }
    }
    
    public static void loadSpriteImageSheet(PK2Sprite spr, String gfxPath) throws IOException {
        var spriteImageSheet = ImageIO.read(new File(gfxPath + File.separatorChar + spr.getImageFile()));
        
        if (spr.getColor() != 255) {
            GFXUtils.adjustSpriteColor(spriteImageSheet, spr.getColor(), spr.getFrameX(), spr.getFrameY(), spr.getFrameWidth(), spr.getFrameHeight(), spr.getFramesAmount());
        }
        
        spr.setImage(GFXUtils.makeTransparent(spriteImageSheet));
    }
}
