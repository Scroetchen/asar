package asar;

public class AsarException extends RuntimeException {
    AsarException(String msg) {
        super(msg);
    }

    AsarException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
