package pekaeds.ui.main;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.io.*;


import java.time.LocalTime;

import org.tinylog.Logger;

import pekaeds.data.EditorConstants;
import pekaeds.data.Layer;
import pekaeds.data.PekaEDSVersion;
import pekaeds.tool.*;
import pekaeds.tool.tools.*;
import pekaeds.ui.actions.*;
import pekaeds.ui.listeners.MainUIWindowListener;
import pekaeds.ui.listeners.RepaintListener;
import pekaeds.ui.mapmetadatapanel.MapMetadataPanel;
import pekaeds.ui.sector.SectorMetadataPanel;
import pekaeds.ui.sector.SectorResizeDialog;
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
    //private ChangeEvent changeEvent = new ChangeEvent(this);

    private PekaEDSGUIView view;
    private PekaEDSGUIModel model;

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

    protected final Session session = new Session();

    public PekaEDSGUI() {
        showEditor();
    }


    void showEditor() {
        // This has to be done before PekaEDSGUIView gets initialized, because it relies on the toolsList in the Tools class.
        registerTools();

        view = new PekaEDSGUIView(this);
        model = new PekaEDSGUIModel();

        setupComponents();
        registerMapConsumers();
        registerRepaintListeners();
        registerPropertyListeners();

        Tool.setToolModeListener(mainToolBar);

        // This has to be done after setupComponents(), because View uses the components initialized in this method
        view.setupMainUI();

        registerChangeListeners();
        installKeyboardShortcuts();

        mapPanel.setLeftMouseTool(new BrushTool());
        mapPanel.setRightMouseTool(new SelectionTool());

        autosaveManager = new AutoSaveManager(this, model.getCurrentMapFile());
        autosaveManager.start();

        setSelectedTool(Tools.getTool(BrushTool.class));

        mapPanel.requestFocus();

        handleStartup();
    }

    private void handleStartup() {
        var fLastSession = FHSHelper.getPrefPath(EditorConstants.LAST_SESSION_FILE);
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
    }

    private void registerMapConsumers() {
        model.addMapConsumer(spritesPanel);
        model.addMapConsumer(mapMetadataPanel);
        model.addMapConsumer(mapPanel);
        model.addMapConsumer(sectorMetadataPanel);
        model.addMapConsumer(sectorListPanel);

        sectorListPanel.addSectorConsumer(sectorMetadataPanel);
        sectorListPanel.addSectorConsumer(miniMapPanel);
        sectorListPanel.addSectorConsumer(mapPanel);
        sectorListPanel.addSectorConsumer(tilesetPanel);
        sectorListPanel.addSectorConsumer(spritesPanel);
        sectorListPanel.addSectorConsumer(resizeDialog);

        model.addSectorConsumer(resizeDialog);
    }

    private void registerRepaintListeners() {
        model.addRepaintListener(mapPanel);
        model.addRepaintListener(tilesetPanel);
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
    public void loadMap(PK2Level map, File mapFile) {
        model.setCurrentMapFile(mapFile);

        if (mapFile != null) {
            session.putFile(mapFile);;
            setupOpenRecentMenu();
        }

        PK2LevelUtils.loadLevelAssets(map);

        Tool.reset();
        setSelectedTool(Tools.getTool(BrushTool.class));

        model.setCurrentMap(map);

        updateMapHolders();
        updateSectorHolders(map.sectors.get(0));

        sectorListPanel.setSelectedSector(0);
        mapPanel.setSelectedLayer(Layer.BOTH);

        mapPanel.resetView();

        updateFrameTitle();
    }

    public void loadMap(File file) {
        try {
            PK2Level level = PK2LevelIO.loadLevel(file);
            loadMap(level, file);

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
            
            mapMetadataPanel.commitValues();
            sectorMetadataPanel.commitValues();

            try {
                if (!file.getName().endsWith(".map")) file = new File(file.getAbsolutePath() + ".map");

                PK2LevelIO.saveLevel(model.getCurrentLevel(), file);
                unsavedChanges = false;

                model.setCurrentMapFile(file);
                session.putFile(file);
                setupOpenRecentMenu();


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
        loadMap(level, null);

        model.setCurrentMapFile(null);
        autosaveManager.setFile(null);
        unsavedChanges = false;

        tilesetPanel.resetSelection();

        sectorListPanel.setSelectedSector(0);

        mapPanel.resetView();

        /*if (episodeManager.hasEpisodeLoaded()) {
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
        }*/
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

    private void updateMapHolders() {
        Tool.setLevel(model.getCurrentLevel());
        for (var m : model.getMapConsumers()) {
            m.setMap(model.getCurrentLevel());
        }
    }

    private void updateSectorHolders(PK2LevelSector sector) {
        Tool.setSector(sector);
        for (var m : model.getSectorConsumers()) {
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
        session.save(FHSHelper.getPrefPath(EditorConstants.LAST_SESSION_FILE));

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

        /*if (episodeManager.hasEpisodeLoaded()) {
            sb.append(episodeManager.getEpisode().getEpisodeName());
            sb.append(" - ");
        }*/

        if (model.getCurrentMapFile() != null) {

            //TODO
            
            sb.append(model.getCurrentMapFile().getParentFile().getName()).append(File.separator).append(model.getCurrentMapFile().getName());
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
