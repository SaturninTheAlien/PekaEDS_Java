package pk2.profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.HashMap;

public class LevelProfile {   
    private List<String> musicFormats = new ArrayList<>();
    
    private List<String> scrollingTypes = new ArrayList<>();
    private List<String> weatherTypes = new ArrayList<>();
    private List<String> mapIconNames = new ArrayList<>();
    private List<String> gameModes = new ArrayList<>();

    private Map<Integer, String> fireColors = new HashMap<>();
    private Map<Integer, String> splashColors = new HashMap<>();

    public LevelProfile(){}

    @Override
    public String toString() {
        var sb = new StringBuilder();
        
        for (var s : musicFormats) {
            sb.append("\n" + s);
        }
        
        for (var s : scrollingTypes) {
            sb.append("\n" + s);
        }
        
        for (var s : weatherTypes) {
            sb.append("\n" + s);
        }
        
        for (var s : mapIconNames) {
            sb.append("\n" + s);
        }
        
        return sb.toString();
    }

    public Map<Integer, String> getFireColors(){
        return this.fireColors;
    }
    public Map<Integer, String> getSplashColors(){
        return this.splashColors;
    }
    
    public List<String> getMusicFormats() {
        return musicFormats;
    }
    
    public List<String> getScrollingTypes() {
        return scrollingTypes;
    }
    
    public List<String> getWeatherTypes() {
        return weatherTypes;
    }
    
    public List<String> getMapIconNames() {
        return mapIconNames;
    }

    public List<String> getGameModes(){
        return gameModes;
    }
}
