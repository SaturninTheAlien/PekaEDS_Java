package pekase3.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import pk2.profile.SpriteProfile;

import org.tinylog.Logger;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public final class ProfileReader {
    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    
    private ProfileReader() {}
    
    public static SpriteProfile loadSpriteProfile(String filename) {
        SpriteProfile sp = new SpriteProfile();
        
        try {
            sp = mapper.readValue(new File(filename), SpriteProfile.class);
        } catch (IOException e) {
            Logger.warn(e, "Unable to load profile file '" + filename + "'!");
            Logger.info("Loading default profile");
            
            try {
                sp = mapper.readValue(ProfileReader.class.getClassLoader().getResourceAsStream("SDL.yml"), SpriteProfile.class);
            } catch (IOException ex) {
                Logger.error("Unable to load default profile file!");
            }
            
            JOptionPane.showMessageDialog(null, "Unable to load profile file: '" + filename + "'!\nSee log file for more information.", "Can't load profile!", JOptionPane.ERROR_MESSAGE);
        }
        
        return sp;
    }
}
