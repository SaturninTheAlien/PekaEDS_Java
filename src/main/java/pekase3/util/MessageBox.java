package pekase3.util;

import javax.swing.*;

public final class MessageBox {
    private MessageBox() {}
    
    public static void showUnknownSpriteError(String file, String message, String title) {
        JOptionPane.showMessageDialog(null, "File: '" + file + "' is not a 1.3 sprite file.\n" + message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    public static void showUnknownSpriteError(String file) {
        JOptionPane.showMessageDialog(null, "File: '" + file + "' is not a 1.3 sprite file.", "Not a 1.3 sprite file!", JOptionPane.ERROR_MESSAGE);
    }
}
