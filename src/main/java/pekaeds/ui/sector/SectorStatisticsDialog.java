package pekaeds.ui.sector;

import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import pekaeds.ui.listeners.PK2SectorConsumer;
import pk2.level.PK2LevelSector;

public class SectorStatisticsDialog extends JDialog implements PK2SectorConsumer{
    private PK2LevelSector sector;
    

    private JTextArea aStatistics;

    public SectorStatisticsDialog(){
        this.aStatistics = new JTextArea();
        this.aStatistics.setEditable(false);


        JScrollPane pane = new JScrollPane();
        pane.setViewportView(this.aStatistics);
        this.add(pane);

        this.setTitle("Sector statistics");
        this.setSize(500, 500);

        this.setModal(true);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
    }

    
    public class TileUsage {
        public final int id;

        public TileUsage(int id, int fgCount, int bgCount){
            this.id = id;
            this.fgCount = fgCount;
            this.bgCount = bgCount;
        }
        
        public int fgCount = 0;
        public int bgCount = 0;
    }

    public Map<Integer, TileUsage> stats;
    public int placedSprites;
    public int brokenSlopes;


    public void calculate(){

        int width = this.sector.getWidth();
        int height = this.sector.getHeight();

        this.placedSprites = 0;
        this.brokenSlopes = 0;

        stats = new HashMap<>();

        for(int y=0;y<height; ++y){
            for(int x=0;x<width; ++x){

                int spriteTile = sector.getSpriteTile(x, y);
                if(spriteTile!=255){
                    ++this.placedSprites;
                }

                int fgTile = sector.getFGTile(x, y);
                if(fgTile!=255){
                    if(stats.containsKey( fgTile )){
                        TileUsage tileUsage = stats.get(fgTile);
                        ++tileUsage.fgCount;
                    }
                    else{
                        TileUsage tileUsage = new TileUsage(fgTile, 1, 0);
                        stats.put(fgTile, tileUsage);
                    }
                }
                int bgTile = sector.getBGTile(x, y);
                if(bgTile!=255){
                    if(stats.containsKey(bgTile)){
                        TileUsage tileUsage = stats.get(bgTile);
                        ++tileUsage.bgCount;
                    }
                    else{
                        TileUsage tileUsage = new TileUsage(bgTile, 0, 1);
                        stats.put(bgTile, tileUsage);
                    }
                }


                if(sector.checkBrokenSlope(x, y)){
                    ++this.brokenSlopes;
                }
            }
        }

        StringBuilder builder = new StringBuilder();

        builder.append("Placed sprites: ");
        builder.append(this.placedSprites);
        builder.append("\n");

        builder.append("Potentially broken slopes (it depends on the tileset): ");
        builder.append(this.brokenSlopes);
        builder.append("\n");
        
        builder.append("tile -> placed: ");
        builder.append("\n");

        this.stats.entrySet().stream()
        .sorted(Map.Entry.comparingByKey())
        .forEach(entry -> {

            int total = entry.getValue().fgCount + entry.getValue().bgCount;

            builder.append(entry.getKey())
                   .append(" -> ")
                   .append(total)
                   .append(" (FG: ")
                   .append(entry.getValue().fgCount)
                   .append(" BG: ")
                   .append(entry.getValue().bgCount)
                   .append(")\n");
        });

        this.aStatistics.setText(builder.toString());
    }
    @Override
    public void setSector(PK2LevelSector sector) {
        this.sector = sector;
    }
    
}
