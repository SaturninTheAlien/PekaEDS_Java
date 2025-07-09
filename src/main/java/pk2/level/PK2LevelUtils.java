package pk2.level;

import pk2.filesystem.PK2FileSystem;
import pk2.settings.Settings;
import pk2.sprite.SpritePrototype;
import pk2.sprite.io.SpriteIO;
import pk2.sprite.io.SpriteMissing;
import pk2.util.GFXUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.tinylog.Logger;

public class PK2LevelUtils {

    public static PK2Level createDefaultLevel() {
        PK2Level level = new PK2Level();
        PK2LevelSector sector = new PK2LevelSector(PK2LevelSector.CLASSIC_WIDTH, PK2LevelSector.CLASSIC_HEIGHT);

        level.spriteFiles.add("rooster.spr");

        sector.name = "main";

        level.name = Settings.getDefaultMapName();
        level.author = Settings.getDefaultAuthor();

        sector.tilesetName = Settings.getDefaultTileset();
        sector.backgroundName = Settings.getDefaultBackground();
        sector.musicName = Settings.getDefaultMusic();

        level.sectors.add(sector);

        return level;
    }

    /**
     * To prevent loading a tileset multiple times
     */
    static BufferedImage findTileset(String name, PK2Level level) {

        name = name.toLowerCase();
        for (PK2LevelSector sector : level.sectors) {
            if (sector.tilesetImage != null && name.equals(sector.tilesetName.toLowerCase())) {
                return sector.tilesetImage;
            } else if (sector.tilesetBgImage != null && name.equals(sector.tilesetBgName.toLowerCase())) {
                return sector.tilesetBgImage;
            }
        }

        return null;
    }

    public static void loadTileset(PK2LevelSector sector) {
        try {
            File tilesetFile = PK2FileSystem.findAsset(sector.tilesetName, PK2FileSystem.TILESET_DIR);

            BufferedImage tileset = ImageIO.read(tilesetFile);
            sector.tilesetImage = GFXUtils.setPaletteToBackgrounds(tileset, sector.getBackgroundImage());
            sector.tilesetImage = GFXUtils.makeTransparent(sector.tilesetImage);
        } catch (IOException e) {
            Logger.error(e);
            JOptionPane.showMessageDialog(null, "Unable to load: \"" + sector.tilesetName + "\"", "Unable to find tileset", JOptionPane.ERROR_MESSAGE);

            //fallback to default
            try {
                File tilesetFile = PK2FileSystem.findAsset(Settings.getDefaultTileset(), PK2FileSystem.TILESET_DIR);
                sector.tilesetImage = GFXUtils.makeTransparent(ImageIO.read(tilesetFile));
            } catch (IOException e2) {
                Logger.error(e2);
            }
        }

    }

    public static void loadTilesetBG(PK2LevelSector sector) {
        if (sector.tilesetBgName == null || sector.tilesetBgName.equals("")) return;

        try {
            File tilesetBgFile = PK2FileSystem.findAsset(sector.tilesetBgName, PK2FileSystem.TILESET_DIR);

            BufferedImage tileset = ImageIO.read(tilesetBgFile);
            sector.tilesetBgImage = GFXUtils.setPaletteToBackgrounds(tileset, sector.getBackgroundImage());
            sector.tilesetBgImage = GFXUtils.makeTransparent(sector.tilesetBgImage);
        } catch (IOException e) {
            Logger.error(e);
            sector.tilesetBgImage = null;
        }
    }

    public static void loadBackground(PK2LevelSector sector) {
        try {
            File backgroundFile = PK2FileSystem.findAsset(sector.backgroundName, PK2FileSystem.SCENERY_DIR);
            sector.setBackgroundImage(ImageIO.read(backgroundFile));
        } catch (IOException e) {
            Logger.error(e);
            JOptionPane.showMessageDialog(null, "Unable to load: \"" + sector.backgroundName + "\"", "Unable to find background", JOptionPane.ERROR_MESSAGE);

            //fallback to default
            try {
                File backgroundFile = PK2FileSystem.findAsset(Settings.getDefaultBackground(), PK2FileSystem.TILESET_DIR);
                sector.tilesetImage = ImageIO.read(backgroundFile);
            } catch (IOException e2) {
                Logger.error(e2);
            }
        }
    }

    public static void loadLevelAssets(PK2Level map) {

        PK2LevelSector.clearBaseSpriteSheets();
        map.sprites.clear();

        for (PK2LevelSector sector : map.sectors) {
            loadBackground(sector);
            loadTileset(sector);
            loadTilesetBG(sector);
        }

        int id = 0;

        for (String spriteName : map.spriteFiles) {
            try {
                File spriteFile = PK2FileSystem.findSprite(spriteName);
                SpritePrototype sprite = SpriteIO.getSpriteReader(spriteFile).readSpriteFile(spriteFile);

                map.sprites.add(sprite);
                map.loadSpriteImage(sprite);

                sprite.setPlacedAmount(map.countSprites(id));
            } catch (Exception e) {
                Logger.warn("Unable to load sprite file: \"" + spriteName + "\"");

                SpritePrototype sprite = new SpriteMissing(spriteName);
                sprite.setPlacedAmount(map.countSprites(id));

                map.sprites.add(sprite);
                map.setSpriteImageMissing(sprite);
                //map.loadSpriteImage(sprite);
            }

            id += 1;
        }
    }

    public static PK2LevelSector createDefaultSector() {
        PK2LevelSector sector = new PK2LevelSector(PK2LevelSector.CLASSIC_WIDTH, PK2LevelSector.CLASSIC_HEIGHT);
        sector.name = "Empty";
        sector.tilesetName = Settings.getDefaultTileset();
        sector.backgroundName = Settings.getDefaultBackground();
        sector.musicName = Settings.getDefaultMusic();

        return sector;
    }
}
