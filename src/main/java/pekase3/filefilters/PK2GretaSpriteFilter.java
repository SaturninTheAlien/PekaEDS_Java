package pekase3.filefilters;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class PK2GretaSpriteFilter extends FileFilter {
    @Override
    public boolean accept(File f) {
        return f.isDirectory() || f.getName().toLowerCase().endsWith(".spr2");
    }
    
    @Override
    public String getDescription() {
        return "Greta Engine Sprite file (*.spr2)";
    }
}
