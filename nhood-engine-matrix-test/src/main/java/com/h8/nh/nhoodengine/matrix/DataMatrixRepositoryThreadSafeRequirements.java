package com.h8.nh.nhoodengine.matrix;

/**
 * This interface defines thread safety requirements for DataMatrixRepository interface
 */
public interface DataMatrixRepositoryThreadSafeRequirements {

    void shouldNotLoseResourcesWhenThoseAreAddedConcurrently()
            throws DataMatrixRepositoryFailedException, InterruptedException;

    void shouldNotLoseResourcesWhenThoseAreAddedAndResolvedConcurrently()
            throws DataMatrixRepositoryFailedException, InterruptedException;

    void shouldResolveAllAlreadyAddedResources()
            throws DataMatrixRepositoryFailedException, InterruptedException;

    void shouldFindAllAlreadyAddedResources()
            throws DataMatrixRepositoryFailedException, InterruptedException;

    void shouldNotFindAnyRemovedResources()
            throws DataMatrixRepositoryFailedException, InterruptedException;
}
