package pekase3.filefilters;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class WAVFilter extends FileFilter {
    @Override
    public boolean accept(File f) {
        return f.isDirectory() || f.getName().toLowerCase().endsWith(".wav");
    }
    
    @Override
    public String getDescription() {
        return "WAV sound files (*.wav)";
    }
}
