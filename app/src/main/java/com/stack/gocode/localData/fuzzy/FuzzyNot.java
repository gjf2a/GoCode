package com.stack.gocode.localData.fuzzy;

import com.stack.gocode.sensors.SensedValues;

/**
 * Created by gabriel on 10/25/18.
 */

public class FuzzyNot extends FuzzyFlag {
    private FuzzyFlag f;

    public final static String TYPE = "not";

    public FuzzyNot(String name, FuzzyFlag f) {
        super(name);
        this.f = f;
    }

    @Override
    public double getFuzzyValue(SensedValues sensed) {
        return 1.0 - f.getFuzzyValue(sensed);
    }

    @Override
    public String toString() {
        return "FuzzyNot:" + f;
    }

    @Override
    public String getType() {return TYPE;}
}
