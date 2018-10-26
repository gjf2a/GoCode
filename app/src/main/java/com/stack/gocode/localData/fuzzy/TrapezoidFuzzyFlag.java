package com.stack.gocode.localData.fuzzy;

/**
 * Created by gabriel on 10/25/18.
 */

public class TrapezoidFuzzyFlag extends FuzzySensedFlag {
    private double start, peakStart, peakEnd, end;

    public final static String TYPE = "trapezoid";

    public TrapezoidFuzzyFlag(String name, String sensor, double start, double peakStart, double peakEnd, double end) {
        super(name, sensor);
        this.start = start;
        this.peakStart = peakStart;
        this.peakEnd = peakEnd;
        this.end = end;
    }

    public double fuzzify(double sensedValue) {
        return sensedValue > end ? 0.0 : sensedValue < start ? 0.0 : sensedValue > peakStart && sensedValue < peakEnd ? 1.0 : sensedValue >= peakEnd ? (end - sensedValue) / (end - peakEnd) : (sensedValue - start) / (peakStart - start);
    }

    @Override
    public String toString() {
        return "TrapezoidFuzzy[" + start + "," + peakStart + "," + peakEnd + "," + end + "]";
    }

    @Override
    public String getType() {return TYPE;}
}
