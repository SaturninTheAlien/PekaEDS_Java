package pk2.sprite.io;

import java.io.IOException;

public final class UnsupportedSpriteFormatException extends IOException {
    public UnsupportedSpriteFormatException(String formatString) {
        super("Unsupported sprite format: " + formatString + "");
    }
}
