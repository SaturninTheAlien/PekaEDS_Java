package pk2.sprite.io;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import pk2.sprite.PK2Sprite;
import pk2.sprite.PK2SpriteAnimation;
import pk2.util.Point2D;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;


class SpriteWriterJson {
    public void save(PK2Sprite sprite, File file) throws IOException {
        JSONObject json = new JSONObject();

        json.put("version", "2.0");
        
        JSONArray aiArray = new JSONArray();
        for (int ai : sprite.getAiList()) {
            if(ai!=0){
                /**
                 * Zero AI shouldn't be saved in .spr2
                 */
                aiArray.put(ai);
            }
        }
        
        json.put("ai", aiArray);
        json.put("always_active", sprite.isAlwaysActive());
        
        json.put("ammo1", sprite.getAttack1SpriteFile());
        json.put("ammo2", sprite.getAttack2SpriteFile());
        
        JSONObject jsonAnimations = new JSONObject();
        List<PK2SpriteAnimation> animations = sprite.getAnimationsList();
        addAnimation(jsonAnimations, "idle", animations.get(0));
        addAnimation(jsonAnimations, "walking", animations.get(1));
        addAnimation(jsonAnimations, "jump_up", animations.get(2));
        addAnimation(jsonAnimations, "jump_down", animations.get(3));
        addAnimation(jsonAnimations, "squat", animations.get(4));
        addAnimation(jsonAnimations, "damage", animations.get(5));
        addAnimation(jsonAnimations, "death", animations.get(6));
        addAnimation(jsonAnimations, "attack1", animations.get(7));
        addAnimation(jsonAnimations, "attack2", animations.get(8));
        
        json.put("animations", jsonAnimations);
        
        json.put("attack1_time", sprite.getAttack1Duration());
        json.put("attack2_time", sprite.getAttack2Duration());
        
        json.put("bonus", sprite.getBonusSpriteFile());
        json.put("bonus_always", sprite.isAlwaysBonus());
        json.put("bonuses_number", sprite.getBonusAmount());
        
        json.put("can_glide", sprite.canGlide());
        json.put("can_open_locks", sprite.isKey());
        json.put("can_swim", sprite.canSwim());
        
        json.put("charge_time", sprite.getAttackPause());
        
        json.put("check_tiles", sprite.isTileCheck());
        
        String colorString = switch (sprite.getColor()) {
            case 255 -> "normal";
            case 0 -> "gray";
            case 32 -> "blue";
            case 64 -> "red";
            case 96 -> "green";
            case 128 -> "orange";
            case 160 -> "violet";
            case 192 -> "turquoise";
            
            default -> "normal";
        };
        
        json.put("color", colorString);
        
        json.put("damage", sprite.getDamage());
        json.put("damage_type", sprite.getDamageType());
        
        json.put("enemy", sprite.isEnemy());
        json.put("energy", sprite.getEnergy());
        
        json.put("frame", new JSONObject()
                .put("pos_x", sprite.getFrameX())
                .put("pos_y", sprite.getFrameY())
                .put("width", sprite.getFrameWidth())
                .put("height", sprite.getFrameHeight()));
        
        json.put("frame_rate", sprite.getFrameRate());
        json.put("frames_number", sprite.getFramesAmount());

        json.put("destruction_effect", sprite.getDestructionEffect());
        json.put("indestructible", sprite.isIndestructible());
        
        json.put("immunity_type", sprite.getImmunityToDamageType());
        
        json.put("is_wall", sprite.isObstacle());
        json.put("is_wall_down", sprite.isWallDown());
        json.put("is_wall_left", sprite.isWallLeft());
        json.put("is_wall_right", sprite.isWallRight());
        json.put("is_wall_up", sprite.isWallUp());
        
        json.put("max_jump", sprite.getMaxJump());
        
        json.put("max_speed", sprite.getMaxSpeed());
        
        json.put("name", sprite.getName());
        
        json.put("picture", sprite.getImageFile());
        
        json.put("projectile_charge_time", sprite.getLoadTime());
        
        json.put("random_sound_frequency", sprite.isRandomSoundFrequency());
        
        json.put("score", sprite.getScore());
        
        json.put("size", new JSONObject()
                .put("height", sprite.getHeight())
                .put("width", sprite.getWidth()));
        
        json.put("sound_frequency", sprite.getSoundFrequency());
        
        json.put("sounds", new JSONObject()
                .put("damage", sprite.getSoundFile(0))
                .put("destruction", sprite.getSoundFile(1))
                .put("attack1", sprite.getSoundFile(2))
                .put("attack2", sprite.getSoundFile(3))
                .put("random", sprite.getSoundFile(4))
                .put("special1", sprite.getSoundFile(5))
                .put("special2", sprite.getSoundFile(6)));
        
        json.put("transformation", sprite.getTransformationSpriteFile());
        
        json.put("type", sprite.getType());
        
        json.put("vibrates", sprite.isShakes());
        
        json.put("weight", sprite.getWeight());

        json.put("info_id", sprite.getInfoID());
        
        if (!sprite.getCommands().isEmpty()) {
            json.put("commands", sprite.getCommands());
        }
        
        if (sprite.hasDeadWeight()) {
            json.put("dead_weight", sprite.getDeadWeight());
        }


        json.put("effect", sprite.getAmbientEffect());
        json.put("blend_mode", sprite.getBlendMode());
        
        if(sprite.getBlendMode()!=0){
            json.put("blend_alpha", sprite.getBlendAlpha());
        }

        this.putPoint2D(json, sprite.getPlayerDetection(), "player_detection");

        if(sprite.getAttack1Offset()!=null){
            this.putPoint2D(json, sprite.getAttack1Offset(), "attack1_offset");
        }

        if(sprite.getAttack2Offset()!=null){
            this.putPoint2D(json, sprite.getAttack2Offset(), "attack2_offset");
        }

        json.put("parallax_type", sprite.getParallaxFactor());


        Map<String, Object> map = json.toMap();

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);

        try (PrintWriter out = new PrintWriter(file)) {
            out.println(mapper.writeValueAsString(map));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void putPoint2D(JSONObject json, Point2D p, String name){

        JSONObject o = new JSONObject();
        o.put("x", p.getX());
        o.put("y", p.getY());
        json.put(name, o);
    }
    
    private void addAnimation(JSONObject json, String name, PK2SpriteAnimation animation) {
        JSONArray sequence = new JSONArray();
        
        for (int i = 0; i < animation.getSequence().length; ++i) {
            if (animation.getSequence()[i] != 0) {
                sequence.put(animation.getSequence()[i]);
            }
        }
        
        json.put(name, new JSONObject()
                .put("loop", animation.loop())
                .put("sequence", sequence));
    }
}
