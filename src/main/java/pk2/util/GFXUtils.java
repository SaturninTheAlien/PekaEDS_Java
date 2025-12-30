package pk2.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.tinylog.Logger;

import pk2.filesystem.PK2FileSystem;
import pk2.sprite.PK2Sprite;
import pk2.sprite.SpritePrototype;
import pk2.sprite.io.SpriteMissing;

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

        try{
            return spriteSheet.getSubimage(spr.getFrameX(), spr.getFrameY(), spr.getFrameWidth(), spr.getFrameHeight());
        }
        catch(RasterFormatException e){


            StringBuilder builder = new StringBuilder();
            builder.append("Sprite: ");
            
            builder.append(spr.getName());

            builder.append(" (");
            builder.append(spr.getFilename());
            builder.append(" )");

            builder.append(" is badly cropped!");

            builder.append("\nImage size: ");
            builder.append(spriteSheet.getWidth());
            builder.append(" x ");
            builder.append(spriteSheet.getHeight());
            builder.append(" pixels\n");

            builder.append("Rect: ");
            builder.append("x: ");
            builder.append(spr.getFrameX());
            builder.append(" y: ");
            builder.append(spr.getFrameY());
            builder.append(" w: ");
            builder.append(spr.getFrameWidth());
            builder.append(" h: ");
            builder.append(spr.getFrameHeight());

            Logger.error(builder.toString());
            return SpriteMissing.getMissingTextureImage();
        }        
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
        loadSpriteImageSheet(sprite, PK2FileSystem.findAsset(sprite.getImageFile(), PK2FileSystem.SPRITES_DIR));
    }


    public static void loadSpriteImageSheet(PK2Sprite sprite, File file) throws IOException{
        var spriteImageSheet = ImageIO.read(file);
        adjustSpriteColor(spriteImageSheet, sprite.getColor());
        spriteImageSheet = makeTransparent(spriteImageSheet);
        sprite.setImage(spriteImageSheet);

        try{
            sprite.setFramesList(GFXUtils.cutFrames(sprite.getImage(), sprite.getFramesAmount(), sprite.getFrameX(), sprite.getFrameY(), sprite.getFrameWidth(), sprite.getFrameHeight()));
        }
        catch(RasterFormatException e){
            JOptionPane.showMessageDialog(null, "Your sprite could be badly cropped!","Cannot cut sprite frames!", JOptionPane.WARNING_MESSAGE);
            sprite.setFramesList(new ArrayList<BufferedImage>());
        }
    }
}
