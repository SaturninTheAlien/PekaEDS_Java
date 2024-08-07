package pekaeds.ui.main;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;

import java.util.List;

import java.time.LocalTime;

import org.tinylog.Logger;

import pekaeds.data.EditorConstants;
import pekaeds.data.Layer;
import pekaeds.data.PekaEDSVersion;
import pekaeds.pk2.file.PK2FileSystem;
import pekaeds.pk2.level.PK2Level;
import pekaeds.pk2.level.PK2LevelIO;
import pekaeds.pk2.level.PK2LevelSector;
import pekaeds.pk2.level.PK2LevelUtils;
import pekaeds.settings.Settings;
import pekaeds.settings.Shortcuts;
import pekaeds.settings.StartupBehavior;
import pekaeds.tool.*;
import pekaeds.tool.tools.*;
import pekaeds.ui.actions.*;
import pekaeds.ui.listeners.MainUIWindowListener;
import pekaeds.ui.listeners.RepaintListener;
import pekaeds.ui.mapmetadatapanel.LevelMetadataPanel;
import pekaeds.ui.mapmetadatapanel.SectorMetadataPanel;
import pekaeds.ui.mappanel.MapPanel;
import pekaeds.ui.mappanel.MapPanelView;
import pekaeds.ui.minimappanel.MiniMapPanel;
import pekaeds.ui.misc.UnsavedChangesDialog;
import pekaeds.ui.spritelistpanel.SpritesPanel;
import pekaeds.ui.tilesetpanel.TilesetPanel;
import pekaeds.ui.toolpropertiespanel.ToolPropertiesPanel;
import pekaeds.util.*;
import pekaeds.util.episodemanager.EpisodeManager;
import pekaeds.util.file.AutoSaveManager;
import pekaeds.util.file.Session;

public class PekaEDSGUI implements ChangeListener {
    //private ChangeEvent changeEvent = new ChangeEvent(this);

    private PekaEDSGUIView view;
    private PekaEDSGUIModel model;
    
    private TilesetPanel tilesetPanel;
    private MapPanel mapPanel;
    private MapPanelView mapPanelView;
    
    private MainToolBar mainToolBar;
    
    private SpritesPanel spritesPanel;
    private LevelMetadataPanel levelMetadataPanel;
    private SectorMetadataPanel sectorMetadataPanel;
    
    private MiniMapPanel miniMapPanel;
    
    private Statusbar statusbar;
    
    private ToolPropertiesPanel toolPropertiesPanel;
    
    private boolean unsavedChanges = false;
    
    private final AutoSaveManager autosaveManager;
    private final EpisodeManager episodeManager;
    
    private final Session session = new Session();
    
    public PekaEDSGUI() {
        // This has to be done before PekaEDSGUIView gets initialized, because it relies on the toolsList in the Tools class.
        registerTools();
    
        episodeManager = new EpisodeManager();
        
        view = new PekaEDSGUIView(this);
        model = new PekaEDSGUIModel();
    
        setupComponents();
        registerMapConsumers();
        registerRepaintListeners();
        registerPropertyListeners();
        
        Tool.setToolModeListener(mainToolBar);
        
        // This has to be done after setupComponents(), because View uses the components initialized in this method
        view.setupMainUI();
        
        mapPanel.getViewport().addChangeListener(miniMapPanel);
        
        registerChangeListeners();
        installKeyboardShortcuts();
        
        mapPanel.setLeftMouseTool(new BrushTool());
        mapPanel.setRightMouseTool(new SelectionTool());
        
        autosaveManager = new AutoSaveManager(this, model.getCurrentMapFile());
        autosaveManager.start();
    
        setSelectedTool(Tools.getTool(BrushTool.class));
        
        mapPanel.requestFocus();
        
        // TODO Optimization: Make this faster. Map loading might also need to be sped up/put in SwingUtils.invokeLater()
        handleStartup();
    }
    
