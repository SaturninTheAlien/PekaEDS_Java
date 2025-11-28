package pekaeds.ui.main;

import javax.swing.*;

import pekaeds.data.Layer;
import pekaeds.tool.Tool;
import pekaeds.tool.ToolModeListener;
import pekaeds.ui.actions.NewLevelAction;
import pekaeds.ui.actions.OpenLevelAction;
import pekaeds.ui.actions.PlayLevelAction;
import pk2.settings.Settings;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class MainToolBar extends JToolBar implements PropertyChangeListener, ToolModeListener {
    private JButton btnNew;
    private JButton btnOpen;
    private JButton btnSave;
    
    private JButton btnPlayTest;
    
    private JLabel lblLayer;
    private JComboBox<String> cbLayer;
    
    private JLabel lblMode;
    private JComboBox<String> cbToolMode;
    
    private JToggleButton tbHighlightSprites;
    private JToggleButton tbShowSprites;
    private JToggleButton tbShowBgSprites;
    private JToggleButton tbShowTransparentLayers;
    private JToggleButton tbShowBrokenSlopes;
    
    private final Settings settings;
    
    private PekaEDSGUI gui;
    
    public MainToolBar(PekaEDSGUI gui) {
        settings = new Settings();
        
        this.gui = gui;
        
        setup();
        addComponents();
        addListeners();
    }
    
    private void setup() {
        btnNew = new JButton("New");
        btnOpen = new JButton("Open");
        btnSave = new JButton("Save");
        
        btnPlayTest = new JButton("Test");
    
        tbHighlightSprites = new JToggleButton("Highlight sprites");
        tbHighlightSprites.setSelected(Settings.highlightSprites);
        
        tbShowSprites = new JToggleButton("Show regular sprites");
        tbShowSprites.setSelected(Settings.showSprites);

        tbShowBgSprites = new JToggleButton("Show BG/FG sprites");
        tbShowBgSprites.setSelected(Settings.showBgSprites);

        tbShowTransparentLayers = new JToggleButton("Show another layer as transparent");
        tbShowTransparentLayers.setSelected(Settings.showTransparentLayers);

        this.tbShowBrokenSlopes = new JToggleButton("Show broken slopes");
        this.tbShowBrokenSlopes.setSelected(Settings.showBrokenSlopes);
        
        btnNew.addActionListener(new NewLevelAction(gui));
        btnOpen.addActionListener(new OpenLevelAction(gui));
        btnPlayTest.addActionListener(new PlayLevelAction(gui));
        
        tbHighlightSprites.addActionListener(e -> {
            Settings.highlightSprites = tbHighlightSprites.isSelected();            
            gui.getMapPanel().repaint();
        });
        
        tbShowSprites.addActionListener(e -> {
            Settings.showSprites = tbShowSprites.isSelected();
            gui.getMapPanel().repaint();
        });


        tbShowBgSprites.addActionListener(e->{
            Settings.showBgSprites = tbShowBgSprites.isSelected();
            gui.getMapPanel().repaint();
        });

        tbShowTransparentLayers.addActionListener(e->{
            Settings.showTransparentLayers = tbShowTransparentLayers.isSelected();
            gui.getMapPanel().repaint();

        });

        this.tbShowBrokenSlopes.addActionListener(e->{
            Settings.showBrokenSlopes = tbShowBrokenSlopes.isSelected();
            gui.getMapPanel().repaint();
        });
        
        lblLayer = new JLabel("Layer:");
        cbLayer = new JComboBox<>(settings.getLayerNames().toArray(new String[0]));
        
        cbLayer.setMaximumSize(new Dimension(100, 30));
        
        lblMode = new JLabel("Mode:");
        cbToolMode = new JComboBox<>();
        cbToolMode.addItem("Tile");
        cbToolMode.addItem("Sprite");
    }
    
    private void addComponents() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        
        add(btnNew);
        add(btnOpen);
        add(btnSave);
        
        addSeparator();
        
        add(btnPlayTest);
        
        addSeparator();
        
        addSeparator();
        
        add(lblLayer);
        add(cbLayer);
        
        addSeparator();
        
        add(lblMode);
        add(cbToolMode);
        
        addSeparator();
        
        add(tbHighlightSprites);
        add(tbShowSprites);
        add(tbShowBgSprites);
        add(tbShowTransparentLayers);
        add(tbShowBrokenSlopes);
    }
    
    private void addListeners() {
        cbLayer.addActionListener(e -> {
            // TODO Seems pretty hacky (and it is), but the "Both" layer should be first, I think. So gotta do this crap.
        
            if (cbLayer.getSelectedIndex() == 0) {
                gui.setLayer(Layer.BOTH); // Set layer to both
            } else {
                gui.setLayer(cbLayer.getSelectedIndex() - 1); // Subtract one to get rid of the both layer, so that foreground is 0 and background is 1. Pretty bad, lmao.
            }
        });
        
        btnSave.addActionListener(e -> {
            gui.saveLevel();
        });
        
        cbToolMode.addActionListener(e -> {
            Tool.setMode(cbToolMode.getSelectedIndex());
        });
    }
    
    // TODO Delete this
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof PekaEDSGUIModel) {
            if (evt.getPropertyName().equals("currentMode")) {
                cbToolMode.setSelectedIndex((int) evt.getNewValue());
            }
        }
    }
    
    public void setToolMode(int mode) {
        cbToolMode.setSelectedIndex(mode);
    }
    
    public void setSelectedLayer(int layer) {
        if (layer == Layer.BOTH) {
            cbLayer.setSelectedIndex(0);
        } else {
            cbLayer.setSelectedIndex(layer + 1);
        }
    }
    
    @Override
    public void changeMode(int mode) {
        cbToolMode.setSelectedIndex(mode);
    }
}
