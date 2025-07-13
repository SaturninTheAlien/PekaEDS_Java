package pekaep;

import java.io.File;

import org.tinylog.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import pk2.filesystem.PK2FileSystem;

public class PK2EpisodeAsset {

    private static EpisodeProfile profile;
    public static void loadEpisodeProfile(){
        try{
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            profile = mapper.readValue(PK2EpisodeAsset.class.getResourceAsStream("/profiles/episode.yml"), EpisodeProfile.class);
        }
        catch(Exception e){
            Logger.warn(e, "Unable to load the sprite profile file.");
            profile = new EpisodeProfile();
        }
    }


    public enum Type{
        LEVEL("Level", null),
        SPRITE("Sprite", PK2FileSystem.SPRITES_DIR),
        SPRITE_TEXTURE("Sprite texture", PK2FileSystem.SPRITES_DIR),
        SPRITE_SOUND("Sprite sound", PK2FileSystem.SPRITES_DIR),

        BACKGROUND("Background", PK2FileSystem.SCENERY_DIR),
        TILESET("Tileset", PK2FileSystem.TILESET_DIR),
        MUSIC("Music", PK2FileSystem.MUSIC_DIR),

        LUA("Lua script", PK2FileSystem.LUA_DIR),
        LIFE("RLE", PK2FileSystem.LIFE_DIR);
        
        private final String name;
        private final String dir;

        private Type(String name, String dir){
            this.name = name;
            this.dir = dir;
        }

        public String getName(){
            return this.name;
        }

        public String getDir(){
            return this.dir;
        }
    }
    
    private final PK2EpisodeAsset parent;
    private final String name;
    private final Type type;

    public boolean obsoleteFormat = false;
    public File file = null;
    public Exception loadingException = null;

    public PK2EpisodeAsset(String name, Type type){
        this(name, type, null);
    }

    public PK2EpisodeAsset(String name, Type type,  PK2EpisodeAsset parent){
        this.type = type;
        this.parent = null;
        this.name = name;
    }

    public PK2EpisodeAsset getParent(){
        return this.parent;
    }

    public String getName(){
        if(this.file!=null){
            return this.file.getName();
        }
        return this.name;
    }

    public Type getType(){
        return this.type;
    }

    public boolean isGood(){
        return this.file != null && this.loadingException == null;
    }


    public boolean equals(PK2EpisodeAsset asset){
        if(asset==null)return false;

        if(this.file!=null && this.file.equals(asset.file)){
            return true;
        }
        
        return this.type == asset.type && this.name.equals(asset.name);
    }

    public boolean isVanillaAsset(){
        if(this.file==null)return false;

        switch (this.type) {
            case MUSIC:
                return profile.getVanillaMusicList().contains(this.getName());

            case TILESET:
                return profile.getVanillaTilesetList().contains(this.getName());
            
            case BACKGROUND:
                return profile.getVanillaBackgroundList().contains(this.getName());

            case SPRITE:
            case SPRITE_TEXTURE:
            case SPRITE_SOUND:
                return profile.getVanillaSpriteList().contains(this.getName());

            default:
                return false;
        }
    }
}
