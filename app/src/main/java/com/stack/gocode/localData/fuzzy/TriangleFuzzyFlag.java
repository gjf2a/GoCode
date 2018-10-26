package com.stack.gocode.localData.fuzzy;

/**
 * Created by gabriel on 10/25/18.
 */

public class TriangleFuzzyFlag extends TrapezoidFuzzyFlag {
    public static final String TYPE = "triangle";
    public TriangleFuzzyFlag(String name, String sensor, double start, double peak, double end) {
        super(name, sensor, start, peak, peak, end);
    }

    @Override
    public String getType() {return TYPE;}
}
