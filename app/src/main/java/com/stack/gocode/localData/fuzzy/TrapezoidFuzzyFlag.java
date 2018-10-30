package com.stack.gocode.localData.fuzzy;

import android.content.ContentValues;

import com.stack.gocode.localData.DatabaseHelper;

/**
 * Created by gabriel on 10/25/18.
 */

public class TrapezoidFuzzyFlag extends FuzzySensedFlag {
    double start, peakStart, peakEnd, end;

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
    protected void addFuzzyBounds(ContentValues values) {
        values.put(DatabaseHelper.FUZZY_FLAGS_ARG1, start);
        values.put(DatabaseHelper.FUZZY_FLAGS_ARG2, peakStart);
        values.put(DatabaseHelper.FUZZY_FLAGS_ARG3, peakEnd);
        values.put(DatabaseHelper.FUZZY_FLAGS_ARG4, end);
    }

    @Override
    public String toString() {
        return "TrapezoidFuzzy[" + start + "," + peakStart + "," + peakEnd + "," + end + "]";
    }

    @Override
    public String getType() {return TYPE;}

    @Override
    public TrapezoidFuzzyFlag updatedName(String newName) {
        return new TrapezoidFuzzyFlag(newName, getSensor(), start, peakStart, peakEnd, end);
    }

    @Override
    public TrapezoidFuzzyFlag updatedSensor(String updatedSensor) {
        return new TrapezoidFuzzyFlag(getName(), updatedSensor, start, peakStart, peakEnd, end);
    }

    @Override
    public TrapezoidFuzzyFlag updatedArg1(String arg1, DatabaseHelper db) {
        return new TrapezoidFuzzyFlag(getName(), getSensor(), Double.parseDouble(arg1), peakStart, peakEnd, end);
    }

    @Override
    public TrapezoidFuzzyFlag updatedArg2(String arg2, DatabaseHelper db) {
        return new TrapezoidFuzzyFlag(getName(), getSensor(), start, Double.parseDouble(arg2), peakEnd, end);
    }

    @Override
    public TrapezoidFuzzyFlag updatedArg3(String arg3, DatabaseHelper db) {
        return new TrapezoidFuzzyFlag(getName(), getSensor(), start, peakStart, Double.parseDouble(arg3), end);
    }

    @Override
    public TrapezoidFuzzyFlag updatedArg4(String arg4, DatabaseHelper db) {
        return new TrapezoidFuzzyFlag(getName(), getSensor(), start, peakStart, peakEnd, Double.parseDouble(arg4));
    }
}
