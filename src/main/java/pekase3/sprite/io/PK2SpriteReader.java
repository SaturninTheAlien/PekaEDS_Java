package pekase3.sprite.io;

import java.io.File;
import java.io.IOException;

import pk2.sprite.PK2Sprite;
import pekase3.util.UnknownSpriteFormatException;

public abstract class PK2SpriteReader {
    
    public abstract PK2Sprite load(File filename, String gfxPath) throws IOException, UnknownSpriteFormatException;
    public abstract PK2Sprite load(File filename) throws IOException, UnknownSpriteFormatException;
}
