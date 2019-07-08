package com.h8.nh.nhoodengine.utils.measurement;

public interface MeasurementChainNode {
    Runnable append(MeasurementChain chain, Runnable runnable);
}
