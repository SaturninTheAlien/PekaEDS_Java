package pekaeds.ui.toolpropertiespanel;

import net.miginfocom.swing.MigLayout;
import pekaeds.tool.Tool;
import pekaeds.tool.tools.CutTool;
import pekaeds.tool.tools.FloodFillTool;
import pekaeds.tool.tools.RectangleTool;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ToolPropertiesPanel extends JPanel {
    private static final String NO_PROP_PANEL = "noPropPanel";
    
    private Tool selectedTool;
    private JPanel noPropPanel;
    
    private CardLayout cardLayout;
    private final List<String> toolList = new ArrayList<>();
    private final List<ToolChangeListener> paneList = new ArrayList<>();
    
    public ToolPropertiesPanel() {
        setup();
    }
    
    private void setup() {
        noPropPanel = new JPanel();
        var lblNoProp = new JLabel("No options");
        lblNoProp.setEnabled(false);
        
        noPropPanel.setLayout(new MigLayout());
        noPropPanel.add(lblNoProp);
    
        var rectPanel = new RectanglePropertiesPanel();
        var cutPanel = new CutToolPropertiesPanel();
        var floodFillPanel = new FloodFillPropertiesPanel();
        
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        add(noPropPanel, NO_PROP_PANEL);
        
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK), "Properties:"));
        
        addPropertyPanel(rectPanel, RectangleTool.class);
        addPropertyPanel(cutPanel, CutTool.class);
        addPropertyPanel(floodFillPanel, FloodFillTool.class);
    }
    
    public void setSelectedTool(Tool tool) {
        this.selectedTool = tool;
        
        if (toolList.contains(selectedTool.getClass().getName())) {
            cardLayout.show(this, selectedTool.getClass().getName());

            for (var p : paneList) {
                p.setSelectedTool(selectedTool);
            }
            this.setVisible(true);
        } else {
            this.setVisible(false);
            cardLayout.show(this, NO_PROP_PANEL);
        }
    }
    
    private void addPropertyPanel(JPanel panel, Class<? extends Tool> tool) {
        toolList.add(tool.getName());
        paneList.add((ToolChangeListener) panel);
        
        add(panel, tool.getName());
    }
}
