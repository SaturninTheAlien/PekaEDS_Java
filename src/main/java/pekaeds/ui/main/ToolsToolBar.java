package pekaeds.ui.main;

import net.miginfocom.swing.MigLayout;
import pekaeds.tool.*;
import pekaeds.tool.tools.*;
import pekaeds.ui.actions.SetSelectedToolAction;

import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The tools toolbar in of the main UI.
 *
 * To add a new button do the following:
 * Add a JButton member to this class, name it btnYourTool.
 * Add it to the layout in setup().
 * Add a SetSelectedToolAction action in setActionListeners().
 */
public class ToolsToolBar extends JToolBar {
    private final PekaEDSGUI gui;
    
    /*private JToggleButton btnBrush;
    private JToggleButton btnEraser;
    private JToggleButton btnLine;
    private JToggleButton btnRect;
    private JToggleButton btnFloodFill;
    private JToggleButton btnCut;*/
    
    private Map<Class<? extends Tool>, JToggleButton> buttonMap = new LinkedHashMap<>();
    
    public ToolsToolBar(PekaEDSGUI ui) {
        this.gui = ui;
        
        setup();
    }
    
    private void setup() {
        setOrientation(VERTICAL);
        
        buttonMap.put(BrushTool.class, new JToggleButton("Brush"));
        buttonMap.put(EraserTool.class, new JToggleButton("Eraser"));
        buttonMap.put(AreaEraserTool.class, new JToggleButton("Area Eraser"));
        buttonMap.put(LineTool.class, new JToggleButton("Line"));
        buttonMap.put(RectangleTool.class, new JToggleButton("Rect"));
        buttonMap.put(FloodFillTool.class, new JToggleButton("Flood Fill"));
        buttonMap.put(CutTool.class, new JToggleButton("Cut"));
        
        setLayout(new MigLayout("flowy"));
        
        for (var btn : buttonMap.entrySet()) {
            add(btn.getValue());
        }
        
        setActionListeners();
    }
    
    private Class<? extends Tool> lastTool = null;
    private Class<? extends Tool> currentTool = null;
    public void setSelectedTool(Tool tool) {
        lastTool = currentTool;
        
        currentTool = tool.getClass();
        
        buttonMap.get(currentTool).setSelected(true);
        if (lastTool != null) buttonMap.get(lastTool).setSelected(false);
    }
    
    private void setActionListeners() {
        buttonMap.get(BrushTool.class).addActionListener(new SetSelectedToolAction(gui, Tools.getTool(BrushTool.class)));
        buttonMap.get(EraserTool.class).addActionListener(new SetSelectedToolAction(gui, Tools.getTool(EraserTool.class)));
        buttonMap.get(LineTool.class).addActionListener(new SetSelectedToolAction(gui, Tools.getTool(LineTool.class)));
        buttonMap.get(AreaEraserTool.class).addActionListener(new SetSelectedToolAction(gui, Tools.getTool(AreaEraserTool.class)));
        buttonMap.get(RectangleTool.class).addActionListener(new SetSelectedToolAction(gui, Tools.getTool(RectangleTool.class)));
        buttonMap.get(FloodFillTool.class).addActionListener(new SetSelectedToolAction(gui, Tools.getTool(FloodFillTool.class)));
        buttonMap.get(CutTool.class).addActionListener(new SetSelectedToolAction(gui, Tools.getTool(CutTool.class)));
    }
}
