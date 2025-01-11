package pekaeds.ui.mappanel;

import javax.swing.*;

import pekaeds.pk2.map.PK2Map;
import pekaeds.pk2.map.PK2MapSector;
import pekaeds.settings.Settings;
import pekaeds.tool.Tool;
import pekaeds.ui.listeners.*;
import pekaeds.ui.minimappanel.MiniMapPanel;

import java.awt.*;

public class MapPanel extends JComponent implements
        PK2MapConsumer,
        PK2SectorConsumer,
        RepaintListener,
        RectangleChangeListener {

    private final MapPanelPainter painter;
    private final MapPanelMouseHandler mpMouseHandler;

    RectangleChangeListener resizeRectListener;

    private final Composite compAlphaHalf = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
    private final Composite compAlphaFull = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);

    // Normally I wouldn't have components depend on each other like this, listeners would be better, but in this case it's very simple and better this way.
    private MiniMapPanel miniMapComponent = null;

    private JScrollPane view = null;

    private Tool leftMouseTool, rightMouseTool;

    private int bgRepeatX = 0;
    private int bgRepeatY = 0;

    private PK2Map currentMap;
    private PK2MapSector currentSector;

    private int selectedLayer = 0;

    private Rectangle viewport = new Rectangle();

    private boolean resizingSector = false;
    Rectangle sectorResizeRect = new Rectangle();

    public MapPanel() {
        painter = new MapPanelPainter(this);
        mpMouseHandler = new MapPanelMouseHandler(this);

        addMouseListener(mpMouseHandler);
        addMouseMotionListener(mpMouseHandler);
        addMouseWheelListener(mpMouseHandler);
    }

    public void setLeftMouseTool(Tool tool) {
        this.leftMouseTool = tool;

        leftMouseTool.setMapPanelPainter(painter);
        mpMouseHandler.setLeftMouseTool(leftMouseTool);
    }

    public void setRightMouseTool(Tool tool) {
        this.rightMouseTool = tool;

        rightMouseTool.setMapPanelPainter(painter);
        mpMouseHandler.setRightMouseTool(rightMouseTool);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        var g2 = (Graphics2D) g;

        /*
        // TODO Solve performance problems
        AffineTransform transform = g2.getTransform();
        transform.translate(model.getZoomPosition().x, model.getZoomPosition().y);
        transform.scale(model.getZoomAmount(), model.getZoomAmount());
        transform.translate(-model.getZoomPosition().x, -model.getZoomPosition().y);
        g2.setTransform(transform);*/

        painter.drawBackground(g2);
        g2.setColor(Color.lightGray);
        g2.drawRect(-1, -1, currentSector.getWidth() * 32 - 1, currentSector.getHeight() * 32 - 1);

        if (Settings.showBgSprites) {
            painter.drawBackgroundSprites(g2);
        }

        if (currentSector.getTilesetImage() != null) painter.drawLayers(g2);

        if (Settings.showSprites) {
            painter.drawRegularSprites(g2);
        }

        if (Settings.showBgSprites) {
            painter.drawForegroundSprites(g2);
        }

        if (resizingSector) {
            g2.setColor(Color.black);
            g2.setComposite(compAlphaHalf);

            int sx = sectorResizeRect.x;
            int sy = sectorResizeRect.y;
            int sw = sectorResizeRect.width;
            int sh = sectorResizeRect.height;

            // left of selection
            g2.fillRect(0, 0, sx, sector().getHeight() * 32);

            // right of selection
            g2.fillRect(sx + sw, 0, (sector().getWidth() * 32) - sx, sector().getHeight() * 32);

            // top of selection
            g2.fillRect(sx, 0, sw, sy);

            // bottom of selection
            g2.fillRect(sx, sy + sh, sw, sector().getHeight() * 32);

            g2.setComposite(compAlphaFull);

            g2.setColor(Color.white);
            g2.drawRect(sx, sy, sw, sh);

            g2.setColor(Color.lightGray);
            g2.fillRect(sx - 4, sy - 4, 8, 8);
            g2.fillRect(sx + sw - 4, sy - 4, 8, 8);
            g2.fillRect(sx - 4, sy + sh - 4, 8, 8);
            g2.fillRect(sx + sw - 4, sy + sh - 4, 8, 8);
        } else {
            if (Settings.highlightSprites) {
                painter.drawSpriteHighlights(g2);
            }

            if (currentSector.getTilesetImage() != null) {
                leftMouseTool.draw(g2);
                rightMouseTool.draw(g2);
            }
        }
    }

    void updateViewportSize(Rectangle newView) {
        viewport = newView;

        miniMapComponent.setViewportSize(newView.width, newView.height);
        painter.setMapViewportSize(newView.width, newView.height);

        if (currentSector != null && currentSector.getBackgroundImage() != null) {
            bgRepeatX = getPreferredSize().width / currentSector.getBackgroundImage().getWidth();
            bgRepeatY = getPreferredSize().height / currentSector.getBackgroundImage().getHeight();

            bgRepeatX++;
            bgRepeatY++;

            repaint();
        }
    }

    public void resizeCurrentSector(int startX, int startY, int newWidth, int newHeight) {
        // Note: It would be cleaner to do this where it belongs but I can't be bothered to figure that out right now
        currentSector.setSize(startX, startY, newWidth, newHeight);

        repaint();
    }

    @Override
    public void setSector(PK2MapSector sector) {
        currentSector = sector;

        setBounds(0, 0, (sector.getWidth() * 32) + 32, (sector.getHeight() * 32) + 32);
        revalidate();

        // TODO Ideally Tool should be or have a PK2SectorConsumer but I'm going to do it like this for now, even though its bad
        Tool.setSector(sector);

        sectorResizeRect.setBounds(0, 0, sector.getWidth() * 32, sector.getHeight() * 32);

        repaint();
    }

    /*
        Might have to do this:
        https://docs.oracle.com/javase/tutorial/uiswing/components/scrollpane.html#update
     */
    @Override
    public Dimension getPreferredSize() {
        Dimension dimension = new Dimension(1000, 1000);

        if (currentSector != null) {
            dimension.setSize((currentSector.getWidth() * 32) + 32, (currentSector.getHeight() * 32) + 32);
        }

        return dimension;
    }

    public void setResizingSector(boolean resizing) {
        resizingSector = resizing;
    }

    boolean isResizingSector() {
        return resizingSector;
    }

    public void setResizeRectListener(RectangleChangeListener listener) {
        resizeRectListener = listener;
    }

    @Override
    public void doRepaint() {
        repaint();
    }

    @Override
    public void setMap(PK2Map map) {
        currentMap = map;
    }

    int getBgRepeatX() {
        return bgRepeatX;
    }

    int getBgRepeatY() {
        return bgRepeatY;
    }

    int getSelectedLayer() {
        return selectedLayer;
    }

    void setView(MapPanelView newView) {
        view = newView;
    }

    void setViewX(int newX) {
        miniMapComponent.setViewX(newX);
    }

    void setViewY(int newY) {
        miniMapComponent.setViewY(newY);
    }

    public void updateViewportPosition(int newX, int newY) {
        viewport.setLocation(newX, newY);
        view.getViewport().setViewPosition(new Point(newX, newY));
    }

    public void resetView() {
        view.getViewport().setViewPosition(new Point(0, 0));
        miniMapComponent.updateViewportPosition(0, 0);
    }

    public void setSelectedLayer(int newLayer) {
        selectedLayer = newLayer;
    }

    final Rectangle viewport() {
        return view.getViewport().getViewRect();
    }

    public PK2MapSector sector() {
        return currentSector;
    }

    final PK2Map map() {
        return currentMap;
    }

    public void setMiniMapPanel(MiniMapPanel miniMapPanel) {
        miniMapComponent = miniMapPanel;
    }

    @Override
    public void rectangleChanged(Rectangle newRectangle) {
        sectorResizeRect = newRectangle;

        repaint();
    }
}
