package pekaeds.util.file;

import org.tinylog.Logger;

import pekaeds.ui.main.PekaEDSGUI;
import pk2.settings.Settings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for creating and keeping track of autosave files.
 *
 * Each delay the originalFile gets copied into an {originalFile}_autosave_{currentFile}.map file and then saved.
 *
 * fileCount is the number of {originalFile}_autosave_{currentFile}.map files the user wants.
 * fileRotation contains a list of these autosave files, so their path (string) doesn't have to be generated each time actionPerformed() is called.
 * filename is the name of the current file, without the .map at the end. (level01 instead of level01.map). This is used in the generation of the autosave file names.
 *
 * currentFile is the current autosave in rotation. It gets updated after each save and reset to 0 when it has reached the fileCount.
 *
 * delay is the time between each save in milliseconds.
 *
 * fileCount is also stored in the settings file and can be changed/accessed via Settings.get/setFileCount()
 * delay is also stored in the settings file and can be accessed via Settings.get/setAutosaveInterval()
 */
public final class AutoSaveManager {
    private File autosavesFolder = new File("autosaves");

    private final Timer timer;
    private final PekaEDSGUI edsGUI; // I really don't like to make this class depend on PekaEDSGUI, but I don't see another way for now. This class needs to access unsavedChangesPresent().
    
    private File originalFile;

    private int fileCount;
    private int currentFile = 0;
    
    private int delay;
    
    private final List<File> fileRotation = new ArrayList<>();
    
    private String filename;

    // TODO Remove file from constructor?
    public AutoSaveManager(PekaEDSGUI eds, File file) {
        this.edsGUI = eds;
        this.originalFile = file;
        
        delay = Settings.getAutosaveInterval();
        timer = new Timer(delay, new SaveAction());
        timer.setInitialDelay(delay);

        fileCount = Settings.getAutosaveFileCount();
        generateFileRotation();
    }
    
    public void start() {
        timer.start();
    }
    
    public void setInterval(int newDelay) {
        this.delay = newDelay;
    
        timer.setDelay(delay);
        timer.setInitialDelay(delay);
    
        timer.restart(); // Restart it with the new interval.
    }
    
    public void setFileCount(int autosaveFileCount) {
        this.fileCount = autosaveFileCount;
        
        generateFileRotation();
    }
    
    public void setFile(File file) {
        this.originalFile = file;
        
        if (file != null) {
            filename = file.getName().substring(0, file.getName().length() - 4); // Only save the name of the file, get rid of the .map at the end. See SaveAction.actionPerformed() for more information.

            autosavesFolder = new File(file.getParentFile().getAbsolutePath() + File.separator + "autosaves");
            if (!autosavesFolder.exists()) {
                autosavesFolder.mkdir();
            }

            generateFileRotation();
        }
    }

    private void generateFileRotation() {
        fileRotation.clear();

        for (int i = 0; i < fileCount; i++) {
            var file = new File(generateAutosaveFilePath(i + 1)); // The autosave files should start at 1. file_autosave_1.map instead of file_autosave_0.map. I think it looks better.
            
            fileRotation.add(file);
        }
    }
    
    /**
        This method constructs the corresponding name/path for the autosave_{filenumber}.map file.
     
        Example, file: "level01.map", file count = 3:
        {path of current file}\autosaves\level01.map
        {path of current file}\autosaves\level01_autosave_1.map
        {path of current file}\autosaves\level01_autosave_2.map
        {path of current file}\autosaves\level01_autosave_3.map
    */
    private String generateAutosaveFilePath(int fileNumber) {
        var sb = new StringBuilder();
        
        if (originalFile != null) {
            sb.append(autosavesFolder.getPath());
            sb.append(File.separatorChar);
            sb.append(filename);
            sb.append("_autosave_");
            sb.append(fileNumber);
            sb.append(".map");
        }
        
        return sb.toString();
    }
    
    private class SaveAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (edsGUI.unsavedChangesPresent()) {
                if (originalFile != null && fileCount > 0 && delay > 0) { // filecount > 0 && delay > 0 is to make sure auto saving isn't enabled. Should probably add an extra option in the settings dialog.
                    // Copy the original file into the current auto save file
                    try {
                        Files.copy(originalFile.toPath(), fileRotation.get(currentFile).toPath(), StandardCopyOption.REPLACE_EXISTING);

                        Logger.info("Created autosave: {}", fileRotation.get(currentFile).getAbsolutePath());
                    } catch (IOException ex) {
                        Logger.warn("Unable to save autosave file: " + fileRotation.get(currentFile).getName());
                    }
        
                    edsGUI.saveLevel(originalFile);
        
                    currentFile++;
                    if (currentFile >= fileCount) {
                        currentFile = 0;
                    }
                }
            }
        }
    }
}
