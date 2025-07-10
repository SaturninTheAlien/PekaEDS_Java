package pk2.sprite;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.json.JSONArray;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class PK2Sprite implements SpritePrototype {
    public static final byte[] VERSION_13 = {0x31, 0x2E, 0x33, 0x00};
    
    private final ChangeEvent changeEvent = new ChangeEvent(this);
    
    private List<ChangeListener> changeListeners = new ArrayList<>();
    
    protected String filename = "";
    
    private int type;
    
    protected String imageFile = "";

    // This string contains whatever is contained in imageFile and color
    // Let's say the image file is "rooster.bmp" and the color is 64, so imageFileIdentifier will be rooster.bmp64
    // This is used to cache the correctly recolored/palette shifted sprite sheet
    protected String imageFileIdentifier = "";
    
    protected String[] soundFiles = new String[7];
    
    protected List<PK2SpriteAnimation> animationsList = new ArrayList<>();
    
    private List<BufferedImage> framesList = new ArrayList<>();
    
    private int framesAmount;
    
    private int animationsAmount; // not used
    private int frameRate;
    
    private int frameX;
    private int frameY;
    
    private int frameWidth;
    private int frameHeight;
    private int frameDistance;
    
    private String name;
    private int width;
    private int height;
    
    private String transformationSpriteFile = "";
    private String bonusSpriteFile = "";
    
    private double weight;
    
    private boolean enemy;
    private int energy;
    private int damage;
    private int immunityToDamageType;
    private int damageType;
    private int score;
    
    private int attack1Duration;
    private int attack2Duration;
    
    private String attack1SpriteFile = "";
    private String attack2SpriteFile = "";
    
    private int attackPause;
    
    private ArrayList<Integer> aiList = new ArrayList<Integer>();
    
    private int maxJump;
    private double maxSpeed;
    
    private int color; // index to a color in the color palette
    
    private boolean obstacle;
    private boolean boss;   // Not used
    private boolean tileCheck;
    
    private boolean wallUp;
    private boolean wallDown;
    private boolean wallLeft;
    private boolean wallRight;

    private int destructionEffect;
    private boolean indestructible = false;
    
    private boolean key;
    private boolean shakes;
    
    private int parallaxFactor;
    
    private boolean isPlayerSprite;
    
    private int bonusAmount;
    private int info_id;
    
    protected boolean randomSoundFrequency;
    protected boolean glide;        // Sprite can glide, like Pekka
    protected boolean alwaysBonus; // Always drop bonus
    
    protected boolean swim;
    
    protected BufferedImage image;
    
    // Greta Engine spr2 properties
    private boolean alwaysActive;
    private double deadWeight = 0.0;
    private boolean hasDeadWeight = false;
    
    private JSONArray commands = new JSONArray();


    //New GE features
    private int ambientEffect = 0;
    private int blendMode = 0;
    private int blendAlpha = 0;

    private Point2D attack1Offest;
    private Point2D attack2Offset;
    private Point2D playerDetection;

    
    public boolean isAlwaysActive() {
        return alwaysActive;
    }
    
    public void setAlwaysActive(boolean always) {
        this.alwaysActive = always;
    }
    
    public double getDeadWeight() {
        return deadWeight;
    }
    
    public void setDeadWeight(double deadWeight) {
        this.deadWeight = deadWeight;
        
        hasDeadWeight = true;
    }
    
    public void setHasDeadWeight(boolean has) {
        hasDeadWeight = has;
    }
    
    public JSONArray getCommands() { return commands; }
    public void setCommands(JSONArray commands) {
        this.commands = commands;
    }
    
    public boolean hasDeadWeight() { return hasDeadWeight; }
    
    public int getLoadTime() {
        return loadTime;
    }
    
    public void setLoadTime(int loadTime) {
        this.loadTime = loadTime;
    }
    
    private int loadTime;
    
    public int getSoundFrequency() {
        return soundFrequency;
    }
    
    public void setSoundFrequency(int soundFrequency) {
        this.soundFrequency = soundFrequency;
    }
    
    private int soundFrequency;
    
    public int getBonusAmount() {
        return bonusAmount;
    }
    
    public void setBonusAmount(int bonusAmount) {
        this.bonusAmount = bonusAmount;
    }
    
    public PK2Sprite() {        
        for (int i = 0; i < 20; i++) {
            animationsList.add(new PK2SpriteAnimation(new byte[10], 0, false));
        }
    }
    
    public void setImage(BufferedImage img) {
        this.image = img;
    }
    
    public BufferedImage getImage() {
        return image;
    }
    
    protected List<BufferedImage> frameImages = new ArrayList<>();
    
    public void setPlayerSprite(boolean is) {
        isPlayerSprite = is;
        
        fireChanges();
    }
    
    public boolean isPlayerSprite() {
        return isPlayerSprite;
    }
    
    public int getType() {
        return type;
    }
    
    public void setType(int type) {
        this.type = type;
        
        fireChanges();
    }
    
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

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;

        imageFileIdentifier = imageFile + color;
        
        fireChanges();
    }
    
    public String getFilename() {
        return filename;
    }
    
    public void setFilename(String filename) {
        this.filename = filename;
        
        fireChanges();
    }
    
    public int getAnimationsAmount() {
        return animationsAmount;
    }
    
    public void setAnimationsAmount(int animationsAmount) {
        this.animationsAmount = animationsAmount;
        
        fireChanges();
    }
    
    public int getFrameRate() {
        return frameRate;
    }
    
    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
        
        fireChanges();
    }
    
    public int getFrameX() {
        return frameX;
    }
    
    public void setFrameX(int frameX) {
        this.frameX = frameX;
        
        fireChanges();
    }
    
    public int getFrameY() {
        return frameY;
    }
    
    public void setFrameY(int frameY) {
        this.frameY = frameY;
        
        fireChanges();
    }
    
    public int getFrameWidth() {
        return frameWidth;
    }
    
    public void setFrameWidth(int frameWidth) {
        this.frameWidth = frameWidth;
        
        fireChanges();
    }
    
    public int getFrameHeight() {
        return frameHeight;
    }
    
    public void setFrameHeight(int frameHeight) {
        this.frameHeight = frameHeight;
        
        fireChanges();
    }
    
    public int getFrameDistance() {
        return frameDistance;
    }
    
    public void setFrameDistance(int frameDistance) {
        this.frameDistance = frameDistance;
        
        fireChanges();
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
        
        fireChanges();
    }
    
    public int getWidth() {
        return width;
    }
    
    public void setWidth(int width) {
        this.width = width;
        
        fireChanges();
    }
    
    public int getHeight() {
        return height;
    }
    
    public void setHeight(int height) {
        this.height = height;
        
        fireChanges();
    }
    
    public String getTransformationSpriteFile() {
        return transformationSpriteFile;
    }
    
    public void setTransformationSpriteFile(String transformationSpriteFile) {
        this.transformationSpriteFile = transformationSpriteFile;
        
        fireChanges();
    }
    
    public String getBonusSpriteFile() {
        return bonusSpriteFile;
    }
    
    public void setBonusSpriteFile(String bonusSpriteFile) {
        this.bonusSpriteFile = bonusSpriteFile;
        
        fireChanges();
    }
    
    public double getWeight() {
        return weight;
    }
    
    public void setWeight(double weight) {
        this.weight = weight;
        
        fireChanges();
    }
    
    public boolean isEnemy() {
        return enemy;
    }
    
    public void setEnemy(boolean enemy) {
        this.enemy = enemy;
        
        fireChanges();
    }
    
    public int getEnergy() {
        return energy;
    }
    
    public void setEnergy(int energy) {
        this.energy = energy;
        
        fireChanges();
    }
    
    public int getDamage() {
        return damage;
    }
    
    public void setDamage(int damage) {
        this.damage = damage;
        
        fireChanges();
    }
    
    public int getDamageType() {
        return damageType;
    }
    
    public void setDamageType(int damageType) {
        this.damageType = damageType;
        
        fireChanges();
    }
    
    public int getImmunityToDamageType() {
        return immunityToDamageType;
    }
    
    public void setImmunityToDamageType(int immunityToDamageType) {
        this.immunityToDamageType = immunityToDamageType;
        
        fireChanges();
    }
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
        
        fireChanges();
    }
    
    public int getAttack1Duration() {
        return attack1Duration;
    }
    
    public void setAttack1Duration(int attack1Duration) {
        this.attack1Duration = attack1Duration;
        
        fireChanges();
    }
    
    public int getAttack2Duration() {
        return attack2Duration;
    }
    
    public void setAttack2Duration(int attack2Duration) {
        this.attack2Duration = attack2Duration;
        
        fireChanges();
    }
    
    public String getAttack1SpriteFile() {
        return attack1SpriteFile;
    }
    
    public void setAttack1SpriteFile(String attack1SpriteFile) {
        this.attack1SpriteFile = attack1SpriteFile;
        
        fireChanges();
    }
    
    public String getAttack2SpriteFile() {
        return attack2SpriteFile;
    }
    
    public void setAttack2SpriteFile(String attack2SpriteFile) {
        this.attack2SpriteFile = attack2SpriteFile;
        
        fireChanges();
    }
    
    public int getAttackPause() {
        return attackPause;
    }
    
    public void setAttackPause(int attackPause) {
        this.attackPause = attackPause;
        
        fireChanges();
    }
    
    public ArrayList<Integer> getAiList() {
        return aiList;
    }
    
    public void setAiList(ArrayList<Integer> aiList) {
        this.aiList = aiList;
        
        fireChanges();
    }
    
    public int getMaxJump() {
        return maxJump;
    }
    
    public void setMaxJump(int maxJump) {
        this.maxJump = maxJump;
        
        fireChanges();
    }
    
    public double getMaxSpeed() {
        return maxSpeed;
    }
    
    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
        
        fireChanges();
    }
    
    public int getColor() {
        return color;
    }
    
    public void setColor(int color) {
        this.color = color;
        
        fireChanges();
    }
    
    public boolean isObstacle() {
        return obstacle;
    }
    
    public void setObstacle(boolean obstacle) {
        this.obstacle = obstacle;
        
        fireChanges();
    }
    
    public boolean isBoss() {
        return boss;
    }
    
    public void setBoss(boolean boss) {
        this.boss = boss;
        
        fireChanges();
    }
    
    public boolean isTileCheck() {
        return tileCheck;
    }
    
    public void setTileCheck(boolean tileCheck) {
        this.tileCheck = tileCheck;
        
        fireChanges();
    }
    
    public boolean isWallUp() {
        return wallUp;
    }
    
    public void setWallUp(boolean wallUp) {
        this.wallUp = wallUp;
        
        fireChanges();
    }
    
    public boolean isWallDown() {
        return wallDown;
    }
    
    public void setWallDown(boolean wallDown) {
        this.wallDown = wallDown;
        
        fireChanges();
    }
    
    public boolean isWallLeft() {
        return wallLeft;
    }
    
    public void setWallLeft(boolean wallLeft) {
        this.wallLeft = wallLeft;
        
        fireChanges();
    }
    
    public boolean isWallRight() {
        return wallRight;
    }
    
    public void setWallRight(boolean wallRight) {
        this.wallRight = wallRight;
        
        fireChanges();
    }
    
    public int getDestructionEffect() {
        return destructionEffect;
    }
    
    public void getDestructionEffect(int destruction) {
        this.destructionEffect = destruction;
        
        fireChanges();
    }

    public boolean isIndestructible(){
        return this.indestructible;
    }

    public void setIndestructible(boolean indestructible){
        this.indestructible = indestructible;

        fireChanges();
    }
    
    public boolean isKey() {
        return key;
    }
    
    public void setKey(boolean key) {
        this.key = key;
        
        fireChanges();
    }
    
    public boolean isShakes() {
        return shakes;
    }
    
    public void setShakes(boolean shakes) {
        this.shakes = shakes;
        
        fireChanges();
    }
    
    public int getParallaxFactor() {
        return parallaxFactor;
    }
    
    public void setParallaxFactor(int parallaxFactor) {
        this.parallaxFactor = parallaxFactor;
        
        fireChanges();
    }
    
    public boolean isRandomSoundFrequency() {
        return randomSoundFrequency;
    }
    
    public void setRandomSoundFrequency(boolean randomSoundFrequency) {
        this.randomSoundFrequency = randomSoundFrequency;
        
        fireChanges();
    }
    
    public boolean canGlide() {
        return glide;
    }
    
    public void setGlide(boolean glide) {
        this.glide = glide;
        
        fireChanges();
    }
    
    public boolean isAlwaysBonus() {
        return alwaysBonus;
    }
    
    public void setAlwaysBonus(boolean alwaysBonus) {
        this.alwaysBonus = alwaysBonus;
        
        fireChanges();
    }
    
    public boolean canSwim() {
        return swim;
    }
    
    public void setSwim(boolean swim) {
        this.swim = swim;
        
        fireChanges();
    }
    
    public int getFramesAmount() {
        return framesAmount;
    }
    
    public void setFramesAmount(int framesAmount) {
        this.framesAmount = framesAmount;
        
        fireChanges();
    }
    
    private void fireChanges() {
        for (var cl : changeListeners) {
            cl.stateChanged(changeEvent);
        }
    }
    
    public void addChangeListener(ChangeListener listener) {
        if (!changeListeners.contains(listener)) {
            changeListeners.add(listener);
        }
    }
    
    public void setSoundFile(String file, int number) {
        soundFiles[number] = file;
    }
    
    public String getSoundFile(int index) {
        return soundFiles[index];
    }
    
    public void setAnimationsList(List<PK2SpriteAnimation> animations) {
        this.animationsList = animations;
    }
    
    public List<PK2SpriteAnimation> getAnimationsList() {
        return animationsList;
    }
    
    public void setFramesList(List<BufferedImage> frames) {
        this.framesList = frames;
    }
    
    public List<BufferedImage> getFramesList() {
        return framesList;
    }

    public int getInfoID(){
        return this.info_id;
    }

    public void setInfoID(int info_id){
        this.info_id = info_id;

        fireChanges();
    }



    public int getAmbientEffect(){
        return ambientEffect;
    }

    public void setAmbientEffect(int effect){
        this.ambientEffect = effect;        
    }

    public int getBlendMode(){
        return blendMode;
    }

    public void setBlendMode(int mode){
        this.blendMode = mode;
    }

    public int getBlendAlpha(){
        return blendAlpha;
    }

    public void setBlendAlpha(int alpha){
        this.blendAlpha = alpha;
    }


    public Point2D getAttack1Offset(){
        return this.attack1Offest;
    }

    public Point2D getAttack2Offset(){
        return this.attack2Offset;
    }

    public Point2D getPlayerDetection(){
        return this.playerDetection;
    }


    public void setAttack1Offset(Point2D offset){
        this.attack1Offest = offset;
    }

    public void setAttack2Offset(Point2D offset){
        this.attack2Offset = offset;
    }

    public void setPlayerDetection(Point2D offset){
        this.playerDetection = offset;
    }



    /**
     * For level editor
     */

    int placedAmount = 0;
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
