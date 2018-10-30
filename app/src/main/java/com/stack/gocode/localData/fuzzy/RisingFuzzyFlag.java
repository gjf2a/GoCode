package com.stack.gocode.localData.fuzzy;

import android.content.ContentValues;

import com.stack.gocode.localData.DatabaseHelper;

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
    protected void addFuzzyBounds(ContentValues values) {
        values.put(DatabaseHelper.FUZZY_FLAGS_ARG1, riseStart);
        values.put(DatabaseHelper.FUZZY_FLAGS_ARG2, riseEnd);
    }

    @Override
    public RisingFuzzyFlag updatedName(String newName) {
        return new RisingFuzzyFlag(newName, getSensor(), riseStart, riseEnd);
    }

    @Override
    public RisingFuzzyFlag updatedSensor(String updatedSensor) {
        return new RisingFuzzyFlag(getName(), updatedSensor, riseStart, riseEnd);
    }

    @Override
    public RisingFuzzyFlag updatedArg1(String arg1, DatabaseHelper db) {
        return new RisingFuzzyFlag(getName(), getSensor(), Double.parseDouble(arg1), riseEnd);
    }

    @Override
    public RisingFuzzyFlag updatedArg2(String arg2, DatabaseHelper db) {
        return new RisingFuzzyFlag(getName(), getSensor(), riseStart, Double.parseDouble(arg2));
    }

    @Override
    public RisingFuzzyFlag updatedArg3(String arg3, DatabaseHelper db) {
        return this;
    }

    @Override
    public RisingFuzzyFlag updatedArg4(String arg4, DatabaseHelper db) {
        return this;
    }

    @Override
    public String toString() {
        return "RisingFuzzy[" + riseStart + "," + riseEnd + "]";
    }

    @Override
    public String getType() {return TYPE;}
}
