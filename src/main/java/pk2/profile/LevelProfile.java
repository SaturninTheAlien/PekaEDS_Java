package pk2.profile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class LevelProfile {

    public static LevelProfile getDefaultProfile(){
        LevelProfile profile = new LevelProfile();

        profile.fireColors = new HashMap<>();
        profile.fireColors.put(0, "Gray");
        profile.fireColors.put(32, "Blue");
        profile.fireColors.put(64,"Red");
        profile.fireColors.put(96, "Green");
        profile.fireColors.put(128,"Orange");
        profile.fireColors.put(160,"Violet");
        profile.fireColors.put(192,"Turquoise");

        profile.splashColors = new HashMap<>();
        profile.splashColors.put(-1, "Default");
        profile.splashColors.putAll(profile.fireColors);

        profile.scrollingTypes.add("None");
        profile.scrollingTypes.add("Vertical");
        profile.scrollingTypes.add("Horizontal");
        profile.scrollingTypes.add("Horizontal & Vertical");
        
        profile.weatherTypes.add("None");
        profile.weatherTypes.add("Rain");
        profile.weatherTypes.add("Leaves");
        profile.weatherTypes.add("Rain & Leaves");
        profile.weatherTypes.add("Snow");
        profile.weatherTypes.add("Dandelions");

        profile.gameModes.add("classic");
        profile.gameModes.add("kill all enemies");

        profile.iconNames = new String[] {
            "Question mark",
            "Forest Hill",
            "Forest Hill at night",
            "Deep Forest",
            "Deep Forest at Night",
            "Field",
            "Field at night",
            "Mountains",
            "Castle",
            "Red Castle",
            "Cave",
            "Boss Battle",
            "Factory",
            "Custom Icon #14",
            "Custom Icon #15",
            "Custom Icon #16",
            "Custom Icon #17",
            "Custom Icon #18",
            "Custom Icon #19",
            "Custom Icon #20",
            "Custom Icon #21",
            "Custom Icon #22"
        };

        return profile;
    }
    
    private List<String> musicFormats = Arrays.asList("xm", "mod", "it", "s3m", "ogg", "mp3");
    
    private List<String> scrollingTypes = new ArrayList<>();
    private List<String> weatherTypes = new ArrayList<>();
    private List<String> mapIconNames = new ArrayList<>();
    private List<String> gameModes = new ArrayList<>();

    private Map<Integer, String> fireColors = new HashMap<>();
    private Map<Integer, String> splashColors = new HashMap<>();

    private String [] iconNames = null;
    protected LevelProfile(){}

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
        
    public String[] getIconNames() {
        return iconNames;
    }
}
