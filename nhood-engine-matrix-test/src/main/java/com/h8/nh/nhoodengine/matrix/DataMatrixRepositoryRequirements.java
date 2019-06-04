package com.h8.nh.nhoodengine.matrix;

/**
 * This interface defines basic requirements for DataMatrixRepository interface
 */
public interface DataMatrixRepositoryRequirements {

    void shouldNotAcceptNullResources();

    void shouldNotAcceptResourcesWithIllegalKeySize();

    void shouldNotAcceptResourcesWithNullKey();

    void shouldNotAcceptResourcesWithNullResource();

    void shouldAcceptNotDuplicateSameResourceWhenAddedMultipleTimes()
            throws DataMatrixRepositoryFailedException;

    void shouldAcceptTwoResourcesWithTheSameKey()
            throws DataMatrixRepositoryFailedException;

    void shouldReturnEmptyIteratorIfEmpty()
            throws DataMatrixRepositoryFailedException;

    void shouldReturnIteratorOfAllElements()
            throws DataMatrixRepositoryFailedException;

    void shouldReturnResourceOfAGivenKeyInTheVeryFirstChunkIfExists()
            throws DataMatrixRepositoryFailedException;

    void shouldReturnNoEmptyChunks()
            throws DataMatrixRepositoryFailedException;

    void shouldProperlyCalculateUnconditionalNextIndicator()
            throws DataMatrixRepositoryFailedException;

    void shouldProperlyCalculateConditionalNextIndicator()
            throws DataMatrixRepositoryFailedException;
}
