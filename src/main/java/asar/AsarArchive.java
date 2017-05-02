package asar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.*;

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
        this.baseOffset = header.size+4;
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

    public String getPath() {
        return path;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public Header getHeader() {
        return header;
    }

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
                files.add(new VirtualFile(asar, path+"/"+key, Integer.parseInt(o.getString("offset")), o.getInt("size")));
            }
        }
    }

    public static class Header {
        private final int size;
        private final JSONObject json;

        Header(int size, JSONObject json) {
            this.size = size;
            this.json = json;
        }

        public int getSize() {
            return size;
        }

        public JSONObject getJson() {
            return json;
        }
    }


}
