package com.stack.gocode.localData.fuzzy;

import android.content.ContentValues;

import com.stack.gocode.localData.DatabaseHelper;

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

    @Override
    protected void addFuzzyBounds(ContentValues values) {
        values.put(DatabaseHelper.FUZZY_FLAGS_ARG1, start);
        values.put(DatabaseHelper.FUZZY_FLAGS_ARG2, peakStart);
        values.put(DatabaseHelper.FUZZY_FLAGS_ARG3, end);
    }

    @Override
    public TriangleFuzzyFlag updatedName(String newName) {
        return new TriangleFuzzyFlag(newName, getSensor(), start, peakStart, end);
    }

    @Override
    public TriangleFuzzyFlag updatedSensor(String updatedSensor) {
        return new TriangleFuzzyFlag(getName(), updatedSensor, start, peakStart, end);
    }

    @Override
    public TriangleFuzzyFlag updatedArg1(String arg1, DatabaseHelper db) {
        return new TriangleFuzzyFlag(getName(), getSensor(), Double.parseDouble(arg1), peakStart, end);
    }

    @Override
    public TriangleFuzzyFlag updatedArg2(String arg2, DatabaseHelper db) {
        return new TriangleFuzzyFlag(getName(), getSensor(), start, Double.parseDouble(arg2), end);
    }

    @Override
    public TriangleFuzzyFlag updatedArg3(String arg3, DatabaseHelper db) {
        return new TriangleFuzzyFlag(getName(), getSensor(), start, peakStart, Double.parseDouble(arg3));
    }

    @Override
    public TriangleFuzzyFlag updatedArg4(String arg4, DatabaseHelper db) {
        return this;
    }
}
