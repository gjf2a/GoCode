package com.stack.gocode.localData.fuzzy;

import android.content.ContentValues;

import com.stack.gocode.localData.DatabaseHelper;

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

    @Override
    protected void addFuzzyBounds(ContentValues values) {
        values.put(DatabaseHelper.FUZZY_FLAGS_ARG1, fallStart);
        values.put(DatabaseHelper.FUZZY_FLAGS_ARG2, fallEnd);
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
    public FallingFuzzyFlag updatedName(String newName) {
        return new FallingFuzzyFlag(newName, getSensor(), fallStart, fallEnd);
    }

    @Override
    public String getType() {return TYPE;}

    @Override
    public FallingFuzzyFlag updatedSensor(String updatedSensor) {
        return new FallingFuzzyFlag(getName(), updatedSensor, fallStart, fallEnd);
    }

    @Override
    public FallingFuzzyFlag updatedArg1(String arg1, DatabaseHelper db) {
        return new FallingFuzzyFlag(getName(), getSensor(), Double.parseDouble(arg1), fallEnd);
    }

    @Override
    public FallingFuzzyFlag updatedArg2(String arg2, DatabaseHelper db) {
        return new FallingFuzzyFlag(getName(), getSensor(), fallStart, Double.parseDouble(arg2));
    }

    @Override
    public FallingFuzzyFlag updatedArg3(String arg3, DatabaseHelper db) {
        return this;
    }

    @Override
    public FallingFuzzyFlag updatedArg4(String arg4, DatabaseHelper db) {
        return this;
    }
}
