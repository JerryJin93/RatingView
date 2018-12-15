package com.jerryjin.ratingview.library;

public class OutOfRatingException extends Exception {

    public OutOfRatingException() {
        super();
    }

    public OutOfRatingException(String message) {
        super(message);
    }

    public OutOfRatingException(String message, Throwable cause) {
        super(message, cause);
    }

    public OutOfRatingException(Throwable cause) {
        super(cause);
    }
}
