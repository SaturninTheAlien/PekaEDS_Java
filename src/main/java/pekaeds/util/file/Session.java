package pekaeds.util.file;

import java.util.*;

import org.tinylog.Logger;

import java.io.*;

public class Session {

    private static final int MAX_RECENT_FILES = 10;
    private ArrayList<File> recentLevels = new ArrayList<>();

    public Session(){
    }


    public void save(File file){
        try (var out = new DataOutputStream(new FileOutputStream(file))) {
            int size = recentLevels.size();
            out.writeInt(size);

            for(int i=0;i<size;++i){
                out.writeUTF(recentLevels.get(i).getPath());
            }
        }
        catch(Exception e){
            Logger.error(e);
        }
    }

    public void load(File file) throws IOException{
        recentLevels.clear();
        try (var in = new DataInputStream(new FileInputStream(file))) {
            int size = in.readInt();
            for(int i=0;i<size;++i){
                String s = in.readUTF();
                recentLevels.add(new File(s));                
            }
        }
    }

    public File getLastFile(){
        return this.recentLevels.get(this.recentLevels.size()-1);
    }

    public void putFile(File newFile){
        if(newFile==null)return;

        int size = this.recentLevels.size();
        for(int i=0;i<size;++i){
            File f = this.recentLevels.get(i);
            if(newFile.equals(f)){
                this.recentLevels.remove(i);
                this.recentLevels.add(newFile);
                return;
            }
        }

        recentLevels.add(newFile);
        while (recentLevels.size() > MAX_RECENT_FILES) {
            recentLevels.remove(0);          
        }   
    }

    public List<File> getRecentFiles(){
        @SuppressWarnings("unchecked")
        ArrayList<File> l = (ArrayList<File>)this.recentLevels.clone();
        Collections.reverse(l);
        return l;
    }

    public void clearRecentFiles(){
        recentLevels.clear();
    }
}
