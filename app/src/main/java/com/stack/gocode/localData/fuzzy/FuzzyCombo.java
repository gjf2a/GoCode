package com.stack.gocode.localData.fuzzy;

import android.content.ContentValues;

import com.stack.gocode.localData.DatabaseHelper;
import com.stack.gocode.sensors.SensedValues;

/**
 * Created by gabriel on 10/25/18.
 */

abstract public class FuzzyCombo extends FuzzyFlag {
    protected FuzzyFlag one, two;

    public FuzzyCombo(String name, FuzzyFlag one, FuzzyFlag two) {
        super(name);
        this.one = one;
        this.two = two;
    }

    public double getFuzzyValue(SensedValues sensed) {
        return op(one.getFuzzyValue(sensed), two.getFuzzyValue(sensed));
    }

    @Override
    protected void addContentValues(ContentValues values) {
        values.put(DatabaseHelper.FUZZY_FLAGS_ARG1, one.getName());
        values.put(DatabaseHelper.FUZZY_FLAGS_ARG2, two.getName());
    }

    @Override
    public String toString() {
        return "(" + one + opName() + two + ")";
    }

    abstract public double op(double v1, double v2);
    abstract public String opName();

    public FuzzyFlag updatedArg3(String arg3, DatabaseHelper db) {
        return this;
    }

    public FuzzyFlag updatedArg4(String arg4, DatabaseHelper db) {
        return this;
    }
}
