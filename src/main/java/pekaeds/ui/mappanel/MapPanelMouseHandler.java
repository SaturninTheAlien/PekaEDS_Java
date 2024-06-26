package pekaeds.ui.mappanel;

import javax.swing.*;

import pekaeds.tool.Tool;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public final class MapPanelMouseHandler extends MouseAdapter {
    private Tool rightMouseTool;
    private Tool leftMouseTool;
    
    private MapPanel mapPanel; // TODO Create a repaintListener, use PropertyChangeSupport instead for repaint?
    
    public MapPanelMouseHandler(MapPanel panel) {
        this.mapPanel = panel;
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            leftMouseTool.mousePressed(e);
        } else if (SwingUtilities.isRightMouseButton(e)) {
            if (!leftMouseTool.useRightMouseButton()) {
                rightMouseTool.mousePressed(e);
            } else {
                leftMouseTool.mousePressed(e);
            }
        } else if (SwingUtilities.isMiddleMouseButton(e)) {
            mapPanel.getModel().setLastPanPoint(e.getPoint());
        }

        // TODO only repaint the affected areas.
        mapPanel.repaint();
        mapPanel.requestFocus(); // This is needed to get the keyboard shortcuts to work.
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            leftMouseTool.mouseReleased(e);
        } else if (SwingUtilities.isRightMouseButton(e)) {
            if (!leftMouseTool.useRightMouseButton()) {
                rightMouseTool.mouseReleased(e);
            } else {
                leftMouseTool.mouseReleased(e);
            }
        }
    
        // TODO only repaint the affected areas.
        mapPanel.repaint();
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            leftMouseTool.mouseDragged(e);
        } else if (SwingUtilities.isRightMouseButton(e)) {
            if (!leftMouseTool.useRightMouseButton()) {
                rightMouseTool.mouseDragged(e);
            } else {
                leftMouseTool.mouseDragged(e);
            }
        } else if (SwingUtilities.isMiddleMouseButton(e)) {
            panView(e.getX(), e.getY());
        }
        
        // TODO only repaint the affected areas.
        mapPanel.repaint();
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        leftMouseTool.mouseMoved(e);
        rightMouseTool.mouseMoved(e);
        
        mapPanel.repaint();
    }
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        mapPanel.getModel().setZoomPosition(e.getPoint());
        
        if (e.getPreciseWheelRotation() < 0) {
            mapPanel.getModel().setZoomAmount(mapPanel.getModel().getZoomAmount() + 0.01f);
        } else {
            mapPanel.getModel().setZoomAmount(mapPanel.getModel().getZoomAmount() - 0.01f);
        }
        
        mapPanel.repaint();
    }
    
    private void panView(int x, int y) {
        int panX = mapPanel.getModel().getLastPanPoint().x - x;
        int panY = mapPanel.getModel().getLastPanPoint().y - y;
        
        var vp = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, mapPanel);
        var viewRect = vp.getViewRect();
        viewRect.x += panX;
        viewRect.y += panY;

        mapPanel.scrollRectToVisible(viewRect);
    }
    
    public void setLeftMouseTool(Tool tool) {
        this.leftMouseTool = tool;
    }
    
    public void setRightMouseTool(Tool tool) {
        this.rightMouseTool = tool;
    }
}
