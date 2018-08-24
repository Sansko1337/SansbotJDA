package nl.imine.vaccine.exception;

public class PackageLoadFailedException extends RuntimeException {

    public PackageLoadFailedException() {
    }

    public PackageLoadFailedException(String message) {
        super(message);
    }

    public PackageLoadFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public PackageLoadFailedException(Throwable cause) {
        super(cause);
    }

    public PackageLoadFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
