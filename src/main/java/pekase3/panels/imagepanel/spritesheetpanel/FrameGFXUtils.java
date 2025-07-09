package pekase3.panels.imagepanel.spritesheetpanel;

import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;

import pekase3.panels.imagepanel.spritesheetpanel.FrameEditMode.MoveOrResizeMode;

import java.awt.*;

public final class FrameGFXUtils {
    public static final Stroke DEFAULT_STROKE = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
    public static final Stroke DASHED_STROKE = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0);
    
    public static final Color colorFirstFrameBG = new Color(128, 128, 134);    // Background color of the first frame
    public static final Color colorFramesBG = new Color(156, 159, 166);        // Background color of the following frame
    public static final Color colorImageBackground = Color.LIGHT_GRAY;                  // Background color of the area behind the sprite sheet image
    
    private FrameGFXUtils() {}
    
    static void drawBackgroundHighlights(Graphics2D g2, Rectangle frame, int framesAmount, int imageWidth, int imageHeight) {
        if (frame.x >= 0 && frame.y >= 0 && frame.x + frame.width <= imageWidth && frame.y + frame.height <= imageHeight) {
            // Draw the background behind the highlighted frame
            g2.setColor(colorFirstFrameBG);
            g2.fillRect(frame.x, frame.y, frame.width, frame.height);
            
            g2.setColor(colorFramesBG);
            int x = frame.x + frame.width + 3; // Start after the first frame
            int y = frame.y;
            int frameCount = 0;
            while (frameCount < framesAmount - 1) { // - 1 because we start after the first frame
                if (x + frame.width > 640) {
                    y += frame.height + 3;
                    
                    x = 1;
                }
                
                //drawDashedFrameRect(g2, x, y, frame.width, frame.height);
                g2.fillRect(x, y, frame.width, frame.height);
                
                x += frame.width + 3;
                
                frameCount++;
            }
        }
    }
    
    static void drawFrameHighlights(Graphics2D g2, Rectangle frame, int framesAmount, int imageWidth, int imageHeight) {
        if (frame.x >= 0 && frame.y >= 0 && frame.x + frame.width <= imageWidth && frame.y + frame.height <= imageHeight) {
            g2.setColor(Color.black);
            g2.drawRect(frame.x - 1, frame.y - 1, frame.width + 1, frame.height + 1);
        }
        
        g2.setColor(Color.black);
        int x = frame.x + frame.width + 3; // Start after the first frame
        int y = frame.y;
        int frameCount = 0;
        while (frameCount < framesAmount - 1) { // - 1 because we start after the first frame
            if (x + frame.width > 640) {
                y += frame.height + 3;
                
                x = 1;
            }
            
            //drawDashedFrameRect(g2, x, y, frame.width, frame.height);
            g2.drawRect(x - 1, y - 1, frame.width + 1, frame.height + 1);
            
            x += frame.width + 3;
            
            frameCount++;
        }
    }
    
    public static void drawDashedFrameRect(Graphics2D g2, int x, int y, int width, int height) {
        //g2.setXORMode(Color.WHITE);
        g2.setStroke(DASHED_STROKE);
        g2.drawLine(x - 1, y - 1, x - 1 + width + 1, y - 1);
        g2.drawLine(x - 1, y - 1, x - 1, y - 1 + height + 1);
        g2.drawLine(x - 1 + width + 1, y - 1 + height + 1, x - 1, y - 1 + height + 1);
        g2.drawLine(x - 1 + width + 1, y - 1, x - 1 + width + 1, y - 1 + height + 1);
    }
}
