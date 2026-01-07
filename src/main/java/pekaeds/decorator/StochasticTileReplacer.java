package pekaeds.decorator;

import java.util.ArrayList;
import java.util.HashSet;

import pk2.level.PK2LevelSector;
import java.util.Random;

public class StochasticTileReplacer implements ISectorDecorator {

    private final HashSet<Integer> acceptedTiles = new HashSet<>();
    private final ArrayList<Integer> resultTiles = new ArrayList<>();
    private final ArrayList<Integer> resultWeights = new ArrayList<>();
    
    private int weightSum = 0;
    private Random random;
    
    public StochasticTileReplacer(long seed){
        this.random = new Random(seed);
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
        for(int y=0; y < height; ++y){
            for(int x=0;x < width; ++x){

                int currentTile = sector.getFgTile(x, y);
                if(acceptedTiles.contains(currentTile)){
                    int r = this.random.nextInt(this.weightSum + 1);

                    for(int i=0; i < this.resultTiles.size();++i){
                        
                        r-= this.resultWeights.get(i);
                        if( r <= 0){
                            sector.setFgTile(x, y, this.resultTiles.get(i));
                            break;
                        }
                    }
                    
                }
            }
        }
    }

    @Override
    public void setSeed(long seed){
        this.random.setSeed(seed);
    }


    public static StochasticTileReplacer getDefaultGroundMixer(){
        StochasticTileReplacer groundMixer = new StochasticTileReplacer(0);
        groundMixer.addAcceptedTile(0);
        groundMixer.addAcceptedTile(2);
        groundMixer.addAcceptedTile(22);
        groundMixer.addAcceptedTile(23);
        groundMixer.addAcceptedTile(24);
        groundMixer.addAcceptedTile(25);
        groundMixer.addAcceptedTile(26);

        groundMixer.addResultTile(0, 25);
        groundMixer.addResultTile(2, 5);
        groundMixer.addResultTile(22, 1);
        groundMixer.addResultTile(23, 2);
        groundMixer.addResultTile(24, 5);
        groundMixer.addResultTile(25, 2);
        groundMixer.addResultTile(26, 5);

        return groundMixer;
    }
}
