package pk2.profile;

import java.util.HashMap;
import java.util.Map;

public final class SpriteProfile {
    private final Map<Integer, String> aiPatternMap = new HashMap<>();
    private final Map<Integer, String> damageMap = new HashMap<>();
    private final Map<Integer, String> typeMap = new HashMap<>();
    private final Map<Integer, String> destructionEffects = new HashMap<>();
    
    private final Map<Integer, String> colorMap = new HashMap<>();
    
    private final Map<Integer, String> destructionType = new HashMap<>();
    
    private final Map<Integer, String> immunityMap = new HashMap<>();
    
    private int animationsAmount = 10;
    
    private String engine;
    
    public SpriteProfile() {
    }
    
    public Map<Integer, String> getAiPatternMap() {
        return aiPatternMap;
    }
    
    public Map<Integer, String> getDamageMap() {
        return damageMap;
    }
    
    public Map<Integer, String> getTypeMap() {
        return typeMap;
    }
    
    public Map<Integer, String> getDestructionEffects() {
        return destructionEffects;
    }
    
    public Map<Integer, String> getColorMap() {
        return colorMap;
    }
    
    public Map<Integer, String> getDestructionType() {
        return destructionType;
    }
    
    public Map<Integer, String> getImmunityMap() {
        return immunityMap;
    }
    
    public int getAnimationsAmount() { return animationsAmount; }
    
    public String getEngine() { return engine; }
}
