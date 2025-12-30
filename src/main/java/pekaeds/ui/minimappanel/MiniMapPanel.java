package pekaeds.ui.minimappanel;

import javax.swing.*;

import pekaeds.tool.Tool;
import pekaeds.ui.listeners.PK2SectorConsumer;
import pekaeds.ui.listeners.TileChangeListener;
import pekaeds.ui.mappanel.MapPanel;
import pk2.level.PK2LevelSector;
import pk2.util.LookAndFeelHelper;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MiniMapPanel extends JPanel implements
        PK2SectorConsumer,
        TileChangeListener,
        MouseListener,
        MouseMotionListener {

    private MapPanel mapPanel = null;

    private PK2LevelSector currentSector;

    private final Rectangle viewport = new Rectangle();

    public MiniMapPanel() {
        setPreferredSize(new Dimension(257, 225));

        addMouseListener(this);
        addMouseMotionListener(this);
    
        Tool.setTileChangeListener(this);
    }
    
    @Override
    public void setSector(PK2LevelSector newSector) {
        currentSector = newSector;

        // Adding 1 to width and height so that the viewport rectangle still gets fully drawn, even if it is at the sectors maximum width/height
        setPreferredSize(new Dimension(newSector.getWidth() + 2, newSector.getHeight() + 2));
        revalidate();

        repaint();
    }
    
    private void paintLayer(Graphics g, final int[] layer, boolean bg) {
        if (currentSector.getTilesetImage() != null) { // && layer.getWidth() == currentSector.getWidth() && layer.getHeight() == 224) {
            g.setColor(LookAndFeelHelper.getBGColor());
            g.fillRect(0, 0, currentSector.getWidth(), currentSector.getHeight());

            for (int x = 0; x < currentSector.getWidth(); x++) {
                for (int y = 0; y < currentSector.getHeight(); y++) {
                    int tile = layer[currentSector.getWidth() * y + x]; //map.getLayers().get(layer)[y][x];
            
                    if (tile >= 0 && tile <= 149) { // The tileid should not be able to go out of these bounds, but for some reason one custom map does that?
                        int tileX = (tile % 10) * 32;
                        int tileY = (tile / 10) * 32;

                        // TODO Use the algorithm and colors of the original level editor
                        if (bg && currentSector.getBackgroundTilesetImage() != null) {
                            g.setColor(new Color(currentSector.getBackgroundTilesetImage().getRGB(tileX, tileY))); // TODO Maybe don't create a new Color object every loop
                        } else {
                            g.setColor(new Color(currentSector.getTilesetImage().getRGB(tileX, tileY))); // TODO Maybe don't create a new Color object every loop
                        }
                        g.drawLine(x, y, x + 1, y + 1);
                    }
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (currentSector != null) {
            g.setColor(LookAndFeelHelper.getBGColor());
            g.fillRect(0, 0, currentSector.getWidth(), currentSector.getHeight());

            paintLayer(g, currentSector.getBackgroundLayer(), true);
            paintLayer(g, currentSector.getForegroundLayer(), false);

            g.setColor(LookAndFeelHelper.getFGColor());
            g.drawRect(viewport.x, viewport.y, viewport.width, viewport.height);
        }
    }

    @Override
    public void tileChanged(int x, int y, int tileID) {
        repaint(x, y, 1, 1);
    }

    private void setViewportPosition(Point pos) {
        if (pos.x + (viewport.width / 2) >= currentSector.getWidth()) {
            viewport.x = currentSector.getWidth() - viewport.width + 1;
        } else if (pos.x - (viewport.width / 2) <= 0) {
            viewport.x = 0;
        } else {
            viewport.x = pos.x - (viewport.width / 2);
        }

        if (pos.y + (viewport.height / 2) >= currentSector.getHeight()) {
            viewport.y = currentSector.getHeight() - viewport.height + 1;
        } else if (pos.y - (viewport.height / 2) <= 0) {
            viewport.y = 0;
        } else {
            viewport.y = pos.y - (viewport.height / 2);
        }

        mapPanel.updateViewportPosition(viewport.x * 32, viewport.y * 32);

        repaint();
    }

    public void setViewX(int newX) {
        viewport.x = newX / 32;

        repaint();
    }

    public void setViewY(int newY) {
        viewport.y = newY / 32;

        repaint();
    }

    public void setViewportSize(int newWidth, int newHeight) {
        viewport.width = newWidth / 32;
        viewport.height = newHeight / 32;

        repaint();
    }

    // This method gets called by MapPanel from the outside
    public void updateViewportPosition(int newX, int newY) {
        viewport.x = newX / 32;
        viewport.y = newY / 32;

        // TODO Only repaint the affected part
        repaint();
    }

    public void setMapPanel(MapPanel newMapPanel) {
        mapPanel = newMapPanel;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        setViewportPosition(e.getPoint());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        setViewportPosition(e.getPoint());
    }

    // Unused
    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mouseMoved(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }
}
