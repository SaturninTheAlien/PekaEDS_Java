package pekase3.panels.spriteeditpane;

import java.awt.image.BufferedImage;
import java.util.List;

public interface SpriteFramesChangeListener {
    void framesChanged(List<BufferedImage> frames);
}
