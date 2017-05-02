package asar;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a file inside an {@link AsarArchive}
 */
public class VirtualFile {
    private final AsarArchive asar;
    private final String path;
    private final int offset, size;

    VirtualFile(AsarArchive asar, String path, int offset, int size) {
        this.asar = asar;
        this.path = path;
        this.offset = offset;
        this.size = size;
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof VirtualFile) {
            VirtualFile o = (VirtualFile)other;
            return Objects.equals(o.asar, asar) && Objects.equals(o.path, path);
        }
        return false;
    }

    @Override
    public String toString() {
        return "AsarFile[path = " + path + ", offset = " + offset + ", size = " + size + "]";
    }

    /**
     * Returns the path inside the asar archive of this file
     *
     * @return The path
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the offset inside the sar archive of this file's contents
     *
     * @return The offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Returns the size of this file, in bytes
     *
     * @return The size
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the bytes of this file
     *
     * Be aware they are copied on every call to this method
     *
     * @return The bytes
     */
    public byte[] getBytes() {
        return Arrays.copyOfRange(asar.bytes, offset, offset+size);
    }
}
