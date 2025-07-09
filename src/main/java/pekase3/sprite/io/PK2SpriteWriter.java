package pekase3.sprite.io;

import java.io.File;
import java.io.IOException;

import pk2.sprite.PK2Sprite;

public abstract class PK2SpriteWriter {
    public abstract void save(PK2Sprite sprite, File file) throws IOException;
}
