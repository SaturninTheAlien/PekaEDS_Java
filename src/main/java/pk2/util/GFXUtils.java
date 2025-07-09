package pk2.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;

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
}
