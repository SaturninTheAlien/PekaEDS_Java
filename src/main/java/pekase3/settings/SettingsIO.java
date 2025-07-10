package pekase3.settings;

import org.tinylog.Logger;

import java.io.*;

@Deprecated
public final class SettingsIO {
    private SettingsIO() {
    
    }
    
    public static void save(String file, Settings settings) {
        try (var dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
            dos.writeUTF(settings.getGamePath());
            dos.writeUTF(settings.getSpriteProfileFile());
        } catch (IOException e) {
            Logger.warn(e);
        }
    }
    
    public static Settings load(String file) throws IOException {
        Settings settings = null;
        
        var dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
        settings = new Settings();
        settings.setGamePath(dis.readUTF());
        settings.setSpriteProfileFile(dis.readUTF());
        dis.close();
        
        if (!new File(settings.getGamePath()).exists()) {
            throw new FileNotFoundException("Game directory '" + settings.getGamePath() + "' doesn't exist!");
        }

        
        
        return settings;
    }
}
