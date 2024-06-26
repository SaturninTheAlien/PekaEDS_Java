package pekaeds.pk2.sprite;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;


import javax.imageio.ImageIO;

import pk2.sprite.Prototype;
import pk2.sprite.PrototypesHandler;

import org.tinylog.Logger;

import pekaeds.settings.Settings;
import pekaeds.util.GFXUtils;

public class SpriteReaderNative implements ISpriteReader {
    
    public static PrototypesHandler handler = null;

    private BufferedImage mProcessSpriteImage(ISpritePrototypeEDS spriteProto,
        BufferedImage image, BufferedImage backgroundImage){
        
        GFXUtils.adjustSpriteColor(image, spriteProto.getColor());
        if (backgroundImage != null) {
            image = GFXUtils.setPaletteToBackgrounds(image, backgroundImage);
        } else {
            image = GFXUtils.makeTransparent(image);
        }

        image = GFXUtils.getFirstSpriteFrame(spriteProto, image);
        return image;
    }
    @Override
    public ISpritePrototypeEDS loadImageData(File filename){
        return this.loadImageData(filename, null, null);
    }

    @Override
    public ISpritePrototypeEDS loadImageData(File filename, String episode_dir){
        return this.loadImageData(filename, episode_dir, null);
    }

    @Override
    public ISpritePrototypeEDS loadImageData(File filename, String episode_dir, BufferedImage backgroundImage){
        Prototype prototype = handler.loadPrototype(filename.getName());

        if(prototype!=null){

            SpritePrototypeEDS prototypeEDS = new SpritePrototypeEDS(prototype);

            BufferedImage image = null;

            try{
                File imgFile = null;
                if(episode_dir!=null){
                    String imageStr = pk2.PekkaKana2.findAsset(episode_dir + File.separatorChar + prototype.getTextureName(), "sprites");
                    if(imageStr!=null){
                        imgFile = new File(imageStr);
                        if(!imgFile.isAbsolute()){
                            imgFile = Paths.get(Settings.getBasePath(), imageStr).toFile();
                        }
                    }
                }
                else{
                    imgFile = Paths.get(Settings.getSpritesPath(), prototype.getTextureName()).toFile();
                }

                if(imgFile!=null && imgFile.exists() && imgFile.isFile()){
                    image = mProcessSpriteImage(prototypeEDS, ImageIO.read(imgFile), backgroundImage);
                }
            }
            catch(IOException e){
                Logger.warn(e, "Unable to load sprite image data.");
            }

            if(image==null){
                image = PK2SpriteMissing.getMissingImage();
            }

            prototypeEDS.setImage(image);

            return prototypeEDS;
        } 

        return null;
    }
}
