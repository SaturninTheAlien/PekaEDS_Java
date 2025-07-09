package pk2.util;

import java.io.*;
import java.util.Map;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;

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
        for (int i = 0; i < length - 1; i++) {
            if (i < string.length()) {
                out.writeByte(string.charAt(i));
            } else {
                out.writeByte(0);
            }
        }
        
        out.writeByte(0); // Terminate the string with \0
    }
    
    /*private static void writeStringAsBytes(DataOutputStream out, String string) throws IOException {
        for (char c : string.toCharArray()) {
            out.writeByte(c);
        }
    }*/
    
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

    public static JSONObject readCBOR(DataInputStream in) throws IOException{
        int bufferSize = Integer.reverseBytes(in.readInt());
       
        byte[] buffer  = new byte[(int)bufferSize];
        in.read(buffer);

        CBORFactory cborFactory = new CBORFactory();
        ObjectMapper objectMapper = new ObjectMapper(cborFactory);

        @SuppressWarnings("unchecked")
        Map<String, Object> cborMap = objectMapper.readValue(new ByteArrayInputStream(buffer), Map.class);
        return new JSONObject(cborMap);
    }

    public static void writeCBOR(DataOutputStream out, JSONObject jsonObject) throws IOException{
        
        CBORFactory cborFactory = new CBORFactory();
        ObjectMapper cborMapper = new ObjectMapper(cborFactory);

        // Convert JSONObject to CBOR
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        cborMapper.writeValue(outputStream, jsonObject.toMap());

        byte[] buffer = outputStream.toByteArray();

        int size = Integer.reverseBytes((int)buffer.length);
        
        out.writeInt(size);
        out.write(buffer);
    }
}
