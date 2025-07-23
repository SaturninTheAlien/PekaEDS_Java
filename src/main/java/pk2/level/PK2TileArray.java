package pk2.level;

import java.awt.Rectangle;
import java.util.Arrays;

public class PK2TileArray {
    private int mWidth;
    private int mHeight;

    private int[] mArray;

    public PK2TileArray(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;

        this.mArray = new int[width * height];

        for (int i = 0; i < this.mArray.length; ++i) {
            this.mArray[i] = 255;
        }
    }

    /**
     * 
     * Copy constructor
     */
    public PK2TileArray(PK2TileArray other){
        this.mWidth = other.mWidth;
        this.mHeight = other.mHeight;

        this.mArray = Arrays.copyOf(other.mArray, other.mArray.length);

    }

    public static PK2TileArray singleTile(int id){
        PK2TileArray a  = new PK2TileArray(1, 1);
        a.set(0, 0, id);
        return a;
    }


    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public int get(int posX, int posY) {
        if (posX >= 0 && posX < mWidth && posY >= 0 && posY < mHeight) {
            return this.mArray[this.mWidth * posY + posX];
        }

        return 255;
    }

    public void set(int posX, int posY, int value) {
        this.mArray[this.mWidth * posY + posX] = value;
    }

    public int getByIndex(int index) {
        return this.mArray[index];
    }

    public void setByIndex(int index, int value) {
        this.mArray[index] = value;
    }

    public int size() {
        return this.mArray.length;
    }

    public void removeID(int id) {
        for (int i = 0; i < this.mArray.length; ++i) {
            if (this.mArray[i] == 255) continue;
            else if (this.mArray[i] == id) {
                this.mArray[i] = 255;
            } else if (this.mArray[i] > id) {
                this.mArray[i] -= 1;
            }
        }
    }

    public Rectangle calculateOffsets() {
        int startX = this.mWidth;
        int width = 0;
        int startY = this.mHeight;
        int height = 0;

        for (int y = 0; y < this.mHeight; y++) {

            for (int x = 0; x < this.mWidth; x++) {

                if (this.get(x, y) != 255) {
                    if (x < startX) {
                        startX = x;
                    }

                    if (y < startY) {
                        startY = y;
                    }

                    if (x > width) {
                        width = x;
                    }

                    if (y > height) {
                        height = y;
                    }
                }
            }
        }

        if (width < startX || height < startY) {
            startX = 0;
            startY = 0;

            height = 1;
            width = 1;
        }

        return new Rectangle(startX, startY, width - startX, height - startY);
    }

    public int countTiles(int id) {
        int result = 0;
        for (int i = 0; i < this.mArray.length; ++i) {
            if (this.mArray[i] == id) {
                ++result;
            }
        }
        return result;
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();

        //TODO

        builder.append("PK2 Tile array ");
        builder.append(this.mWidth);
        builder.append("x");
        builder.append(this.mHeight);

        return builder.toString();
    }

}
