package pekaeds.ui.settings;

import net.miginfocom.swing.MigLayout;
import pekaeds.pk2.file.PK2FileSystem;
import pekaeds.settings.Settings;
import pekaeds.settings.StartupBehavior;
import pekaeds.ui.main.PekaEDSGUI;

import java.io.FileNotFoundException;

import javax.swing.*;

public class PanelGeneral extends JPanel implements ISettingsPanel {
    private JLabel lblGamePath;
    private JButton btnSetPath;
    private JTextField tfGamePath;
    
    //private JLabel lblTestingParameters;

    //private JTextField tfTestingParameters;
    
    private ButtonGroup buttonGroup;
    private JRadioButton rbLoadEpisode;
    private JRadioButton rbLoadMap;
    private JRadioButton rbNewMap;
      
    private JCheckBox cbShowTileNumbers;
    private JCheckBox cbHighlightSelection;
    
    private JSpinner spAutosaveInterval;
    private JSpinner spAutosaveFileCount;

    private PekaEDSGUI pkeds;
    
    public PanelGeneral(PekaEDSGUI pkeds) {

        this.pkeds = pkeds;

        setupGamePath();
        setupButtonGroup();
        setupAutosave();
        
        setBorder(BorderFactory.createTitledBorder("General"));
        
        setLayout(new MigLayout());
        
        add(lblGamePath, "cell 0 0");
        add(tfGamePath, "cell 0 1, width 250px");
        add(btnSetPath, "cell 1 1");
        
        var lblStartup = new JLabel("On startup:");
        add(lblStartup, "cell 0 3");
        add(rbLoadEpisode, "cell 0 4");
        add(rbLoadMap, "cell 0 5");
        add(rbNewMap, "cell 0 6");
    
        //add(lblTestingParameters, "cell 0 8");
        //add(tfTestingParameters, "cell 0 9, width 250px");
            
        cbShowTileNumbers = new JCheckBox("Show tileset number in tileset?");
        add(cbShowTileNumbers, "cell 0 11");
        
        cbHighlightSelection = new JCheckBox("Highlight tile selection?");
        add(cbHighlightSelection, "cell 0 12");
        
        var autosavePanel = new JPanel();
        autosavePanel.setBorder(BorderFactory.createTitledBorder("Autosave"));
        autosavePanel.setLayout(new MigLayout());
        
        var lblAutosaveInterval = new JLabel("Interval:");
        lblAutosaveInterval.setToolTipText("Specifies the delay between each save in minutes.");
        autosavePanel.add(lblAutosaveInterval, "cell 0 0");
        autosavePanel.add(spAutosaveInterval, "cell 1 0");
        
        var lblAutosaveMins = new JLabel("minutes");
        autosavePanel.add(lblAutosaveMins, "cell 2 0");
        
        var lblAutosaveFileCount = new JLabel("Files:");
        lblAutosaveFileCount.setToolTipText("The amount of files to rotate through.");
        autosavePanel.add(lblAutosaveFileCount, "cell 3 0");
        autosavePanel.add(spAutosaveFileCount, "cell 4 0");
        
        add(autosavePanel, "cell 0 13");
    }
    
    private void setupGamePath() {
        lblGamePath = new JLabel("Game contents:");
        tfGamePath = new JTextField(Settings.getBasePath());
        btnSetPath = new JButton("Browse");
        
        btnSetPath.addActionListener(e -> {
            var fc = new JFileChooser("Path to Pekka Kana 2 content");
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                tfGamePath.setText(fc.getSelectedFile().getAbsolutePath());
            }
        });
    }
        
    private void setupButtonGroup() {
        buttonGroup = new ButtonGroup();
        
        rbLoadEpisode = new JRadioButton("Load last episode");
        rbLoadMap = new JRadioButton("Load last map");
        rbNewMap = new JRadioButton("Create new map");
    
        rbNewMap.setSelected(Settings.getDefaultStartupBehavior() == StartupBehavior.NEW_MAP);
        rbLoadMap.setSelected(Settings.getDefaultStartupBehavior() == StartupBehavior.LOAD_LAST_MAP);
        rbLoadEpisode.setSelected(Settings.getDefaultStartupBehavior() == StartupBehavior.LOAD_LAST_EPISODE);
        
        buttonGroup.add(rbLoadEpisode);
        buttonGroup.add(rbLoadMap);
        buttonGroup.add(rbNewMap);
    }
    
    private void setupAutosave() {
        spAutosaveInterval = new JSpinner();
        spAutosaveFileCount = new JSpinner();
        
        spAutosaveInterval.setValue((Settings.getAutosaveInterval() / 1000) / 60);
        spAutosaveFileCount.setValue(Settings.getAutosaveFileCount());
    }

    @Override
    public void saveSettings(){

        //String previousGamePath = Settings.getBasePath();
        String gamePath = this.tfGamePath.getText();

        try{
            PK2FileSystem.setAssetsPath(gamePath);
            Settings.setBasePath(gamePath);
            this.pkeds.setupOpenRecentMenu();
        }
        catch(FileNotFoundException e){
            JOptionPane.showMessageDialog(this, gamePath + "\nis not a PK2 directory!", "Incorrect game path!", JOptionPane.ERROR_MESSAGE);
        }

        Settings.setDefaultStartupBehavior(this.getStartupBehavior());
        Settings.setShowTileNumberInTileset(this.cbShowTileNumbers.isSelected());

        Settings.setHighlightSelection(cbHighlightSelection.isSelected());

        Settings.setAutosaveInterval((this.getAutosaveInterval() * 60) * 1000);
        Settings.setAutosaveFileCount(this.getAutosaveFileCount());
    }
    
    @Override
    public void setupValues() {
        this.tfGamePath.setText(Settings.getBasePath());


        rbNewMap.setSelected(Settings.getDefaultStartupBehavior() == StartupBehavior.NEW_MAP);
        rbLoadMap.setSelected(Settings.getDefaultStartupBehavior() == StartupBehavior.LOAD_LAST_MAP);
        rbLoadEpisode.setSelected(Settings.getDefaultStartupBehavior() == StartupBehavior.LOAD_LAST_EPISODE);
        
        cbShowTileNumbers.setSelected(Settings.showTilesetNumberInTileset());
        
        cbHighlightSelection.setSelected(Settings.highlightSelection());
        
        spAutosaveInterval.setValue((Settings.getAutosaveInterval() / 1000) / 60);
        spAutosaveFileCount.setValue(Settings.getAutosaveFileCount());
    }
    
    private int getStartupBehavior() {
        int behavior = -1;
        
        if (rbNewMap.isSelected()) behavior = StartupBehavior.NEW_MAP;
        if (rbLoadMap.isSelected()) behavior = StartupBehavior.LOAD_LAST_MAP;
        if (rbLoadEpisode.isSelected()) behavior = StartupBehavior.LOAD_LAST_EPISODE;
        
        return behavior;
    }    

    private int getAutosaveInterval() {
        return (int) spAutosaveInterval.getValue();
    }
    
    private int getAutosaveFileCount() {
        return (int) spAutosaveFileCount.getValue();
    }
    
}
