package pekase3.util;

import java.io.*;
import java.util.Arrays;

public final class PK2FileUtils {
    private PK2FileUtils() {}
    
    /**
     *  Reads a "PK2 string" from an InputStream.
     *
     *  Pekka Kana 2 pads strings with 0xCC and 0xCD.
     *  This method reads and cleans such strings.
     *
     * @param in
     * @param length
     * @return
     */
    public static String readString(DataInputStream in, int length) throws IOException {
        var sb = new StringBuilder();
        
        // Read all the chars first.
        var chars = new char[length];
        for (int i = 0; i < length; i++) {
            chars[i] = (char) (in.readByte() & 0xFF);
        }
        
        // Then process them down here. It's important to read every char first, so that all the data is read. If you return from the first loop you might not read the whole LENGTH of data and mess up the FileStream.
        for (char c : chars) {
            // Some strings are padded with 0xCC and 0xCD. Probably because it was compiled in debug mode? No idea.
            if (c != 0x0 && c != 0xCC && c != 0xCD) {
                sb.append(c);
            } else {
                return sb.toString().trim();
            }
        }
        
        return sb.toString().trim();
    }
    
    public static void writeString(DataOutputStream out, String string, int length) throws IOException {
        for (int i = 0; i < length; i++) {
            if (i < string.length()) {
                out.writeByte(string.charAt(i));
            } else if (i == string.length()) {
                out.writeByte(0);
            }  else {
                out.writeByte(0xCC);
            }
        }
    }
    
    private static void writeStringAsBytes(DataOutputStream out, String string) throws IOException {
        for (char c : string.toCharArray()) {
            out.writeByte(c);
        }
    }
    
    /**
     *  Reads a "PK2 integer" from an InputStream.
     *
     *  Some integers are stored as 8 length strings, others not.
     *  For those that are stored like that this function reads and converts them to an integer.
     *
     * @param in
     * @return The stored integer.
     */
    public static int readInt(DataInputStream in) throws IOException {
        var str = readString(in, 8);
        
        return Integer.parseInt(str);
    }
    
    public static void writeInt(DataOutputStream out, int value) throws IOException {
        var valueStr = Integer.toString(value);
        
        for (int i = 0; i < 8; i++) {
            if (i < valueStr.length()) {
                out.writeByte(valueStr.charAt(i));
            } else {
                out.writeByte(0);
            }
        }
    }
}
