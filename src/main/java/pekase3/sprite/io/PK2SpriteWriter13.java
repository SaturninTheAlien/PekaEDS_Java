package pekase3.sprite.io;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import pk2.sprite.PK2Sprite;
import pekase3.util.PK2FileUtils;

public final class PK2SpriteWriter13 extends PK2SpriteWriter {
    public void save(PK2Sprite sprite, File file) throws IOException {
        try (var dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
            dos.write(PK2Sprite.VERSION_13);
            
            dos.writeInt(Integer.reverseBytes(sprite.getType()));
            
            PK2FileUtils.writeString(dos, sprite.getImageFile(), 100);
            
            for (int i = 0; i < 7; i++) {
                PK2FileUtils.writeString(dos, sprite.getSoundFile(i), 100);
            }
            
            for (int i = 0; i < 7; i++) {
                dos.writeInt(0xFFFFFFFF); // Unused sound data
            }
            
            dos.writeByte(sprite.getFramesAmount());

            for (int i = 0; i < 20; i++) {
                var ani = sprite.getAnimationsList().get(i);
                
                for (int j = 0; j < 10; j++) {
                    dos.writeByte(ani.getSequence()[j]);
                }
                
                dos.writeByte(ani.getFramesAmount());
                dos.writeBoolean(ani.loop());
            }
            
            dos.writeByte(0); // Unused, animations amount
            
            dos.writeByte(sprite.getFrameRate());
            
            dos.writeByte(0xCC); // unused/padding
            
            dos.writeInt(Integer.reverseBytes(sprite.getFrameX()));
            dos.writeInt(Integer.reverseBytes(sprite.getFrameY()));
            
            dos.writeInt(Integer.reverseBytes(sprite.getFrameWidth()));
            dos.writeInt(Integer.reverseBytes(sprite.getFrameHeight()));
            
            dos.writeInt(0); // Unused, frame distance
            
            PK2FileUtils.writeString(dos, sprite.getName(), 30);
            
            // Unused/Padding
            dos.writeByte(0xCC);
            dos.writeByte(0xCC);
            
            // Hitbox width and height
            dos.writeInt(Integer.reverseBytes(sprite.getWidth()));
            dos.writeInt(Integer.reverseBytes(sprite.getHeight()));
            
            ByteBuffer b = ByteBuffer.allocate(8);
            b.putDouble(sprite.getWeight());
            b.order(ByteOrder.LITTLE_ENDIAN);
            
            dos.writeDouble(b.getDouble(0));
            
            dos.writeBoolean(sprite.isEnemy());
            
            // Unused/Padding
            dos.writeByte(0xCC);
            dos.writeByte(0xCC);
            dos.writeByte(0xCC);
            
            dos.writeInt(Integer.reverseBytes(sprite.getEnergy()));
            dos.writeInt(Integer.reverseBytes(sprite.getDamage()));
            
            dos.writeByte(sprite.getDamageType());
            dos.writeByte(sprite.getImmunityToDamageType());
            
            // Unused/Padding
            dos.writeByte(0xCC);
            dos.writeByte(0xCC);
            
            dos.writeInt(Integer.reverseBytes(sprite.getScore()));
            
            for (int i = 0; i < 10; i++) {
                dos.writeInt(Integer.reverseBytes(sprite.getAiList().get((i))));
            }
            
            dos.writeByte(sprite.getMaxJump());
            
            // Unused/Padding
            dos.writeByte(0xCC);
            dos.writeByte(0xCC);
            dos.writeByte(0xCC);
            
            ByteBuffer b2 = ByteBuffer.allocate(8);
            b2.putDouble(sprite.getMaxSpeed());
            b2.order(ByteOrder.LITTLE_ENDIAN);
            
            dos.writeDouble(b2.getDouble(0));
            
            dos.writeInt(Integer.reverseBytes(sprite.getLoadTime()));
            
            dos.writeByte(sprite.getColor());
            
            dos.writeBoolean(sprite.isObstacle());
            
            // Unused/Padding
            dos.writeByte(0xCC);
            dos.writeByte(0xCC);
            
            dos.writeInt(Integer.reverseBytes(sprite.getDestruction()));
            
            dos.writeBoolean(sprite.isKey());
            dos.writeBoolean(sprite.isShakes());
            
            dos.writeByte(sprite.getBonusAmount());
            
            // Unused/Padding
            dos.writeByte(0xCC);
            
            dos.writeInt(Integer.reverseBytes(sprite.getAttack1Duration()));
            dos.writeInt(Integer.reverseBytes(sprite.getAttack2Duration()));
            
            dos.writeInt(Integer.reverseBytes(sprite.getParallaxFactor()));
            
            PK2FileUtils.writeString(dos, sprite.getTransformationSpriteFile(), 100);
            PK2FileUtils.writeString(dos, sprite.getBonusSpriteFile(), 100);
            
            PK2FileUtils.writeString(dos, sprite.getAttack1SpriteFile(), 100);
            PK2FileUtils.writeString(dos, sprite.getAttack2SpriteFile(), 100);
            
            dos.writeBoolean(sprite.isTileCheck());
            
            // Unused/Padding
            dos.writeByte(0xCC);
            dos.writeByte(0xCC);
            dos.writeByte(0xCC);
            
            dos.writeInt(Integer.reverseBytes(sprite.getSoundFrequency()));
            dos.writeBoolean(sprite.isRandomSoundFrequency());
            
            dos.writeBoolean(sprite.isWallUp());
            dos.writeBoolean(sprite.isWallDown());
            dos.writeBoolean(sprite.isWallRight());
            dos.writeBoolean(sprite.isWallLeft());
            
            dos.writeByte(0x0); // Unused -Transparency
            dos.writeByte(0x0); // Unused - Glow
            dos.writeByte(0xCC); // Unused - ???
            
            dos.writeInt(Integer.reverseBytes(sprite.getAttackPause()));
            
            dos.writeBoolean(sprite.canGlide());
            dos.writeBoolean(sprite.isBoss());
            dos.writeBoolean(sprite.isAlwaysBonus());
            dos.writeBoolean(sprite.canSwim());
        }
    }
}
