package pekaeds.ui.filefilters;

import javax.swing.filechooser.FileFilter;

import pk2.settings.Settings;

import java.io.File;

public class MusicFilter extends FileFilter {
    @Override
    public boolean accept(File f) {
        boolean returnVal = false;
        
        if (!f.isDirectory()) {
            var fileExtension = f.getName().split("\\.");
    
            if (fileExtension.length == 0 && !f.isDirectory()) return false;
            
            returnVal = Settings.getMapProfile().getMusicFormats().contains(fileExtension[1].toLowerCase());
        }
        
        return returnVal || f.isDirectory();
    }
    
    @Override
    public String getDescription() {
        return "Music file " + Settings.getMapProfile().getMusicFormats().toString(); // TODO Fix: this string, show: (*.s3m, *.ogg, *.mp3) etc.
    }
}
