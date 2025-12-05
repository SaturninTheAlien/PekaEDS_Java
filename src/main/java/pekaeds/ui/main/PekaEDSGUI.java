package pekaeds.ui.main;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.io.*;


import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.tinylog.Logger;

import pekaeds.data.Layer;
import pekaeds.data.PekaEDSVersion;
import pekaeds.tool.*;
import pekaeds.tool.tools.*;
import pekaeds.ui.actions.*;
import pekaeds.ui.decorator.DecoratorDialog;
import pekaeds.ui.listeners.MainUIWindowListener;
import pekaeds.ui.listeners.PK2MapConsumer;
import pekaeds.ui.listeners.PK2SectorConsumer;
import pekaeds.ui.listeners.RepaintListener;
import pekaeds.ui.mapmetadatapanel.MapMetadataPanel;
import pekaeds.ui.sector.SectorMetadataPanel;
import pekaeds.ui.sector.SectorResizeDialog;
import pekaeds.ui.sector.SectorStatisticsDialog;
import pekaeds.ui.mappanel.MapPanel;
import pekaeds.ui.minimappanel.MiniMapPanel;
import pekaeds.ui.misc.UnsavedChangesDialog;
import pekaeds.ui.sectorlistpanel.SectorListPanel;
import pekaeds.ui.spritelistpanel.SpritesPanel;
import pekaeds.ui.tilesetpanel.TilesetPanel;
import pekaeds.ui.toolpropertiespanel.ToolPropertiesPanel;
import pekaeds.util.*;
import pekaeds.util.file.AutoSaveManager;
import pekaeds.util.file.Session;
import pk2.filesystem.FHSHelper;
import pk2.filesystem.PK2FileSystem;
import pk2.level.PK2Level;
import pk2.level.PK2LevelIO;
import pk2.level.PK2LevelSector;
import pk2.level.PK2LevelUtils;
import pk2.settings.Settings;
import pk2.settings.Shortcuts;
import pk2.settings.StartupBehavior;
import pk2.util.LevelTestingUtil;

public class PekaEDSGUI implements ChangeListener, IPekaEdsApp {

    private static final String LAST_SESSION_FILE = "last.session";

    private PekaEDSGUIView view;
    private TilesetPanel tilesetPanel;
    private MapPanel mapPanel;

    private MainToolBar mainToolBar;

    private SpritesPanel spritesPanel;
    private MapMetadataPanel mapMetadataPanel;
    private SectorMetadataPanel sectorMetadataPanel;
    private SectorListPanel sectorListPanel;

    private MiniMapPanel miniMapPanel;

    private Statusbar statusbar;

    private ToolPropertiesPanel toolPropertiesPanel;

    private boolean unsavedChanges = false;

    private AutoSaveManager autosaveManager;

    private SectorResizeDialog resizeDialog;

    protected SectorStatisticsDialog statisticsDialog;
    protected DecoratorDialog decoratorDialog;

    protected final Session session = new Session();


    private PK2Level currentLevel;
    private File currentFile = null;    
    private final List<PK2MapConsumer> mapConsumers = new ArrayList<>();
    private final List<PK2SectorConsumer> sectorConsumers = new ArrayList<>();
    private final List<RepaintListener> repaintListeners = new ArrayList<>();

    public PekaEDSGUI() {
        showEditor();
    }


    void showEditor() {
        // This has to be done before PekaEDSGUIView gets initialized, because it relies on the toolsList in the Tools class.
        registerTools();

        view = new PekaEDSGUIView(this);

        setupComponents();
        registerMapConsumers();
        registerRepaintListeners();

        Tool.setToolModeListener(mainToolBar);

        // This has to be done after setupComponents(), because View uses the components initialized in this method
        view.setupMainUI();

        registerChangeListeners();
        installKeyboardShortcuts();

        mapPanel.setLeftMouseTool(new BrushTool());
        mapPanel.setRightMouseTool(new SelectionTool());

        autosaveManager = new AutoSaveManager(this, this.currentFile);
        autosaveManager.start();

        setSelectedTool(Tools.getTool(BrushTool.class));

        mapPanel.requestFocus();

        handleStartup();
    }

    private void handleStartup() {
        var fLastSession = FHSHelper.getPrefPath(LAST_SESSION_FILE);
        if (fLastSession.exists()) {
            try {
                Logger.info("Trying to load last.session...");
                this.session.load(fLastSession);
                this.setupOpenRecentMenu();

                switch (Settings.getDefaultStartupBehavior()) {
                    case StartupBehavior.NEW_MAP -> {
                        newMap();

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
                        if (this.session.getLastFile().exists()) {
                            loadMap(session.getLastFile());

                            // TODO Store and load last position?
                            //mapPanelView.getViewport().setViewPosition(new Point(lastSession.getLastViewportX(), lastSession.getLastViewportY()));

                            Logger.info("Loaded last level: {}", session.getLastFile().getAbsolutePath());
                        } else {
                            newMap();

                            Logger.info("Unable to load last level: {}. Creating new map instead.", session.getLastFile().getAbsolutePath());
                        }
                    }
                }
            } catch (IOException e) {
                Logger.info(e, "Unable to load last session file. Creating new map.");

                newMap();
            }
        } else {
            Logger.info("No last session found. Creating new map.");

            newMap();
        }
    }

