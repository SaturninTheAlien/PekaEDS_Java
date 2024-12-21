package pekaeds.ui.actions;

import java.util.ArrayList;

import java.io.File;
import java.nio.file.Paths;

import javax.swing.*;

import org.tinylog.Logger;

import pekaeds.pk2.file.PK2FileSystem;
import pekaeds.settings.LevelTestingSettings;
import pekaeds.settings.Settings;
import pekaeds.ui.main.PekaEDSGUI;
import pekaeds.ui.misc.UnsavedChangesDialog;

import java.awt.event.ActionEvent;



public class PlayLevelAction extends AbstractAction {
    private static final String WINDOWS_EXECUTABLE = "pk2.exe";
    private static final String LINUX_AND_MAC_EXECUTABLE = "./pekka-kana-2";
    private static boolean isWindows(){
        String osname = System.getProperty("os.name").toLowerCase();
        return osname.contains("win");
    }

    private PekaEDSGUI gui;

    private final File executableDirectory;
    private final String executable;

    private Process process;
    
    public PlayLevelAction(PekaEDSGUI ui) {
        this.gui = ui;

        File assetsPath = PK2FileSystem.getAssetsPath();
        if(isWindows()){
            this.executable = WINDOWS_EXECUTABLE;
            this.executableDirectory = assetsPath;
        }
        else{
            if(assetsPath.getName().equals("res")){
                this.executable = "./bin/pekka-kana-2";
                this.executableDirectory = assetsPath.getParentFile();
            }
            else{
                this.executable = LINUX_AND_MAC_EXECUTABLE;
                this.executableDirectory = assetsPath;
            }
        }

    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (gui.unsavedChangesPresent()) {
            int result = UnsavedChangesDialog.show(gui);
            
            if (result != JOptionPane.CANCEL_OPTION && result != JOptionPane.CLOSED_OPTION) {
                playLevel();
            }
        } else {
            playLevel();
        }
    }


    private void playLevel(){
        if(process!=null && process.isAlive()){
            return;
        }
        
        try {
            LevelTestingSettings lts = Settings.levelTestingSettings;

            String exec;
            File dir;

            if(lts.customExecutable){
                exec = lts.executable;
            }
            else{
                exec = this.executable;
            }


            if(lts.customWorkingDirectory){
                dir = new File(lts.workingDirectory);
            }
            else{
                dir = this.executableDirectory;
            }

            /**
             * Windows for some reason requires a full path to .exe
             */
            if(isWindows()){
                exec = Paths.get(dir.getPath(), exec).toString();
            }

            ArrayList<String> commands = new ArrayList<>();
            commands.add(exec);

            if(lts.devMode){
                commands.add("--dev");
            }

            commands.add("--test");
            commands.add(PK2FileSystem.getEpisodeName() + "/" + gui.getCurrentFile().getName());

            ProcessBuilder builder = new ProcessBuilder();
            builder.directory(dir);

            builder.command(commands);
            process = builder.start();

        } catch (Exception e) {
            Logger.error(e);
        }
    }
}
