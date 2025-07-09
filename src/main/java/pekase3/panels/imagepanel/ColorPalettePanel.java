package pekase3.panels.imagepanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.IndexColorModel;
import java.util.ArrayList;
import java.util.List;

public class ColorPalettePanel extends JPanel {
    private List<Color> colorList = new ArrayList<>();

    public ColorPalettePanel() {
        setPreferredSize(new Dimension(256, 256));
        setMinimumSize(new Dimension(256, 256));
        setMaximumSize(new Dimension(256, 256));
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (!colorList.isEmpty()) {
            int x = 0;
            int y = 0;
            int col = 0;
            
            do {
                g.setColor(colorList.get(col));
                
                g.fillRect(x, y, 16, 16);
                
                x += 16;
                
                if (x >= 256) {
                    x = 0;
                    
                    y += 16;
                }
                
                col++;
            } while (col < 256);
        }
    }

    public void setPalette(IndexColorModel p) {
        int[] palette = new int[256 * 3];
        p.getRGBs(palette);
        
        for (int col : palette) {
            colorList.add(new Color(col));
        }
        
        repaint();
    }
}
