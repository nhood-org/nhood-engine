package com.h8.nh.nhoodengine.core.utils;

import java.util.DoubleSummaryStatistics;

public final class DoubleRunningStatistics extends DoubleSummaryStatistics {

    private double sumOfSquare = 0.0d;
    private double sumOfSquareCompensation;
    private double simpleSumOfSquare;

    @Override
    public void accept(final double value) {
        super.accept(value);
        double squareValue = value * value;
        simpleSumOfSquare += squareValue;
        sumOfSquareWithCompensation(squareValue);
    }

    public double getStandardDeviation() {
        if (getCount() > 0) {
            return Math.sqrt((getSumOfSquare() / getCount()) - Math.pow(getAverage(), 2));
        } else {
            return 0.0d;
        }
    }

    private void sumOfSquareWithCompensation(final double value) {
        double tmp = value - sumOfSquareCompensation;
        double vel = sumOfSquare + tmp;
        sumOfSquareCompensation = (vel - sumOfSquare) - tmp;
        sumOfSquare = vel;
    }

    private double getSumOfSquare() {
        double tmp = sumOfSquare + sumOfSquareCompensation;
        if (Double.isNaN(tmp) && Double.isInfinite(simpleSumOfSquare)) {
            return simpleSumOfSquare;
        }
        return tmp;
    }
}
