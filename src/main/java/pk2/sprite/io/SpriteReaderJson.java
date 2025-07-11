package pk2.sprite.io;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pk2.sprite.PK2Sprite;
import pk2.sprite.PK2SpriteAnimation;
import pk2.util.Point2D;

class SpriteReaderJson implements SpriteReader {

    @Override
    public PK2Sprite readSpriteFile(File file) throws IOException, JSONException {
        String fileContents = Files.readString(file.toPath());
        JSONObject json = new JSONObject(fileContents);
        PK2Sprite sprite = parseSprite(json);
        sprite.setFilename(file.getName());
        return sprite;
    }


    protected PK2Sprite parseSprite(JSONObject json) throws JSONException{
        PK2Sprite sprite = new PK2Sprite();
        
            
        ArrayList<Integer> aiList = new ArrayList<>();
        JSONArray aiArray = json.getJSONArray("ai");
        for (int i = 0; i < aiArray.length(); ++i) {
            aiList.add(aiArray.getInt(i));
        }
        
        sprite.setAiList(aiList);

        sprite.setAlwaysActive(json.getBoolean("always_active"));
        
        sprite.setAttack1SpriteFile(json.getString("ammo1"));
        sprite.setAttack2SpriteFile(json.getString("ammo2"));
        
        JSONObject jsonAnimations = json.getJSONObject("animations");

        ArrayList<PK2SpriteAnimation> animationsList = new ArrayList<>();
        animationsList.add(JSONToAnimation(jsonAnimations.getJSONObject("idle")));
        animationsList.add(JSONToAnimation(jsonAnimations.getJSONObject("walking")));
        animationsList.add(JSONToAnimation(jsonAnimations.getJSONObject("jump_up")));
        animationsList.add(JSONToAnimation(jsonAnimations.getJSONObject("jump_down")));
        animationsList.add(JSONToAnimation(jsonAnimations.getJSONObject("squat")));
        animationsList.add(JSONToAnimation(jsonAnimations.getJSONObject("damage")));
        animationsList.add(JSONToAnimation(jsonAnimations.getJSONObject("death")));
        animationsList.add(JSONToAnimation(jsonAnimations.getJSONObject("attack1")));
        animationsList.add(JSONToAnimation(jsonAnimations.getJSONObject("attack2")));
        
        sprite.setAnimationsList(animationsList);
        
        sprite.setAttack1Duration(json.getInt("attack1_time"));
        sprite.setAttack2Duration(json.getInt("attack2_time"));
        
        sprite.setBonusSpriteFile(json.getString("bonus"));
        sprite.setAlwaysBonus(json.getBoolean("bonus_always"));
        sprite.setBonusAmount(json.getInt("bonuses_number"));
        
        sprite.setGlide(json.getBoolean("can_glide"));
        sprite.setKey(json.getBoolean("can_open_locks"));
        sprite.setSwim(json.getBoolean("can_swim"));
        
        sprite.setAttackPause(json.getInt("charge_time"));
        
        sprite.setTileCheck(json.getBoolean("check_tiles"));
        
        HashMap<String, Integer> colorMap = new HashMap<String, Integer>();
        colorMap.put("normal", 255);
        colorMap.put("gray", 0);
        colorMap.put("blue", 32);
        colorMap.put("red", 64);
        colorMap.put("green", 96);
        colorMap.put("orange", 128);
        colorMap.put("violet", 160);
        colorMap.put("turquoise", 192);
        
        sprite.setColor(colorMap.get(json.getString("color")));
        
        sprite.setDamage(json.getInt("damage"));
        sprite.setDamageType(json.getInt("damage_type"));
        
        sprite.setEnemy(json.getBoolean("enemy"));
        sprite.setEnergy(json.getInt("energy"));
        
        JSONObject jsonFrame = json.getJSONObject("frame");
        sprite.setFrameX(jsonFrame.getInt("pos_x"));
        sprite.setFrameY(jsonFrame.getInt("pos_y"));
        sprite.setFrameWidth(jsonFrame.getInt("width"));
        sprite.setFrameHeight(jsonFrame.getInt("height"));
        
        sprite.setFrameRate(json.getInt("frame_rate"));
        sprite.setFramesAmount(json.getInt("frames_number"));

        if(json.has("how_destroyed")){
            /**
             * deprecated
             */

            int destruction = json.getInt("how_destroyed");
            sprite.getDestructionEffect(destruction);
            sprite.setIndestructible(destruction==0);

        }
        else{
            sprite.getDestructionEffect(json.getInt("destruction_effect"));
            sprite.setIndestructible(json.getBoolean("indestructible"));
        }
                    
        sprite.setImmunityToDamageType(json.getInt("immunity_type"));
        
        sprite.setObstacle(json.getBoolean("is_wall"));
        sprite.setWallDown(json.getBoolean("is_wall_down"));
        sprite.setWallLeft(json.getBoolean("is_wall_left"));
        sprite.setWallRight(json.getBoolean("is_wall_right"));
        sprite.setWallUp(json.getBoolean("is_wall_up"));
        
        sprite.setMaxJump(json.getInt("max_jump"));
        
        sprite.setMaxSpeed(json.getDouble("max_speed"));
        
        sprite.setName(json.getString("name"));
        sprite.setImageFile(json.getString("picture"));
        
        sprite.setLoadTime(json.getInt("projectile_charge_time"));
        sprite.setRandomSoundFrequency(json.getBoolean("random_sound_frequency"));
        
        sprite.setScore(json.getInt("score"));
        
        JSONObject jsonHitbox = json.getJSONObject("size");
        sprite.setHeight(jsonHitbox.getInt("height"));
        sprite.setWidth(jsonHitbox.getInt("width"));
        
        sprite.setSoundFrequency(json.getInt("sound_frequency"));
        
        JSONObject jsonSounds = json.getJSONObject("sounds");
        sprite.setSoundFile(jsonSounds.getString("damage"), 0);
        sprite.setSoundFile(jsonSounds.getString("destruction"), 1);
        sprite.setSoundFile(jsonSounds.getString("attack1"), 2);
        sprite.setSoundFile(jsonSounds.getString("attack2"), 3);
        sprite.setSoundFile(jsonSounds.getString("random"), 4);
        sprite.setSoundFile(jsonSounds.getString("special1"), 5);
        sprite.setSoundFile(jsonSounds.getString("special2"), 6);
        
        sprite.setTransformationSpriteFile(json.getString("transformation"));
        
        sprite.setType(json.getInt("type"));
        
        sprite.setShakes(json.getBoolean("vibrates"));
        
        sprite.setWeight(json.getDouble("weight"));
        
        if (json.has("commands")) {
            sprite.setCommands(json.getJSONArray("commands"));
        }
        
        if (json.has("dead_weight")) {
            sprite.setHasDeadWeight(true);
            sprite.setDeadWeight(json.getDouble("dead_weight"));
        }

        if(json.has("info_id")){
            sprite.setInfoID(json.getInt("info_id"));
        }

        if(json.has("effect")){
            sprite.setAmbientEffect(json.getInt("effect"));
        }

        if(json.has("blend_mode")){
            sprite.setBlendMode(json.getInt("blend_mode"));
        }

        if(json.has("player_detection")){
            sprite.setPlayerDetection( this.parsePoint2D(json, "player_detection"));
        }

        if(json.has("attack1_offset")){
            sprite.setAttack1Offset(this.parsePoint2D(json, "attack1_offset"));
        }

        if(json.has("attack2_offset")){
            sprite.setAttack2Offset(this.parsePoint2D(json, "attack2_offset"));
        }

        return sprite;
    }

    protected Point2D parsePoint2D(JSONObject json, String name){
        JSONObject point = json.getJSONObject(name);
        return new Point2D(point.getDouble("x"), point.getDouble("y"));
    }
    
    private PK2SpriteAnimation JSONToAnimation(JSONObject json) {
        JSONArray jsonArray = json.getJSONArray("sequence");
        boolean loop = json.getBoolean("loop");
        
        byte[] sequence = new byte[jsonArray.length()];                
        for (int i = 0; i < jsonArray.length(); ++i) {
            sequence[i] = (byte) jsonArray.getInt(i);
        }        
        return new PK2SpriteAnimation(sequence, sequence.length, loop);
    }
    
}