    private void setupComponents() {
        tilesetPanel = new TilesetPanel(this);
        mapPanel = new MapPanel();

        mainToolBar = new MainToolBar(this);

        spritesPanel = new SpritesPanel(this);
        mapMetadataPanel = new MapMetadataPanel(this);
        sectorMetadataPanel = new SectorMetadataPanel(this);
        sectorListPanel = new SectorListPanel(this);

        miniMapPanel = new MiniMapPanel();

        statusbar = new Statusbar(this);

        toolPropertiesPanel = new ToolPropertiesPanel();

        mapPanel.setMiniMapPanel(miniMapPanel);
        miniMapPanel.setMapPanel(mapPanel);

        resizeDialog = new SectorResizeDialog(this);
        statisticsDialog = new SectorStatisticsDialog();
        decoratorDialog = new DecoratorDialog(this);
    }

    private void registerMapConsumers() {
        this.mapConsumers.add(spritesPanel);
        this.mapConsumers.add(mapMetadataPanel);
        this.mapConsumers.add(mapPanel);
        this.mapConsumers.add(sectorMetadataPanel);
        this.mapConsumers.add(sectorListPanel);

        sectorListPanel.addSectorConsumer(sectorMetadataPanel);
        sectorListPanel.addSectorConsumer(miniMapPanel);
        sectorListPanel.addSectorConsumer(mapPanel);
        sectorListPanel.addSectorConsumer(tilesetPanel);
        sectorListPanel.addSectorConsumer(spritesPanel);
        sectorListPanel.addSectorConsumer(resizeDialog);
        sectorListPanel.addSectorConsumer(statisticsDialog);
        sectorListPanel.addSectorConsumer(decoratorDialog);

        this.sectorConsumers.add(resizeDialog);
    }

    private void registerRepaintListeners() {
        this.repaintListeners.add(mapPanel);
        this.repaintListeners.add(tilesetPanel);
    }

    private void registerChangeListeners() {
        view.setWindowListener(new MainUIWindowListener(this));

        spritesPanel.setChangeListener(this);
        mapMetadataPanel.setChangeListener(this);
        sectorMetadataPanel.setChangeListener(this);

        mapPanel.setResizeRectListener(resizeDialog);
        resizeDialog.setResizeListener(mapPanel);

        Tool.setChangeListener(this);
        Tool.setToolInformationListener(statusbar);
    }

    public void startSectorResize() {
        mapPanel.setResizingSector(true);
        resizeDialog.setVisible(true);
    }

    public void resizeSector(int startX, int startY, int newWidth, int newHeight) {
        mapPanel.resizeCurrentSector(startX,startY,newWidth,newHeight);
        //mapPanel.setResizingSector(false);

        setUnsavedChangesPresent(true);
    }

    public void cancelResizing(){
        mapPanel.setResizingSector(false);
    }

    public void updateRepaintListeners() {
        this.repaintListeners.forEach(RepaintListener::doRepaint);
    }

    private void registerTools() {
        Tools.addTool(BrushTool.class);
        Tools.addTool(LineTool.class);
        Tools.addTool(RectangleTool.class);
        Tools.addTool(EraserTool.class);
        Tools.addTool(AreaEraserTool.class);
        Tools.addTool(FloodFillTool.class);
        Tools.addTool(CutTool.class);
    }

    public void setCurrentFile(File file){       
        this.currentFile = file;
        autosaveManager.setFile(file);
        if(file!=null){
            session.putFile(file);
            setupOpenRecentMenu();
            PK2FileSystem.setEpisodeDir(file.getParentFile());
        }
    }


    public void setCurrentSector(PK2LevelSector sector){
        Tool.setSector(sector);
        for (var m : this.sectorConsumers) {
            m.setSector(sector);
        }
    }

    public void setCurrentLevel(PK2Level level){

        this.currentLevel = level;

        PK2LevelUtils.loadLevelAssets(level);

        Tool.reset();
        setSelectedTool(Tools.getTool(BrushTool.class));


        Tool.setLevel(this.currentLevel);
        for (var m : this.mapConsumers) {
            m.setMap(this.currentLevel);
        }

        this.setCurrentSector(level.sectors.get(0));

        tilesetPanel.resetSelection();
        sectorListPanel.setSelectedSector(0);
        //mapPanel.setSelectedLayer(Layer.BOTH);

        mapPanel.resetView();

        updateFrameTitle();
    }

