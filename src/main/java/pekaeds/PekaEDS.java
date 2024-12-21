package pekaeds;

import com.formdev.flatlaf.FlatDarkLaf;

import pekaeds.pk2.file.PK2FileSystem;
import pekaeds.settings.Settings;
import pekaeds.ui.main.PekaEDSGUI;
import pekaeds.ui.misc.InitialSetupDialog;

import java.util.Locale;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;


import javax.swing.*;

public class PekaEDS {

    private static InitialSetupDialog initialSetupDialog;

    public static void main(String[] args) {
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
    
        //System.setProperty( "flatlaf.menuBarEmbedded", "false" );

        System.setProperty("sun.java2d.noddraw", "true");
        Locale.setDefault(Locale.ENGLISH);

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            Logger.info(e, "TODO: Log Uncaught exception");
        });

        launch();
    }

    private static boolean loadSettings() {
        boolean success = false;
        File settingsFile = new File("settings.dat");

        if(settingsFile.exists()){
            try{
                Settings.load(settingsFile);

                File file = new File(Settings.getBasePath());
                /**
                 * TODO ??? Should an exception be thrown here?
                 * If there's something wrong (e.g nota  PK2 directory), it throws an exception
                 */
                PK2FileSystem.setAssetsPath(file);

                success = true;
            }
            catch(IOException e){
                Logger.error(e);
            }
        }

        return success;
    }

    public static void launch() {
        if (!loadSettings()) {
            initialSetupDialog = new InitialSetupDialog(null);

            if (initialSetupDialog.setupCompleted()) {
                // TODO Does it make sense to check if the settings were able to be loaded again, what then? Prompt the user again? Or just set default settings? Probably the latter
                loadSettings();
                SwingUtilities.invokeLater(PekaEDSGUI::new);
            }

            initialSetupDialog.dispose();
        } else {
            SwingUtilities.invokeLater(PekaEDSGUI::new);
        }
    }
}