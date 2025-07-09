package pekase3.filefilters;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class ImageFilter extends FileFilter {
    @Override
    public boolean accept(File f) {
        return f.isDirectory() || f.getName().toLowerCase().endsWith(".bmp") || f.getName().toLowerCase().endsWith(".png");
    }
    
    @Override
    public String getDescription() {
        return "256 color indexed image file (*.bmp, *.png)";
    }
}
