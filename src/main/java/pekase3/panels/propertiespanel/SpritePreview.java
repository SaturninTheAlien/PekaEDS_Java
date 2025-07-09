package pekase3.panels.propertiespanel;

import net.miginfocom.swing.MigLayout;
import pekase3.panels.FrameImagePanel;

import javax.swing.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpritePreview extends JPanel {
    private Map<String, Object> propertyMap;
    
    private List<JLabel> labelsPropertyNames;
    private List<JLabel> labelsValues;
    
    private FrameImagePanel imagePanel;
    
    public SpritePreview(BufferedImage image, Map<String, Object> properties) {
        imagePanel = new FrameImagePanel(image);
        
        this.propertyMap = new HashMap<>(properties);
        labelsPropertyNames = new ArrayList<>();
        labelsValues = new ArrayList<>();
        
        setup();
    }
    
    public SpritePreview() {
        imagePanel = new FrameImagePanel();
        
        this.propertyMap = new HashMap<>();
        
        labelsPropertyNames = new ArrayList<>();
        labelsValues = new ArrayList<>();
        
        setup();
    }
    
    public void setMaxSize(int width, int height) {
        imagePanel.setMaxSize(width, height);
    }
    
    public void setImage(BufferedImage image) {
        imagePanel.setImage(image);
    }
    
    public void setProperty(String property, Object value) {
        if (propertyMap.containsKey(property)) {
            for (int i = 0; i < labelsPropertyNames.size(); i++) {
                labelsValues.get(i).setText(value.toString());
            }
        } else {
            var lblProp = new JLabel(property + ":");
            labelsPropertyNames.add(lblProp);
            
            var lblValue = new JLabel(value.toString());
            labelsValues.add(lblValue);
            
            add(lblProp);
            add(lblValue, "wrap");
        }
        
        propertyMap.put(property, value);
    }
    
    public void reset() {
        for (var value : propertyMap.entrySet()) {
            value.setValue("");
        }
        
        for (var lbl : labelsValues) {
            lbl.setText("");
        }
        
        imagePanel.setImage(null);
    }
    
    private void setup() {
        imagePanel.setMinimumSize(new Dimension(32, 32));
        
        setLayout(new MigLayout());
     
        add(imagePanel, "dock north");
        
        for (var e : propertyMap.entrySet()) {
            var lblProp = new JLabel(e.getKey() + ":");
            var lblVal = new JLabel(e.getValue().toString());
            
            add(lblProp);
            add(lblVal, "wrap");
            
            labelsPropertyNames.add(lblProp);
            labelsValues.add(lblVal);
        }
    }
}
