package com.h8.nh.nhoodengine.matrix;

/**
 * This class is an exception that may be thrown by DataMatrixRepository
 */
public class DataDoesNotExistException extends Exception {

    public DataDoesNotExistException(
            final String message) {
        super(message);
    }

    public DataDoesNotExistException(
            final String message, final Throwable cause) {
        super(message, cause);
    }
}
