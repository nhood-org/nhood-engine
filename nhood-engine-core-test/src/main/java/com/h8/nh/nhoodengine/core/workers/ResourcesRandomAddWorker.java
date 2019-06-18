package com.h8.nh.nhoodengine.core.workers;

import com.h8.nh.nhoodengine.core.DataFinderTestContext;
import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.core.DataResourceKey;

import java.security.SecureRandom;
import java.util.Arrays;

public final class ResourcesRandomAddWorker<K extends DataResourceKey, D> implements Runnable {

    private final DataFinderTestContext<K, D> ctx;

    private boolean hasErrors = false;

    private ResourcesRandomAddWorker(
            final DataFinderTestContext<K, D> ctx) {
        this.ctx = ctx;
    }

    public static <K extends DataResourceKey, D> ResourcesRandomAddWorker<K, D> of(
            final DataFinderTestContext<K, D> ctx) {
        return new ResourcesRandomAddWorker<>(ctx);
    }

    public boolean hasErrors() {
        return hasErrors;
    }

    @Override
    public void run() {
        DataResource<K, D> resource = generateRandomResource();
        System.out.println(Thread.currentThread().getName()
                + " : Adding random data: " + Arrays.toString(resource.getKey().unified()));
        try {
            ctx.register(resource);
        } catch (Exception e) {
            System.err.println(Thread.currentThread().getName()
                    + " : Could not register random data because of"
                    + " an exception: " + e.getClass().getSimpleName() + " : " + e.getMessage());

            // Uncomment for troubleshooting purposes only
            // e.printStackTrace(System.err);

            hasErrors = true;
        }
    }

    private DataResource<K, D> generateRandomResource() {
        SecureRandom random = new SecureRandom();
        K metadata = ctx.dataKey(
                random.nextInt(Integer.MAX_VALUE / 2),
                random.nextInt(Integer.MAX_VALUE / 2),
                random.nextInt(Integer.MAX_VALUE / 2));
        return DataResource.<K, D>builder()
                .key(metadata)
                .data(ctx.data(metadata))
                .build();
    }
}
