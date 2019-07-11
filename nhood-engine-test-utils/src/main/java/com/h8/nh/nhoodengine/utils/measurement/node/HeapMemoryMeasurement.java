package com.h8.nh.nhoodengine.utils.measurement.node;

import com.h8.nh.nhoodengine.utils.measurement.MeasurementChain;
import com.h8.nh.nhoodengine.utils.measurement.MeasurementChainNode;

public final class HeapMemoryMeasurement implements MeasurementChainNode {

    private static final int UNIT = 1024;

    private static final HeapMemoryMeasurement INSTANCE =
            new HeapMemoryMeasurement();

    private HeapMemoryMeasurement() {
    }

    public static HeapMemoryMeasurement getInstance() {
        return INSTANCE;
    }

    @Override
    public Runnable append(final MeasurementChain chain, final Runnable runnable) {
        return () -> {
            long memoryUsage = readUsedMemory();
            runnable.run();
            memoryUsage = readUsedMemory() - memoryUsage;
            chain.out("Heap Memory", humanReadableByteCount(memoryUsage));
        };
    }

    private static long readUsedMemory() {
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }

    private static String humanReadableByteCount(
            final long bytes) {
        if (bytes < UNIT) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(UNIT));
        String pre = "KMGTPE".charAt(exp - 1) + "i";
        return String.format("%.1f %sB", bytes / Math.pow(UNIT, exp), pre);
    }
}
