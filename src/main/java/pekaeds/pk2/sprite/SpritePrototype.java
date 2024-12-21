package pekaeds.pk2.sprite;

import java.awt.image.BufferedImage;

public interface SpritePrototype {
    
    public static final int TYPE_BACKGROUND = 5;
    public static final int TYPE_FOREGROUND = 6;
    public static final int TYPE_CHARACTER = 1;


    public int getType();
    public String getFilename();
    public String getName();

    public int getWidth();
    public int getHeight();

    public String getImageFile();

    public String getImageFileIdentifier();
    public void setSpecialImageFileIdentifier(String identifier);

    public int getColor();
    public int getFrameX();
    public int getFrameY();
    public int getFrameWidth();
    public int getFrameHeight();

    public void setFrameX(int frameX);
    public void setFrameY(int frameY);
    public void setFrameWidth(int frameWidth);
    public void setFrameHeight(int frameHeight);

    public void setPlayerSprite(boolean is);
    public boolean isPlayerSprite();
    public BufferedImage getImage();
    public void setImage(BufferedImage img);

    public int getPlacedAmount();
    public void setPlacedAmount(int amount);

    public void increasePlacedAmount();
    public void decreasePlacedAmount();
}