    private void handleStartup() {
        var fLastSession = new File(EditorConstants.LAST_SESSION_FILE);
        if (fLastSession.exists()) {
            try {
                Logger.info("Trying to load last.session...");
                this.session.load(fLastSession);
                this.setupOpenRecentMenu(this.session.getRecentLevelFiles());
                
                switch (Settings.getDefaultStartupBehavior()) {
                    case StartupBehavior.NEW_MAP -> {
                        newLevel();
    
                        Logger.info("Creating new map.");
                    }
    
                    /*case StartupBehavior.LOAD_LAST_EPISODE -> {
                        if (lastSession.getLastEpisodeFile().exists()) {
                            episodeManager.loadEpisode(lastSession.getLastEpisodeFile());
            
                            loadLevel(lastSession.getLastLevelFile());
                            mapPanelView.getViewport().setViewPosition(new Point(lastSession.getLastViewportX(), lastSession.getLastViewportY()));
                            
                            Logger.info("Loaded last episode: {} file: {}", episodeManager.getEpisode().getEpisodeName(), lastSession.getLastLevelFile().getAbsolutePath());
                        } else {
                            newLevel();
                            
                            Logger.info("Unable to load last episode: {}. Creating new map instead.", lastSession.getLastEpisodeFile().getAbsolutePath());
                        }
                    }*/
                    
                    case StartupBehavior.LOAD_LAST_MAP -> {
                        if (this.session.getLastLevelFile().exists()) {
                            loadLevel(session.getLastLevelFile());
                            
                            //mapPanelView.getViewport().setViewPosition(new Point(lastSession.getLastViewportX(), lastSession.getLastViewportY()));
                            
                            Logger.info("Loaded last level: {}", session.getLastLevelFile().getAbsolutePath());
                        } else {
                            newLevel();
                            
                            Logger.info("Unable to load last level: {}. Creating new map instead.", session.getLastLevelFile().getAbsolutePath());
                        }
                    }
                }
            } catch (IOException e) {
                Logger.info(e, "Unable to load last session file. Creating new map.");
                
                newLevel();
            }
        } else {
            Logger.info("No last session found. Creating new map.");
            
            newLevel();
        }
    }
    
    private void setupComponents() {
        tilesetPanel = new TilesetPanel(this);
        mapPanel = new MapPanel();
        mapPanelView = new MapPanelView(mapPanel);
        
        mainToolBar = new MainToolBar(this);
        
        spritesPanel = new SpritesPanel(this);
        levelMetadataPanel = new LevelMetadataPanel(this);
        sectorMetadataPanel = new SectorMetadataPanel(this);
        
        miniMapPanel = new MiniMapPanel();
        
        statusbar = new Statusbar(this);
    
        toolPropertiesPanel = new ToolPropertiesPanel();
    }
    
    private void registerMapConsumers() {
        model.addMapConsumer(spritesPanel);
        model.addMapConsumer(levelMetadataPanel);
        model.addMapConsumer(mapPanel);
        model.addMapConsumer(sectorMetadataPanel);

        model.addSectorConsumer(sectorMetadataPanel);
        model.addSectorConsumer(miniMapPanel);
        model.addSectorConsumer(mapPanel);        
        model.addSectorConsumer(tilesetPanel);
        
    }
    
    private void registerRepaintListeners() {
        model.addRepaintListener(mapPanel);
        model.addRepaintListener(tilesetPanel);
    }
    
    private void registerChangeListeners() {
        view.setWindowListener(new MainUIWindowListener(this));
        
        spritesPanel.setChangeListener(this);
        levelMetadataPanel.setChangeListener(this);
        sectorMetadataPanel.setChangeListener(this);

        Tool.setChangeListener(this);
        
        Tool.setToolInformationListener(statusbar);
    }
    
    private void registerPropertyListeners() {
        model.addPropertyChangeListener(mainToolBar);
    }
    
    public void updateRepaintListeners() {
        model.getRepaintListeners().forEach(RepaintListener::doRepaint);
    }
    
    private void registerTools() {
        Tools.addTool(BrushTool.class);
        Tools.addTool(LineTool.class);
        Tools.addTool(RectangleTool.class);
        Tools.addTool(EraserTool.class);
        Tools.addTool(FloodFillTool.class);
        Tools.addTool(CutTool.class);
    }
    
