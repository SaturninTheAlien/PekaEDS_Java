package pekaeds.pk2.level;

import java.util.ArrayList;
import java.util.List;

import pekaeds.pk2.sprite.ISpritePrototype;

public class PK2Level {
    public List<PK2LevelSector> sectors = new ArrayList<>();

    protected List<String> spriteNames = new ArrayList<>();
    protected List<ISpritePrototype> sprites = new ArrayList<>();

    public String name;
    public String author;

    public int      level_number          = 0;                            // level of the episode
    public int      time           = 0;                            // time (in (dec)conds)
    public int      extra          = 0;                            // extra config - not used

    public int      player_sprite_index = 0;                            // player prototype

    public int      icon_x = 0;                                         // icon x pos
	public int      icon_y = 0;                                         // icon x pos
    public int      icon_id = 0;                                        // icon id

    public String lua_script = "";                        // lua script
    public int game_mode = 0;                                          // game mode

    public List<ISpritePrototype> getSpriteList(){
        return this.sprites;
    }

    public ISpritePrototype getSprite(int index){
        return this.sprites.get(index);
    }

    public int getLastSpriteIndex(){
        return this.sprites.size() - 1;
    }

    /**
     * 
     * @param sprite
     * @return The index of added sprite
     */
    public int addSprite(ISpritePrototype sprite){

        int size = this.sprites.size();
        
        /**
         * To prevent adding a sprite multiple times
         */
        for(int i=0;i<this.sprites.size();++i){
            if(this.sprites.get(i).getFilename().equals(sprite.getFilename())){
                return i;
            }
        }

        this.sprites.add(sprite);
        this.spriteNames.add(sprite.getFilename());
        return size;
    }

    public void removeSprite(ISpritePrototype sprite){
        int index = this.sprites.indexOf(sprite);
        if(index!=-1){
            if(index < this.player_sprite_index){
                --this.player_sprite_index;
            }
            else if(index==this.player_sprite_index){
                this.sprites.get(index).setPlayerSprite(false);   
            }

            this.sprites.remove(index);
            this.spriteNames.remove(index);

            for(PK2LevelSector sector: this.sectors){
                sector.removeSprite(index);
            }
            
            if(index==this.player_sprite_index){    
                this.player_sprite_index = 0;
                if(this.sprites.size()>0){
                    this.sprites.get(0).setPlayerSprite(true);
                }
            }            
        }
    }

    public int countSprites(int id){
        int result = 0;
        for(PK2LevelSector sector:this.sectors){
            result += sector.spriteTiles.countTiles(id);
        }
        return result;
    }
}
