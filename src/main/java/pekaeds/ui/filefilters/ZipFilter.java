package pekaeds.ui.filefilters;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class ZipFilter extends FileFilter{

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) return true;

        return f.getName().toLowerCase().endsWith(".zip");
    }

    @Override
    public String getDescription() {
        return "ZIP file (*.zip)";
    }
    
}
