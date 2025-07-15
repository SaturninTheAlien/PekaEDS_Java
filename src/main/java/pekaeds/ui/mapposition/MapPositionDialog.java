package pekaeds.ui.mapposition;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.tinylog.Logger;

import pk2.filesystem.PK2FileSystem;

public class MapPositionDialog extends JDialog {
    private BufferedImage backgroundImage = null;
    private MapIcon mapIcon = null;
    
    private JSpinner spX, spY;
        
    public MapPositionDialog() {
        try {
            File mapFile = PK2FileSystem.findAsset("map.bmp", PK2FileSystem.GFX_DIR);
            backgroundImage = ImageIO.read(mapFile);
        } catch (IOException e) {
            Logger.error(e);
        }
               
        add(new PositionPanel(), BorderLayout.CENTER);
        
        setTitle("Set icon position on map");
        setSize(new Dimension(640, 480));
        
        setAlwaysOnTop(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        
        pack();
    }
    
    public void setMapIcon(BufferedImage img, Point pos) {
        mapIcon = new MapIcon(img, pos);
    }

    public void updateIconImage(BufferedImage img) {
        if (mapIcon != null) {
            mapIcon.setImage(img);
            
            repaint();
        }
    }
    
    public void updatePosition(Point pos) {
        if (mapIcon != null) {
            mapIcon.setPosition(pos);
    
            if (spX != null) spX.setValue(pos.x);
            if (spY != null) spY.setValue(pos.y);
            
            repaint();
        }
    }
    
    public void setPositionSpinners(JSpinner spMapPosX, JSpinner spMapPosY) {
        this.spX = spMapPosX;
        this.spY = spMapPosY;
    }
    
    private class PositionPanel extends JPanel {
        public PositionPanel() {
            setPreferredSize(new Dimension(640, 480));

            addMouseListener(new MapPositionMouseHandler(MapPositionDialog.this));
        }
    
        //private final Composite compAlphaHalf = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f);
        private final Composite compAlphaFull = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
        
            var g2 = (Graphics2D) g;
            
            if (MapPositionDialog.this.backgroundImage != null) {
                g2.drawImage(backgroundImage, 0, 0, null);
            }
    
            /*g2.setComposite(compAlphaHalf);
            for (var icon : manager.getMapIcons()) {
                g2.drawImage(icon.getImage(), icon.getPosition().x, icon.getPosition().y, null);
            }*/
            
            g2.setComposite(compAlphaFull);
            if (mapIcon != null) {
                g2.drawImage(mapIcon.getImage(), mapIcon.getPosition().x - 9, mapIcon.getPosition().y - 14, null);
            }
        }
    }
}