    /*
        * Map related methods
     */
    public void loadLevel(PK2Level level, File levelFile) {

        model.setCurrentMapFile(levelFile);

        if(levelFile!=null){

            session.putLevelFile(levelFile);
            this.setupOpenRecentMenu(session.getRecentLevelFiles());

            File episodeDir = levelFile.getParentFile();
            if(episodeDir.exists()){
                PK2FileSystem.setEpisodeName(episodeDir.getName());
            }
            else{
                PK2FileSystem.setEpisodeName(null);
            }
        }
        else{
            PK2FileSystem.setEpisodeName(null);
        }
        
        PK2LevelUtils.loadLevelAssets(level);   
    
        Tool.reset();
        setSelectedTool(Tools.getTool(BrushTool.class));
   
        model.setCurrentLevel(level);

        updatelevelHolders();
        updateSectorHolders(level.sectors.get(0));
        
        updateFrameTitle();
    }
    
    public void loadLevel(File file) {

        try{
            Logger.info("Trying to load level file: {}", file.getAbsolutePath());
            PK2Level level = PK2LevelIO.loadLevel(file);
            this.loadLevel(level, file);
            unsavedChanges = false;
        }
        catch(Exception e){
            Logger.error(e);

        }
    }

    public void loadLevelSafe(File file){
        if (this.unsavedChangesPresent()) {
            int result = UnsavedChangesDialog.show(this);
    
            if (result != JOptionPane.CANCEL_OPTION && result != JOptionPane.CLOSED_OPTION) {
                this.loadLevel(file);
            }
        } else {
            this.loadLevel(file);
        }
    }
    
    public void saveLevel() {
        // If the file has not been saved yet, ask the user to give it a name and location
        if (model.getCurrentMapFile() == null) {
            var fc = new JFileChooser(PK2FileSystem.getAssetsPath(PK2FileSystem.EPISODES_DIR));
            fc.setDialogTitle("Save map...");
        
            if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                var file = fc.getSelectedFile();
            
                if (!file.getName().endsWith(".map")) file = new File(file.getPath() + ".map");
                
                model.setCurrentMapFile(file);
                autosaveManager.setFile(model.getCurrentMapFile());
            }
        }
        
