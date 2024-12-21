package pekaeds.pk2.sprite.io;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import pekaeds.pk2.sprite.SpritePrototype;

// TODO CLEAN UP wtf is this mess
public class SpriteOld implements SpritePrototype {
    protected static List<Integer> ID = new ArrayList<>();

    protected String filename;
    
    private int type;
    
    protected String imageFile;
    protected String imageFileIdentifier;

    protected String[] soundFiles = new String[7];
    
    protected int framesAmount;
    
    protected int animationsAmount;
    protected int frameRate;
    
    protected int frameX;
    protected int frameY;
    
    protected int frameWidth;
    protected int frameHeight;
    protected int frameDistance;
    
    protected String name;
    protected int width;
    protected int height;
    
    protected String transformationSpriteFile;
    protected String bonusSpriteFile;
    
    protected double weight;
    
    protected boolean enemy;
    protected int energy;
    protected int damage;
    private int immunityToDamageType;
    protected int damageType;
    protected int score;
    
    protected int attack1Duration;
    protected int attack2Duration;
    
    protected String attack1SpriteFile;
    protected String attack2SpriteFile;
    
    protected int attackPause;
    
    protected int[] aiList = new int[10];
    
    protected int maxJump;
    protected double maxSpeed;
    
    protected int color; // index to a color in the color palette
    
    protected boolean obstacle;
    protected boolean boss;
    protected boolean tileCheck;
    
    protected boolean wallUp;
    protected boolean wallDown;
    protected boolean wallLeft;
    protected boolean wallRight;
    
    protected int destruction; // effect?
    
    protected boolean key;
    protected boolean shakes;
    
    protected int parallaxFactor;
    
    private boolean isPlayerSprite;
    
    protected boolean randomSoundFrequency;
    protected boolean glide;        // Sprite can glide, like Pekka
    protected boolean alwaysBonus; // Always drop bonus
    
    protected boolean swim;
    
    protected BufferedImage image;
    
    protected int placedAmount = 0;
    
    public void setImage(BufferedImage img) {
        this.image = img;
    }
    
    public BufferedImage getImage() {
        return image;
    }
    
    protected List<BufferedImage> frameImages = new ArrayList<>();
    
    public void setPlayerSprite(boolean is) {
        isPlayerSprite = is;
    }
    
    public boolean isPlayerSprite() {
        return isPlayerSprite;
    }
    
    public int getType() {
        return type;
    }
    
    public void setType(int type) {
        this.type = type;
    }
    
    public String getTextureName() {
        return imageFile;
    }
    
    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;

