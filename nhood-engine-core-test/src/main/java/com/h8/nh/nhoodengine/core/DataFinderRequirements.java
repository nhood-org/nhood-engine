package com.h8.nh.nhoodengine.core;

/**
 * This interface defines add basic requirements for DataFinder interface
 */
public interface DataFinderRequirements {

    void shouldThrowAnExceptionWhenCriteriaIsNull();

    void shouldThrowAnExceptionWhenCriteriaMetadataVectorIsNull();

    void shouldThrowAnExceptionWhenCriteriaMetadataVectorIsEmpty();

    void shouldThrowAnExceptionWhenCriteriaMetadataVectorSizeDoesNotMatch();

    void shouldThrowAnExceptionWhenCriteriaLimitIsNegative();

    void shouldReturnAnEmptyResultListWhenCriteriaLimitZero()
            throws DataFinderFailedException;

    void shouldReturnResultListOfLimitSizeWhenCriteriaLimitIsBelowDataSetSize()
            throws DataFinderFailedException;

    void shouldReturnWholeResultSetWhenCriteriaLimitHigherThanDataSetSize()
            throws DataFinderFailedException;

    void shouldReturnListOfClosestResultForAGivenMetadataVector()
            throws DataFinderFailedException;

    void shouldReturnListOfClosestResultForALowestPossibleMetadataVector()
            throws DataFinderFailedException;

    void shouldCalculateScoresOfClosestResultForAGivenMetadataVector()
            throws DataFinderFailedException;

    void shouldOrderClosestResultForAGivenMetadataVector()
            throws DataFinderFailedException;

    void shouldReturnListOfClosestResultForAHighestPossibleMetadataVector()
            throws DataFinderFailedException;

    void shouldReturnListOfClosestResultForAllZeroesMetadataVector()
            throws DataFinderFailedException;
}
