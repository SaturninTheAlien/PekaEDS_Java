package pekaeds.pk2.map;

import java.io.*;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.Rectangle;

import pekaeds.util.TileUtils;
import pekaeds.util.file.PK2FileUtils;

public final class PK2LevelIO {
    private static final int TILES_COMPRESSION_NONE = 0;
    private static final int TILES_OFFSET_NEW = 1;
    private static final int TILES_OFFSET_LEGACY = 2;

    private static void readTilesArray(DataInputStream in, int[] layer, int layerWidth, int compression) throws Exception {

        switch (compression) {
            case TILES_COMPRESSION_NONE: {
                byte[] data = new byte[layer.length];
                in.read(data);

                for (int i = 0; i < data.length; ++i) {
                    layer[i] = (int) data[i] & 0xFF;
                }
            }
            break;

            case TILES_OFFSET_NEW: {
                int startX = Integer.reverseBytes(in.readInt());
                int startY = Integer.reverseBytes(in.readInt());
                int width = Integer.reverseBytes(in.readInt());
                int height = Integer.reverseBytes(in.readInt());

                for (int y = startY; y <= startY + height; y++) {
                    for (int x = startX; x <= startX + width; x++) {
                        layer[layerWidth * y + x] = in.readByte() & 0xFF;
                    }
                }
            }
            break;

            case TILES_OFFSET_LEGACY: {
                int startX = PK2FileUtils.readInt(in);
                int startY = PK2FileUtils.readInt(in);
                int width = PK2FileUtils.readInt(in);
                int height = PK2FileUtils.readInt(in);

                for (int y = startY; y <= startY + height; y++) {
                    for (int x = startX; x <= startX + width; x++) {
                        layer[layerWidth * y + x] = in.readByte() & 0xFF;
                    }
                }
            }
            break;

            default:
                throw new IOException("[PK2 Level IO] Not supported tiles compression!");
        }
    }

    static void writeTilesArray(DataOutputStream out, PK2TileArray array) throws Exception {
        byte[] data = new byte[array.size()];
        for (int i = 0; i < data.length; ++i) {
            data[i] = (byte) array.getByIndex(i);
        }
        out.write(data);
    }

    static void writeLayerWithOffset(DataOutputStream out, int[] layer, int layerWidth, int layerHeight) throws Exception {
        Rectangle r = TileUtils.calculateOffsets(layer, layerWidth, layerHeight);

        int startX = r.x;
        int startY = r.y;
        int width = r.width;
        int height = r.height;

        out.writeInt(Integer.reverseBytes(startX));
        out.writeInt(Integer.reverseBytes(startY));
        out.writeInt(Integer.reverseBytes(width));
        out.writeInt(Integer.reverseBytes(height));

        for (int y = startY; y <= startY + height; y++) {
            for (int x = startX; x <= startX + width; x++) {
                if (x < layerWidth && y < layerHeight) {
                    out.writeByte(layer[layerWidth * y + x]);
                }
            }
        }
    }

