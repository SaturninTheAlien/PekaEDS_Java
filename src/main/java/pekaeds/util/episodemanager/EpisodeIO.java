package pekaeds.util.episodemanager;

import org.tinylog.Logger;

import pk2.filesystem.PK2FileSystem;

import java.io.*;
import java.util.ArrayList;

final class EpisodeIO {
    EpisodeIO() {
    
    }
    
    /**
     * Loads the episode data from the file in parameter file.
     * @param file
     * @return An instance of the Episode class, or if something goes wrong null.
     */
    Episode load(File file) throws IOException {
        var in = new DataInputStream(new FileInputStream(file));
    
        String name = in.readUTF();
        String folder = in.readUTF();
    
        var fileList = new ArrayList<File>();
        int fileCount = in.readInt();
        for (int i = 0; i < fileCount; i++) {
            fileList.add(new File(in.readUTF()));
        }

        in.close();
        
        return new Episode(fileList, new File(folder), name);
    }
    
    /**
     * Saves the episode's data into a file called (episode name).episode, that is stored in EditorConstants.EPISODES_FOLDER
     * @param episode Instance of the episode class, containing the necessary data.
     */
    void save(Episode episode) {
        var episodeFile = new File(PK2FileSystem.EPISODES_DIR  + episode.getEpisodeName() + ".episode");
        
        try (var out = new DataOutputStream(new FileOutputStream(episodeFile))) {
            out.writeUTF(episode.getEpisodeName());
            out.writeUTF(episode.getEpisodeFolder().getAbsolutePath());
            
            out.writeInt(episode.getFileList().size());
            for (var file : episode.getFileList()) {
                out.writeUTF(file.getAbsolutePath());
            }
            
            Logger.info("Episode file \"{}\" saved.", episodeFile.getAbsolutePath());
        } catch (IOException e) {
            Logger.info(e, "Unable to write episode file.");
        }
    }
}
