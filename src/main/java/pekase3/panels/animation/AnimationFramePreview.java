package pekase3.panels.animation;

import net.miginfocom.swing.MigLayout;
import pekase3.panels.FrameImagePanel;

import javax.swing.*;

import java.awt.image.BufferedImage;

public class AnimationFramePreview extends JPanel {
    private FrameImagePanel frameImage;
    private JLabel lblFrameNumber;
    
    int width;
    int height;
    
    public AnimationFramePreview(BufferedImage image, int frame) {
        this(image, frame, image.getWidth(), image.getHeight());
    }
    
    public AnimationFramePreview(BufferedImage image, int frame, int width, int height) {
        frameImage = new FrameImagePanel(image);
        lblFrameNumber = new JLabel(Integer.toString(frame));
        
        this.width = width;
        this.height = height;
        
        setup();
    }
    
    private void setup() {
        setLayout(new MigLayout("flowy"));
        
        frameImage = new FrameImagePanel();

        add(frameImage, "dock center");
        add(lblFrameNumber, "align 50%");
    }
    
    public void setData(BufferedImage frame, int index) {
        frameImage.setImage(frame);
        
        lblFrameNumber.setText(Integer.toString(index));
    }
    
    public int getFrameNumber() {
        return Integer.parseInt(lblFrameNumber.getText());
    }
}
