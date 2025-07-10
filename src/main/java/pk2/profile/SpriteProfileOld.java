package pk2.profile;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class SpriteProfileOld {
    protected static class AiTablePair {
        private int id;
        private String description;
    
        public int getId() {
            return id;
        }
    
        public void setId(int id) {
            this.id = id;
        }
    
        public String getDescription() {
            return description;
        }
    
        public void setDescription(String description) {
            this.description = description;
        }
    }
    
    private int stringLengthName = 32;
    private int stringLengthFiles = 100;
    private int amountOfSounds = 7;
    private int amountOfAnimations = 20;
    private int amountOfAI = 10;
    
    private List<String> types = List.of("Character", "Bonus Item", "Ammo", "Teleport", "Background", "Foreground");
    
    private List<AiTablePair> aiTable = new ArrayList<>();
    
    public List<String> getTypes() {
        return types;
    }
    
    public int getStringLengthName() {
        return stringLengthName;
    }
    
    public void setStringLengthName(int stringLengthName) {
        this.stringLengthName = stringLengthName;
    }
    
    public int getStringLengthFiles() {
        return stringLengthFiles;
    }
    
    public void setStringLengthFiles(int stringLengthFiles) {
        this.stringLengthFiles = stringLengthFiles;
    }
    
    public int getAmountOfSounds() {
        return amountOfSounds;
    }
    
    public void setAmountOfSounds(int amountOfSounds) {
        this.amountOfSounds = amountOfSounds;
    }
    
    public int getAmountOfAnimations() {
        return amountOfAnimations;
    }
    
    public void setAmountOfAnimations(int amountOfAnimations) {
        this.amountOfAnimations = amountOfAnimations;
    }
    
    public int getAmountOfAI() {
        return amountOfAI;
    }
    
    public void setAmountOfAI(int amountOfAI) {
        this.amountOfAI = amountOfAI;
    }
    
    public List<AiTablePair> getAiTable() {
        return aiTable;
    }
    
    public void setAiTable(List<AiTablePair> aiTable) {
        this.aiTable = aiTable;
    }
}
