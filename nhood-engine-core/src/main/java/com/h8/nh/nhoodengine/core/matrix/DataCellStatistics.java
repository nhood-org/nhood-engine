package com.h8.nh.nhoodengine.core.matrix;

import com.h8.nh.nhoodengine.core.utils.DoubleRunningStatistics;

import java.util.Vector;

final class DataCellStatistics {

    private final Vector<Double> id;

    private final Vector<DoubleRunningStatistics> statistics;

    DataCellStatistics(final Vector<Double> id) {
        this.id = id;

        this.statistics = new Vector<>(id.size());
        for (int i = 0; i < id.size(); i++) {
            this.statistics.add(i, new DoubleRunningStatistics());
        }
    }

    Vector<Double> getId() {
        return id;
    }

    void accept(final Vector<Double> index) {
        for (int i = 0; i < index.size(); i++) {
            this.statistics.get(i).accept(index.get(i));
        }
    }

    int getHighestUnifiedStandardDeviationIndex() {
        int index = 0;
        double hsd = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < statistics.size(); i++) {
            DoubleRunningStatistics stats = statistics.get(i);
            double sd = stats.getStandardDeviation() / (stats.getMax() - stats.getAverage());
            if (hsd < sd) {
                hsd = sd;
                index = i;
            }
        }
        return index;
    }

    int getWidestRangeIndex() {
        int index = 0;
        double wr = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < statistics.size(); i++) {
            DoubleRunningStatistics stats = statistics.get(i);
            double range = stats.getMax() - stats.getMin();
            if (wr < range) {
                wr = range;
                index = i;
            }
        }
        return index;
    }

}
