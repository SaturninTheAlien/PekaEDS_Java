package pk2.sprite.io;

import pk2.sprite.PK2Sprite;

import java.io.*;
import java.util.Arrays;

public class SpriteIO {
    private static final int[] ID_1_1 = {0x31, 0x2E, 0x31, 0x00};
    private static final int[] ID_1_2 = {0x31, 0x2E, 0x32, 0x00};
    private static final int[] ID_1_3 = {0x31, 0x2E, 0x33, 0x00};

    private static final SpriteReader reader1_1 = new SpriteReader11();
    private static final SpriteReader reader1_2 = new SpriteReader12();
    private static final SpriteReader reader1_3 = new SpriteReader13Old();
    
    private static final SpriteReader13 reader1_3_fixed = new SpriteReader13();
    private static final SpriteReaderJson reader_json = new SpriteReaderJson();
    private static final SpriteWriterJson writer_json = new SpriteWriterJson();


    public static SpriteReader getSpriteReader(File file) throws IOException {
        String filename = file.getName();
        if (filename.endsWith(".spr2")) {
            return reader_json;
        } else if (filename.endsWith(".spr")) {
            DataInputStream dis = new DataInputStream(new FileInputStream(file));

            int[] id = new int[4];
            for (int i = 0; i < id.length; i++) {
                id[i] = dis.readByte() & 0xFF;
            }

            dis.close();

            if (Arrays.equals(ID_1_3, id)) {
                return reader1_3;
            } else if (Arrays.equals(ID_1_2, id)) {
                return reader1_2;
            } else if (Arrays.equals(ID_1_1, id)) {
                return reader1_1;
            } else {
                throw new UnsupportedSpriteFormatException(Arrays.toString(id));
            }
        }

        throw new IOException("Unable to recognize file as Pekka Kana 2 sprite.");
    }


    public static PK2Sprite loadSpriteFile(File file) throws IOException{
        if(!file.exists()){
            throw new FileNotFoundException("Sprite \""+file.toString()+"\" not found!");
        }

        String filename = file.getName();
        if(filename.endsWith(".spr2")){
            return reader_json.readSpriteFile(file);
        }
        else if(filename.endsWith(".spr")){
            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            int[] id = new int[4];
            for (int i = 0; i < id.length; i++) {
                id[i] = dis.readByte() & 0xFF;
            }
            dis.close();

            if(Arrays.equals(ID_1_3, id)){
                return reader1_3_fixed.load(file);
            }
            else{
                throw new UnsupportedSpriteFormatException(Arrays.toString(id));
            }
        }

        throw new IOException("Unable to recognize file as Pekka Kana 2 sprite.");
    }
    

    public static void saveSprite(PK2Sprite sprite, File file) throws IOException{

        if(file.getName().endsWith(".spr")){
            file = new File(file.getAbsolutePath()+"2");
        }
        else if(!file.getName().endsWith(".spr2")){
            file = new File(file.getAbsolutePath()+".spr2");
        }

        //sprite.setImage(null);

        //TODO
        //Replace obsolete sprite features here!

        writer_json.save(sprite, file);
    }
}
