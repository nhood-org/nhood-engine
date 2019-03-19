package com.h8.nh.nhoodengine.core;

/**
 * This class is a general checked exception that may be thrown by DataFinder
 */
public final class DataFinderFailedException extends Exception {

    /**
     * A default exception constructor
     * @param message exception message
     */
    public DataFinderFailedException(final String message) {
        super(message);
    }

    /**
     * An exception constructor
     * @param message exception message
     * @param cause nested cause Throwable
     */
    public DataFinderFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
