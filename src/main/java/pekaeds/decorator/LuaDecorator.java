package pekaeds.decorator;

import java.io.File;
import java.nio.file.Files;

import party.iroiro.luajava.Lua;
import party.iroiro.luajava.lua54.Lua54;
import party.iroiro.luajava.value.LuaFunction;
import party.iroiro.luajava.value.LuaValue;
import pk2.level.PK2LevelSector;
import pk2.util.TileProfile;

public class LuaDecorator implements ISectorDecorator {

    protected final String luaSource;
    protected Lua L;    
    protected long seed;

    public LuaDecorator(String luaSource){
        this.luaSource = luaSource;
    }


    public static class PK2SectorMiddleware {

        private final PK2LevelSector sector;

        public PK2SectorMiddleware(PK2LevelSector sector){
            this.sector = sector;
        }

        public int getWidth(){
            return this.sector.getWidth();
        }

        public int getHeight(){
            return this.sector.getHeight();
        }

        public int getFgTile(int x, int y){
            return this.sector.getFgTile(x, y);
        }

        public int getBgTile(int x, int y){
            return this.sector.getBgTile(x, y);
        }

        public int getFgTileType(int x, int y){
            return sector.getFGTileType(x, y).ordinal();
        }

        public void setFgTile(int x, int y, int value){
            this.sector.setFgTile(x, y, value);
        }

        public void setBgTile(int x, int y, int value){
            this.sector.setBgTile(x, y, value);
        }

        public int getSprTile(int x, int y){
            return this.sector.getSpriteTile(x, y);
        }

        public void setSpriteTile(int x, int y, int value){
            this.sector.setSpriteTile(x, y, value);
        }

        public boolean checkBrokenSlope(int x, int y){
            return this.sector.checkBrokenSlope(x, y);
        }
    }

    @Override
    public void perform(PK2LevelSector sector) throws Exception {

        PK2SectorMiddleware sectorMiddleware = new PK2SectorMiddleware(sector);

        L = new Lua54();
        L.openLibraries();

        L.getGlobal("package");
        L.getField(-1, "preload"); 

        L.push(new LuaFunction() {
            @Override
            public LuaValue[] call(Lua L, LuaValue[] args) {

                L.newTable();
                L.push(new LuaFunction() {
                    @Override
                    public LuaValue[] call(Lua L, LuaValue[] args) {
                        L.push(123);
                        return new LuaValue[] { L.get() };
                    }
                });
                L.setField(-2, "hello");

                L.push(new LuaFunction() {
                    @Override
                    public LuaValue[] call(Lua L, LuaValue[] args) {

                        if (args.length < 1) return new LuaValue[0];
                        LuaValue cb = args[0];

                        int w = sectorMiddleware.getWidth();
                        int h = sectorMiddleware.getHeight();

                        for (int y = 0; y < h; ++y) {
                            for (int x = 0; x < w; ++x) {
                                cb.call(x, y);
                            }
                        }
                        return new LuaValue[0];
                    }
                });
                L.setField(-2, "forEachTile");

                L.pushJavaObject(sectorMiddleware);
                L.setField(-2, "sector");

                L.newTable();
                for (TileProfile.Type t : TileProfile.Type.values()) {
                    L.push(t.ordinal());
                    L.setField(-2, t.name());
                }
                L.setField(-2, "tileProfile");

                return new LuaValue[] { L.get() };
            }
        });

        L.setField(-2, "pk2-editor"); 
        L.pop(2);

        L.getGlobal("math");
        L.getField(-1, "randomseed");
        LuaValue lf = L.get();
        lf.call(this.seed);
        L.pop(1);

        L.run(this.luaSource);
    }


    @Override
    public void setSeed(long seed) {
        this.seed = seed;
    }


    public static void main(String args[]){

        PK2LevelSector sector = new PK2LevelSector(4,4);
        sector.setFgTile(0, 0, 13);
        sector.setFgTile(1, 2, 12);

        try{
            LuaDecorator luaDecorator = new LuaDecorator(Files.readString(new File("test.lua").toPath()));
            luaDecorator.setSeed(13);
            luaDecorator.perform(sector);
        }
        catch(Exception e){
            System.out.println(e);
        }        

    }

}
