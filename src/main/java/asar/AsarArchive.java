package asar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Represents a .asar file
 */
public class AsarArchive implements Iterable<VirtualFile> {
    private final String path;
    /*package*/ final byte[] bytes;
    private final Header header;
    private final int baseOffset;
    private final Set<VirtualFile> files = new HashSet<>();

    public AsarArchive(String path) throws IOException {
        this(new java.io.File(path));
    }

    public AsarArchive(File file) throws IOException {
        if(file == null) throw new NullPointerException();
        this.path = file.getAbsolutePath();
        this.bytes = Utils.readAllBytes(file);
        this.header = readHeader(bytes);
        this.baseOffset = header.size+8;
        try {
            files(this, "", header.json.getJSONObject("files"), files);
        } catch(JSONException ex) {
            throw new AsarException("Unable to build file set", ex);
        }
    }

    @Override
    public Iterator<VirtualFile> iterator() {
        return files.iterator();
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof AsarArchive) {
            AsarArchive o = (AsarArchive)other;
            return o.baseOffset == baseOffset && Objects.equals(path, o.path) && Arrays.equals(o.bytes, bytes);
        }
        return false;
    }

    /**
     * Returns the path of the loaded asar file
     *
     * @return The path
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the full contents of this file
     *
     * @return The contents
     */
    public byte[] getBytes() {
        return bytes;
    }

    /**
     * Returns the {@link Header} of this file
     *
     * @return The header
     */
    public Header getHeader() {
        return header;
    }

    /**
     * Returns the base offset for the files inside this file, in bytes
     *
     * @return The offset
     */
    public int getBaseOffset() {
        return baseOffset;
    }

    private static Header readHeader(byte[] bytes) {
        try {
            int headerSize = (int)(ByteBuffer.wrap(Arrays.copyOfRange(bytes, 4, 8)).order(ByteOrder.LITTLE_ENDIAN).getInt() & 0xFFFFFFFFL);
            return new Header(headerSize, new JSONObject(new String(Arrays.copyOfRange(bytes, 16, 16+headerSize), Charset.defaultCharset())));
        } catch(JSONException ex) {
            throw new AsarException("Invalid json", ex);
        }
    }

    private static void files(AsarArchive asar, String path, JSONObject obj, Set<VirtualFile> files) throws JSONException {
        Iterator<String> keys = obj.keys();
        while(keys.hasNext()) {
            String key = keys.next();
            JSONObject o = obj.getJSONObject(key);
            JSONObject filesObj = o.optJSONObject("files");
            if(filesObj != null) {
                files(asar, path + "/" + key, filesObj, files);
            } else {
                files.add(new VirtualFile(asar, path+"/"+key, Integer.parseInt(o.getString("offset"))+asar.baseOffset, o.getInt("size")));
            }
        }
    }

    /**
     * Represents the header of an asar file
     */
    public static class Header {
        private final int size;
        private final JSONObject json;

        Header(int size, JSONObject json) {
            this.size = size;
            this.json = json;
        }

        /**
         * Returns the size of the header, in bytes
         *
         * @return The size
         */
        public int getSize() {
            return size;
        }

        /**
         * Returns the {@link JSONObject} of this header
         *
         * @return The {@link JSONObject}
         */
        public JSONObject getJson() {
            return json;
        }
    }
}
