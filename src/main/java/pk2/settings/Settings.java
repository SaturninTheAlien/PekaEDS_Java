package pk2.settings;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.tinylog.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import pekaeds.ui.misc.LookAndFeelHelper;
import pk2.profile.LevelProfile;
import pk2.profile.SpriteProfile;

/**
 * TODO: This class shouldn't have anything static in it.
 * It being static will cause problems with initialization!
 */
public class Settings {
    private static final List<String> layerNames = new ArrayList<>();

    static {
        layerNames.add("Both");
        layerNames.add("Foreground");
        layerNames.add("Background");
    }
    
    private static String basePath;
    private static String lookAndFeel;
    
    private static String defaultTileset = "tiles01.bmp";
    private static String defaultBackground = "castle.bmp";
    
    private static String defaultAuthor = "Unknown";
    private static String defaultMapName = "Unnamed";
    
    private static String defaultMusic = "song01.xm";

    public static LevelTestingSettings levelTestingSettings = new LevelTestingSettings();
    
    private static LevelProfile mapProfile = LevelProfile.getDefaultProfile();
    private static SpriteProfile spriteProfile;
        
    public static boolean highlightSprites = true;
    private static boolean showTileNumberInTileset = true;
    
    private static final Map<String, KeyStroke> keyboardShortcuts = new HashMap<>();
    
    private static int defaultStartupBehavior = StartupBehavior.NEW_MAP;

    private static int autosaveInterval = 120000; // 2 minutes
    private static int autosaveFileCount = 3;
    
    public static boolean showSprites = true;
    public static boolean showBgSprites = true;
    public static boolean showTransparentLayers = true;

