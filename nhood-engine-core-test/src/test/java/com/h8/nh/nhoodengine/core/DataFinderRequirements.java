package com.h8.nh.nhoodengine.core;


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

    void shouldReturnListOfClosestResultForAHighestPossibleMetadataVector()
            throws DataFinderFailedException;

    void shouldReturnListOfClosestResultForAAllZeroesMetadataVector()
            throws DataFinderFailedException;
}
