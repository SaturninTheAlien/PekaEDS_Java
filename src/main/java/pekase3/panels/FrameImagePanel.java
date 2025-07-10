package pekase3.panels;

import javax.swing.*;

import pekaeds.ui.misc.LookAndFeelHelper;

import java.awt.*;
import java.awt.image.BufferedImage;

public final class FrameImagePanel extends JPanel {
    private int prefWidth = 0;
    private int prefHeight = 0;
    
    private int maxWidth = 160;
    private int maxHeight = 160;
    
    private int minWidth = 32;
    private int minHeight = 32;
    
    private BufferedImage image;
    
    public FrameImagePanel() {
        setup();
    }
    
    public FrameImagePanel(int prefWidth, int prefHeight, int maxWidth, int maxHeight) {
        this.prefWidth = prefWidth;
        this.prefHeight = prefHeight;
        
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        
        setup();
    }
    
    public FrameImagePanel(BufferedImage image) {
        this.image = image;
        
        setup();
    }
    
    private void setup() {
        if (prefWidth > 0) {
            setPreviewSize(prefWidth, prefHeight);
        }
    }
    
    public void setMaxSize(int width, int height) {
        this.maxWidth = width;
        this.maxHeight = height;
    }
    
    private int lastWidth = 0;
    private int lastHeight = 0;
    public void setImage(BufferedImage img) {
        this.image = img;
        
        if (prefWidth == 0 || prefHeight == 0) {
            if (img != null) {
                setPreviewSize(image.getWidth(), image.getHeight());
            } else {
                setPreviewSize(lastWidth, lastHeight);
            }
        } else {
            setPreviewSize(prefWidth, prefHeight);
        }
        
        revalidate();
        repaint();
    }
    
    /**
     * Sets the panel's width and height to width + (width / 2), height + (height / 2)
     * @param width
     * @param height
     */
    public void setPreviewSize(int width, int height) {
        if (width < minWidth) width = minWidth;
        if (height < minHeight) height = minHeight;
        
        if (width > maxWidth || height > maxHeight) {
            float ratio = Math.min((((float) maxWidth / (float) width)), ((float) maxHeight / (float) height));
  
            width = (int) (width * ratio);
            height = (int) (height * ratio);
        }
        
        lastWidth = width;
        lastHeight = height;
        
        // Add 1/3 of the width/height to the panels size to add padding around the image
        int w = width + (width / 3);
        int h = height + (height / 3);

        setPreferredSize(new Dimension(w, h));
        setMinimumSize(new Dimension(w, h));
        setMaximumSize(new Dimension(w, h));
    }
    
    public void paintComponent(Graphics g) {
        setBorder(BorderFactory.createLineBorder(LookAndFeelHelper.getBorderColor()));
        setBackground(LookAndFeelHelper.getBGColor());
        super.paintComponent(g);
        
        
        var g2 = (Graphics2D) g;
        
        if (image != null) {
            if (image.getWidth() <= maxWidth && image.getHeight() <= maxHeight) {
                g2.drawImage(image, (getWidth() / 2) - (image.getWidth() / 2), (getHeight() / 2) - (image.getHeight() / 2), null); // Draw the image in the middle of this panel.
            } else {
                // Keep the image in visible within the maximum bounds and maintain its aspect ratio.
                int width = image.getWidth();
                int height = image.getHeight();
                
                float ratio = Math.min((((float) maxWidth / (float) width)), ((float) maxHeight / (float) height));
                
                width = (int) (width * ratio);
                height = (int) (height * ratio);
   
                g2.drawImage(image, (getWidth() / 2) - (width / 2), (getHeight() / 2) - (height / 2), width, height, null);
            }
        }
    }
}
