package pekaeds.ui.tilesetpanel;

import javax.swing.*;

import pekaeds.tool.Tool;
import pekaeds.util.TileUtils;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class TilesetPanelMouseHandler extends MouseAdapter {
    private final TilesetPanel tilesetPanel;
    
    private Point selectionStart, selectionEnd;
    
    public TilesetPanelMouseHandler(TilesetPanel tp) {
        this.tilesetPanel = tp;
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        tilesetPanel.setMousePosition(e.getPoint());
        
        tilesetPanel.repaint();
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            selectionStart = e.getPoint();
            selectionEnd = e.getPoint();

            tilesetPanel.resetSelection();
            tilesetPanel.setSelectionRect(e.getX(), e.getY(), 1, 1);

            tilesetPanel.setMousePosition(e.getPoint());
            tilesetPanel.repaint();
        }
    }
    
    // TODO This breaks when dragging from bottom to top.
    @Override
    public void mouseDragged(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            selectionEnd = e.getPoint();
            
            keepSelectionInBounds();
            
            tilesetPanel.setSelectionRect(TileUtils.calculateSelectionRectangleInScene(selectionStart, selectionEnd, tilesetPanel.getSector()));
    
            tilesetPanel.repaint();
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            var rect = TileUtils.calculateSelectionRectangleInScene(selectionStart, selectionEnd, tilesetPanel.getSector());
            
            int[][] selection = new int[0][];
            
            if (rect.width > 1 || rect.height > 1) {
                selection = new int[rect.height][rect.width];
        
                Point tilePos = new Point(0, 0);

                for (int sx = 0; sx < selection[0].length; sx++) {
                    for (int sy = 0; sy < selection.length; sy++) {
                        tilePos.setLocation(rect.x + (sx * 32), rect.y + (sy * 32));
    
                        selection[sy][sx] = TileUtils.getTileIdFromTilesetPosition(tilePos);
                    }
                }
            } else if (rect.width == 1 && rect.height == 1) {
                selection = new int[1][1];
    
                selection[0][0] = TileUtils.getTileIdFromTilesetPosition(tilesetPanel.getSelectionRectStart());
            }
    
            Tool.setSelectionSize(rect.width, rect.height);
            Tool.setSelection(selection);
            
            Tool.setMode(Tool.MODE_TILE);
            
            tilesetPanel.repaint();
        }
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
        tilesetPanel.setMouseInsidePanel(false);
        
        tilesetPanel.repaint();
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
        tilesetPanel.setMouseInsidePanel(true);
        
        tilesetPanel.repaint();
    }
    
    private void keepSelectionInBounds() {
        if (selectionStart.x < 0) selectionStart.x = 0;
        if (selectionStart.y < 0) selectionStart.y = 0;
        
        if (selectionEnd.x < 0) selectionEnd.x = 0;
        if (selectionEnd.y < 0) selectionEnd.y = 0;
        if (selectionEnd.x > tilesetPanel.getWidth()) selectionEnd.x = tilesetPanel.getWidth() - 32;
        if (selectionEnd.y > tilesetPanel.getHeight()) selectionEnd.y = tilesetPanel.getHeight() - 32;
    }
}
