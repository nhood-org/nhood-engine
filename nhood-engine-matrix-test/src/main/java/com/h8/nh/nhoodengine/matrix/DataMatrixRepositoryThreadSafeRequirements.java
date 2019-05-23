package com.h8.nh.nhoodengine.matrix;

/**
 * This interface defines thread safety requirements for DataMatrixRepository interface
 */
public interface DataMatrixRepositoryThreadSafeRequirements {

    void shouldNotLoseResourcesWhenThoseAreAddedConcurrently();

    void shouldNotLoseResourcesWhenThoseAreAddedAndResolvedConcurrently();

    void shouldResolveAllAlreadyAddedResources();
}