    private static PK2Map load15Level(DataInputStream in, boolean iconOnly) throws Exception {
        PK2Map level = new PK2Map();

        int compression = TILES_OFFSET_NEW;

        int sectors_number;
        {
            JSONObject header = PK2FileUtils.readCBOR(in);
            level.name = header.getString("name");
            level.author = header.getString("author");
            level.level_number = header.getInt("number");

            level.icon_x = header.getInt("icon_x");
            level.icon_y = header.getInt("icon_y");
            level.icon_id = header.getInt("icon_id");

            compression = header.getInt("compression");

            if (iconOnly) {
                return level;
            }

            sectors_number = header.getInt("sectors");
            JSONArray spritesArray = header.getJSONArray("sprite_prototypes");
            for (int i = 0; i < spritesArray.length(); ++i) {
                level.spriteFiles.add(spritesArray.getString(i));
            }

            level.player_sprite_index = header.getInt("player_index");
            level.time = header.getInt("time");
            level.lua_script = header.getString("lua_script");

            level.game_mode = header.getInt("game_mode");
        }

        for (int i = 0; i < sectors_number; ++i) {
            JSONObject sector_header = PK2FileUtils.readCBOR(in);

            int width = sector_header.getInt("width");
            int height = sector_header.getInt("height");


            PK2MapSector sector = new PK2MapSector(width, height);

            sector.tilesetName = sector_header.getString("tileset");

            if(sector_header.has("gfx")){
                sector.pk2stuffName = sector_header.getString("gfx");
            }
            else{
                sector.pk2stuffName = "";
            }

            if (sector_header.has("tileset_bg")) {
                sector.tilesetBgName = sector_header.getString("tileset_bg");
            }

            sector.backgroundName = sector_header.getString("background");

            if (sector_header.has("name")) {
                sector.name = sector_header.getString("name");
            } else {
                sector.name = "";
            }

            if(sector_header.has("rain_color")){
                sector.rain_color = sector_header.getInt("rain_color");
            }
            else{
                sector.rain_color = 40;
            }
            
            sector.musicName = sector_header.getString("music");

            sector.background_scrolling = sector_header.getInt("scrolling");
            sector.weather = sector_header.getInt("weather");

            sector.splash_color = sector_header.getInt("splash_color");
            sector.fire_color_1 = sector_header.getInt("fire_color_1");
            sector.fire_color_2 = sector_header.getInt("fire_color_2");

            int sectorWidth = sector.getWidth();
            int sectorHeight = sector.getHeight();

            int[] tmpBackgroundLayer = new int[sectorWidth * sectorHeight];
            Arrays.fill(tmpBackgroundLayer, 255);

            readTilesArray(in, tmpBackgroundLayer, sectorWidth, compression);
            sector.setBackgroundLayer(tmpBackgroundLayer);

            int[] tmpForegroundLayer = new int[sectorWidth * sectorHeight];
            Arrays.fill(tmpForegroundLayer, 255);

            readTilesArray(in, tmpForegroundLayer, sectorWidth, compression);
            sector.setForegroundLayer(tmpForegroundLayer);

            int[] tmpSpritesLayer = new int[sectorWidth * sectorHeight];
            Arrays.fill(tmpSpritesLayer, 255);

            readTilesArray(in, tmpSpritesLayer, sectorWidth, compression);
            sector.setSpriteLayer(tmpSpritesLayer);

            level.sectors.add(sector);
        }

        return level;
    }

    private static PK2Map load13Level(DataInputStream in, boolean iconOnly) throws Exception {
        PK2Map map = new PK2Map();
        PK2MapSector sector = new PK2MapSector(PK2MapSector.CLASSIC_WIDTH, PK2MapSector.CLASSIC_HEIGHT);

        sector.tilesetName = PK2FileUtils.readString(in, 13);
        sector.backgroundName = PK2FileUtils.readString(in, 13);
        sector.musicName = PK2FileUtils.readString(in, 13);

        map.name = PK2FileUtils.readString(in, 40);
        sector.name = "legacy sector";
        map.author = PK2FileUtils.readString(in, 40);
        map.level_number = PK2FileUtils.readInt(in);

        sector.weather = PK2FileUtils.readInt(in);

        // Switch values 1-3, these values are not used in this map format and are hardcoded to be 2000
        in.readNBytes(8);
        in.readNBytes(8);
        in.readNBytes(8);

        map.time = PK2FileUtils.readInt(in);

        map.extra = PK2FileUtils.readInt(in); // "Extra", not used?

        sector.background_scrolling = PK2FileUtils.readInt(in);

        map.player_sprite_index = PK2FileUtils.readInt(in);

        map.icon_x = PK2FileUtils.readInt(in);
        map.icon_y = PK2FileUtils.readInt(in);

        map.icon_id = PK2FileUtils.readInt(in);

        if (iconOnly) {
            return map;
        }

        int spritesAmount = PK2FileUtils.readInt(in);

        for (int i = 0; i < spritesAmount; i++) {
            map.spriteFiles.add(PK2FileUtils.readString(in, 13));
        }

        int sectorWidth = sector.getWidth();
        int sectorHeight = sector.getHeight();

        int[] tmpBackgroundLayer = new int[sectorWidth * sectorHeight];
        Arrays.fill(tmpBackgroundLayer, 255);

        readTilesArray(in, tmpBackgroundLayer, sectorWidth, TILES_OFFSET_LEGACY);
        sector.setBackgroundLayer(tmpBackgroundLayer);

        int[] tmpForegroundLayer = new int[sectorWidth * sectorHeight];
        Arrays.fill(tmpForegroundLayer, 255);

        readTilesArray(in, tmpForegroundLayer, sectorWidth, TILES_OFFSET_LEGACY);
        sector.setForegroundLayer(tmpForegroundLayer);

        int[] tmpSpritesLayer = new int[sectorWidth * sectorHeight];
        Arrays.fill(tmpSpritesLayer, 255);

        readTilesArray(in, tmpSpritesLayer, sectorWidth, TILES_OFFSET_LEGACY);
        sector.setSpriteLayer(tmpSpritesLayer);

        map.sectors.add(sector);

        return map;
    }

