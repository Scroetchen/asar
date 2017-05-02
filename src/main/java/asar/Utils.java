package asar;

import java.io.*;

class Utils {
    static byte[] readAllBytes(File file) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try(FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int read;
            while((read = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }
        }
        return baos.toByteArray();
    }

    static void writeBytes(File file, byte[] bytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try(FileOutputStream fos = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int read;
            while((read = bais.read(buffer)) != -1) {
                fos.write(buffer, 0, read);
            }
        }
    }
}
