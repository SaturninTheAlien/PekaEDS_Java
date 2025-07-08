package pekaeds;



import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.swing.SwingUtilities;

import org.tinylog.Logger;

import pekaeds.pk2.file.PK2FileSystem;
import pekaeds.settings.Settings;
import pekaeds.ui.main.PekaEDSGUI;
import pekaeds.ui.misc.InitialSetupDialog;
import pekaeds.ui.misc.LookAndFeelHelper;
import pekaeds.util.file.FHSUtils;

public class PekaEDS {

    private static InitialSetupDialog initialSetupDialog;

    public static void main(String[] args) {

        //parse args
        int state=0;
        for(String arg: args){

            switch (state) {
            case 0:{
                if(arg=="--theme"){
                    state = 1;
                }
            }
            break;    
            case 1:{
                Settings.setLookAndFeel(arg);
            }    
            break;
            default:
                break;
            }
        }
        // Is it still necessary?
        //System.setProperty("sun.java2d.noddraw", "true");
        FHSUtils.preparePaths();
        Locale.setDefault(Locale.ENGLISH);

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            Logger.info(e, "TODO: Log Uncaught exception");
        });

        launch();
    }

    private static boolean loadSettings() {
        boolean success = false;
        File settingsFile =  FHSUtils.getSettingsFile();

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

            if(Settings.getLookAndFeel()==null){
                Settings.setLookAndFeel(LookAndFeelHelper.getDefaultTheme());
            }

            
            LookAndFeelHelper.updateTheme();
            initialSetupDialog = new InitialSetupDialog(null);
            if (initialSetupDialog.setupCompleted()) {
                loadSettings();
                SwingUtilities.invokeLater(PekaEDSGUI::new);
            }
            initialSetupDialog.dispose();
        } else {

            LookAndFeelHelper.updateTheme();
            SwingUtilities.invokeLater(PekaEDSGUI::new);
        }
    }
}