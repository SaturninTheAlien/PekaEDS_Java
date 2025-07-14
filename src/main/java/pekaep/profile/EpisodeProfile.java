package pekaep.profile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import pk2.filesystem.PK2FileSystem;
import pk2.util.StringNaturalComparator;

public class EpisodeProfile {
    private List<String> vanillaSpriteList = new ArrayList<>();
    private List<String> vanillaTilesetList = new ArrayList<>();
    private List<String> vanillaBackgroundList = new ArrayList<>();
    private List<String> vanillaMusicList = new ArrayList<>();

    public EpisodeProfile(){

    }

    public List<String> getVanillaSpriteList(){
        return this.vanillaSpriteList;
    }

    public List<String> getVanillaTilesetList(){
        return this.vanillaTilesetList;
    }

    public List<String> getVanillaBackgroundList(){
        return this.vanillaBackgroundList;
    }

    public List<String> getVanillaMusicList(){
        return this.vanillaMusicList;
    }

    private static ArrayList<String> listDir(String name){
        File dir = Paths.get(PK2FileSystem.getAssetsPath().getAbsolutePath(), name).toFile();
        
        ArrayList<String> result = new ArrayList<>(Arrays.stream(dir.listFiles()).map(f -> f.getName()).toList());
        result.sort(new StringNaturalComparator());
        return result;
    }


    public static EpisodeProfile prepareProfile(){
        EpisodeProfile profile = new EpisodeProfile();

        profile.vanillaSpriteList = listDir(PK2FileSystem.SPRITES_DIR);
        profile.vanillaMusicList = listDir(PK2FileSystem.MUSIC_DIR);
        profile.vanillaBackgroundList = listDir(PK2FileSystem.SCENERY_DIR);
        profile.vanillaTilesetList = listDir(PK2FileSystem.TILESET_DIR);

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {

            PrintStream out = new PrintStream(new FileOutputStream("episode.yml"));
            out.println(mapper.writeValueAsString(profile));
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return profile;
    }

}
