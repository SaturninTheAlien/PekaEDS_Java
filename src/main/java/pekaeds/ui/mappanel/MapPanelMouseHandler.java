package pekaeds.ui.mappanel;

import javax.swing.*;

import pekaeds.tool.Tool;
import pk2.util.TileUtils;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public final class MapPanelMouseHandler extends MouseAdapter {
    private Tool rightMouseTool;
    private Tool leftMouseTool;
    
    private final MapPanel mapPanel; // TODO Create a repaintListener, use PropertyChangeSupport instead for repaint?

    private Point lastPanPoint = new Point();

    public MapPanelMouseHandler(MapPanel panel) {
        this.mapPanel = panel;
    }

    private enum ResizeDirection {
        NORTH, EAST, SOUTH, WEST, NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST, NONE
    }

    private ResizeDirection resizeDirection;
    private Rectangle clickBounds;
    
    @Override
    public void mousePressed(MouseEvent e) {
        if (!mapPanel.isResizingSector()) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                leftMouseTool.mousePressed(e);
            } else if (SwingUtilities.isRightMouseButton(e)) {
                if (!leftMouseTool.useRightMouseButton()) {
                    rightMouseTool.mousePressed(e);
                } else {
                    leftMouseTool.mousePressed(e);
                }
            }
        } else {
            if (SwingUtilities.isLeftMouseButton(e)) {
                resizeDirection = getResizeDirection(e);

                clickBounds = new Rectangle(mapPanel.sectorResizeRect);
            } else if (SwingUtilities.isRightMouseButton(e)) {
                Point p = e.getPoint();
                TileUtils.alignPointToGrid(p);

                mapPanel.sectorResizeRect.setLocation(p);
            }
        }

        if (SwingUtilities.isMiddleMouseButton(e)) {
            lastPanPoint = e.getPoint();
        }

        mapPanel.repaint();
        mapPanel.requestFocus(); // This is needed to get the keyboard shortcuts to work.
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        if (!mapPanel.isResizingSector()) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                leftMouseTool.mouseReleased(e);
            } else if (SwingUtilities.isRightMouseButton(e)) {
                if (!leftMouseTool.useRightMouseButton()) {
                    rightMouseTool.mouseReleased(e);
                } else {
                    leftMouseTool.mouseReleased(e);
                }
            }
        }

        if (mapPanel.isResizingSector()) {
            mapPanel.setCursor(Cursor.getDefaultCursor());
        }

        mapPanel.repaint();
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if (!mapPanel.isResizingSector()) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                leftMouseTool.mouseDragged(e);
            } else if (SwingUtilities.isRightMouseButton(e)) {
                if (!leftMouseTool.useRightMouseButton()) {
                    rightMouseTool.mouseDragged(e);
                } else {
                    leftMouseTool.mouseDragged(e);
                }
            }
        } else {
            handleResizeWindow(e);
        }

        if (SwingUtilities.isMiddleMouseButton(e)) {
            panView(e.getX(), e.getY());
        }

        mapPanel.repaint();
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        if (!mapPanel.isResizingSector()) {
            leftMouseTool.mouseMoved(e);
            rightMouseTool.mouseMoved(e);
        } else {
            int sx = mapPanel.sectorResizeRect.x;
            int sy = mapPanel.sectorResizeRect.y;
            int sw = mapPanel.sectorResizeRect.x + mapPanel.sectorResizeRect.width;
            int sh = mapPanel.sectorResizeRect.y + mapPanel.sectorResizeRect.height;

            if (e.getX() >= sx - 4 && e.getX() <= sx + 4 && e.getY() >= sy - 4 && e.getY() <= sy + 4) {
                mapPanel.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
            } else if (e.getX() >= sx - 4 && e.getX() <= sx + 4 && e.getY() >= sh - 4 && e.getY() <= sh + 4) {
                mapPanel.setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
            } else if (e.getX() >= sw - 4 && e.getX() <= sw + 4 && e.getY() >= sy - 4 && e.getY() <= sy + 4) {
                    mapPanel.setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
            } else if (e.getX() >= sw - 4 && e.getX() <= sw + 4 && e.getY() >= sh - 4 && e.getY() <= sh + 4) {
                mapPanel.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
            } else if (e.getX() >= sx - 2 && e.getX() <= sx + 2) {
                mapPanel.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
            } else if (e.getX() >= sw - 2 && e.getX() <= sw + 2) {
                mapPanel.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
            } else if (e.getY() >= sy - 2 && e.getY() <= sy + 2) {
                mapPanel.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
            } else if (e.getY() >= sh - 2 && e.getY() <= sh + 2) {
                mapPanel.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
            } else {
                mapPanel.setCursor(Cursor.getDefaultCursor());
            }
        }

        mapPanel.repaint();
    }
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        // mapPanel.getModel().setZoomPosition(e.getPoint());
        
        if (e.getPreciseWheelRotation() < 0) {
            //mapPanel.getModel().setZoomAmount(mapPanel.getModel().getZoomAmount() + 0.01f);
        } else {
            //mapPanel.getModel().setZoomAmount(mapPanel.getModel().getZoomAmount() - 0.01f);
        }
        
        mapPanel.repaint();
    }
    
    private void panView(int x, int y) {
        int panX = lastPanPoint.x - x;
        int panY = lastPanPoint.y - y;

        var vp = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, mapPanel);
        var viewRect = vp.getViewRect();
        viewRect.x += panX;
        viewRect.y += panY;

        mapPanel.scrollRectToVisible(viewRect);
    }

    private ResizeDirection getResizeDirection(MouseEvent e) {
        int sx = mapPanel.sectorResizeRect.x;
        int sy = mapPanel.sectorResizeRect.y;
        int sw = sx + mapPanel.sectorResizeRect.width;
        int sh = sy + mapPanel.sectorResizeRect.height;

        ResizeDirection direction = ResizeDirection.NONE;

        if (e.getX() >= sx - 4 && e.getX() <= sx + 4 && e.getY() >= sy - 4 && e.getY() <= sy + 4) {
            direction = ResizeDirection.NORTH_WEST;
        } else if (e.getX() >= sx - 4 && e.getX() <= sx + 4 && e.getY() >= sh - 4 && e.getY() <= sh + 4) {
            direction = ResizeDirection.SOUTH_WEST;
        } else if (e.getX() >= sw - 4 && e.getX() <= sw + 4 && e.getY() >= sy - 4 && e.getY() <= sy + 4) {
            direction = ResizeDirection.NORTH_EAST;
        } else if (e.getX() >= sw - 4 && e.getX() <= sw + 4 && e.getY() >= sh - 4 && e.getY() <= sh + 4) {
            direction = ResizeDirection.SOUTH_EAST;
        } else if (e.getX() >= sx - 2 && e.getX() <= sx + 2) {
            direction = ResizeDirection.WEST;
        } else if (e.getX() >= sw - 2 && e.getX() <= sw + 2) {
            direction = ResizeDirection.EAST;
        } else if (e.getY() >= sy - 2 && e.getY() <= sy + 2) {
            direction = ResizeDirection.NORTH;
        } else if (e.getY() >= sh - 2 && e.getY() <= sh + 2) {
            direction = ResizeDirection.SOUTH;
        }

        return direction;
    }

    private void handleResizeWindow(MouseEvent e) {
        Point p = e.getPoint();
        TileUtils.alignPointToGrid(p);

        if (SwingUtilities.isLeftMouseButton(e)) {
            if (p.x >= 0 &&
                    p.y >= 0 &&
                    p.x < mapPanel.sector().getWidth() * 32 &&
                    p.y < mapPanel.sector().getHeight() * 32) {
                Rectangle bounds = mapPanel.sectorResizeRect;

                switch (resizeDirection) {
                    case NONE -> {

                    }
                    case NORTH_WEST -> {
                        bounds.setBounds(p.x, p.y, clickBounds.width - (p.x - clickBounds.x), clickBounds.height - (p.y - clickBounds.y));

                        if (bounds.width < 32) bounds.setSize(32, bounds.height);
                        if (bounds.height < 32) bounds.setSize(bounds.width, 32);
                    }

                    case NORTH_EAST -> {
                        bounds.setBounds(bounds.x, p.y, p.x - bounds.x, clickBounds.height - (p.y - clickBounds.y));

                        if (bounds.width < 32) bounds.setSize(32, bounds.height);
                        if (bounds.height < 32) bounds.setSize(bounds.width, 32);
                    }

                    case SOUTH_WEST -> {
                        bounds.setBounds(p.x, clickBounds.y, clickBounds.width - (p.x - clickBounds.x), p.y - bounds.y);

                        if (bounds.width < 32) bounds.setSize(32, bounds.height);
                        if (bounds.height < 32) bounds.setSize(bounds.width, 32);
                    }

                    case SOUTH_EAST -> {
                        bounds.setBounds(clickBounds.x, clickBounds.y, p.x - bounds.x, p.y - bounds.y);

                        if (bounds.width < 32) bounds.setSize(32, bounds.height);
                        if (bounds.height < 32) bounds.setSize(bounds.width, 32);
                    }

                    case NORTH -> {
                        bounds.setBounds(bounds.x, p.y, bounds.width, clickBounds.height - (p.y - clickBounds.y));

                        if (bounds.height < 32) {
                            bounds.setSize(bounds.width, 32);
                        }
                    }

                    case SOUTH -> {
                        bounds.setSize(bounds.width, p.y - bounds.y);

                        if (bounds.height < 32) {
                            bounds.setSize(bounds.width, 32);
                        }
                    }

                    case WEST -> {
                        bounds.setBounds(p.x, bounds.y, clickBounds.width - (p.x - clickBounds.x), bounds.height);

                        if (bounds.width < 32) {
                            bounds.setSize(32, bounds.height);
                        }
                    }

                    case EAST -> bounds.setSize(p.x - bounds.x, bounds.height);
                }

                mapPanel.resizeRectListener.rectangleChanged(bounds);
            }
        } else if (SwingUtilities.isRightMouseButton(e)) {
            mapPanel.sectorResizeRect.setLocation(p);

            mapPanel.resizeRectListener.rectangleChanged(mapPanel.sectorResizeRect);
        }
    }

    public void setLeftMouseTool(Tool tool) {
        this.leftMouseTool = tool;
    }
    
    public void setRightMouseTool(Tool tool) {
        this.rightMouseTool = tool;
    }
}
