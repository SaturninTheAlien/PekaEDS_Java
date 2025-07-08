package pekaeds.util.episodemanager;

import org.tinylog.Logger;

import pekaeds.pk2.file.PK2FileSystem;
import pekaeds.pk2.map.PK2Map;
import pekaeds.pk2.map.PK2LevelIO;
import pekaeds.ui.episodepanel.EpisodeChangeListener;
import pekaeds.ui.mapposition.MapIcon;
import pekaeds.util.GFXUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public final class EpisodeManager {
    private static final EpisodeIO episodeIO = new EpisodeIO();
    
    private Episode episode = null;
    private final List<MapIcon> mapIcons = new ArrayList<>();
    
    private File episodeFile; // This episode's (episodename).episode file in the episodes folder
    
    private EpisodeChangeListener changeListener;
    
    public EpisodeManager() {
        var episodesFolder = PK2FileSystem.getAssetsPath(PK2FileSystem.EPISODES_DIR);
        
        if (!episodesFolder.exists()) {
            episodesFolder.mkdir();
        }
    }
    
    public void newEpisode(File folder) {
        var tmpFiles = new ArrayList<File>();
        for (var f : folder.listFiles()) {
            if (f.getName().endsWith(".map")) {
                tmpFiles.add(f);
            }
        }
        
        episode = new Episode(tmpFiles, folder, folder.getName());
        episodeIO.save(episode);
    
        loadEpisode(new File(PK2FileSystem.getAssetsPath(PK2FileSystem.EPISODES_DIR) + episode.getEpisodeName() + ".episode"));
    }
    
    public void loadEpisode(File episodeFile) {
        try {
            episode = episodeIO.load(episodeFile);
            this.episodeFile = episodeFile;
            
            changeListener.episodeChanged(episode);
            
            var removedFilesList = new ArrayList<File>();
            mapIcons.clear();
            for (var file : episode.getFileList()) {
                if (file.exists()) {
                    addIconToList(file);
                } else {
                    removedFilesList.add(file);
                }
            }
            
            if (!removedFilesList.isEmpty()) {
                var sb = new StringBuilder();
                for (var file : removedFilesList) {
                    sb.append(file.getName()).append("\n");
                    
                    removeFileFromEpisode(file.getName(), false);
                }
    
                JOptionPane.showMessageDialog(null, "The following files could not be found and were removed from the episode: " + sb.toString(), "Removed files", JOptionPane.INFORMATION_MESSAGE);
            }
            
            Logger.info("Loaded episode: {}, files: {}, folder: {}", episode.getEpisodeName(), episode.getFileList().size(), episode.getEpisodeFolder().getAbsolutePath());
        } catch (IOException e) {
            Logger.info(e, "Unable to load episode file.");
        }
    }
    
    public void addFileToEpisode(File file) {
        if (episode != null) {
            episode.getFileList().add(file);
            
            changeListener.episodeChanged(episode);
    
            addIconToList(file);
        }
    }
    
    private void addIconToList(File file) {

        try{
            PK2Map level = PK2LevelIO.loadLevel(file);
            BufferedImage iconSheet = null;

            iconSheet = ImageIO.read( PK2FileSystem.getPK2StuffFile());
    
            iconSheet = GFXUtils.makeTransparent(iconSheet);
            var iconImage = iconSheet.getSubimage(1 + (level.icon_id * 28), 452, 27, 27);
            
            mapIcons.add(new MapIcon(iconImage, new Point(level.icon_x, level.icon_y)));
            

        }
        catch(Exception e){
            Logger.error(e);
        }
    }
    
    /**
     * Removes the file filename from the episode. You only need to provide the name of the file itself, not it's path. This method takes care of that.
     * @param filename Filename of the file to remove
     * @param delete Delete file from episode only or from disk too?
     */
    public void removeFileFromEpisode(String filename, boolean delete) {
        if (episode != null) {
            var file = new File(episode.getEpisodeFolder().getAbsolutePath() + File.separator + filename);
            
            if (episode.getFileList().contains(file)) {
                int index = episode.getFileList().indexOf(file);
                if (index < mapIcons.size()) {
                    mapIcons.remove(index);
                }
            }
            episode.getFileList().remove(file);
            
            if (file.exists() && delete) {
                var result = file.delete();
    
                if (result) {
                    Logger.info("File \"{}\" removed from episode and disk.", file.getAbsolutePath());
                } else {
                    Logger.info("Unable to remove file \"{}\" from disk.", file.getAbsolutePath());
                }
            } else {
                Logger.info("File \"{}\" doesn't exist!", file.getAbsolutePath());
            }
            
            changeListener.episodeChanged(episode);
            
            episodeIO.save(episode);
        }
    }
    
    public void importFileIntoEpisode(File selectedFile) {
        if (episode != null) {
            var target = new File(episode.getEpisodeFolder().getAbsolutePath() + File.separator + selectedFile.getName());
    
            try {
                Files.move(Path.of(selectedFile.toURI()), Path.of(target.toURI()), StandardCopyOption.REPLACE_EXISTING);
                
                addFileToEpisode(target);
                
                episodeIO.save(episode);
            } catch (IOException e) {
                Logger.info(e, "Unable to copy file into episode folder.");
            }
        }
    }
    
    public Episode getEpisode() {
        return episode;
    }
    
    public boolean hasEpisodeLoaded() {
        return episode != null;
    }

    public List<MapIcon> getMapIcons() {
        return mapIcons;
    }
    
    public void setChangeListener(EpisodeChangeListener listener) {
        this.changeListener = listener;
    }
    
    public File getEpisodeFile() {
        return episodeFile;
    }
}