        imageFileIdentifier = imageFile + color;
    }
    
    public String getFilename() {
        return filename;
    }
    
    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    public int getAnimationsAmount() {
        return animationsAmount;
    }
    
    public void setAnimationsAmount(int animationsAmount) {
        this.animationsAmount = animationsAmount;
    }
    
    public int getFrameRate() {
        return frameRate;
    }
    
    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
    }
    
    public int getFrameX() {
        return frameX;
    }
    
    public void setFrameX(int frameX) {
        this.frameX = frameX;
    }
    
    public int getFrameY() {
        return frameY;
    }
    
    public void setFrameY(int frameY) {
        this.frameY = frameY;
    }
    
    public int getFrameWidth() {
        return frameWidth;
    }
    
    public void setFrameWidth(int frameWidth) {
        this.frameWidth = frameWidth;
    }
    
    public int getFrameHeight() {
        return frameHeight;
    }
    
    public void setFrameHeight(int frameHeight) {
        this.frameHeight = frameHeight;
    }
    
    public int getFrameDistance() {
        return frameDistance;
    }
    
    public void setFrameDistance(int frameDistance) {
        this.frameDistance = frameDistance;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getWidth() {
        return width;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public int getHeight() {
        return height;
    }

    @Override
    public String getImageFile() {
        return imageFile;
    }

    @Override
    public String getImageFileIdentifier() {
        return imageFileIdentifier;
    }

    @Override
    public void setSpecialImageFileIdentifier(String identifier){
        this.imageFileIdentifier = identifier;
    }

    public void setHeight(int height) {
        this.height = height;
    }
    
    public String getTransformationSpriteFile() {
        return transformationSpriteFile;
    }
    
    public void setTransformationSpriteFile(String transformationSpriteFile) {
        this.transformationSpriteFile = transformationSpriteFile;
    }
    
    public String getBonusSpriteFile() {
        return bonusSpriteFile;
    }
    
    public void setBonusSpriteFile(String bonusSpriteFile) {
        this.bonusSpriteFile = bonusSpriteFile;
    }
    
    public double getWeight() {
        return weight;
    }
    
    public void setWeight(double weight) {
        this.weight = weight;
    }
    
    public boolean isEnemy() {
        return enemy;
    }
    
    public void setEnemy(boolean enemy) {
        this.enemy = enemy;
    }
    
    public int getEnergy() {
        return energy;
    }
    
    public void setEnergy(int energy) {
        this.energy = energy;
    }
    
    public int getDamage() {
        return damage;
    }
    
    public void setDamage(int damage) {
        this.damage = damage;
    }
    
    public int getDamageType() {
        return damageType;
    }
    
    public void setDamageType(int damageType) {
        this.damageType = damageType;
    }
    
    public int getImmunityToDamageType() {
        return immunityToDamageType;
    }
    
    public void setImmunityToDamageType(int immunityToDamageType) {
        this.immunityToDamageType = immunityToDamageType;
    }
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public int getAttack1Duration() {
        return attack1Duration;
    }
    
    public void setAttack1Duration(int attack1Duration) {
        this.attack1Duration = attack1Duration;
    }
    
    public int getAttack2Duration() {
        return attack2Duration;
    }
    
    public void setAttack2Duration(int attack2Duration) {
        this.attack2Duration = attack2Duration;
    }
    
    public String getAttack1SpriteFile() {
        return attack1SpriteFile;
    }
    
    public void setAttack1SpriteFile(String attack1SpriteFile) {
        this.attack1SpriteFile = attack1SpriteFile;
    }
    
    public String getAttack2SpriteFile() {
        return attack2SpriteFile;
    }
    
    public void setAttack2SpriteFile(String attack2SpriteFile) {
        this.attack2SpriteFile = attack2SpriteFile;
    }
    
    public int getAttackPause() {
        return attackPause;
    }
    
    public void setAttackPause(int attackPause) {
        this.attackPause = attackPause;
    }
    
    public int[] getAiList() {
        return aiList;
    }
    
    public void setAiList(int[] aiList) {
        this.aiList = aiList;
    }
    
    public int getMaxJump() {
        return maxJump;
    }
    
    public void setMaxJump(int maxJump) {
        this.maxJump = maxJump;
    }
    
    public double getMaxSpeed() {
        return maxSpeed;
    }
    
    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }
    
    public int getColor() {
        return color;
    }
    
    public void setColor(int color) {
        this.color = color;
    }
    
    public boolean isObstacle() {
        return obstacle;
    }
    
    public void setObstacle(boolean obstacle) {
        this.obstacle = obstacle;
    }
    
    public boolean isBoss() {
        return boss;
    }
    
    public void setBoss(boolean boss) {
        this.boss = boss;
    }
    
    public boolean isTileCheck() {
        return tileCheck;
    }
    
    public void setTileCheck(boolean tileCheck) {
        this.tileCheck = tileCheck;
    }
    
    public boolean isWallUp() {
        return wallUp;
    }
    
    public void setWallUp(boolean wallUp) {
        this.wallUp = wallUp;
    }
    
    public boolean isWallDown() {
        return wallDown;
    }
    
    public void setWallDown(boolean wallDown) {
        this.wallDown = wallDown;
    }
    
    public boolean isWallLeft() {
        return wallLeft;
    }
    
    public void setWallLeft(boolean wallLeft) {
        this.wallLeft = wallLeft;
    }
    
    public boolean isWallRight() {
        return wallRight;
    }
    
    public void setWallRight(boolean wallRight) {
        this.wallRight = wallRight;
    }
    
    public int getDestruction() {
        return destruction;
    }
    
    public void setDestruction(int destruction) {
        this.destruction = destruction;
    }
    
    public boolean isKey() {
        return key;
    }
    
    public void setKey(boolean key) {
        this.key = key;
    }
    
    public boolean isShakes() {
        return shakes;
    }
    
    public void setShakes(boolean shakes) {
        this.shakes = shakes;
    }
    
    public int getParallaxFactor() {
        return parallaxFactor;
    }
    
    public void setParallaxFactor(int parallaxFactor) {
        this.parallaxFactor = parallaxFactor;
    }
    
    public boolean isRandomSoundFrequency() {
        return randomSoundFrequency;
    }
    
    public void setRandomSoundFrequency(boolean randomSoundFrequency) {
        this.randomSoundFrequency = randomSoundFrequency;
    }
    
    public boolean isGlide() {
        return glide;
    }
    
    public void setGlide(boolean glide) {
        this.glide = glide;
    }
    
    public boolean isAlwaysBonus() {
        return alwaysBonus;
    }
    
    public void setAlwaysBonus(boolean alwaysBonus) {
        this.alwaysBonus = alwaysBonus;
    }
    
    public boolean isSwim() {
        return swim;
    }
    
    public void setSwim(boolean swim) {
        this.swim = swim;
    }
    
    public int getFramesAmount() {
        return framesAmount;
    }
    
    public void setFramesAmount(int framesAmount) {
        this.framesAmount = framesAmount;
    }
    
    public int getPlacedAmount() {
        return placedAmount;
    }

    public void setPlacedAmount(int amount){
        this.placedAmount = amount;
    }
    
    public void increasePlacedAmount() {
        placedAmount++;
    }
    
    public void decreasePlacedAmount() {
        if (placedAmount - 1 >= 0) placedAmount--;
    }
}
