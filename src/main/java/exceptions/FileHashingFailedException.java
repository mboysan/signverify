package exceptions;

public class FileHashingFailedException extends Exception {
    public FileHashingFailedException(String message) {
        super(message);
    }

    public FileHashingFailedException(Throwable cause) {
        super(cause);
    }
}
