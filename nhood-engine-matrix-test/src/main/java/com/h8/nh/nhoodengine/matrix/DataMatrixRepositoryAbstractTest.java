package com.h8.nh.nhoodengine.matrix;

import com.h8.nh.nhoodengine.core.DataResourceKey;
import org.junit.jupiter.api.BeforeEach;

/**
 * DataFinderAbstractTest is an abstract test class
 * that implements DataMatrixRepositoryRequirements.
 *
 * TODO!!!
 *
 * @param <K> a generic type of data metadata key vector. Extends {@link DataResourceKey}.
 * @param <D> a generic type of data resource.
 */
public abstract class DataMatrixRepositoryAbstractTest<K extends DataResourceKey, D> implements DataMatrixRepositoryRequirements {

    private DataMatrixRepositoryTestContext<K, D> ctx;

    private DataMatrixRepository<K, D> dataMatrixRepository;

    /**
     * Creates a new instance of DataMatrixRepositoryTestContext which is ctx for the whole test suite.
     * This instance is initialized before each single test execution.
     *
     * @return an instance of DataMatrixRepositoryTestContext.
     */
    protected abstract DataMatrixRepositoryTestContext<K, D> initializeContext();

    @BeforeEach
    final void setUp() {
        if (ctx != null) {
            return;
        }
        ctx = initializeContext();
        dataMatrixRepository = ctx.initializerRepository();
    }

    @Override
    public void shouldNotAcceptResourcesWithIllegalKeySize() {

    }

    @Override
    public void shouldNotAcceptResourcesWithNullKey() {

    }

    @Override
    public void shouldNotAcceptResourcesWithNullResource() {

    }

    @Override
    public void shouldAcceptSameResourceMultipleTimes()
            throws DataMatrixRepositoryFailedException {

    }

    @Override
    public void shouldReturnEmptyIteratorIfEmpty()
            throws DataMatrixRepositoryFailedException {

    }

    @Override
    public void shouldReturnIteratorOfAllElements()
            throws DataMatrixRepositoryFailedException {

    }

    @Override
    public void shouldReturnResourceOfAGivenKeyInTheVeryFirstChunkIfExists()
            throws DataMatrixRepositoryFailedException {

    }

    @Override
    public void shouldReturnNoEmptyChunks()
            throws DataMatrixRepositoryFailedException {

    }

    @Override
    public void shouldProperlyCalculateUnconditionalNextIndicator()
            throws DataMatrixRepositoryFailedException {

    }

    @Override
    public void shouldProperlyCalculateConditionalNextIndicator()
            throws DataMatrixRepositoryFailedException {

    }
}
