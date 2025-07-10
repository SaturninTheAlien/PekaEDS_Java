package pekase3.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;


import pk2.filesystem.PK2FileSystem;
import pk2.sprite.PK2Sprite;
import pk2.sprite.io.SpriteIO;
import pk2.util.LevelTestingUtil;

public class QuickSpriteTest {

    private static final String TEST_SPRITE_NAME = "test.spr2";

    private static File arenaDir = null;
    private static File arenaLevel = null;
    private static File arenaSpritesDir = null;

    private static void prepareArena() throws IOException{
        arenaDir = Files.createTempDirectory("pk2-arena").toFile();

        Path p = Paths.get(arenaDir.getAbsolutePath(), "arena.map");
        Files.copy(QuickSpriteTest.class.getResourceAsStream("/arena.map"), p , StandardCopyOption.REPLACE_EXISTING);

        arenaLevel = p.toFile();

        arenaSpritesDir = Paths.get(arenaDir.getAbsolutePath(), PK2FileSystem.SPRITES_DIR).toFile();
        arenaSpritesDir.mkdirs();
    }

    private ArrayList<File> dependencies = new ArrayList<>();

    private PK2Sprite findSprite(String name) throws IOException{

        if(name==null || name.isBlank()){
            return null;
        }
        
        File res = PK2FileSystem.findSprite(name);
        if(!dependencies.contains(res)){
            dependencies.add(res);
            return SpriteIO.loadSpriteFile(res);
        }
        return null;        
    }

    private File findAsset(String name) throws FileNotFoundException {
        if(name==null || name.isBlank()){
            return null;
        }

        File res = PK2FileSystem.findAsset(name, PK2FileSystem.SPRITES_DIR);
        if(!dependencies.contains(res)){
            dependencies.add(res);
        }
        return res;
    }

    private void findDependencies(PK2Sprite sprite) throws IOException{

        if(sprite==null)return;

        findDependencies(findSprite(sprite.getAttack1SpriteFile()));
        findDependencies(findSprite(sprite.getAttack2SpriteFile()));
        findDependencies(findSprite(sprite.getTransformationSpriteFile()));
        findDependencies(findSprite(sprite.getBonusSpriteFile()));

        findAsset(sprite.getImageFile());
        
        for(int i=0;i<7;++i){
            findAsset(sprite.getSoundFile(i));
        }
    }

    public void testSprite(PK2Sprite sprite) throws IOException{

        if(arenaDir==null){
            prepareArena();
        }

        this.findDependencies(sprite);
        SpriteIO.saveSprite(sprite, Paths.get(arenaSpritesDir.getAbsolutePath(), TEST_SPRITE_NAME).toFile());

        for(File file: this.dependencies){
            Files.copy( file.toPath(),
                Paths.get(arenaSpritesDir.getAbsolutePath(), file.getName()),
                StandardCopyOption.REPLACE_EXISTING);
        }
        LevelTestingUtil.playLevel(arenaLevel);

    }    
}
