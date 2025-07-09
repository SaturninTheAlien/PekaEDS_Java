package pekase3.panels.animation.preview;

import net.miginfocom.swing.MigLayout;
import pekase3.listener.UnsavedChangesListener;
import pekase3.panels.FrameImagePanel;
import pekase3.panels.animation.AnimationsEditModel;
import pekase3.panels.animation.dragndrop.AnimationFrameDropTarget;
import pekase3.settings.Settings;
import pk2.sprite.PK2Sprite;
import pk2.sprite.PK2SpriteAnimation;
import pekase3.util.AnimationManager;

import javax.swing.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public final class AnimationPanel extends JPanel {
    private JCheckBox cbLoop;
    
    private List<AnimationContainer> containers;
    
    private Settings settings;
    private AnimationsEditModel editModel;
    
    private FrameImagePanel animationPreview;
    private AnimationManager animationManager;
    
    private JButton btnCopy;
    private JButton btnPaste;
    private JButton btnClear;
    
    private JButton btnPlay;
    private JButton btnStop;
    
    private PK2Sprite sprite;
    
    private int animationIndex;

    public AnimationPanel(AnimationsEditModel editModel, Settings settings) {
        this.editModel = editModel;
        this.settings = settings;
        
        animationPreview = new FrameImagePanel();
        animationManager = new AnimationManager();
        
        containers = new ArrayList<>();
        
        setup();
    }
    
    private void setup() {
        cbLoop = new JCheckBox("Loop");
        
        setLayout(new MigLayout());
        
        // Commenting to avoid confusion. The standard amount of animations is 10, this probably isn't going to change but it is stored in the SpriteProfile class.
        for (int i = 0; i < 10; i++) {
            containers.add(new AnimationContainer());
        }
        
        btnCopy = new JButton("Copy");
        btnPaste = new JButton("Paste");
        btnClear = new JButton("Clear All");
        
        btnPlay = new JButton("Play");
        btnStop = new JButton("Stop");
        
        generateLayout();
        addListeners();
        
        animationManager.setImagePanel(animationPreview);
    }
    
    private void generateLayout() {
        var containerPanel = new JPanel();
        containerPanel.setLayout(new MigLayout());
        
        for (var c : containers) {
            containerPanel.add(c);
            
            var dropTargetListener = new AnimationFrameDropTarget(c);
        }
        
        var pnlFrameActions = new JPanel();
        pnlFrameActions.setLayout(new MigLayout("flowx"));
        pnlFrameActions.add(new JLabel("Frames:"));
        pnlFrameActions.add(btnCopy);
        pnlFrameActions.add(btnPaste);
        pnlFrameActions.add(btnClear);
        
        var pnlPreview = new JPanel();
        pnlPreview.setLayout(new MigLayout("flowy"));
        pnlPreview.add(cbLoop);
        pnlPreview.add(animationPreview);
        pnlPreview.add(btnPlay);
        pnlPreview.add(btnStop);
        
        var pnlCenter = new JPanel();
        pnlCenter.setLayout(new MigLayout());
        
        var spContainer = new JScrollPane(containerPanel);
        spContainer.getVerticalScrollBar().setUnitIncrement(8);
        spContainer.getHorizontalScrollBar().setUnitIncrement(8);
        
        pnlCenter.add(pnlPreview, "dock west");
        pnlCenter.add(spContainer, "dock center");
        
        add(pnlCenter, "dock center");
        add(pnlFrameActions, "dock south");
    }
    
    private void addListeners() {
        btnCopy.addActionListener(e -> {
            editModel.setAnimationFrames(getAnimationFrames());
        });
        
        btnPaste.addActionListener(e -> {
            if (editModel.getAnimationFrames() != null) {
                for (int i = 0; i < editModel.getAnimationFrames().length; i++) {
                    containers.get(i).setFrameNumber(editModel.getAnimationFrames()[i]);
                    
                }
                
                //animationPreviewPanel.setImage(sprite.getFramesList().get(editModel.getAnimationFrames()[0] - 1));
            }
        });
        
        btnClear.addActionListener(e -> {
            var warningDialog = JOptionPane.showConfirmDialog(this, "Do you want to clear all frames?", "Clear frames?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (warningDialog == JOptionPane.YES_OPTION) {
                for (var c : containers) {
                    c.setFrameNumber(0);
                }
                
                animationPreview.setImage(null);
            }
        });
        
        btnPlay.addActionListener(e -> animationManager.play(sprite.getFramesList(), getAnimationFrames(), cbLoop.isSelected(), getFramesAmount()));
        btnStop.addActionListener(e -> animationManager.stop());
    }
    
    public void setFrameRate(int rate) {
        animationManager.setFrameRate(rate);
    }
    
    public static void setFramesList(List<BufferedImage> frames) {
        AnimationContainer.setFramesList(frames);
    }
    
    public void setSprite(PK2Sprite sprite, int animationIndex) {
        this.sprite = sprite;
        this.animationIndex = animationIndex;
        
        setupAnimation(sprite, animationIndex);
    }
    
    private void setupAnimation(PK2Sprite sprite, int animationIndex) {
       if (sprite != null) {
           var animation = sprite.getAnimationsList().get(animationIndex);
           
           cbLoop.setSelected(animation.loop());
           
           AnimationContainer.setFramesList(sprite.getFramesList());
           
           var sequence = animation.getSequence();
           for (int i = 0; i < sequence.length; i++) {
               containers.get(i).setFrameNumber(sequence[i]);
               
               // If the frame is not 0, meaning empty, we set the image and use the image's dimensions to set the frame preview
               if (sequence[i] > 0 && ((sequence[i] - 1) < sprite.getFramesList().size())) {
                   containers.get(i).setImage(sprite.getFramesList().get(sequence[i] - 1));
               } else { // Otherwise we only set the dimensions of the frame preview panel to the first frame's dimensions, so that all panels are the same size. No matter if they are empty or not.
                   containers.get(i).setImage(null);
                   
                   if (!sprite.getFramesList().isEmpty()) {
                       containers.get(i).setFramePreviewDimensions(sprite.getFramesList().get(0).getWidth(), sprite.getFramesList().get(0).getHeight());
                   } else {
                       containers.get(i).setFramePreviewDimensions(32, 32);
                   }
               }
           }
           
           var frame0 = animation.getSequence()[0];
           if (frame0 > sprite.getFramesList().size()) {
               animationPreview.setImage(null);
           } else {
               if (frame0 >= 1) { // If the first frame in the animation sequence is greater than 0, show whatever frame it contains
                   animationPreview.setImage(sprite.getFramesList().get(frame0 - 1));
               } else { // Otherwise show the first frame of the animation
                   if (!sprite.getFramesList().isEmpty()) {
                       animationPreview.setImage(sprite.getFramesList().get(frame0));
                   } else {
                       animationPreview.setImage(null);
                   }
               }
           }
           
           if (!sprite.getFramesList().isEmpty()) setPreferredSize(new Dimension((int) getPreferredSize().getWidth(), sprite.getFramesList().get(0).getHeight()));
       }
    }
    
    public void reset() {
        cbLoop.setSelected(false);
        
        animationPreview.setImage(null);
        
        for (var c : containers) {
            c.reset();
        }
    }
    
    public byte[] getAnimationFrames() {
        var frames = new byte[settings.getSpriteProfile().getAnimationsAmount()];
        for (int i = 0; i < frames.length; i++) {
            frames[i] = containers.get(i).getFrameNumber();
        }
        
        return frames;
    }
    
    public int getFramesAmount() {
        int frames = 0;
        
        for (var c : containers) {
            if (c.getFrameNumber() != 0) {
                frames++;
            } else { // 0 marks the end of the animation sequence. It can occur anywhere in the sequence.
                break;
            }
        }
        
        return frames;
    }
    
    public boolean loop() {
        return cbLoop.isSelected();
    }
    
    public void setUnsavedChangesListener(UnsavedChangesListener listener) {
        cbLoop.addActionListener(listener);
        
        for (var c : containers) {
            c.setUnsavedChangesListener(listener);
        }
    }
    
    public void repaintPreviews() {
        setupAnimation(sprite, animationIndex);
    }
}