    public void loadMap(File file) {
        try {
            PK2Level level = PK2LevelIO.loadLevel(file);
            this.setCurrentFile(file);
            this.setCurrentLevel(level);

            unsavedChanges = false;
        } catch (Exception e) {
            Logger.error(e);
        }
    }

    public void loadLevelSafe(File file) {
        if (unsavedChangesPresent()) {
            int result = UnsavedChangesDialog.show(this);

            if (result != JOptionPane.CANCEL_OPTION && result != JOptionPane.CLOSED_OPTION) {
                loadMap(file);
            }
        } else {
            loadMap(file);
        }
    }

    public void saveLevel() {
        // If the file has not been saved yet, ask the user to give it a name and location
        if (this.currentFile == null) {
            var fc = new JFileChooser(PK2FileSystem.getAssetsPath(PK2FileSystem.EPISODES_DIR));
            fc.setDialogTitle("Save level...");

            if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                var file = fc.getSelectedFile();
                this.saveLevel(file);       
            }
        }
        else{
            this.saveLevel(this.currentFile);
        }
    }

    public void saveLevel(File file) {
        if (file != null) {
            
            mapMetadataPanel.commitValues();
            sectorMetadataPanel.commitValues();

            try {
                if (!file.getName().endsWith(".map")) file = new File(file.getAbsolutePath() + ".map");

                PK2LevelIO.saveLevel(this.currentLevel, file);
                this.setCurrentFile(file);
                unsavedChanges = false;

            } catch (Exception e) {
                Logger.error(e);
            }

            statusbar.setLastChangedTime(LocalTime.now());
            updateFrameTitle();
        }
    }

    public void newMap() {

        PK2LevelSector.clearBaseSpriteSheets();
        Tool.reset();
        setSelectedTool(Tools.getTool(BrushTool.class));

        PK2Level level = PK2LevelUtils.createDefaultLevel();
        this.setCurrentFile(null);
        this.setCurrentLevel(level);
        unsavedChanges = false;
    }

    public void setLayer(int layer) {
        Tool.setSelectedLayer(layer);

        mainToolBar.setSelectedLayer(layer);

        switch (layer) {
            case Layer.BACKGROUND -> tilesetPanel.useBackgroundTileset(true);
            case Layer.FOREGROUND, Layer.BOTH -> tilesetPanel.useBackgroundTileset(false);
        }

        tilesetPanel.repaint();

        mapPanel.setSelectedLayer(layer);
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
        session.save(FHSHelper.getPrefPath(LAST_SESSION_FILE));

        System.exit(0);
    }

    public File getCurrentFile() {
        return this.currentFile;
    }


    private void updateFrameTitle() {
        var sb = new StringBuilder();

        /*if (episodeManager.hasEpisodeLoaded()) {
            sb.append(episodeManager.getEpisode().getEpisodeName());
            sb.append(" - ");
        }*/

        if (this.currentFile != null) {

            //TODO
            
            sb.append(this.currentFile.getParentFile().getName()).append(File.separator).append(this.currentFile.getName());
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


    public void setSelectedToolIfEraser(Tool selectedTool){
        if(this.currentTool.isEraser()){
            this.setSelectedTool(selectedTool);
        }
    }


    public void testLevel(){

        if (this.getCurrentFile()==null || this.unsavedChangesPresent()) {
            int result = UnsavedChangesDialog.show(this);
            if (result != JOptionPane.CANCEL_OPTION && result != JOptionPane.CLOSED_OPTION) {
                this.saveLevel();
                LevelTestingUtil.playLevel(this.getCurrentFile());
            }
        }
        else {
            LevelTestingUtil.playLevel(this.getCurrentFile());
        }
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

    MapMetadataPanel getLevelMetadataPanel() {
        return mapMetadataPanel;
    }

    SectorMetadataPanel getSectorMetadataPanel() {
        return this.sectorMetadataPanel;
    }

    Statusbar getStatusbar() {
        return statusbar;
    }

    ToolPropertiesPanel getToolPropertiesPanel() {
        return toolPropertiesPanel;
    }

    SectorListPanel getSectorListPanel() {
        return sectorListPanel;
    }

    public void updateAutosaveManager() {
        autosaveManager.setInterval(Settings.getAutosaveInterval());
        autosaveManager.setFileCount(Settings.getAutosaveFileCount());
    }

    public void setupOpenRecentMenu() {
        this.view.setupOpenRecentMenu(session.getRecentFiles());
    }

    public void setUnsavedChangesPresent(boolean value) {
        unsavedChanges = value;

        updateFrameTitle();
    }


    @Override
    public void updateLookAndFeel() {
        SwingUtilities.updateComponentTreeUI(this.view.getFrame());
    }
}
