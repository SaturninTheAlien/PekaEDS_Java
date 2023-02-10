package pk.pekaeds.ui.main;

import net.miginfocom.swing.MigLayout;
import pk.pekaeds.data.PekaEDSVersion;
import pk.pekaeds.tools.Tool;
import pk.pekaeds.ui.actions.NewMapAction;
import pk.pekaeds.ui.actions.OpenMapAction;
import pk.pekaeds.ui.actions.SaveMapAction;
import pk.pekaeds.ui.listeners.MainUIWindowListener;
import pk.pekaeds.ui.mappanel.MapPanelBackground;
import pk.pekaeds.ui.mappanel.MapPanelView;
import pk.pekaeds.ui.settings.SettingsDialog;
import pk.pekaeds.ui.toolpropertiespanel.ToolPropertiesPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PekaEDSGUIView {
    private JFrame frame;
    private JMenuBar menuBar;
    
    private JMenu mFile;
    private JMenuItem mFileNew;
    private JMenuItem mFileOpen;
    private JMenuItem mFileSave;
    private JMenuItem mFileSaveAs;
    private JMenuItem mFileExit;
    
    private JMenu mEpisode;
    private JMenuItem mEpisodeNew;
    private JMenuItem mEpisodeOpen;
    private JMenuItem mEpisodeExport;
    
    private JMenu mOther;
    private JMenuItem mOtherSettings;
    private JMenuItem mOtherAbout;
    
    private PekaEDSGUI edsUI;
   
    private JToolBar toolsToolBar;
    
    private SettingsDialog settingsDialog = null;
   
    private ToolPropertiesPanel toolPropertiesPanel;
    
    public PekaEDSGUIView(PekaEDSGUI gui) {
        this.edsUI = gui;
    }
    
    void setupMainUI() {
        frame = new JFrame("PekaEDS");
        
        var mainToolBar = edsUI.getMainToolBar();
        
        var tilesetPanel = edsUI.getTilesetPanel();

        var spritesPanel = edsUI.getSpritesPanel();
        var mapMetadataPanel = edsUI.getMapMetadataPanel();
        
        var miniMapPanel = edsUI.getMiniMapPanel();

        var mapPanelBackground = edsUI.getMapPanelBackground();
        var mapView = new MapPanelView(edsUI.getMapPanel());
        miniMapPanel.setViewport(mapView.getViewport());
        
        var tabbedPane = new JTabbedPane();
        tabbedPane.add("Map data", mapMetadataPanel);
        tabbedPane.add("Sprites", spritesPanel);
        
        frame.add(mainToolBar, BorderLayout.PAGE_START);
        
        var sp = new JPanel();
        sp.setLayout(new MigLayout());
        sp.add(tabbedPane, "dock center");
        sp.add(miniMapPanel, "dock south");
    
        toolPropertiesPanel = edsUI.getToolPropertiesPanel();
        
        var tilesetAndToolPropPanel = new JPanel();
        var tsetScrollPane = new JScrollPane(tilesetPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        tilesetAndToolPropPanel.setLayout(new MigLayout("", "fill"));
        tilesetAndToolPropPanel.add(tsetScrollPane, "cell 0 0");
        tilesetAndToolPropPanel.add(toolPropertiesPanel, "cell 0 1");
        
        
        menuBar = new JMenuBar();
        mFile = new JMenu("File");
        mFileNew = new JMenuItem("New");
        mFileOpen = new JMenuItem("Open");
        mFileSave = new JMenuItem("Save");
        mFileSaveAs = new JMenuItem("Save as...");
        mFileExit = new JMenuItem("Exit");
        
        mFile.add(mFileNew);
        mFile.add(mFileOpen);
        mFile.addSeparator();
        mFile.add(mFileSave);
        mFile.add(mFileSaveAs);
        mFile.addSeparator();
        mFile.add(mFileExit);
        
        mEpisode = new JMenu("Episode");
        mEpisodeNew = new JMenuItem("New");
        mEpisodeOpen = new JMenuItem("Open");
        mEpisodeExport = new JMenuItem("Export");
        
        mEpisode.setEnabled(false);
        mEpisode.add(mEpisodeNew);
        mEpisode.add(mEpisodeOpen);
        mEpisode.addSeparator();
        mEpisode.add(mEpisodeExport);
        
        mOther = new JMenu("Other");
        mOtherSettings = new JMenuItem("Settings");
        mOtherAbout = new JMenuItem("About");
        mOtherAbout.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, PekaEDSVersion.VERSION_STRING, "PekaEDS", JOptionPane.INFORMATION_MESSAGE);
        });
        
        mOther.add(mOtherSettings);
        mOther.add(mOtherAbout);
        
        menuBar.add(mFile);
        menuBar.add(mEpisode);
        menuBar.add(mOther);
        
        toolsToolBar = new ToolsToolBar(edsUI);
        
        setActionListeners();
        
        var tilesetAndToolsPanel = new JPanel();
        tilesetAndToolsPanel.setLayout(new MigLayout());
        tilesetAndToolsPanel.add(toolsToolBar, "dock west");
        tilesetAndToolsPanel.add(tilesetAndToolPropPanel, "dock center");
        
        frame.setJMenuBar(menuBar);
        
        frame.add(tilesetAndToolsPanel, BorderLayout.WEST);
        frame.add(mapView, BorderLayout.CENTER);
        frame.add(sp, BorderLayout.EAST);
        frame.add(edsUI.getStatusbar(), BorderLayout.SOUTH);
        
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(new Dimension(1920, 1080)); // Set to lower size and maximize on startup
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        setFrameIcon();
    }
    
    private void setFrameIcon() {
        // Not sure if this should be logged, it shouldn't fail. But if it does the program will not function correctly so just check and be safe.
        BufferedImage iconImg = null;
        try {
            var iconResource = getClass().getClassLoader().getResourceAsStream("levelEditorIcon.png");
        
            if (iconResource != null) iconImg = ImageIO.read(iconResource);
        } catch (IOException e) {
            System.out.println("unable to load icon");
        }
    
        if (iconImg != null) frame.setIconImage(iconImg);
    }
    
    private void setActionListeners() {
        mFileNew.addActionListener(new NewMapAction(edsUI));
        mFileOpen.addActionListener(new OpenMapAction(edsUI));
        mFileSave.addActionListener(new SaveMapAction(edsUI));
        
        mFileSaveAs.addActionListener(e -> {
            var fc = new JFileChooser("Save as...");
            fc.setCurrentDirectory(edsUI.getCurrentFile().getParentFile());
            
            fc.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().toLowerCase().endsWith(".map");
                }
    
                @Override
                public String getDescription() {
                    return "Pekka Kana 2 map file (*.map)";
                }
            });
            
            if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                edsUI.setCurrentFile(fc.getSelectedFile());
                
                edsUI.saveMap();
            }
        });
        
        mOtherSettings.addActionListener(e -> {
            if (settingsDialog == null) {
                settingsDialog = new SettingsDialog(edsUI);
            }
            
            settingsDialog.setVisible(true);
        });
        
        mFileExit.addActionListener(e -> System.exit(0));
    }
    
    void setFrameTitle(String title) {
        frame.setTitle(title);
    }
    
    public void setWindowListener(MainUIWindowListener mainUIWindowListener) {
        frame.addWindowListener(mainUIWindowListener);
    }
}
