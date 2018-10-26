package com.stack.gocode.localData.fuzzy;

/**
 * Created by gabriel on 10/25/18.
 */

public class RisingFuzzyFlag extends FuzzySensedFlag {
    private double riseStart, riseEnd;

    public final static String TYPE = "rising";

    public RisingFuzzyFlag(String name, String sensor, double riseStart, double riseEnd) {
        super(name, sensor);
        this.riseStart = riseStart;
        this.riseEnd = riseEnd;
    }

    public double fuzzify(double sensedValue) {
        return sensedValue > riseEnd ? 1.0
                : sensedValue < riseStart ? 0.0 : (sensedValue - riseStart) / (riseEnd - riseStart);
    }

    @Override
    public String toString() {
        return "RisingFuzzy[" + riseStart + "," + riseEnd + "]";
    }

    @Override
    public String getType() {return TYPE;}
}
