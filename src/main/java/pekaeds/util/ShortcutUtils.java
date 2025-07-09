package pekaeds.util;

import javax.swing.*;

import pk2.settings.Settings;

/**
 * Helper class to install custom keyboard shortcuts.
 *
 * To install a new shortcut you have to add it in:
 *
 * The Shortcuts class you need to register the name of the shortcuts action.
 * PekaEDSGUI.installKeyboardShortcuts() to register the shortcut's action.
 * Settings.resetKeyboardShortcuts() so that it can be reset and it's KeyStroke can be registered.
 *
 */
public final class ShortcutUtils {
    private ShortcutUtils() {}
    
    public static void install(JComponent target, String actionName, AbstractAction action) {
        target.getActionMap().put(actionName, action);
        target.getInputMap(JComponent.WHEN_FOCUSED).put(Settings.getKeyboardShortcutFor(actionName), actionName);
    }
}