        saveLevel(model.getCurrentMapFile());
    }
    
    public void saveLevel(File file) {
        if (file != null) {

            {
                session.putLevelFile(file);
                this.setupOpenRecentMenu(session.getRecentLevelFiles());
                File episodeDir = file.getParentFile();
                if(episodeDir.exists()){
                    PK2FileSystem.setEpisodeName(episodeDir.getName());
                }
            }
            levelMetadataPanel.commitValues();
            sectorMetadataPanel.commitValues();

            try{
                if (!file.getName().endsWith(".map")) file = new File(file.getPath() + ".map");

                PK2LevelIO.saveLevel(model.getCurrentLevel(), file);

                unsavedChanges = false;

            }
            catch(Exception e){
                Logger.error(e);
            }
    
            statusbar.setLastChangedTime(LocalTime.now());
            updateFrameTitle();
        }
    }
    
    public void newLevel() {
        Tool.reset();
        setSelectedTool(Tools.getTool(BrushTool.class));

        PK2Level level = PK2LevelUtils.createDefaultLevel();
        loadLevel(level, null);
        
        model.setCurrentMapFile(null);
        autosaveManager.setFile(null);
        unsavedChanges = false;
        
        tilesetPanel.resetSelection();
        
        mapPanelView.getViewport().setViewPosition(new Point(0, 0));
        
        if (episodeManager.hasEpisodeLoaded()) {
            var jopAddToEpisode = JOptionPane.showConfirmDialog(null, "Add file to episode \"" + episodeManager.getEpisode().getEpisodeName() + "\"?", "Add to episode?", JOptionPane.YES_NO_OPTION);
            
            if (jopAddToEpisode == JOptionPane.YES_OPTION) {
                var fc = new JFileChooser(episodeManager.getEpisode().getEpisodeFolder());
                fc.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.getName().endsWith(".map");
                    }
    
                    @Override
                    public String getDescription() {
                        return "Pekka Kana 2 map file (*.map)";
                    }
                }); // TODO Create PK2MapFilterFilter?
                
                // TODO Create a PK2MapFileChooser?
                fc.setDialogTitle("Save map file as...");
                
                if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    var selectedFile = fc.getSelectedFile();
                    
                    if (!selectedFile.getName().endsWith(".map")) {
                        selectedFile = new File(selectedFile.getAbsolutePath() + ".map");
                    }
                    
                    episodeManager.addFileToEpisode(selectedFile);
                    
                    model.setCurrentMapFile(selectedFile);
                    
                    saveLevel(selectedFile);
                } else {
                    JOptionPane.showMessageDialog(null, "File has not been added to episode.", "Not added to episode", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }
    
    public void setLayer(int layer) {
        Tool.setSelectedLayer(layer);
        
        mainToolBar.setSelectedLayer(layer);

        switch (layer) {
                case Layer.BACKGROUND -> tilesetPanel.useBackgroundTileset(true);
                case Layer.FOREGROUND, Layer.BOTH -> tilesetPanel.useBackgroundTileset(false);
        }
        
        tilesetPanel.repaint();
        
        mapPanel.getModel().setSelectedLayer(layer);
        mapPanel.repaint(); // TODO Optimize: Only repaint viewport
    }
    
    /*
        * Installing keyboard shortcuts
     */
    
    // Should probably add these to view.getFrame().getRootPane() or whatever
    public void installKeyboardShortcuts() {
        //Settings.resetKeyboardShortcuts();
        mapPanel.resetKeyboardActions();

        ShortcutUtils.install(mapPanel, Shortcuts.UNDO_ACTION, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Tool.getUndoManager().undoLastAction();
            
                mapPanel.repaint(); // TODO Repaint only affected areas?
            }
        });
    
        ShortcutUtils.install(mapPanel, Shortcuts.REDO_ACTION, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Tool.getUndoManager().redoLastAction();
            
                mapPanel.repaint(); // TODO Repaint only affected areas?
            }
        });
        
        ShortcutUtils.install(mapPanel, Shortcuts.SAVE_FILE_ACTION, new SaveLevelAction(this));
        ShortcutUtils.install(mapPanel, Shortcuts.OPEN_FILE_ACTION, new OpenLevelAction(this));
        ShortcutUtils.install(mapPanel, Shortcuts.TEST_MAP_ACTION, new PlayLevelAction(this));
        
        ShortcutUtils.install(mapPanel, Shortcuts.SELECT_BOTH_LAYER_ACTION, new SwitchLayerAction(this, Layer.BOTH));
        ShortcutUtils.install(mapPanel, Shortcuts.SELECT_FOREGROUND_LAYER_ACTION, new SwitchLayerAction(this, Layer.FOREGROUND));
        ShortcutUtils.install(mapPanel, Shortcuts.SELECT_BACKGROUND_LAYER_ACTION, new SwitchLayerAction(this, Layer.BACKGROUND));
        
        ShortcutUtils.install(mapPanel, Shortcuts.SELECT_TILE_MODE, new SwitchModeAction(this, Tool.MODE_TILE));
        ShortcutUtils.install(mapPanel, Shortcuts.SELECT_SPRITE_MODE, new SwitchModeAction(this, Tool.MODE_SPRITE));
        
        ShortcutUtils.install(mapPanel, Shortcuts.TOOL_BRUSH, new SetSelectedToolAction(this, Tools.getTool(BrushTool.class)));
        ShortcutUtils.install(mapPanel, Shortcuts.TOOL_ERASER, new SetSelectedToolAction(this, Tools.getTool(EraserTool.class)));
        ShortcutUtils.install(mapPanel, Shortcuts.TOOL_LINE, new SetSelectedToolAction(this, Tools.getTool(LineTool.class)));
        ShortcutUtils.install(mapPanel, Shortcuts.TOOL_RECT, new SetSelectedToolAction(this, Tools.getTool(RectangleTool.class)));
        ShortcutUtils.install(mapPanel, Shortcuts.TOOL_CUT, new SetSelectedToolAction(this, Tools.getTool(CutTool.class)));
        ShortcutUtils.install(mapPanel, Shortcuts.TOOL_FLOOD_FILL, new SetSelectedToolAction(this, Tools.getTool(FloodFillTool.class)));
    }
    
    private void updatelevelHolders() {
        Tool.setLevel(model.getCurrentLevel());
        for (var m : model.getMapConsumers()) {
            m.setLevel(model.getCurrentLevel());
        }
    }

    private void updateSectorHolders(PK2LevelSector sector){
        Tool.setSector(sector);
        for(var m: model.getSectorConsumers()){
            m.setSector(sector);
        }
    }
    
    public void repaintView() {
        mapPanel.repaint();
    }
    
    public boolean unsavedChangesPresent() {
        return unsavedChanges;
    }
    
    /**
     * This method gets called when the whole application shuts down.
     */
    public void close() {
        this.session.save(new File(EditorConstants.LAST_SESSION_FILE));
        
        System.exit(0);
    }
    
    public File getCurrentFile() {
        return model.getCurrentMapFile();
    }
    
    public void setCurrentFile(File file) {
        model.setCurrentMapFile(file);
    }
    
    private void updateFrameTitle() {
        var sb = new StringBuilder();
        
        if (episodeManager.hasEpisodeLoaded()) {
            sb.append(episodeManager.getEpisode().getEpisodeName());
            sb.append(" - ");
        }
        
        if (model.getCurrentMapFile() != null) {
            if (episodeManager.hasEpisodeLoaded()) {
                sb.append(model.getCurrentMapFile().getName());
            } else {
                sb.append(model.getCurrentMapFile().getParentFile().getName()).append(File.separator).append(model.getCurrentMapFile().getName());
            }
        } else {
            sb.append("Unnamed");
        }

        if (unsavedChanges) {
            sb.append("*");
        }
        
        sb.append(" - PekaEDS ");
        sb.append(PekaEDSVersion.VERSION_STRING);
        
        view.setFrameTitle(sb.toString());
    }
    
    @Override
    public void stateChanged(ChangeEvent e) {
        if (!unsavedChanges) {
            unsavedChanges = true;
            
            updateFrameTitle();
        }
    }
    
    private Tool currentTool = null;
    public void setSelectedTool(Tool selectedTool, boolean ignorePrompts) {
        if (currentTool != selectedTool) {
            if (currentTool != null) currentTool.onDeselect(ignorePrompts);
            //Tool.setMode(Tool.MODE_TILE);
            
            mapPanel.setLeftMouseTool(selectedTool);
    
            toolPropertiesPanel.setSelectedTool(selectedTool);
    
            ((ToolsToolBar) view.getToolsToolBar()).setSelectedTool(selectedTool);
            
            currentTool = selectedTool;
            currentTool.onSelect();
        }
    }
    
    public void setSelectedTool(Tool selectedTool) {
        setSelectedTool(selectedTool, true);
    }
    
    /*
        Getters for components that get added to the frame in PekaEDSGUIView
     */
    
    TilesetPanel getTilesetPanel() {
        return tilesetPanel;
    }
    
    MapPanel getMapPanel() {
        return mapPanel;
    }
    
    MainToolBar getMainToolBar() {
        return mainToolBar;
    }
    
    SpritesPanel getSpritesPanel() {
        return spritesPanel;
    }
    
    MiniMapPanel getMiniMapPanel() {
        return miniMapPanel;
    }
    
    LevelMetadataPanel getLevelMetadataPanel() {
        return levelMetadataPanel;
    }

    SectorMetadataPanel getSectorMetadataPanel(){
        return this.sectorMetadataPanel;
    }
    
    Statusbar getStatusbar() { return statusbar; }
    
    ToolPropertiesPanel getToolPropertiesPanel() {
        return toolPropertiesPanel;
    }
    
    public EpisodeManager getEpisodeManager() {
        return episodeManager;
    }
    
    public void updateAutosaveManager() {
        autosaveManager.setInterval(Settings.getAutosaveInterval());
        autosaveManager.setFileCount(Settings.getAutosaveFileCount());
    }
    

    public MapPanelView getMapPanelView() {
        return mapPanelView;
    }

    public void setupOpenRecentMenu(List<File> files){
        this.view.setupOpenRecentMenu(files);
    }
}
