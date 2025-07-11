package pk2.sprite.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.json.JSONException;
import org.json.JSONObject;

import pk2.filesystem.PK2FileSystem;
import pk2.sprite.PK2Sprite;

class SpriteReaderJsonCompat extends SpriteReaderJson{    
    @Override
    public PK2Sprite readSpriteFile(File file) throws IOException, JSONException {

        String fileContents = Files.readString(file.toPath());
        JSONObject json = new JSONObject(fileContents);

        if(json.has("parent")){
            File parentSprite = PK2FileSystem.findSprite(json.getString("parent"));
            JSONObject parentJson = new JSONObject(Files.readString(parentSprite.toPath()));

            for(String key: parentJson.keySet()){
                if(!json.has(key)){
                    json.put(key, parentJson.get(key));
                }
            }
        }
        
        PK2Sprite sprite = parseSprite(json);
        sprite.setFilename(file.getName());
        return sprite;
    }

}
