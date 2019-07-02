package com.h8.nh.nhoodengine.utils.measurement.node;

import com.h8.nh.nhoodengine.utils.measurement.MeasurementChainNode;

import java.time.Duration;
import java.time.LocalTime;

public final class ExecutionTimeMeasurement implements MeasurementChainNode {

    private static final ExecutionTimeMeasurement INSTANCE =
            new ExecutionTimeMeasurement();

    private ExecutionTimeMeasurement() {
    }

    public static ExecutionTimeMeasurement getInstance() {
        return INSTANCE;
    }

    @Override
    public Runnable append(final Runnable runnable) {
        return () -> {
            LocalTime start = LocalTime.now();
            runnable.run();
            LocalTime end = LocalTime.now();
            System.out.println(
                    "Measurement::Time:         " + Duration.between(start, end));
        };
    }
}
