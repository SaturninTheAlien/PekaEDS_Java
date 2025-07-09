package pk2.profile;

//import java.util.logging.Logger;


@Deprecated
public final class ProfileReader {
    //private static final Logger logger = Logger.getLogger(ProfileReader.class.getName());
    
    public static LevelProfile readMapProfile(String filename) {
        /*var mapper = new YAMLMapper(new YAMLFactory());
        MapProfile profile = null; // TODO Hard code a default profile?
        
        try {
            profile = mapper.readValue(new File(filename), MapProfile.class);
    
            System.out.println(profile);
        } catch (IOException e) {
            logger.warning("Couldn't load map profile file.\n" + e.getMessage());
        }*/
        
        return new LevelProfile();
    }
    
    public static SpriteProfileOld readSpriteProfile(String filename) {
        /*var mapper = new YAMLMapper(new YAMLFactory());
        SpriteProfile spriteProfile = null;
    
        try {
            spriteProfile = mapper.readValue(new File(filename), SpriteProfile.class);
        } catch (IOException e) {
            logger.warning("Couldn't load sprite profile.\n" + e.getMessage()); // TODO Load defaults
        }*/
        
        return new SpriteProfileOld();
    }
}
