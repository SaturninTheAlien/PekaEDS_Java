package pekaeds.decorator;

import java.util.HashSet;
import pk2.level.PK2LevelSector;
import pk2.util.TileProfile;


public class SolidGrassPlacer implements ISectorDecorator {

    private final HashSet<Integer> acceptedTiles = new HashSet<>();
    private SolidGrassPlacer(){
        this.acceptedTiles.add(0);
        this.acceptedTiles.add(2);
        this.acceptedTiles.add(22);
        this.acceptedTiles.add(23);
        this.acceptedTiles.add(24);
        this.acceptedTiles.add(25);
        this.acceptedTiles.add(26);
    }

    public void addAcceptedTile(int tile){
        this.acceptedTiles.add(tile);
    }

    @Override
    public void perform(PK2LevelSector sector) {

        int width = sector.getWidth();
        int height = sector.getHeight();
        for(int y=1; y < height; ++y){
            for(int x=0;x < width; ++x){

                int tileid = sector.getFgTile(x, y);
                if(tileid==50 && sector.getFGTileType(x + 1, y - 1) == TileProfile.Type.EMPTY){
                    sector.setFgTile(x, y, 58);
                }
                else if(tileid==51 && sector.getFGTileType(x - 1, y - 1) == TileProfile.Type.EMPTY){
                    sector.setFgTile(x, y, 59);
                }
                else if(sector.getFGTileType(x, y - 1) == TileProfile.Type.EMPTY && sector.getFgTile(x, y - 1) != 129){
                    if(tileid==3){
                        sector.setFgTile(x, y, 5);
                    }
                    else if(tileid==4){
                        sector.setFgTile(x, y, 6);
                    }
                    else if(acceptedTiles.contains(tileid)){
                        sector.setFgTile(x, y, 1);
                    }
                }
            }
        }
    }

    @Override
    public void setSeed(long seed) {
    }
    

    public static SolidGrassPlacer getInstance(){
        SolidGrassPlacer decorator = new SolidGrassPlacer();
       

        return decorator;
    }
}
