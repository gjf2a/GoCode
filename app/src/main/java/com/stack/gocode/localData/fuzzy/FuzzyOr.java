package com.stack.gocode.localData.fuzzy;

import com.stack.gocode.localData.DatabaseHelper;

/**
 * Created by gabriel on 10/25/18.
 */

public class FuzzyOr extends FuzzyCombo {
    public FuzzyOr(String name, FuzzyFlag one, FuzzyFlag two) {
        super(name, one, two);
    }

    public final static String TYPE = "or";

    @Override
    public double op(double v1, double v2) {
        return Math.max(v1, v2);
    }

    @Override
    public String opName() {return "|";}

    @Override
    public String getType() {return TYPE;}

    @Override
    public FuzzyOr updatedName(String newName) {
        return new FuzzyOr(newName, one, two);
    }

    @Override
    public FuzzyOr updatedSensor(String updatedSensor) {
        return new FuzzyOr(getName(), one.updatedSensor(updatedSensor), two.updatedSensor(updatedSensor));
    }

    @Override
    public FuzzyOr updatedArg1(String arg1, DatabaseHelper db) {
        return new FuzzyOr(getName(), db.getFuzzyFlag(arg1), two);
    }

    @Override
    public FuzzyOr updatedArg2(String arg2, DatabaseHelper db) {
        return new FuzzyOr(getName(), one, db.getFuzzyFlag(arg2));
    }
}
