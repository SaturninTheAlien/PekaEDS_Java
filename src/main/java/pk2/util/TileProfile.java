package pk2.util;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import org.tinylog.Logger;

public class TileProfile {
    public enum Type{
        EMPTY,
        SOLID,
        SLOPE_LEFT,
        SLOPE_RIGHT,
        BARRIER_DOWN,
        WATER,
        FIRE,
        SWITCH
    }


    private Type[] types;
    public TileProfile(){
        types = new Type[150];

        for(int i=0; i<types.length;++i){
            types[i] = Type.EMPTY;
        }
    }

    public void setImage(BufferedImage tilesetImage){
        
        for(int i=0; i <= 49; ++i){
            types[i] = Type.SOLID;
        }


        for(int i=60; i <= 79; ++i){
            types[i] = Type.SOLID;
        }

        for(int i=130; i <= 139; ++i){
            types[i] = Type.WATER;
        }

        types[144] = Type.FIRE;
        
        types[145] = Type.SWITCH;
        types[146] = Type.SWITCH;
        types[147] = Type.SWITCH;
        types[40] = Type.BARRIER_DOWN;

        try{
            Raster raster = tilesetImage.getRaster();
            for(int i=0;i<10;++i){
                int pl = raster.getSample(32*i, 160, 0);
                int pr = raster.getSample(32*i + 31, 160, 0);

                if(pl==255 && pr!=255){
                    types[50+i] = Type.SLOPE_RIGHT;
                }
                else if(pl!=255 && pr==255){
                    types[50+i] = Type.SLOPE_LEFT;
                }
                else{
                    types[50+i] = Type.BARRIER_DOWN;
                }
            }
        }
        catch(Exception e){
            Logger.error(e);
        }
    }


    public Type getTypeByID(int tileID){
        if(tileID < 0 || tileID > types.length) return Type.EMPTY;
        return this.types[tileID];
    }
}
