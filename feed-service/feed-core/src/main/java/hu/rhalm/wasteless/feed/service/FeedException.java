package hu.rhalm.wasteless.feed.service;

public class FeedException extends RuntimeException {
    public FeedException() { }

    public FeedException(String message) {
        super(message);
    }

    public FeedException(String message, Throwable cause) {
        super(message, cause);
    }

    public FeedException(Throwable cause) {
        super(cause);
    }

    public FeedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
