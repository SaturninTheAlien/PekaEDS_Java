package pekaeds.ui.toolpropertiespanel;

import net.miginfocom.swing.MigLayout;
import pekaeds.tool.Tool;
import pekaeds.tool.tools.CutTool;
import pekaeds.ui.listeners.CutToolListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class CutToolPropertiesPanel extends JPanel implements ToolChangeListener, CutToolListener {
    private CutTool tool;

    private JCheckBox cbForegroundLayer;
    private JCheckBox cbBackgroundLayer;
    private JCheckBox cbSpritesLayer;

    private JCheckBox cbReplaceEmptyTiles;
    
    private JRadioButton rbCut, rbRemove;

    private JSpinner spCutX;
    private JSpinner spCutY;
    private JSpinner spCutWidth;
    private JSpinner spCutHeight;

    public CutToolPropertiesPanel() {
        setup();
    }

    private void setup() {
        cbForegroundLayer = new JCheckBox("Foreground layer");
        cbBackgroundLayer = new JCheckBox("Background layer");
        cbSpritesLayer = new JCheckBox("Sprites layer");

        cbReplaceEmptyTiles = new JCheckBox("Replace empty tiles");
        cbReplaceEmptyTiles.setSelected(true);

        /*lblClickDrag = new JLabel("Right click and drag to select");
        lblClick = new JLabel("Right click again to place selection");*/
        
        rbCut = new JRadioButton("Cut");
        rbRemove = new JRadioButton("Remove");

        setLayout(new MigLayout("flowy, fillx"));

        JPanel pnlLayers = new JPanel();
        TitledBorder layersBorder = BorderFactory.createTitledBorder("Affected Layers");
        pnlLayers.setBorder(layersBorder);

        pnlLayers.setLayout(new MigLayout("flowy"));
        pnlLayers.add(cbForegroundLayer);
        pnlLayers.add(cbBackgroundLayer);
        pnlLayers.add(cbSpritesLayer);

        add(cbReplaceEmptyTiles);

        spCutX = new JSpinner();
        spCutY = new JSpinner();
        spCutWidth = new JSpinner();
        spCutHeight = new JSpinner();

        JPanel positionPanel = new JPanel();
        TitledBorder positionBorder = BorderFactory.createTitledBorder("Position");
        positionPanel.setBorder(positionBorder);

        positionPanel.setLayout(new MigLayout("wrap 2, gap 5, insets 5"));
        positionPanel.add(new JLabel("X:"));
        positionPanel.add(spCutX);
        positionPanel.add(new JLabel("Y:"));
        positionPanel.add(spCutY);
        positionPanel.add(new JLabel("Width:"));
        positionPanel.add(spCutWidth);
        positionPanel.add(new JLabel("Height:"));
        positionPanel.add(spCutHeight);

        add(positionPanel, "grow");
        add(pnlLayers, "grow");

        setListeners();
    }

    private void setListeners() {
        cbForegroundLayer.addActionListener(e -> {
            tool.setCutForegroundLayer(cbForegroundLayer.isSelected());
        });

        cbBackgroundLayer.addActionListener(e -> {
            tool.setCutBackgroundLayer(cbBackgroundLayer.isSelected());
        });

        cbSpritesLayer.addActionListener(e -> {
            tool.setCutSpritesLayer(cbSpritesLayer.isSelected());
        });

        spCutX.addChangeListener(c -> {
            tool.setSelectionX((int) spCutX.getValue());
        });

        spCutY.addChangeListener(c -> {
            tool.setSelectionY((int) spCutY.getValue());
        });

        spCutWidth.addChangeListener(c -> {
            tool.setSelectionWidth((int) spCutWidth.getValue());
        });

        spCutHeight.addChangeListener(c -> {
            tool.setSelectionHeight((int) spCutHeight.getValue());
        });
    }

    @Override
    public void setSelectedTool(Tool selectedTool) {
        if (selectedTool instanceof CutTool) {
            this.tool = (CutTool) selectedTool;

            // TODO Should only do this once, no need to do it everytime the tool is selected!
            tool.setSelectionListener(this);

            cbForegroundLayer.setSelected(tool.cutForegroundLayer());
            cbBackgroundLayer.setSelected(tool.cutBackgroundLayer());
            cbSpritesLayer.setSelected(tool.cutSpritesLayer());

            rbCut.setSelected(tool.cutSelection());
            rbRemove.setSelected(!tool.cutSelection());
        }
    }

    @Override
    public void selectionUpdated(Rectangle selection) {
        // TODO Beware of recursion, when changing position from the spinners!
        spCutX.setValue(selection.x);
        spCutY.setValue(selection.y);

        spCutWidth.setValue(selection.width);
        spCutHeight.setValue(selection.height);
    }
}
