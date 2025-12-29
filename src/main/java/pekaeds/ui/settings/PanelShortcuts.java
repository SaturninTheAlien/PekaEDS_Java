package pekaeds.ui.settings;

import net.miginfocom.swing.MigLayout;
import pekaeds.ui.main.PekaEDSGUI;
import pk2.settings.Settings;
import pk2.settings.Shortcuts;

import javax.swing.*;

import org.tinylog.Logger;

import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class PanelShortcuts extends JPanel implements ISettingsPanel {

    private PekaEDSGUI eds;
    private final Map<String, KeyStroke> shortcutMap = new HashMap<>();
    
    private final Map<String, JToggleButton> buttonMap = new HashMap<>(); // This map is needed so that the buttons text can be reset, when the user decides not to apply the changes.
    
    public PanelShortcuts(PekaEDSGUI eds) {
        this.eds = eds;
        setBorder(BorderFactory.createTitledBorder("Set keyboard shortcuts"));
        
        setLayout(new MigLayout("wrap 3, fillx", "[fill][fill][fill]"));
        
        addShortcutEntry("Save file", Shortcuts.SAVE_FILE_ACTION);
        addShortcutEntry("Open file", Shortcuts.OPEN_FILE_ACTION);
        addShortcutEntry("Test map", Shortcuts.TEST_MAP_ACTION);
        
        addShortcutEntry("Select layer \"Both\"", Shortcuts.SELECT_BOTH_LAYER_ACTION);
        addShortcutEntry("Select layer \"Foreground\"", Shortcuts.SELECT_FOREGROUND_LAYER_ACTION);
        addShortcutEntry("Select layer \"Background\"", Shortcuts.SELECT_BACKGROUND_LAYER_ACTION);
        
        addShortcutEntry("Select tile mode", Shortcuts.SELECT_TILE_MODE);
        addShortcutEntry("Select sprite mode", Shortcuts.SELECT_SPRITE_MODE);
        
        addShortcutEntry("Brush tool", Shortcuts.TOOL_BRUSH);
        addShortcutEntry("Eraser tool", Shortcuts.TOOL_ERASER);
        addShortcutEntry("Area eraser tool", Shortcuts.TOOL_AREA_ERASER);

        addShortcutEntry("Line tool", Shortcuts.TOOL_LINE);
        addShortcutEntry("Rectangle tool", Shortcuts.TOOL_RECT);
        addShortcutEntry("Cut tool", Shortcuts.TOOL_CUT);
        addShortcutEntry("Flood fill tool", Shortcuts.TOOL_FLOOD_FILL);
    }
    
    private JToggleButton lastPressedButton = null;
    private String lastPressedButtonText = "";
    private void addShortcutEntry(String description, String actionName) {
        var shortcut = Settings.getKeyboardShortcutFor(actionName);
        var shortcutStr = shortcut.toString();
        
        // KeyStroke.ToString() includes the state of the key, we don't want that, so it gets replaced by a "+"
        shortcutStr = shortcutStr.replace("pressed", "+");
        
        // If no modifier key is set the string will be "pressed KEY", in that case it would turn into "+ KEY", we don't want that either.
        if (shortcutStr.charAt(0) == '+') {
            shortcutStr = shortcutStr.substring(1);
        }
        
        var lbl = new JLabel(description);
        var btn = new JToggleButton(shortcutStr.toUpperCase());
        
        btn.addActionListener(e -> {
            if (lastPressedButton != null) {
                lastPressedButton.setSelected(false);
                lastPressedButton.setText(lastPressedButtonText);
            }
            
            lastPressedButton = btn;
            lastPressedButtonText = btn.getText();
            
            btn.setSelected(true);
            btn.setText("Press keys...");
        });
        
        btn.addKeyListener(new ButtonKeyListener(btn, actionName));
        
        add(lbl);
        add(new JPanel(), "width 40%");
        add(btn);
        
        buttonMap.put(actionName, btn);
    }
    
    Map<String, KeyStroke> getShortcutMap() {
        return shortcutMap;
    }
    
    // The shortcuts stored in the Settings class are only update when the user hits the "OK" button. If they click on "Cancel" the values stay the same, so we can do the following to reset the buttons.
    public void setupValues() {
        for (var e : buttonMap.entrySet()) {
            var keysAsString = keyStrokeToString(Settings.getKeyboardShortcutFor(e.getKey()));
            
            e.getValue().setText(keysAsString);
        }
    }
    
    private String keyStrokeToString(KeyStroke keyStroke) {
        String keysAsString = KeyEvent.getKeyText(keyStroke.getKeyCode());
    
        if (keyStroke.getModifiers() != 0) {
            keysAsString = InputEvent.getModifiersExText(keyStroke.getModifiers()) + " + " + KeyEvent.getKeyText(keyStroke.getKeyCode());
        }
    
        return keysAsString.toUpperCase();
    }
    
    private class ButtonKeyListener implements KeyListener {
        private final JToggleButton button;
        private final String actionName;
        
        private KeyStroke keyStroke;
        
        public ButtonKeyListener(JToggleButton btn, String action) {
            this.button = btn;
            this.actionName = action;
        }
        
        @Override
        public void keyTyped(KeyEvent e) {
        
        }
    
        // Sometimes only one key gets registered, even though both were pressed. This makes this way makes it work a bit better.
        @Override
        public void keyPressed(KeyEvent e) {
            keyStroke = KeyStroke.getKeyStroke(e.getKeyCode(), e.getModifiersEx());
        }
        
        @Override
        public void keyReleased(KeyEvent e) {
            if (button.isSelected()) {
                var keysAsString = keyStrokeToString(keyStroke);
                
                if (e.getKeyCode() != KeyEvent.VK_ESCAPE) {
                    button.setText(keysAsString);
                    lastPressedButtonText = keysAsString;
                
                    shortcutMap.put(actionName, KeyStroke.getKeyStroke(keyStroke.getKeyCode(), keyStroke.getModifiers()));
                } else {
                    button.setText(lastPressedButtonText);
                }
    
                button.setSelected(false);
            }
        }
    }

    @Override
    public void saveSettings() {
        for (var entry : this.getShortcutMap().entrySet()) {
            Settings.setKeyboardShortcutFor(entry.getKey(), entry.getValue());
    
            Logger.info("Settings shortcut for action {} to keys {}", entry.getKey(), entry.getValue());
        }
        
        eds.installKeyboardShortcuts();
        eds.updateAutosaveManager();
    }
}
