package pekaeds.decorator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import pk2.level.PK2LevelSector;

public class GrassPlacer implements ISectorDecorator {

    Random random = new Random();


    private final HashSet<Integer> baseTiles = new HashSet<>();
    private final HashSet<Integer> acceptedTiles = new HashSet<>();
    private final ArrayList<Integer> resultTiles = new ArrayList<>();
    private final ArrayList<Integer> resultWeights = new ArrayList<>();
    
    private int weightSum = 0;
    
    public GrassPlacer(){
    }


    public void addBaseTile(int tile){
        this.baseTiles.add(tile);
    }


    public void addAcceptedTile(int tile){
        this.acceptedTiles.add(tile);
    }

    public void addResultTile(int tile, int weight){
        this.resultTiles.add(tile);
        this.resultWeights.add(weight);        
        this.weightSum += weight;
    }

    @Override
    public void perform(PK2LevelSector sector) {

        int width = sector.getWidth();
        int height = sector.getHeight();
        for(int y=0; y < height - 1; ++y){
            for(int x=0;x < width; ++x){

                int tileid = sector.getFGTile(x, y);
                if(acceptedTiles.contains(tileid) && baseTiles.contains(sector.getFGTile(x, y + 1 ))){

                    int r = this.random.nextInt(this.weightSum + 1);
                    for(int i=0; i < this.resultTiles.size();++i){
                        
                        r-= this.resultWeights.get(i);
                        if( r <= 0){
                            sector.setForegroundTile(x, y, this.resultTiles.get(i));
                            break;
                        }
                    }               
                }                
            }
        }
    }

    @Override
    public void setSeed(long seed) {
        random.setSeed(seed);
    }

    public void addGrassTile(int tileId, int weight){
        this.addAcceptedTile(tileId);
        this.addResultTile(tileId, weight);
    }


    public static GrassPlacer getSparseGrassPlacer(){
        GrassPlacer grassPlacer = new GrassPlacer();

        grassPlacer.addBaseTile(1);
        grassPlacer.addBaseTile(5);
        grassPlacer.addBaseTile(6);
        grassPlacer.addBaseTile(56);

        grassPlacer.addAcceptedTile(255);
        grassPlacer.addGrassTile(85, 15);
        grassPlacer.addGrassTile(86, 15);
        grassPlacer.addGrassTile(87, 3);
        grassPlacer.addGrassTile(88, 2);

        return grassPlacer;
    }


    public static GrassPlacer getDenseGrassPlacer(){
        GrassPlacer grassPlacer = new GrassPlacer();

        grassPlacer.addBaseTile(1);
        grassPlacer.addBaseTile(5);
        grassPlacer.addBaseTile(6);
        grassPlacer.addBaseTile(56);

        grassPlacer.addAcceptedTile(255);
        grassPlacer.addGrassTile(96, 13);
        grassPlacer.addGrassTile(98, 7);
        grassPlacer.addGrassTile(97, 1);

        return grassPlacer;
    }    
}
