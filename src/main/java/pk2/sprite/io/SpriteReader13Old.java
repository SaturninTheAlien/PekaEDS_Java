package pk2.sprite.io;

import java.io.*;

import pk2.settings.Settings;
import pk2.sprite.SpritePrototype;
import pk2.util.PK2FileUtils;

class SpriteReader13Old implements SpriteReader {

    @Override
    public SpritePrototype readSpriteFile(File file) throws IOException {

        var spr = new SpriteOld();        
        DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
        in.readNBytes(4); // Skip the magic number
        
        spr.setType(Integer.reverseBytes(in.readInt()));

        spr.setImageFile(PK2FileUtils.readString(in, Settings.getSpriteProfile().getStringLengthFiles()));
        
        // Skip sound files
        for (int i = 0; i < Settings.getSpriteProfile().getAmountOfSounds(); i++) {
            in.readNBytes(100);
        }
        
        // Skip unused data
        for (int i = 0; i < Settings.getSpriteProfile().getAmountOfSounds(); i++) {
            in.readInt();
        }
        
        // Not used
        spr.setFramesAmount((int) in.readByte() & 0xFF);
        
        // Skip animation data
        for (int i = 0; i < Settings.getSpriteProfile().getAmountOfAnimations(); i++) {
            for (int j = 0; j < 10; j++) {
                in.readByte();
            }
            
            in.readByte();
            in.readBoolean();
        }
        
        in.readByte(); // animations amount of sprite
        in.readByte(); // frame rate
        
        in.readByte(); // Unknown byte, maybe padding?
        
        spr.setFrameX(Integer.reverseBytes(in.readInt()));
        spr.setFrameY(Integer.reverseBytes(in.readInt()));
        
        spr.setFrameWidth(Integer.reverseBytes(in.readInt()));
        spr.setFrameHeight(Integer.reverseBytes(in.readInt()));
        
        in.readInt(); // Frame distance, doesn't seem to be used.
        
        spr.setName(PK2FileUtils.readString(in, Settings.getSpriteProfile().getStringLengthName()));

        in.readInt(); // width
        in.readInt(); // height
        
        in.readDouble(); // weight
        
        in.readBoolean(); // is enemy?
        
        in.readByte(); // unused
        in.readByte(); // unused
        in.readByte(); // unused
        
        in.readInt(); // energy
        in.readInt(); // damage
        
        in.readByte(); // damage type
        in.readByte(); // immunity
        
        in.readByte(); // unused
        in.readByte(); // unused
        
        in.readInt(); // score
        
        for (int i = 0; i < 10; i++) {
            in.readInt(); // AI
        }
        
        in.readByte(); // maxJump
        
        in.readByte(); // Unused
        in.readByte(); // Unused
        in.readByte(); // Unused
        
        in.readDouble(); // max speed
        
        in.readInt(); // loading time
        
        spr.setColor(in.readByte() & 0xFF);
        
        spr.setFilename(file.getName());
        
        return spr;
    }
    
}