    private static boolean highlightSelection = true;
    
    
    /**
     * Register actions with keystrokes.
     *
     * If you want to add a new shortcut you also need to do it here.
     *
     * If you want the keystroke to not have a modifier pass 0. Like Shortcuts.TEST_MAP_ACTION: The key is F5, the InputEvent is 0. That means that the shortcut is just F5.
     */
    public static void resetKeyboardShortcuts() {
        keyboardShortcuts.put(Shortcuts.UNDO_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        keyboardShortcuts.put(Shortcuts.REDO_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
        
        keyboardShortcuts.put(Shortcuts.SAVE_FILE_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        keyboardShortcuts.put(Shortcuts.OPEN_FILE_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        
        keyboardShortcuts.put(Shortcuts.TEST_MAP_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        
        keyboardShortcuts.put(Shortcuts.SELECT_BOTH_LAYER_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_1, 0));
        keyboardShortcuts.put(Shortcuts.SELECT_FOREGROUND_LAYER_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_2, 0));
        keyboardShortcuts.put(Shortcuts.SELECT_BACKGROUND_LAYER_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_3, 0));
        
        keyboardShortcuts.put(Shortcuts.SELECT_TILE_MODE, KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.CTRL_DOWN_MASK));
        keyboardShortcuts.put(Shortcuts.SELECT_SPRITE_MODE, KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.CTRL_DOWN_MASK));
        
        keyboardShortcuts.put(Shortcuts.TOOL_BRUSH, KeyStroke.getKeyStroke(KeyEvent.VK_W, 0));
        keyboardShortcuts.put(Shortcuts.TOOL_ERASER, KeyStroke.getKeyStroke(KeyEvent.VK_E, 0));
        keyboardShortcuts.put(Shortcuts.TOOL_LINE, KeyStroke.getKeyStroke(KeyEvent.VK_R, 0));
        keyboardShortcuts.put(Shortcuts.TOOL_RECT, KeyStroke.getKeyStroke(KeyEvent.VK_T, 0));
        keyboardShortcuts.put(Shortcuts.TOOL_CUT, KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0));
        keyboardShortcuts.put(Shortcuts.TOOL_FLOOD_FILL, KeyStroke.getKeyStroke(KeyEvent.VK_F, 0));
    }
    
    public static void load(File file) throws IOException {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            basePath = dis.readUTF();
            lookAndFeel = dis.readUTF();

            defaultTileset = dis.readUTF();
            defaultBackground = dis.readUTF();
            defaultAuthor = dis.readUTF();
            defaultMapName = dis.readUTF();
            defaultMusic = dis.readUTF();

            levelTestingSettings.load(dis);
            
            highlightSprites = dis.readBoolean();
            showTileNumberInTileset = dis.readBoolean();
            showSprites = dis.readBoolean();
            showBgSprites = dis.readBoolean();
            
            highlightSelection = dis.readBoolean();
            
            defaultStartupBehavior = dis.readInt();
            
            autosaveInterval = dis.readInt();
            autosaveFileCount = dis.readInt();

           
            int shortcutAmount = dis.readInt();
            for (int i = 0; i < shortcutAmount; i++) {
                setKeyboardShortcutFor(dis.readUTF(), KeyStroke.getKeyStroke(dis.readInt(), dis.readInt()));
            }
            
            setBasePath(basePath);           


        } catch (IOException e) {
            Logger.warn(e, "Unable to load settings file.");
            
            // TODO Why did I throw this here?
            throw e;
        }


        //Load sprite profile
        try{
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            spriteProfile = mapper.readValue(SpriteProfile.class.getResourceAsStream("/profiles/sprite.yml"), SpriteProfile.class);
        }
        catch(Exception e){
            Logger.warn(e, "Unable to load the sprite profile file.");
            spriteProfile = new SpriteProfile();
        }
    }
    
    public static void save(File file) {
        try (var dos = new DataOutputStream(new FileOutputStream(file))){
            dos.writeUTF(basePath);
            dos.writeUTF(lookAndFeel);

            dos.writeUTF(defaultTileset);
            dos.writeUTF(defaultBackground);
            dos.writeUTF(defaultAuthor);
            dos.writeUTF(defaultMapName);
            dos.writeUTF(defaultMusic);
            
            levelTestingSettings.save(dos);
            
            dos.writeBoolean(highlightSprites);
            dos.writeBoolean(showTileNumberInTileset);
            dos.writeBoolean(showSprites);
            dos.writeBoolean(showBgSprites);
            dos.writeBoolean(highlightSelection);
            
            dos.writeInt(defaultStartupBehavior);
            
            dos.writeInt(autosaveInterval);
            dos.writeInt(autosaveFileCount);
                        
            dos.writeInt(keyboardShortcuts.size());
            for (var e : keyboardShortcuts.entrySet()) {
                dos.writeUTF(e.getKey());
                dos.writeInt(e.getValue().getKeyCode());
                dos.writeInt(e.getValue().getModifiers());
            }
            
            dos.flush();
        } catch (IOException e) {
            Logger.warn("Unable to save settings file.");
        }
    }
    
    public static void reset() {

        lookAndFeel = LookAndFeelHelper.getDefaultTheme();
    
        defaultTileset = "tiles01.bmp";
        defaultBackground = "castle.bmp";
    
        defaultAuthor = "Unknown";
        defaultMapName = "Unnamed";
    
        defaultMusic = "song01.xm";

        levelTestingSettings = new LevelTestingSettings();
        
        defaultStartupBehavior = StartupBehavior.NEW_MAP;
        
        highlightSprites = true;
        showTileNumberInTileset = true;
        
        showSprites = true;
        highlightSelection = true;
        
        autosaveInterval = 120000;
        autosaveFileCount = 3;

        resetKeyboardShortcuts();
    }
    /*
        Getters & Setters
     */
    
    public static void setBasePath(String path) {
        basePath = path;
    }

    public static void setLookAndFeel(String theme){
        lookAndFeel = theme;
    }
    
    public static void setKeyboardShortcutFor(String actionName, KeyStroke keyStroke) {
        keyboardShortcuts.put(actionName, keyStroke);
    }
    
    public static KeyStroke getKeyboardShortcutFor(String actionName) {
        return keyboardShortcuts.get(actionName);
    }
    
    public static void setShowTileNumberInTileset(boolean show) {
        showTileNumberInTileset = show;
    }
    
    public static boolean showTilesetNumberInTileset() {
        return showTileNumberInTileset;
    }
    
    public List<String> getLayerNames() {
        return new ArrayList<>(layerNames);
    }
    
    public static boolean doesBasePathExist() {
        return Files.exists(Path.of(basePath));
    }
    
    public static String getBasePath() {
        return basePath;
    }

    public static String getLookAndFeel(){
        return lookAndFeel;
    }
    
    public static String getDefaultTileset() {
        return defaultTileset;
    }
    
    public static String getDefaultBackground() {
        return defaultBackground;
    }
    
    public static void setDefaultTileset(String tileset) {
        defaultTileset = tileset;
    }
    
    public static void setDefaultBackground(String background) {
        defaultBackground = background;
    }
    
    public static LevelProfile getMapProfile() {
        return mapProfile;
    }
    
    public static String getDefaultAuthor() {
        return defaultAuthor;
    }
    
    public static void setDefaultAuthor(String defaultAuthor) {
        Settings.defaultAuthor = defaultAuthor;
    }
    
    public static String getDefaultMapName() {
        return defaultMapName;
    }
    
    public static void setDefaultMapName(String defaultMapName) {
        Settings.defaultMapName = defaultMapName;
    }
    
    public static String getDefaultMusic() {
        return defaultMusic;
    }
    
    public static void setDefaultMusic(String defaultMusic) {
        Settings.defaultMusic = defaultMusic;
    }
    
    public void setMapProfile(LevelProfile mProfile) {
        mapProfile = mProfile;
    }
    
    public static SpriteProfile getSpriteProfile() {
        return spriteProfile;
    }

    public static void setSpriteProfile(SpriteProfile sprProfile) {
        spriteProfile = sprProfile;
    }
    
    public static int getDefaultStartupBehavior() {
        return defaultStartupBehavior;
    }
    
    public static void setDefaultStartupBehavior(int behavior) {
        defaultStartupBehavior = behavior;
    }
    
    public static void setAutosaveInterval(int delay) {
        autosaveInterval = delay;
    }
    
    public static int getAutosaveInterval() {
        return autosaveInterval;
    }
    
    public static int getAutosaveFileCount() {
        return autosaveFileCount;
    }
    
    public static void setAutosaveFileCount(int count) {
        autosaveFileCount = count;
    }
    
    public static void setHighlightSelection(boolean highlight) {
        highlightSelection = highlight;
    }
    
    public static boolean highlightSelection() {
        return highlightSelection;
    }
}
