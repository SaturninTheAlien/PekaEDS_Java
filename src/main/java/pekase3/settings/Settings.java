package pekase3.settings;

import java.io.File;

import pekase3.profile.SpriteProfile;

@Deprecated
public final class Settings {
    public static final String FILE = "settings.dat";
    
    private String gamePath;
    private String spritesPath;
    
    private SpriteProfile spriteProfile;
    private String spriteProfileFile = "greta.yml";
    
    public String getGamePath() {
        return gamePath;
    }
    
    public void setGamePath(String path) {
        this.gamePath = path;
        
        spritesPath = path + File.separatorChar + "sprites";
    }
    
    public void setSpriteProfileFile(String file) {
        this.spriteProfileFile = file;
    }
    
    public String getSpriteProfileFile() {
        return spriteProfileFile;
    }
    
    public String getSpritesPath() {
        return spritesPath;
    }
    
    public void setSpriteProfile(SpriteProfile sp) {
        this.spriteProfile = sp;
    }
    
    public SpriteProfile getSpriteProfile() {
        return spriteProfile;
    }
}
