package com.h8.nh.nhoodengine.core;


public interface DataFinderRequirements {

    void shouldThrowAnExceptionWhenCriteriaIsNull();

    void shouldThrowAnExceptionWhenCriteriaMetadataVectorIsNull();

    void shouldThrowAnExceptionWhenCriteriaMetadataVectorSizeDoesNotMatch();

    void shouldThrowAnExceptionWhenCriteriaLimitIsNegative();

    void shouldReturnAnEmptyResultListWhenCriteriaLimitZero();

    void shouldReturnResultListOfLimitSizeWhenCriteriaLimitIsBelowDataSetSize();

    void shouldReturnWholeResultSetWhenCriteriaLimitHigherThanDataSetSize();

    void shouldReturnListOfClosestResultForAGivenMetadataVector();

    void shouldReturnListOfClosestResultForALowestPossibleMetadataVector();

    void shouldReturnListOfClosestResultForAHighestPossibleMetadataVector();

    void shouldReturnListOfClosestResultForAAllZeroesMetadataVector();
}
