package com.h8.nh.nhoodengine.utils.measurement;

public final class MeasurementChain {

    private String name;

    private Runnable runnable;

    private MeasurementChain(
            final String name, final Runnable runnable) {
        this.name = name;
        this.runnable = runnable;
    }

    public static MeasurementChain of(
            final String name, final Runnable runnable) {
        return new MeasurementChain(name, runnable);
    }

    public MeasurementChain measure(final MeasurementChainNode node) {
        Runnable chained = node.append(this, this.runnable);
        return new MeasurementChain(name, chained);
    }

    public void run() {
        runnable.run();
    }

    public void out(final String metric, final Object value) {
        System.out.println("Measurement:" + name + "::" + metric + " - " + value);
    }
}
