package pekaeds.decorator;

import pk2.level.PK2LevelSector;
import pk2.util.TileProfile;

public class SlopeFixer implements ISectorDecorator {

    @Override
    public void perform(PK2LevelSector sector) {
        int width = sector.getWidth();
        int height = sector.getHeight();
        for(int y=0; y < height; ++y){
            for(int x=0;x < width; ++x){

                if(sector.checkBrokenSlope(x, y)){

                    int tileid = sector.getFgTile(x, y);

                    //bad skull
                    if(tileid==49){
                        sector.setFgTile(x, y, 255);
                        sector.setBgTile(x, y, 49);
                    }
                    else if(tileid==0 && sector.getFGTileType(x, y-1) != TileProfile.Type.EMPTY){
                        sector.setFgTile(x, y, 129);
                    }
                    else if(tileid==1){
                        sector.setFgTile(x, y, 56);
                    }
                    else{
                        sector.setFgTile(x, y, 40);
                        sector.setBgTile(x, y, tileid);
                    }
                }
            }
        }
    }

    @Override
    public void setSeed(long seed) {

    }
}
