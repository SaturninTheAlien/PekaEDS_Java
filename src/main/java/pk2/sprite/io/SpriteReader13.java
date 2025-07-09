package pk2.sprite.io;

import pk2.sprite.PK2Sprite;
import pk2.sprite.PK2SpriteAnimation;
import pekase3.util.PK2FileUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;

class SpriteReader13 {  
    /**
     * Loads the sprites data only.
     * @param filename
     * @return
     * @throws IOException
     */
    public PK2Sprite load(File filename) throws IOException, UnsupportedSpriteFormatException {
        var spr = new PK2Sprite();
        
        DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)));
        
        byte[] magicNumber = in.readNBytes(4);
        
        if (!Arrays.equals(magicNumber, PK2Sprite.VERSION_13)) {
            var sb = new StringBuilder();
            
            for (var b : magicNumber) {
                if (b != 0x0) sb.append((char) b);
            }
            in.close();
            throw new UnsupportedSpriteFormatException(sb.toString());
        }
        
        spr.setType(Integer.reverseBytes(in.readInt()));
        
        spr.setImageFile(PK2FileUtils.readString(in, 100));
        
        for (int i = 0; i < 7; i++) {
            spr.setSoundFile(PK2FileUtils.readString(in, 100), i);
        }
        
        // Skip unused data
        for (int i = 0; i < 7; i++) {
            in.readInt();
        }
        
        spr.setFramesAmount((int) in.readByte() & 0xFF);
        
        var animationsList = new ArrayList<PK2SpriteAnimation>();
        for (int i = 0; i < 20; i++) {
            byte[] sequence = new byte[10];
            for (int j = 0; j < 10; j++) {
                sequence[j] = in.readByte();
            }
            
            int frames = (int) in.readByte() & 0xFF;
            boolean loop = in.readBoolean();
            
            var animation = new PK2SpriteAnimation(sequence, frames, loop);
            
            animationsList.add(animation);
        }
        
        spr.setAnimationsList(animationsList);
        
        spr.setAnimationsAmount(in.readByte() & 0xFF); // Not actually used
        spr.setFrameRate(in.readByte() & 0xFF);
        
        in.readByte(); // Unknown byte, maybe padding?
        
        spr.setFrameX(Integer.reverseBytes(in.readInt()));
        spr.setFrameY(Integer.reverseBytes(in.readInt()));
        
        spr.setFrameWidth(Integer.reverseBytes(in.readInt()));
        spr.setFrameHeight(Integer.reverseBytes(in.readInt()));
        
        in.readInt(); // Frame distance, doesn't seem to be used.
        
        spr.setName(PK2FileUtils.readString(in, 30));
        
        // Padding? Misc bytes
        in.readByte();
        in.readByte();
        
        // Hitbox width and height
        spr.setWidth(Integer.reverseBytes(in.readInt()));
        spr.setHeight(Integer.reverseBytes(in.readInt()));
        
        double weight = in.readDouble();
        ByteBuffer b2 = ByteBuffer.allocate(8);
        b2.putDouble(weight);
        b2.order(ByteOrder.LITTLE_ENDIAN);
        
        spr.setWeight(b2.getDouble(0));
        
        spr.setEnemy(in.readBoolean());
        
        in.readByte(); // unused
        in.readByte(); // unused
        in.readByte(); // unused
        
        spr.setEnergy(Integer.reverseBytes(in.readInt())); // energy
        spr.setDamage(Integer.reverseBytes(in.readInt())); // damage
        
        spr.setDamageType(in.readByte() & 0xFF); // damage type
        spr.setImmunityToDamageType(in.readByte() & 0xFF); // immunity
        
        in.readByte(); // unused
        in.readByte(); // unused
        
        spr.setScore(Integer.reverseBytes(in.readInt())); // score
        
        ArrayList<Integer> aiList = new ArrayList<Integer>();
        for (int i = 0; i < 10; i++) {
            aiList.add(Integer.reverseBytes(in.readInt())); // AI
        }
        
        spr.setAiList(aiList);
        
        spr.setMaxJump(in.readByte() & 0xFF); // maxJump
        
        in.readByte(); // Unused
        in.readByte(); // Unused
        in.readByte(); // Unused
        
        double maxSpeed = in.readDouble();
        ByteBuffer b3 = ByteBuffer.allocate(8);
        b3.putDouble(maxSpeed);
        b3.order(ByteOrder.LITTLE_ENDIAN);
        spr.setMaxSpeed(b3.getDouble(0)); // max speed
        
        spr.setLoadTime(Integer.reverseBytes(in.readInt()));
        
        spr.setColor(in.readByte() & 0xFF);
        
        spr.setObstacle(in.readBoolean());
        
        in.readByte(); // Padding?
        in.readByte();

        int destruction = Integer.reverseBytes(in.readInt());
        
        spr.setDestruction(destruction);
        spr.setIndestructible(destruction==0);
        
        spr.setKey(in.readBoolean());
        
        spr.setShakes(in.readBoolean());
        
        spr.setBonusAmount(in.readByte() & 0xFF);
        
        in.readByte();
        
        spr.setAttack1Duration(Integer.reverseBytes(in.readInt()));
        spr.setAttack2Duration(Integer.reverseBytes(in.readInt()));
        
        spr.setParallaxFactor(Integer.reverseBytes(in.readInt()));
        
        spr.setTransformationSpriteFile(PK2FileUtils.readString(in, 100));
        spr.setBonusSpriteFile(PK2FileUtils.readString(in, 100));
        
        spr.setAttack1SpriteFile(PK2FileUtils.readString(in, 100));
        spr.setAttack2SpriteFile(PK2FileUtils.readString(in, 100));
        
        spr.setTileCheck(in.readBoolean());
        
        in.readByte();
        in.readByte();
        in.readByte();
        
        spr.setSoundFrequency(Integer.reverseBytes(in.readInt()));
        spr.setRandomSoundFrequency(in.readBoolean());
        
        spr.setWallUp(in.readBoolean());
        spr.setWallDown(in.readBoolean());
        spr.setWallRight(in.readBoolean());
        spr.setWallLeft(in.readBoolean());
        
        in.readByte(); // transparency - unused
        in.readByte(); // glow - unused
        in.readByte(); // ???
        
        spr.setAttackPause(Integer.reverseBytes(in.readInt()));
        
        spr.setGlide(in.readBoolean());
        spr.setBoss(in.readBoolean());
        spr.setAlwaysBonus(in.readBoolean());
        spr.setSwim(in.readBoolean());

        spr.setFilename(filename.getName());
        
        return spr;
    }
}
