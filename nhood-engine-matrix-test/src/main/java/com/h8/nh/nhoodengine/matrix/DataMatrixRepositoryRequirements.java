package com.h8.nh.nhoodengine.matrix;

/**
 * This interface defines basic requirements for DataMatrixRepository interface
 */
public interface DataMatrixRepositoryRequirements {

    void shouldNotAcceptNullResources();

    void shouldNotAcceptResourcesWithIllegalKeySize();

    void shouldNotAcceptResourcesWithNullKey();

    void shouldNotAcceptResourcesWithNullResource();

    void shouldAcceptSameResourceWhenAddedMultipleTimes()
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

    void shouldFindAndReturnAddedData()
            throws DataMatrixRepositoryFailedException, DataDoesNotExistException;

    void shouldThrowAnExceptionWhenSearchedDataDoesNotExist()
            throws DataMatrixRepositoryFailedException, DataDoesNotExistException;

    void shouldThrowAnExceptionWhenSearchedDataWasRemoved()
            throws DataMatrixRepositoryFailedException, DataDoesNotExistException;

    void shouldRemoveAndReturnAddedData()
            throws DataMatrixRepositoryFailedException, DataDoesNotExistException;

    void shouldThrowAnExceptionWhenRemovedDataDoesNotExist()
            throws DataMatrixRepositoryFailedException, DataDoesNotExistException;

    void shouldThrowAnExceptionWhenRemovedDataWasRemoved()
            throws DataMatrixRepositoryFailedException, DataDoesNotExistException;

    void shouldNotReturnRemovedDataAsNeighbour()
            throws DataMatrixRepositoryFailedException, DataDoesNotExistException;
}
