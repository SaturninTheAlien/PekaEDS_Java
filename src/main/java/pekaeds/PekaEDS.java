package pekaeds;



import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.swing.SwingUtilities;

import org.tinylog.Logger;

import pekaeds.ui.main.PekaEDSGUI;
import pekaeds.ui.misc.AppEnum;
import pekaeds.ui.misc.AppSelectionDialog;
import pekaeds.ui.misc.InitialSetupDialog;
import pekaep.PekaEPGUI;
import pekase3.PekaSE3GUI;
import pk2.filesystem.FHSHelper;
import pk2.filesystem.PK2FileSystem;
import pk2.settings.Settings;
import pk2.util.LookAndFeelHelper;

public class PekaEDS {
    private static AppEnum selectedApp = AppEnum.NOT_SELECTED;

    public static void main(String[] args) {
        //parse args
        int state=0;
        for(String arg: args){

            switch (state) {
            case 0:{
                if(arg=="--theme"){
                    state = 1;
                }
                else if(arg=="level-editor"){
                    selectedApp = AppEnum.LEVEL_EDITOR;
                }
                else if(arg=="sprite-editor"){
                    selectedApp = AppEnum.SPRITE_EDITOR;
                }
                else if(arg=="episode-packer"){
                    selectedApp = AppEnum.EPISODE_PACKER; 
                }
            }
            break;    
            case 1:{
                Settings.setLookAndFeel(arg);
                state = 0;
            }    
            break;
            default:
                break;
            }
        }

        launch();
    }

    public static void launch(){

        FHSHelper.preparePaths();
        Locale.setDefault(Locale.ENGLISH);

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            Logger.info(e, "TODO: Log Uncaught exception");
        });

        if (!loadSettings()) {

            if(Settings.getLookAndFeel()==null){
                Settings.setLookAndFeel(LookAndFeelHelper.getDefaultTheme());
            }

            
            LookAndFeelHelper.updateTheme();
            InitialSetupDialog initialSetupDialog = new InitialSetupDialog(null);
            if (initialSetupDialog.setupCompleted()) {
                initialSetupDialog.dispose();
                loadSettings();
                selectApp();
                //SwingUtilities.invokeLater(PekaEDSGUI::new);
            }
        } else {

            LookAndFeelHelper.updateTheme();
            selectApp();
            //SwingUtilities.invokeLater(PekaEDSGUI::new);
        }
    }

    private static void selectApp(){

        if(selectedApp==AppEnum.NOT_SELECTED){
            AppSelectionDialog appSelectionDialog = new AppSelectionDialog(null);
            selectedApp = appSelectionDialog.getSelectedApp();
            appSelectionDialog.dispose();
        }
        switch (selectedApp) {
            case LEVEL_EDITOR:
                SwingUtilities.invokeLater(PekaEDSGUI::new);
                break;

            case SPRITE_EDITOR:
                SwingUtilities.invokeLater(() -> {
                    new PekaSE3GUI().setup();
                });
                break;

            case EPISODE_PACKER:
                SwingUtilities.invokeLater(PekaEPGUI::new);
                break;
            
            default:
                break;
        }
    }

    private static boolean loadSettings() {
        boolean success = false;
        File settingsFile =  FHSHelper.getSettingsFile();

        if(settingsFile.exists()){
            try{
                Settings.load(settingsFile);

                File file = new File(Settings.getBasePath());
                PK2FileSystem.setAssetsPath(file);

                success = true;
            }
            catch(IOException e){
                Logger.error(e);
            }
        }

        return success;
    }
}