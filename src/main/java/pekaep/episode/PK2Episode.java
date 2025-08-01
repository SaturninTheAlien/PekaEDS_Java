package pekaep.episode;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import pk2.filesystem.PK2FileSystem;
import pk2.level.PK2Level;
import pk2.level.PK2LevelIO;
import pk2.level.PK2LevelSector;
import pk2.settings.Settings;
import pk2.sprite.PK2Sprite;
import pk2.sprite.io.SpriteIO;
import pk2.util.SpriteUtils;
import pk2.util.StringNaturalComparator;

public class PK2Episode {
    private File dir;
    private String name;

    private File tmpDir;
    private List<File> levelFiles;
    private List<PK2EpisodeAsset> assetList = new ArrayList<>();

    public PK2Episode(){
        this.name = "Untitled episode";
    }

    public PK2Episode(File dir){
        this.findLevels(dir);
    }

    public void removeUnknownSpriteAIs(){
        for(PK2EpisodeAsset asset : assetList){
            if(asset.getType()==PK2EpisodeAsset.Type.SPRITE && asset.file!=null
            && asset.isSuspicious()){

                try{
                    if(tmpDir==null){
                        this.tmpDir = Files.createTempDirectory("pk2-packer").toFile();
                    }

                    File newFile = Paths.get(this.tmpDir.getAbsolutePath(), asset.file.getName()).toFile();

                    PK2Sprite sprite = SpriteIO.loadSpriteFile(asset.file);
                    SpriteUtils.removeUnknownAIs(sprite, Settings.getSpriteProfile());

                    SpriteIO.saveSprite(sprite, newFile);
                    asset.file = newFile;
                }
                catch(Exception e){
                    System.out.println(e);
                }               
            }
        }
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

    private boolean isOptionAsset(File file){

        String nameLowercase = file.getName().toLowerCase();

        if(nameLowercase.endsWith(".map") || nameLowercase.endsWith(".pk2lvl")){
            return false;
        }
        else if(nameLowercase.endsWith(".proxy")){
            return true;
        }
        for(String s: PK2EpisodeAsset.profile.getOptionalMapAssests()){          
            if(nameLowercase.equals(s)){
                return true;
            };
        }

        return false;
    }

    public void findAssets(){
        this.assetList.clear();

        PK2FileSystem.setEpisodeDir(dir);

        File[] optionalFiles = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return isOptionAsset(pathname);
            }
        });

        for(File f: optionalFiles){
            PK2EpisodeAsset mapAsset = new PK2EpisodeAsset(name, PK2EpisodeAsset.Type.MAP_ASSET);
            mapAsset.file = f;
            this.assetList.add(mapAsset);
        }


        for(File levelFile: this.levelFiles){
            this.findLevel(levelFile);
        }

        if(this.shouldListLua()){

            File lua1 = Paths.get(this.dir.getAbsolutePath(), PK2FileSystem.LUA_DIR).toFile();
            if(lua1.exists()){
                this.listLuaDir(lua1);
            }

            File lua2 = Paths.get(PK2FileSystem.getAssetsPath().getAbsolutePath(), PK2FileSystem.LUA_DIR).toFile();
            if(lua2.exists()){
                this.listLuaDir(lua2);
            }
        }
    }
    
    private boolean shouldListLua(){
        for(PK2EpisodeAsset asset: this.assetList){
            if(asset.getType()==PK2EpisodeAsset.Type.LUA){
                return true;
            }
        }

        return false;
    }

    private void listLuaDir(File dir){
        File[] luaFiles = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".lua");
            }
        });

        for(File luaFile: luaFiles){
            this.addLuaAsset(luaFile);   
        }
    }

    private void addLuaAsset(File luaFile){
        PK2EpisodeAsset asset = new PK2EpisodeAsset(luaFile.getName(),PK2EpisodeAsset.Type.LUA);
        asset.file = luaFile;
        for(PK2EpisodeAsset asset2: this.assetList){
            if(asset2.equals(asset))return;
        }
        this.assetList.add(asset);
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

                if(sector.pk2stuffName!=null && !sector.pk2stuffName.isBlank()){
                    findAsset(sector.pk2stuffName, PK2EpisodeAsset.Type.GFX, levelAsset);
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

            
            if(sprite.deprecatedFormat){
                /**
                 * Convert obsolete sprites
                 */
                try{
                    if(tmpDir==null){
                        this.tmpDir = Files.createTempDirectory("pk2-packer").toFile();
                    }

                    File newFile = Paths.get(this.tmpDir.getAbsolutePath(), file.getName()).toFile();
                    SpriteIO.saveSprite(sprite, newFile);
                    asset.file = newFile;
                }
                catch(Exception e){
                    System.out.println(e);
                }

            }

            List<Integer> unknownAIs = SpriteUtils.getUnknownAIs(sprite, Settings.getSpriteProfile());
            if(unknownAIs!=null && unknownAIs.size() > 0){
                asset.unknowsAIs = unknownAIs;
            }

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
        catch(Exception e){
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


    public void findPK2Stuff(String name){

        PK2EpisodeAsset asset = new PK2EpisodeAsset(name, PK2EpisodeAsset.Type.GFX, null);
        for(PK2EpisodeAsset a: this.assetList){
            if(a.equals(asset))return;
        }

        try {
            File file = PK2FileSystem.findAsset(name + ".png",  PK2FileSystem.GFX_DIR);
            asset.file = file;
        }
        catch (FileNotFoundException e) {

            try{
                File file = PK2FileSystem.findAsset(name + ".bmp",  PK2FileSystem.GFX_DIR);
                asset.file = file;
            }
            catch (FileNotFoundException e2){

                System.out.println(e2);
                asset.loadingException = e2;
            }
        }
        this.assetList.add(asset);
    }

    public List<PK2EpisodeAsset> getAssetList(){
        return this.assetList;
    }

    public List<PK2EpisodeAsset> getMissingAssetsList(){
        return this.assetList.stream().filter( a -> !a.isGood()).toList();
    }

    public List<PK2EpisodeAsset> getSuspiciousAssetsList(){
        return this.assetList.stream().filter( a -> a.isSuspicious()).toList();
    }
}
