package com.h8.nh.nhoodengine.matrix;

/**
 * This class is a general checked exception that may be thrown by DataMatrixRepository
 */
public class DataMatrixRepositoryFailedException extends Exception {

    public DataMatrixRepositoryFailedException(
            final String message) {
        super(message);
    }

    public DataMatrixRepositoryFailedException(
            final String message, final Throwable cause) {
        super(message, cause);
    }
}