    private static final List<Integer> ID1_5 = List.of((int) '1', (int) '.', (int) '5', (int) '\0');
    private static final List<Integer> ID1_3 = List.of((int) '1', (int) '.', (int) '3', (int) '\0');

    private static boolean checkID(List<Integer> id, byte[] version) {
        for (int i = 0; i < id.size(); ++i) {
            int by = version[i] & 0xFF;

            if (by != id.get(i)) return false;
        }

        return true;
    }

    public static PK2Map loadLevel(File file) throws Exception {
        return loadLevel(file, false);
    }

    public static PK2Map loadLevel(File file, boolean iconOnly) throws Exception {
        PK2Map res = null;
        DataInputStream in = new DataInputStream(new FileInputStream(file));

        byte[] version = new byte[5];
        in.read(version);

        if (checkID(ID1_5, version)) {
            res = load15Level(in, iconOnly);
        } else if (checkID(ID1_3, version)) {
            res = load13Level(in, iconOnly);
        }

        in.close();
        return res;
    }

    public static void saveLevel(PK2Map level, File file) throws Exception {
        DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
        out.writeByte('1');
        out.writeByte('.');
        out.writeByte('5');
        out.writeByte('\0');
        out.writeByte('\0');

        int sectors_number = level.sectors.size();
        {
            JSONObject header = new JSONObject();
            header.put("name", level.name);
            header.put("author", level.author);
            header.put("number", level.level_number);
            header.put("icon_id", level.icon_id);
            header.put("icon_x", level.icon_x);
            header.put("icon_y", level.icon_y);

            header.put("sprite_prototypes", level.spriteFiles);


            header.put("player_index", level.player_sprite_index);
            header.put("sectors", sectors_number);


            header.put("time", level.time);
            header.put("lua_script", level.lua_script);
            header.put("game_mode", level.game_mode);
            header.put("compression", TILES_OFFSET_NEW);

            PK2FileUtils.writeCBOR(out, header);
        }

        for (int i = 0; i < sectors_number; ++i) {
            PK2MapSector sector = level.sectors.get(i);
            JSONObject sector_header = new JSONObject();
            sector_header.put("name", sector.name);

            sector_header.put("width", sector.getWidth());
            sector_header.put("height", sector.getHeight());

            sector_header.put("tileset", sector.tilesetName);

            if (sector.tilesetBgName != null &&
                    !sector.tilesetBgName.equals("") &&
                    !sector.tilesetBgName.equals(sector.tilesetName)) {
                sector_header.put("tileset_bg", sector.tilesetBgName);
            }

            sector_header.put("music", sector.musicName);
            sector_header.put("background", sector.backgroundName);

            sector_header.put("scrolling", sector.background_scrolling);
            sector_header.put("weather", sector.weather);

            sector_header.put("splash_color", sector.splash_color);
            sector_header.put("fire_color_1", sector.fire_color_1);
            sector_header.put("fire_color_2", sector.fire_color_2);

            sector_header.put("gfx", sector.pk2stuffName);

            sector_header.put("rain_color", sector.rain_color);

            PK2FileUtils.writeCBOR(out, sector_header);

            writeLayerWithOffset(out, sector.getBackgroundLayer(), sector.getWidth(), sector.getHeight());

            writeLayerWithOffset(out, sector.getForegroundLayer(), sector.getWidth(), sector.getHeight());

            writeLayerWithOffset(out, sector.getSpritesLayer(), sector.getWidth(), sector.getHeight());
        }

        out.close();
    }
}