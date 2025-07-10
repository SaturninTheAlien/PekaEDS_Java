package pk2.util;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.tinylog.Logger;

import pk2.filesystem.FHSHelper;
import pk2.filesystem.PK2FileSystem;
import pk2.settings.LevelTestingSettings;
import pk2.settings.Settings;

public class LevelTestingUtil {
    private static Process process = null;


    private static boolean isWindows(){
        String osname = System.getProperty("os.name").toLowerCase();
        return osname.contains("win");
    }


    private static String getDefaultExecutable(){

        File assetsPath = PK2FileSystem.getAssetsPath();
        if(isWindows()){

            return Paths.get(assetsPath.getAbsolutePath(), "pk2.exe").toString();
        }
        else{
            if(assetsPath.getAbsolutePath().startsWith("/usr/local")){
                return "/usr/local/games/pekka-kana-2";
            }
            else if(assetsPath.getAbsolutePath().startsWith("/usr/share")){
                return "/usr/games/pekka-kana-2";
            }
            else{

                File r = assetsPath.getName().equals("res") ? assetsPath.getParentFile() : assetsPath;
                
                return Paths.get(r.getAbsolutePath(), "bin/pekka-kana-2").toString();
            }
        }

    }

    public static void playLevel(File levelFile){
        if(process!=null && process.isAlive()){
            return;
        }
        try {
            LevelTestingSettings lts = Settings.levelTestingSettings;

            String exec;

            if(lts.customExecutable){
                exec = lts.executable;
            }
            else{
                exec = getDefaultExecutable();
            }

            ArrayList<String> commands = new ArrayList<>();
            commands.add(exec);

            if(lts.devMode){
                commands.add("--dev");
            }

            commands.add("--test");
            commands.add(levelFile.getAbsolutePath());

            commands.add("--assets-path");
            commands.add(PK2FileSystem.getAssetsPath().getAbsolutePath());



            if(lts.customDataDirectory){
                commands.add("--data-path");
                commands.add(lts.dataDirectory);
            }
            else if(exec.startsWith("/usr")){
                commands.add("--data-path");
                commands.add(FHSHelper.getPrefPathGame().getAbsolutePath());
            }


            for(String s: commands){
                System.out.println(s);
            }

            ProcessBuilder builder = new ProcessBuilder();
            builder.directory(PK2FileSystem.getAssetsPath());
            builder.command(commands);
            process = builder.start();

        } catch (Exception e) {
            Logger.error(e);
        }

    }
    
}
