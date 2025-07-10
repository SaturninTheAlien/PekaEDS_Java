package pk2.settings;

import java.io.*;

public class LevelTestingSettings {
    public boolean devMode = false;
        
    public boolean customDataDirectory = false;
    
    public String dataDirectory = "";

    public boolean customExecutable = false;
    public String executable = "";

    public void load(DataInputStream in) throws IOException{
        this.devMode = in.readBoolean();

        this.customDataDirectory = in.readBoolean();
        this.dataDirectory = in.readUTF();
        
        this.customExecutable = in.readBoolean();
        this.executable = in.readUTF();
    }

    public void save(DataOutputStream out) throws IOException{
        out.writeBoolean(this.devMode);

        out.writeBoolean(this.customDataDirectory);

        out.writeUTF(this.dataDirectory);

        out.writeBoolean(this.customExecutable);

        out.writeUTF(this.executable);
    }
}
