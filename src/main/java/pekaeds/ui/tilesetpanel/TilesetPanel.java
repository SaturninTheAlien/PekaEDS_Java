package pekaeds.ui.tilesetpanel;

import javax.swing.*;

import pekaeds.ui.listeners.PK2SectorConsumer;
import pekaeds.ui.listeners.RepaintListener;
import pekaeds.ui.main.PekaEDSGUI;
import pk2.level.PK2LevelSector;
import pk2.settings.Settings;
import pk2.util.TileUtils;

import java.awt.*;

// TODO Clean up this class. Probably should create a TilesetPanelPainter
public class TilesetPanel extends JPanel implements PK2SectorConsumer, RepaintListener {
    private TilesetPanelModel model;
    
    private Rectangle selectionRect = new Rectangle(0, 0, 1, 1);
    
    private Point mousePosition = new Point();
    
    private FontMetrics fontMetrics = null;
    
    private boolean isMouseInsidePanel = false;
    
    private boolean useBackgroundTileset = false;
    
    public TilesetPanel(PekaEDSGUI mainUI) {
        this.model = new TilesetPanelModel();
        
        setup();
    }


    
    private void setup() {
        var mouseHandler = new TilesetPanelMouseHandler(this);
        
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
        
        setPreferredSize(new Dimension(320, 480));
        setMinimumSize(new Dimension(320, 480));
        setMaximumSize(new Dimension(320, 480));
    }
    
    private final Font tileIDFont = new Font(getFont().getName(), Font.BOLD, 12);
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (model.getMap() != null && model.getMap().getTilesetImage() != null) {
            var g2 = (Graphics2D) g;
    
            if (fontMetrics == null) fontMetrics = getFontMetrics(getFont());
    
            g2.setColor(Color.GRAY);
            g2.fillRect(0, 0, model.getMap().getTilesetImage().getWidth(), model.getMap().getTilesetImage().getHeight());
    
            if (useBackgroundTileset && model.getMap().getBackgroundTilesetImage() != null) {
                g2.drawImage(model.getMap().getBackgroundTilesetImage(), 0, 0, null);
            } else {
                g2.drawImage(model.getMap().getTilesetImage(), 0, 0, null);
            }
    
            if (selectionRect.width == 1 && selectionRect.height == 1) {
                drawSelectionRectangle(g2, new Point(selectionRect.x, selectionRect.y));
                drawTileId(g2, new Point(selectionRect.x, selectionRect.y)); // Draw the tile id of the selected tile.
        
                if (isMouseInsidePanel) {
                    drawSelectionRectangle(g2, mousePosition);
                    drawTileId(g2, mousePosition); // Draw the tile id of the tile at the mouse coordinates.
                }
            } else {
                drawSelectionRectangle(g2, getSelectionRectStart());
            }
        }
    }
   
    private void drawTileId(Graphics2D g2, Point position) {
        if (Settings.showTilesetNumberInTileset()) {
            g2.setFont(tileIDFont);
            g2.setColor(Color.WHITE);
    
            String tileIDStr = Integer.toString(TileUtils.getTileIdFromTilesetPosition(position));
            int fontWidth = fontMetrics.stringWidth(tileIDStr);
       
        /*
            Hacky! If the width is 18, that means the number is something over 100, make the middle point 14 pixels, instead of 16.
            This needs to be done because for some reason the string won't center correctly when it has a (font) width of 18.
            
            It's hacky, but it works. That's the way she goes!
        */
            int middlePos = fontWidth == 18 ? 14 : 16;
            int offset = middlePos - (fontWidth / 2);
    
            // TODO Maybe draw an outline around the text to make it more visible.
            g2.drawString(tileIDStr, position.x + offset, position.y + 20); // The y offset (20) gets hard coded because that's good enough.
        }
    }
    
    void setMousePosition(Point pos) {
        TileUtils.alignPointToGrid(pos);
        
        this.mousePosition = pos;
    }
    
    private void drawSelectionRectangle(Graphics2D g, Point position) {
        if (position.x != -1) {
            g.setColor(Color.BLACK);
            g.drawRect(position.x, position.y, selectionRect.width * 32, selectionRect.height * 32);
            g.drawRect(position.x + 2, position.y + 2, (selectionRect.width * 32) - 4, (selectionRect.height * 32) - 4);
            
            g.setColor(Color.WHITE);
            g.drawRect(position.x + 1, position.y + 1, (selectionRect.width * 32) - 2, (selectionRect.height * 32) - 2);
        }
    }
    
    public void resetSelection() {
        selectionRect.setBounds(0, 0, 1, 1);

        mousePosition.setLocation(0, 0);
    }
    
    /*
        Methods used by TilesetPanelMouseHandler
     */
    void setSelectionRect(Rectangle rect) {
        this.selectionRect = rect;
    }
    
    void setSelectionRect(int x, int y, int width, int height) {
        selectionRect.x = x / 32 * 32;
        selectionRect.y = y / 32 * 32;
        
        selectionRect.width = width;
        selectionRect.height = height;
    }
    
    Point getSelectionRectStart() {
        return new Point(selectionRect.x, selectionRect.y);
    }
    
    @Override
    public void setSector(PK2LevelSector map) {
        model.setMap(map);
        
        repaint();
    }

    public PK2LevelSector getSector(){
        return model.getMap();
    }
    
    @Override
    public void doRepaint() {
        repaint();
    }
    
    public void setMouseInsidePanel(boolean inside) {
        this.isMouseInsidePanel = inside;
    }
    
    public void useBackgroundTileset(boolean use) {
        this.useBackgroundTileset = use;
    }
}
