package pekaep;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import pk2.util.StringNaturalComparator;

public class Episode {
    private File dir;
    private List<File> levelFiles;

    public Episode(File dir){
        this.dir = dir;        
        this.levelFiles = Arrays.asList(dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".map");
            }
        }));

        this.levelFiles.sort(new Comparator<File>() {
            private StringNaturalComparator snc = new StringNaturalComparator();
            @Override
            public int compare(File a, File b) {
                return snc.compare(a.toString(), b.toString());
            } 
        });
    }

    public File getDir(){
        return this.dir;
    }

    public List<File> getLevels(){
        return this.levelFiles;
    }
}
