package pekase3.dialogs;

import javax.swing.*;

import pekase3.PekaSE3GUI;

public final class UnsavedChangesDialog {
    public static int show(PekaSE3GUI gui) {
        return show(gui, false);
    }
    
    /**
     *<pre>
     * Shows a dialog asking the user if they want to save any unsaved changes. Only shows up when unsaved changes are present.
     *
     * If the user clicks "Yes" the main controller (PekaEDSGUI) will save the currently loaded map. Then exists, if close is true.
     * If the user clicks "No" the program exists, if close is true.
     * If the user clicks "Cancel" or closes the dialog nothing happens.
     *</pre>
     *
     * @param gui Instance of PekaEDSGUI Main UI controller class
     * @param close If this is true the program will exit.
     * @return The selected JOptionPane option.
     */
    public static int show(PekaSE3GUI gui, boolean close) {
        int result = JOptionPane.showConfirmDialog(null, "Unsaved changes. Save?");
        
        if (result != JOptionPane.CANCEL_OPTION && result != JOptionPane.CLOSED_OPTION) {
            if (result == JOptionPane.YES_OPTION) {
                gui.handleUnsavedChanges();
            }
            
            if (close) {
                System.exit(0);
            }
        }
        
        return result;
    }
}