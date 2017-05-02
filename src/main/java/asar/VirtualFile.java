package asar;

import java.util.Arrays;
import java.util.Objects;

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

    public String getPath() {
        return path;
    }

    public int getOffset() {
        return offset;
    }

    public int getSize() {
        return size;
    }

    public byte[] getBytes() {
        return Arrays.copyOfRange(asar.bytes, offset, offset+size);
    }
}
