package pekase3.util;

import javax.swing.*;

import pekase3.panels.FrameImagePanel;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.List;

public class AnimationManager extends AbstractAction {
    private final Timer timer = new Timer(16, this); // 16 milliseconds, (1.0 / 60.0) * 1000, about 60 updates per second

    private FrameImagePanel imagePanel;
    private byte[] frameSequence;
    private boolean loop;
    private int intro;
    
    private int frameRate;
    
    private int frameTimer;
    
    private int framesAmount;
    
    private List<BufferedImage> framesList;
    
    private int currentIndex;
    
    public AnimationManager() {
    }
    
    public void setImagePanel(FrameImagePanel frameImagePanel) {
        this.imagePanel = frameImagePanel;
    }
    
    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
    }
    
    public void play(List<BufferedImage> frames, byte[] frameSequence, boolean loop, int intro, int framesAmount) {
        this.framesList = frames;
        this.frameSequence = frameSequence;
        this.loop = loop;
        this.intro = intro;
        this.framesAmount = framesAmount;
        
        frameTimer = 0;
        currentIndex = 0;
        
        timer.stop();
        timer.start();
    }
    
    public void stop() {
        timer.stop();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (currentIndex >= framesAmount) {
            currentIndex = 0;
        }
        
        // In PK2 frame indices start at 1 instead of 0, so we have to subtract 1
        int frame = frameSequence[currentIndex] - 1;
        
        // Increase frame timer, until the sprite's frame rate in frames has been hit
        if (frameTimer < frameRate) {
            frameTimer++;
        } else { // Then change the image to the next frame
            frameTimer = 0;
            
            // If there are any animations left, go to the next one
            if (currentIndex < framesAmount - 1) {
                currentIndex++;
            } else { // If not loop or stop the timer
                if (loop) {
                    currentIndex = this.intro;
                } else {
                    timer.stop();
                }
            }
        }
        
        if (frame < 0) {
            frame = 0;
        }
        
        // If the index is greater than the amount of frames present set it to the last frame
        if (frame >= framesList.size()) {
            frame = framesList.size() - 1;
        }
        
        imagePanel.setImage(framesList.get(frame));
    }
}
