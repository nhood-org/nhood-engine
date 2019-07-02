package com.h8.nh.nhoodengine.utils.measurement;

public interface MeasurementChainNode {
    Runnable append(Runnable runnable);
}
