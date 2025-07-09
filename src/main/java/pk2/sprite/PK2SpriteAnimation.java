package pk2.sprite;

import java.util.Arrays;

public final class PK2SpriteAnimation {
    final int ANIMATION_MAX_SEQUENCES = 10;
    
    private byte[] sequence = new byte[ANIMATION_MAX_SEQUENCES];
    
    private int frames; // amount of frames
    private boolean loop; // whether the animation loops or not
    
    public PK2SpriteAnimation(byte[] sequence, int frames, boolean loop) {
        this.sequence = sequence;
        this.frames = frames;
        this.loop = loop;
    }
    
    public PK2SpriteAnimation() {
    
    }
    
    public String toString() {
        return Arrays.toString(sequence) + " - "+ frames + " - " + loop;
    }
    
    public byte[] getSequence() {
        return sequence;
    }
    
    public int getFramesAmount() {
        return frames;
    }
    
    public boolean loop() {
        return loop;
    }
    
    public void setSequence(byte[] sequence) {
        this.sequence = sequence;
    }
    
    public void setFramesAmount(int frames) {
        this.frames = frames;
    }
    
    public void setLoop(boolean loop) {
        this.loop = loop;
    }
}
