package pekaep.episode;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import pk2.filesystem.PK2FileSystem;
import pk2.level.PK2Level;
import pk2.level.PK2LevelIO;
import pk2.level.PK2LevelSector;
import pk2.sprite.PK2Sprite;
import pk2.sprite.io.SpriteIO;
import pk2.util.StringNaturalComparator;

public class PK2Episode {
    private File dir;
    private String name;

    private List<File> levelFiles;
    private List<PK2EpisodeAsset> assetList = new ArrayList<>();

    public PK2Episode(){
        this.name = "Untitled episode";
    }

    public PK2Episode(File dir){
        this.findLevels(dir);
    }


    public void findLevels(File dir){
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

        this.name = dir.getName();
    }

    public String getName(){
        return this.name;
    }

    public File getDir(){
        return this.dir;
    }

    public List<File> getLevels(){
        return this.levelFiles;
    }

    public void findAssets(){
        this.assetList.clear();

        PK2FileSystem.setEpisodeDir(dir);

        for(File levelFile: this.levelFiles){
            this.findLevel(levelFile);
        }
    }

    public void sortAssets(){

        this.assetList.sort(new Comparator<PK2EpisodeAsset>() {

            private StringNaturalComparator snc = new StringNaturalComparator();
            @Override
            public int compare(PK2EpisodeAsset a, PK2EpisodeAsset b) {
                return snc.compare(a.getName(), b.getName());
            }
            
        });
    }

    private void findLevel(File file){
        PK2EpisodeAsset levelAsset = new PK2EpisodeAsset(file.getName(),PK2EpisodeAsset.Type.LEVEL);       
        try{
            PK2Level level = PK2LevelIO.loadLevel(file, false);

            if(level.lua_script!=null && !level.lua_script.isBlank()){
                findAsset(level.lua_script, PK2EpisodeAsset.Type.LUA, levelAsset);
            }

            for(PK2LevelSector sector: level.sectors){

                findAsset(sector.musicName, PK2EpisodeAsset.Type.MUSIC, levelAsset);
                findAsset(sector.backgroundName, PK2EpisodeAsset.Type.BACKGROUND, levelAsset);
                findAsset(sector.tilesetName, PK2EpisodeAsset.Type.TILESET, levelAsset);

                if(sector.tilesetBgName!=null && !sector.tilesetBgName.isBlank()){
                    findAsset(sector.tilesetBgName, PK2EpisodeAsset.Type.TILESET, levelAsset);
                }
            }

            for(String spriteName: level.getSpriteNameList()){
                this.findSprite(spriteName, levelAsset);
            }
            levelAsset.file = file;
        }
        catch(Exception e){
            levelAsset.loadingException = e;
        }
        this.assetList.add(levelAsset);
    }

    private void findSprite(String name, PK2EpisodeAsset parent){
        if(name==null || name.isBlank())return;

        PK2EpisodeAsset asset = new PK2EpisodeAsset(name, PK2EpisodeAsset.Type.SPRITE, parent);
        
        for(PK2EpisodeAsset a: this.assetList){
            if(a.equals(asset))return;
        }

        this.assetList.add(asset);

        try{
            File file = PK2FileSystem.findSprite(name);
            asset.file = file;
            

            PK2Sprite sprite = SpriteIO.loadSpriteFile(file);

            findSprite(sprite.getAttack1SpriteFile(), asset);
            findSprite(sprite.getAttack2SpriteFile(), asset);
            findSprite(sprite.getTransformationSpriteFile(), asset);
            findSprite(sprite.getBonusSpriteFile(), asset);
            
            findAsset(sprite.getImageFile(), PK2EpisodeAsset.Type.SPRITE_TEXTURE, asset);

            for(int i=0; i<7; ++i){
                String soundName = sprite.getSoundFile(i);
                if(soundName!=null && !soundName.isBlank()){
                    findAsset(soundName, PK2EpisodeAsset.Type.SPRITE_SOUND, asset);
                }
            }
        }
        catch(IOException e){
            asset.loadingException = e;
        }
        
    }

    private void findAsset(String name, PK2EpisodeAsset.Type type, PK2EpisodeAsset parent){
        PK2EpisodeAsset asset = new PK2EpisodeAsset(name, type, parent);
        for(PK2EpisodeAsset a: this.assetList){
            if(a.equals(asset))return;
        }

        this.assetList.add(asset);

        try {
            File file = PK2FileSystem.findAsset(name, type.getDir());
            asset.file = file;
        }
        catch (FileNotFoundException e) {
            asset.loadingException = e;
        }
    }

    public List<PK2EpisodeAsset> getAssetList(){
        return this.assetList;
    }

    public List<PK2EpisodeAsset> getMissingAssetsList(){
        return this.assetList.stream().filter( a -> !a.isGood()).toList();
    }
}
