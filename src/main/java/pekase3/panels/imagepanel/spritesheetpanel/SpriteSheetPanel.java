package pekase3.panels.imagepanel.spritesheetpanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pekase3.panels.imagepanel.spritesheetpanel.FrameEditMode.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.util.Map;

public class SpriteSheetPanel extends JPanel implements ChangeListener {
    private static final Map<Class<? extends FrameEditMode>, ? extends  FrameEditMode> frameEditModes = Map.of(
            DetectFrameMode.class, new DetectFrameMode(),
            DrawFrameMode.class, new DrawFrameMode(),
            MoveOrResizeMode.class, new MoveOrResizeMode(),
            PickSeparatorColorFrameMode.class, new PickSeparatorColorFrameMode(),
            SetRangeFrameMode.class, new SetRangeFrameMode()
    );
    
    private FrameEditMode frameEditMode = frameEditModes.get(MoveOrResizeMode.class);
    
    public static final int COLOR_TRANSPARENT = -255;
    
    private SpriteSheetModel model;
 
    public SpriteSheetPanel() {
        model = new SpriteSheetModel();
        model.addListener(this);
        
        frameEditMode.setModel(model);
        frameEditMode.setSheetPanel(this);
        
        model.setEditMode(MoveOrResizeMode.class);
        
        setBackground(Color.DARK_GRAY);
        
        setPreferredSize(new Dimension(640, 480));
        
        var mouseAdapter = new SpriteSheetMouseAdapter(model, this);
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
        
        for (var e : frameEditModes.entrySet()) {
            e.getValue().setModel(model);
            e.getValue().setSheetPanel(this);
        }
    }
    
    public SpriteSheetModel getModel() {
        return model;
    }
    
    private Stroke defaultStroke = null;
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        var g2 = (Graphics2D) g;
        
        if (defaultStroke == null) {
            defaultStroke = g2.getStroke();
        }
        
        if (model.getImage() != null) {
            // Fill a rectangle to show where the image is going to be displayed.
            g2.setColor(FrameGFXUtils.colorImageBackground);
            g2.fillRect(0, 0, model.getImage().getWidth(), model.getImage().getHeight());
            
            frameEditMode.drawBehindSpriteSheet(g2);
                g2.drawImage(model.getImage(), 0, 0, null);
            frameEditMode.drawInFrontSpriteSheet(g2);
        }
    }
    public void setImage(BufferedImage img){
        this.setImage(img, true);

    }
    
    public void setImage(BufferedImage img, boolean resetFrameData) {
        model.setImage(img);
        
        guessBorderColor(img);

        if(resetFrameData)resetFrameData();
    }
    
    public void resetFrameData() {
        model.setFramePosition(0, 0);
        model.setFrameDimension(0, 0);
    }
    
    public void setFrameData(int x, int y, int width, int height) {
        model.setFramePosition(x, y);
        model.setFrameDimension(width, height);
    }
    
    Color col;
    private void guessBorderColor(BufferedImage image) {
        if (image != null) {
            var pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            
            // Check if the first pixel is the transparency color
            if ((pixels[0] & 0xFF) != 255) {
                // If it isn't use the pixels color to try to detect frame borders
                
                col = new Color(((IndexColorModel) image.getColorModel()).getRGB(pixels[0]));
            }
            
            model.setSeparatorColor(col);
        }
    }
    
    public Color getBorderColor() {
        return col;
    }
    
    @Override
    public void stateChanged(ChangeEvent e) {
        repaint();
    }
    
    public void lockToFrame(boolean lock) {
        model.setLockToFrame(lock);
    }
    
    public Class<? extends FrameEditMode> getEditMode() {
        return model.getEditMode();
    }
    
    public void setEditMode(Class<? extends FrameEditMode> mode) {
        frameEditMode = frameEditModes.get(mode);
    }
    
    public FrameEditMode getFrameEditMode() {
        return frameEditMode;
    }
}
