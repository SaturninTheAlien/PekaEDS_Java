package pekaeds.util;

import java.util.List;
import java.util.Map;

import pk2.profile.SpriteProfile;
import pk2.sprite.SpritePrototype;

public final class SpriteUtils {
    private SpriteUtils() {}
    
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
