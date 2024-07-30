package pekaeds.pk2.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.nio.file.Paths;


public class PK2FileSystem {

public static final PK2FileSystem INSTANCE = new PK2FileSystem();

/**
 * 
 * @param 
 * debugging
 */
public static void main(String[] args){
    System.out.println("Hello world");

    try{
        INSTANCE.setAssetsPath("/Users/saturninufolud/c++/pk2_greta");
        
        System.out.println(INSTANCE.getAssetsPath().toString());
                
        INSTANCE.SetEpisodeName("Debug Island");


        System.out.println("--------\nTEST: findAsset:");

        File f = INSTANCE.findAsset("tiles01.bmp", TILESET_DIR);
        System.out.println(f);

        /**
         * 
         */

        System.out.println("--------\nTEST: findSprite:");

        f = INSTANCE.findSprite("mune.spr2");
        System.out.println(f);

        f = INSTANCE.findSprite("PiG.sPr");
        System.out.println(f);
        
    }
    catch(Exception e){
        System.out.println(e);
    }
}

public static final String PK2_STUFF_NAME = "pk2stuff.bmp";
public static final String SPRITES_DIR = "sprites";
public static final String EPISODES_DIR = "episodes";

public static final String TILESET_DIR = "gfx"+File.separator+"tiles";
public static final String SCENERY_DIR = "gfx"+File.separator+"scenery";

public static final String MUSIC_DIR = "music";
public static final String LUA_DIR = "lua";

private File mAssetsPath;
private String mEpisodeName;

public void setAssetsPath(String assetsPath) throws FileNotFoundException{
    this.setAssetsPath(new File(assetsPath));
}

public void setAssetsPath(File assetsPath) throws FileNotFoundException{   
    if(!assetsPath.exists() || !assetsPath.isDirectory()){
        throw new FileNotFoundException("Not a directory: "+assetsPath.toString());
    }

    File pk2stuff = Paths.get(assetsPath.getPath(), "gfx", PK2_STUFF_NAME).toFile();
    if(pk2stuff.exists() && !pk2stuff.isDirectory()){

        this.mAssetsPath = assetsPath;
    }
    else{
        pk2stuff = Paths.get(assetsPath.getPath(),"res", "gfx", PK2_STUFF_NAME).toFile();

        if(pk2stuff.exists() && !pk2stuff.isDirectory()){
            this.mAssetsPath = Paths.get(assetsPath.getPath(), "res").toFile();
        }
        else{
            throw new FileNotFoundException("Not a PK2 directory!");
        }
    }
}

public File getAssetsPath(){
    return this.mAssetsPath;
}

public File getAssetsPath(String subfolder){
    return Paths.get(this.mAssetsPath.getPath(), subfolder).toFile();
}

public File getPK2StuffFile(){
    return Paths.get(this.mAssetsPath.getPath(), "gfx", PK2_STUFF_NAME).toFile();
}

public void SetEpisodeName(String name){
    this.mEpisodeName = name;
}

public String GetEpisodeName(){
    return this.mEpisodeName;
}

public boolean isEpisodeSet(){
    return this.mEpisodeName!=null && this.mEpisodeName!="";
}


private File findFile(File dir, String lowercase){

    if(!dir.exists())return null;
           
    FilenameFilter filter = (d, name) -> lowercase.equals(name.toLowerCase());
    File[] res = dir.listFiles(filter);

    return (res ==null || res.length==0) ? null : res[0];
}

public File findAsset(String assetName, String defaultDir){

    File f = new File(assetName);
    /**
     * full path
     */
    if(f.exists() && !f.isDirectory())return f;

    String lowercase = f.getName().toLowerCase();

    if(this.isEpisodeSet()){
        f = findFile(
            Paths.get(mAssetsPath.getPath(), EPISODES_DIR, this.mEpisodeName).toFile(),lowercase);
        
        if(f!=null)return f;

        f = findFile(
            Paths.get(mAssetsPath.getPath(), EPISODES_DIR,this.mEpisodeName, defaultDir).toFile(),lowercase);

        if(f!=null)return f;
    }

    f = findFile(Paths.get(mAssetsPath.getPath(), defaultDir).toFile(),lowercase);

    return f;
}

public File findSprite(String spriteName){
    if(spriteName.toLowerCase().endsWith(".spr")){

        //.spr2 first
        File f = findAsset(spriteName + "2", SPRITES_DIR);
        if(f!=null)return f;

        //.spr
        return findAsset(spriteName, SPRITES_DIR);
    }
    else{
        return findAsset(spriteName, SPRITES_DIR);
    }
}


}