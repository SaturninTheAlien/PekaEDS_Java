package pk2.filesystem;

import java.io.*;
import java.nio.file.Paths;

public class FHSHelper {

    private static File prefPath = null;
    private static File prefPathGame = null;
    private static File lastSessionFile = null;
    private static File settingsFile = null;

    public static void preparePaths(){

        String os = System.getProperty("os.name").toLowerCase();
        String dotLocalPath = Paths.get("piste-gamez", "peka-eds-java").toString();
        String dotLocalPathGame = Paths.get("piste-gamez", "pekka-kana-2").toString();

        //Windows
        if(os.contains("win")){
            String appdata = System.getenv("APPDATA");
            prefPath = Paths.get(appdata, dotLocalPath).toFile();
            prefPathGame = Paths.get(appdata, dotLocalPathGame).toFile();
        }
        //Linux and MacOS
        else{
            String home = System.getProperty("user.home");
            prefPath = Paths.get(home, ".local/share/", dotLocalPath).toFile();
            prefPathGame = Paths.get(home, ".local/share/", dotLocalPathGame).toFile();
        }

        if(!prefPath.exists()){
            prefPath.mkdirs();
        }

        lastSessionFile = Paths.get(prefPath.getPath(), "last.session").toFile();
        settingsFile = Paths.get(prefPath.getPath(), "settings.dat").toFile();
    }
    /**
     * @return
     * the directory for saving the settings, autosaves and so on
     */

    public static File getPrefPath(){
        return prefPath;
    }

    public static File getPrefPathGame(){
        return prefPathGame;
    }

    public static File getSettingsFile(){
        return settingsFile;
    }

    public static File getLastSessionFile(){
        return lastSessionFile;
    }

    /**
     * 
     * @return
     * username from the OS
     */
    public static String getSystemUserName(){

        String s = System.getProperty("user.name");
        if(s!=null)return s;

        s = System.getenv("USER");
        if(s!=null)return s;

        return System.getenv("USERNAME");
    }

    /**
     * tries to detect PK2 automatically
     */
    public static File findPK2(){
        String os = System.getProperty("os.name").toLowerCase();
        //Windows
        if(os.contains("win")){
            File programFiles = new File("C:\\Program Files\\Piste Gamez\\Pekka Kana 2");
            if(programFiles.exists()){
                return programFiles;
            }            
        }
        else{
            File usr_share = new File("/usr/share/games/pekka-kana-2");
            if(usr_share.exists()){
                return usr_share;
            }

            File local_share = new File("/usr/local/share/games/pekka-kana-2");
            if(local_share.exists()){
                return local_share;
            }

            File opt_pk2 = new File("/opt/piste-gamez/pekka-kana-2");
            if(opt_pk2.exists()){
                return opt_pk2;
            }
        }

        return null;
    }
    
}
