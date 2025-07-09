package pekase3;

import com.formdev.flatlaf.FlatDarkLaf;
import org.tinylog.Logger;

import javax.swing.*;
import java.util.Locale;

public class PekaSE3 {
    public static final String VERSION = "1.3";
    
    public static void main(String[] args) {
        Logger.info("PekaSE2 version " + VERSION);

        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            
            Logger.info("FlatDarkLaf installed.");
        } catch (UnsupportedLookAndFeelException e) {
            Logger.info(e, "Unable to install FlatDarkLaf, trying to set to system laf.");
            
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                
                Logger.info("System LaF installed.");
            } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException |
                     InstantiationException ex) {
                Logger.info(e, "Unable to set system looking feel.");
            }
        }
        
        Locale.setDefault(Locale.ENGLISH);
        
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            Logger.info(e, "Uncaught exception");
        });
        
        SwingUtilities.invokeLater(() -> {
            new PekaSE3GUI().setup();
        });
    }
}
