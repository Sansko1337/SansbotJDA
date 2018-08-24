package nl.imine.vaccine.exception;

public class DependencyInstantiationException extends RuntimeException {

    public DependencyInstantiationException() {
    }

    public DependencyInstantiationException(String message) {
        super(message);
    }

    public DependencyInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DependencyInstantiationException(Throwable cause) {
        super(cause);
    }

    public DependencyInstantiationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
