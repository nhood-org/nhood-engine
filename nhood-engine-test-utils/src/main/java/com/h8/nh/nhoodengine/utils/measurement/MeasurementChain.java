package com.h8.nh.nhoodengine.utils.measurement;

public final class MeasurementChain {

    private Runnable runnable;

    private MeasurementChain(final Runnable runnable) {
        this.runnable = runnable;
    }

    public static MeasurementChain of(final Runnable runnable) {
        return new MeasurementChain(runnable);
    }

    public MeasurementChain measure(final MeasurementChainNode node) {
        Runnable chained = node.append(this.runnable);
        return new MeasurementChain(chained);
    }

    public void run() {
        runnable.run();
    }
}
