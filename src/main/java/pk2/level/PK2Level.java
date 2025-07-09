package pk2.level;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pk2.filesystem.PK2FileSystem;
import pk2.sprite.SpritePrototype;
import pk2.sprite.io.SpriteMissing;
import pk2.util.GFXUtils;

import javax.imageio.ImageIO;

public class PK2Level {
    public List<PK2LevelSector> sectors = new ArrayList<>();

    protected List<String> spriteFiles = new ArrayList<>();
    protected ArrayList<SpritePrototype> sprites = new ArrayList<>();

    /**
     * String        - The filename of the background image (i.e. castle.bmp)
     * HashMap<String, BufferedImage> The string is the filename of the sprite image sheet, BufferedImage contains the image with the correct background palette
     */
    private HashMap<String, HashMap<String, BufferedImage>> spriteSheetCache = new HashMap<>();


    public String name;
    public String author;

    public int level_number = 0;                            // level of the episode
    public int time = 0;                            // time (in seconds)
    public int extra = 0;                            // extra config - not used

    public int player_sprite_index = 0;                            // player prototype

    public int icon_x = 0;                                         // icon x pos
    public int icon_y = 0;                                         // icon x pos
    public int icon_id = 0;                                        // icon id

    public String lua_script = "";                        // lua script
    public int game_mode = 0;                                          // game mode

    /**
     * @param sprite
     * @return The index of added sprite
     */
    public int addSprite(SpritePrototype sprite) {
        int size = sprites.size();

        // To prevent adding a sprite multiple times
        for (int i = 0; i < sprites.size(); ++i) {
            if (sprites.get(i).getFilename().equals(sprite.getFilename())) {
                return i;
            }
        }

        sprites.add(sprite);
        spriteFiles.add(sprite.getFilename());

        loadSpriteImage(sprite);

        return size;
    }

    public void loadSpriteImage(SpritePrototype sprite) {
        if (!PK2LevelSector.isSpriteSheetLoaded(sprite.getImageFileIdentifier())) {
            try {
                File spriteImage = PK2FileSystem.findAsset(sprite.getImageFile(), PK2FileSystem.SPRITES_DIR);
                BufferedImage spriteSheet = ImageIO.read(spriteImage);

                GFXUtils.adjustSpriteColor(spriteSheet, sprite.getColor());

                PK2LevelSector.registerSpriteSheet(sprite.getImageFileIdentifier(), spriteSheet);
                
                loadSpriteImagesForAllSectors(sprite);

            } catch (IOException e) {
                setSpriteImageMissing(sprite);
            }
        }
    }

    public void loadSpriteImagesForAllSectors(SpritePrototype sprite) {
        for (PK2LevelSector sector : sectors) {
            sector.addSpriteSheet(sprite);
        }
    }

    public void removeSprite(SpritePrototype sprite) {
        int index = sprites.indexOf(sprite);

        if (index != -1) {
            if (index < player_sprite_index) {
                --player_sprite_index;
            } else if (index == player_sprite_index) {
                sprites.get(index).setPlayerSprite(false);
            }

            sprites.remove(index);
            spriteFiles.remove(index);

            for (PK2LevelSector sector : sectors) {
                sector.removeSprite(index);
            }

            if (index == player_sprite_index) {
                player_sprite_index = 0;

                if (!sprites.isEmpty()) {
                    sprites.get(0).setPlayerSprite(true);
                }
            }
        }
    }

    public void addSector(final PK2LevelSector newSector) {
        if (newSector != null) {
            PK2LevelUtils.loadBackground(newSector);
            PK2LevelUtils.loadTileset(newSector);
            PK2LevelUtils.loadTilesetBG(newSector);

            for (SpritePrototype sprite : sprites) {
                loadSpriteImage(sprite, newSector);

                newSector.addSpriteSheet(sprite);
            }

            sectors.add(newSector);
        }
    }

    public int countSprites(int id) {
        int result = 0;

        for (PK2LevelSector sector : sectors) {
            result += sector.countTiles(id);
        }

        return result;
    }

    private void loadSpriteImage(SpritePrototype sprite, final PK2LevelSector sector) {
        if (spriteSheetCache.containsKey(sector.backgroundName)) {
            if (spriteSheetCache.get(sector.backgroundName).containsKey(sprite.getImageFileIdentifier())) {
                sprite.setImage(spriteSheetCache.get(sector.backgroundName).get(sprite.getImageFileIdentifier()));

                return;
            }
        }

        try {
            File spriteImage = PK2FileSystem.findAsset(sprite.getImageFile(), PK2FileSystem.SPRITES_DIR);

            try {
                BufferedImage spriteSheetImage = ImageIO.read(spriteImage);

                GFXUtils.adjustSpriteColor(spriteSheetImage, sprite.getColor());
                spriteSheetImage = GFXUtils.setPaletteToBackgrounds(spriteSheetImage, sector.getBackgroundImage());
                spriteSheetImage = GFXUtils.makeTransparent(spriteSheetImage);

                sprite.setImage(spriteSheetImage);

                if (!spriteSheetCache.containsKey(sector.backgroundName)) {
                    spriteSheetCache.put(sector.backgroundName, new HashMap<String, BufferedImage>());
                }

                spriteSheetCache.get(sector.backgroundName).put(sprite.getImageFileIdentifier(), spriteSheetImage);
            } catch (IOException e) {
                setSpriteImageMissing(sprite);
            }
        } catch (FileNotFoundException e) {
            setSpriteImageMissing(sprite);
        }
    }

    public void setSpriteImageMissing(SpritePrototype sprite) {
        sprite.setImage(SpriteMissing.getMissingTextureImage());

        sprite.setSpecialImageFileIdentifier("!missing_texture");
        sprite.setFrameX(0);
        sprite.setFrameY(0);
        sprite.setFrameWidth(32);
        sprite.setFrameHeight(32);
    }

    public void removeSector(int index) {
        sectors.remove(index);
    }

    public ArrayList<SpritePrototype> getSpriteList() {
        return sprites;
    }

    public SpritePrototype getSprite(int index) {
        return sprites.get(index);
    }

    public int getLastSpriteIndex() {
        return sprites.size() - 1;
    }
}
