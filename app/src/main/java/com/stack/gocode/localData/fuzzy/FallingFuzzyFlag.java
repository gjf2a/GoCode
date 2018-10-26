package com.stack.gocode.localData.fuzzy;

/**
 * Created by gabriel on 10/25/18.
 */

public class FallingFuzzyFlag extends FuzzySensedFlag {
    private double fallStart, fallEnd;

    public static final String TYPE = "falling";

    public FallingFuzzyFlag(String name, String sensor, double fallStart, double fallEnd) {
        super(name, sensor);
        this.fallStart = fallStart;
        this.fallEnd = fallEnd;
    }

    public double fuzzify(double sensedValue) {
        return sensedValue > fallEnd ? 0.0
                : sensedValue < fallStart ? 1.0 : (fallEnd - sensedValue) / (fallEnd - fallStart);
    }

    @Override
    public String toString() {
        return "FallingFuzzy[" + fallStart + "," + fallEnd + "]";
    }

    @Override
    public String getType() {return TYPE;}
}
