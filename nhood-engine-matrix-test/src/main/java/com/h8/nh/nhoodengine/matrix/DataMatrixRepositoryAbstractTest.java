package com.h8.nh.nhoodengine.matrix;

import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.core.DataResourceKey;
import com.h8.nh.nhoodengine.matrix.workers.ResourcesAddWorker;
import com.h8.nh.nhoodengine.utils.DataKeyGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.h8.nh.nhoodengine.core.DataResourceKey.UNIFIED_BIG_DECIMAL_ROUNDING_MODE;
import static com.h8.nh.nhoodengine.core.DataResourceKey.UNIFIED_BIG_DECIMAL_SCALE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * DataFinderAbstractTest is an abstract test class
 * that implements DataMatrixRepositoryRequirements.
 * <p>
 * This class has been abstracted in order to maintain generic approach.
 * <p>
 * It is assumed that all sequences of metadata types may be mapped
 * into a sequence of integers and all relations, and geometrical features are inherited.
 * Therefore all tests are based on integer-typed vectors.
 * <p>
 * While testing a concrete implementation of DataMatrixRepository
 * an implementer has to implement a DataMatrixRepositoryTestContext interface.
 *
 * @param <K> a generic type of data metadata key vector. Extends {@link DataResourceKey}.
 * @param <D> a generic type of data resource.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public abstract class DataMatrixRepositoryAbstractTest<K extends DataResourceKey, D>
        implements DataMatrixRepositoryRequirements {

    private static final int METADATA_SIZE = 3;

    private static final BigDecimal DISTANCE_ZERO = BigDecimal.ZERO
            .setScale(UNIFIED_BIG_DECIMAL_SCALE, UNIFIED_BIG_DECIMAL_ROUNDING_MODE);

    private static final double DIAGONAL_CUBE = Math.sqrt(3.0);
    private static final BigDecimal DISTANCE_DIAGONAL_CUBE = BigDecimal.valueOf(DIAGONAL_CUBE)
            .setScale(UNIFIED_BIG_DECIMAL_SCALE, UNIFIED_BIG_DECIMAL_ROUNDING_MODE);

    private static final Integer[] KEY_VECTOR_MIN_LIMIT = new Integer[]{-20, -20, -20};

    private static final Integer[] KEY_VECTOR_MAX_LIMIT = new Integer[]{10, 10, 10};

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
    @Test
    public final void shouldNotAcceptNullResources() {
        // given / when / then
        assertThatThrownBy(() -> dataMatrixRepository.add(null))
                .isInstanceOf(DataMatrixRepositoryFailedException.class)
                .hasMessage("Data resource may not be null")
                .hasNoCause();
    }

    @Override
    @Test
    public final void shouldNotAcceptResourcesWithIllegalKeySize() {
        // given
        K metadata = ctx.dataKey(0, 0);
        DataResource<K, D> resource = DataResource.<K, D>builder()
                .key(metadata)
                .data(ctx.data(0, 0, 0))
                .build();

        // when / then
        assertThatThrownBy(() -> dataMatrixRepository.add(resource))
                .isInstanceOf(DataMatrixRepositoryFailedException.class)
                .hasMessage("Data resource has invalid key size: " + metadata.unified().length
                        + ". Expected: " + METADATA_SIZE)
                .hasNoCause();
    }

    @Override
    @Test
    public final void shouldNotAcceptResourcesWithNullKey() {
        // given
        DataResource<K, D> resource = DataResource.<K, D>builder()
                .key(null)
                .data(ctx.data(0, 0, 0))
                .build();

        // when / then
        assertThatThrownBy(() -> dataMatrixRepository.add(resource))
                .isInstanceOf(DataMatrixRepositoryFailedException.class)
                .hasMessage("Data resource has invalid key: null")
                .hasNoCause();
    }

    @Override
    @Test
    public final void shouldNotAcceptResourcesWithNullResource() {
        // given
        K metadata = ctx.dataKey(0, 0, 0);
        DataResource<K, D> resource = DataResource.<K, D>builder()
                .key(metadata)
                .data(null)
                .build();

        // when / then
        assertThatThrownBy(() -> dataMatrixRepository.add(resource))
                .isInstanceOf(DataMatrixRepositoryFailedException.class)
                .hasMessage("Data resource has invalid data: null")
                .hasNoCause();
    }

    @Override
    @Test
    public final void shouldAcceptSameResourceWhenAddedMultipleTimes()
            throws DataMatrixRepositoryFailedException {
        // given
        DataResource<K, D> resource = ctx.resource(0, 0, 0);

        // when
        dataMatrixRepository.add(resource);
        dataMatrixRepository.add(resource);

        // then
        DataMatrixResourceIterator<K, D> iterator =
                dataMatrixRepository.findNeighbours(resource.getKey());

        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next()).containsExactly(resource);
        assertThat(iterator.hasNext()).isFalse();
    }

    @Override
    @Test
    public final void shouldAcceptTwoResourcesWithTheSameKey()
            throws DataMatrixRepositoryFailedException {
        // given
        DataResource<K, D> r1 = ctx.resource(0, 0, 0);
        DataResource<K, D> r2 = ctx.resource(0, 0, 0);

        assertThat(r1).isNotEqualTo(r2);

        // when
        dataMatrixRepository.add(r1);
        dataMatrixRepository.add(r2);

        // then
        DataMatrixResourceIterator<K, D> iterator =
                dataMatrixRepository.findNeighbours(r1.getKey());

        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next()).containsExactlyInAnyOrder(r1, r2);
        assertThat(iterator.hasNext()).isFalse();
    }

    @Override
    @Test
    public final void shouldReturnEmptyIteratorIfEmpty()
            throws DataMatrixRepositoryFailedException {
        // given
        K key = ctx.dataKey(0, 0, 0);

        // when
        DataMatrixResourceIterator<K, D> iterator =
                dataMatrixRepository.findNeighbours(key);

        // then
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next()).isEmpty();
        assertThat(iterator.hasNext()).isFalse();
    }

    @Override
    @Test
    public final void shouldReturnIteratorOfAllElements()
            throws DataMatrixRepositoryFailedException {
        // given
        List<DataResource<K, D>> resources = populateRepositoryWithResources();
        DataResource<K, D> resource = resources.get(0);

        // when
        DataMatrixResourceIterator<K, D> iterator =
                dataMatrixRepository.findNeighbours(resource.getKey());

        // then
        List<DataResource<K, D>> retrievedResources = new ArrayList<>();

        while (iterator.hasNext()) {
            retrievedResources.addAll(iterator.next());
        }

        assertThat(retrievedResources)
                .containsOnlyElementsOf(resources);
        assertThat(retrievedResources)
                .containsAll(resources);
    }

    @Override
    @Test
    public final void shouldReturnResourceOfAGivenKeyInTheVeryFirstChunkIfExists()
            throws DataMatrixRepositoryFailedException {
        // given
        List<DataResource<K, D>> resources = populateRepositoryWithResources();
        DataResource<K, D> resource = resources.get(0);

        // when
        DataMatrixResourceIterator<K, D> iterator =
                dataMatrixRepository.findNeighbours(resource.getKey());

        // then
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next())
                .contains(resource);
    }

    @Override
    @Test
    public final void shouldReturnNoEmptyChunks()
            throws DataMatrixRepositoryFailedException {
        // given
        List<DataResource<K, D>> resources = populateRepositoryWithResources();
        DataResource<K, D> resource = resources.get(0);

        // when
        DataMatrixResourceIterator<K, D> iterator =
                dataMatrixRepository.findNeighbours(resource.getKey());

        // then
        while (iterator.hasNext()) {
            assertThat(iterator.next()).isNotEmpty();
        }

    }

    @Override
    @Test
    public final void shouldProperlyCalculateUnconditionalNextIndicator()
            throws DataMatrixRepositoryFailedException {
        // given
        List<DataResource<K, D>> resources = populateRepositoryWithResources();
        DataResource<K, D> resource = resources.get(0);

        // when
        DataMatrixResourceIterator<K, D> iterator =
                dataMatrixRepository.findNeighbours(resource.getKey());

        // then
        int retrievedCounter = 0;
        while (iterator.hasNext()) {
            retrievedCounter += iterator.next().size();
            boolean shouldContainMore = resources.size() > retrievedCounter;
            assertThat(iterator.hasNext()).isEqualTo(shouldContainMore);
        }
    }

    @Override
    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public final void shouldProperlyCalculateConditionalNextIndicator()
            throws DataMatrixRepositoryFailedException {
        // given
        List<DataResource<K, D>> resources = populateRepositoryWithResources();
        K key = ctx.dataKey(0, 0, 0);

        // when
        DataMatrixResourceIterator<K, D> iterator =
                dataMatrixRepository.findNeighbours(key);

        // then
        int retrievedCounter = 0;
        while (iterator.hasNext()) {
            retrievedCounter += iterator.next().size();
            boolean shouldContainMore = resources.size() > retrievedCounter;
            assertThat(iterator.hasNextWithinRange(DISTANCE_ZERO))
                    .isEqualTo(false);
            assertThat(iterator.hasNextWithinRange(
                    DISTANCE_DIAGONAL_CUBE.multiply(BigDecimal.valueOf(20))))
                    .isEqualTo(shouldContainMore);
        }
    }

    private List<DataResource<K, D>> generateResources() {
        return DataKeyGenerator
                .generate(KEY_VECTOR_MIN_LIMIT, KEY_VECTOR_MAX_LIMIT)
                .map(ctx::dataKey)
                .map(k -> DataResource.<K, D>builder()
                        .key(k)
                        .data(ctx.data(k))
                        .build())
                .collect(Collectors.toList());
    }

    private List<DataResource<K, D>> populateRepositoryWithResources() {
        List<DataResource<K, D>> resources = generateResources();
        ResourcesAddWorker.of(dataMatrixRepository, resources).run();
        return resources;
    }
}
