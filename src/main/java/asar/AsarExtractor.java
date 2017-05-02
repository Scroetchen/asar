package asar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Extracts files from an {@link AsarArchive}
 */
public class AsarExtractor {
    private AsarExtractor(){}

    /**
     * Extracts a single file from an {@link AsarArchive}
     *
     * @see AsarExtractor#extract(AsarArchive, String, File)
     */
    public static void extract(AsarArchive asar, String filePath, String destination) throws IOException {
        extract(asar, filePath, new File(destination));
    }

    /**
     * Extracts a single file from an {@link AsarArchive}
     *
     * @param asar The source
     * @param filePath The path inside the asar file of the wanted file
     * @param destination The {@link File} to save the extracted file
     *
     * @throws IOException If there's an error writing the file
     * @throws IllegalArgumentException If the path to extract doesn't exist
     */
    public static void extract(AsarArchive asar, String filePath, File destination) throws IOException {
        if(asar == null || filePath == null || destination == null) throw new NullPointerException();
        destination.getParentFile().mkdirs();
        String[] path = filePath.split("/");
        JSONObject token = asar.getHeader().getJson();
        for(String s : path) {
            JSONObject obj1 = token.optJSONObject("files");
            if(obj1 == null) throw new IllegalArgumentException("JSONObject \"files\" not found");
            if((token = obj1.optJSONObject(s)) == null) throw new IllegalArgumentException("JSONObject \"" + s + "\" not found");
        }

        int size;
        int offset;
        try {
            size = token.getInt("size");
            offset = asar.getBaseOffset() + token.getInt("offset");
        } catch(JSONException e) {
            throw new IllegalArgumentException("Invalid ASAR header", e);
        }

        byte[] fileBytes = Arrays.copyOfRange(asar.getBytes(), offset, offset+size);

        Utils.writeBytes(destination, fileBytes);
    }

    /**
     * Extracts all the contents of an {@link AsarArchive} to a given folder
     *
     * @see AsarExtractor#extractAll(AsarArchive, File)
     */
    public static void extractAll(AsarArchive asar, String destination) throws IOException {
        extractAll(asar, new File(destination));
    }

    /**
     * Extracts all the contents of an {@link AsarArchive} to a given folder
     *
     * @param asar The source asar
     * @param destination The destination folder
     * @throws IOException If there's an error writing the files
     * @throws IllegalArgumentException If the destination is invalid
     */
    public static void extractAll(AsarArchive asar, File destination) throws IOException {
        if(asar == null || destination == null) throw new NullPointerException();
        if(destination.exists() && !destination.isDirectory())
            throw new IllegalArgumentException("destination must be a directory or not exist");
        for(VirtualFile f : asar) {
            File d = new File(destination, f.getPath());
            d.getParentFile().mkdirs();
            Utils.writeBytes(d, f.getBytes());
        }
    }
}
