package com.stack.gocode.localData.fuzzy;

import com.stack.gocode.sensors.SensedValues;

/**
 * Created by gabriel on 10/25/18.
 */

abstract public class FuzzySensedFlag extends FuzzyFlag {
    private String sensor;

    public FuzzySensedFlag(String name, String sensor) {
        super(name);
        this.sensor = sensor;
    }

    public String getSensor() {
        return sensor;
    }
    public void setSensor(String sensor) {
        this.sensor = sensor;
    }

    public double getFuzzyValue(SensedValues sensed) {
        return fuzzify(sensed.getSensedValueFor(getSensor()));
    }

    abstract public double fuzzify(double sensedValue);
}
