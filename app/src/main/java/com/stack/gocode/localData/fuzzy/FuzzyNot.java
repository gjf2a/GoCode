package com.stack.gocode.localData.fuzzy;

import android.content.ContentValues;

import com.stack.gocode.localData.DatabaseHelper;
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
    protected void addContentValues(ContentValues values) {
        values.put(DatabaseHelper.FUZZY_FLAGS_ARG1, f.getName());
    }

    @Override
    public FuzzyNot updatedName(String newName) {
        return new FuzzyNot(newName, f);
    }

    @Override
    public FuzzyNot updatedSensor(String updatedSensor) {
        return new FuzzyNot(getName(), f.updatedSensor(updatedSensor));
    }

    @Override
    public FuzzyNot updatedArg1(String arg1, DatabaseHelper db) {
        return new FuzzyNot(getName(), db.getFuzzyFlag(arg1));
    }

    @Override
    public FuzzyNot updatedArg2(String arg2, DatabaseHelper db) {
        return this;
    }

    @Override
    public FuzzyNot updatedArg3(String arg3, DatabaseHelper db) {
        return this;
    }

    @Override
    public FuzzyNot updatedArg4(String arg4, DatabaseHelper db) {
        return this;
    }

    @Override
    public String getType() {return TYPE;}
}
