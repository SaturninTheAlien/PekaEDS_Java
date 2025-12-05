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

                    int tileid = sector.getFGTile(x, y);

                    //bad skull
                    if(tileid==49){
                        sector.setForegroundTile(x, y, 255);
                        sector.setBackgroundTile(x, y, 49);
                    }
                    else if(tileid==0 && sector.getFGTileType(x, y-1) != TileProfile.Type.EMPTY){
                        sector.setForegroundTile(x, y, 129);
                    }
                    else if(tileid==1){
                        sector.setForegroundTile(x, y, 56);
                    }
                    else{
                        sector.setForegroundTile(x, y, 40);
                        sector.setBackgroundTile(x, y, tileid);
                    }
                }
            }
        }
    }

    @Override
    public void setSeed(long seed) {

    }
}
