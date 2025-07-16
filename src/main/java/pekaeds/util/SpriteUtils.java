package pekaeds.util;

import java.util.List;
import java.util.Map;

import pk2.profile.SpriteProfile;
import pk2.sprite.SpritePrototype;

public final class SpriteUtils {
    private SpriteUtils() {}
    
    /**
     * Calculates the amount of times each sprite in spriteList has been placed on the map.
     * @param layer The sprites layer
     * @param spriteList The list of PK2Sprites, used in the map
     */
    public static void calculatePlacementAmountForSprites(int[][] layer, List<SpritePrototype> spriteList) {
        for (int x = 0; x < layer[0].length; x++) {
            for (int y = 0; y < layer.length; y++) {
                int sprite = layer[y][x];
                if (sprite != 255) { // If the id in the layer is 255 that means there is no sprite
                    // Check if the sprite is contained in the list, just to be safe
                    if (sprite >= 0 && sprite < spriteList.size()) {
                        spriteList.get(sprite).increasePlacedAmount();
                    }
                }
            }
        }
    }


    public static boolean filenameEquals(SpritePrototype sprite1, SpritePrototype sprite2){

        String s1 = sprite1.getFilename().toLowerCase();
        if(s1.endsWith(".spr")){
            s1 = s1 + "2";
        }

        String s2 = sprite2.getFilename().toLowerCase();
        if(s2.endsWith(".spr")){
            s2 = s2 + "2";
        }

        return s1.equals(s2);
    }

    public static List<Integer> getUnknownAIs(SpritePrototype sprite, SpriteProfile profile){

        Map<Integer, String> map = profile.getAiPatternMap();
        return sprite.getAiList().stream().filter( ai-> (ai<200 || ai > 301) && map.get(ai)==null ).toList();
    }
}
