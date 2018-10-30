package com.stack.gocode.localData.fuzzy;

import com.stack.gocode.localData.DatabaseHelper;

/**
 * Created by gabriel on 10/25/18.
 */

public class FuzzyAnd extends FuzzyCombo {
    public final static String TYPE = "and";

    public FuzzyAnd(String name, FuzzyFlag one, FuzzyFlag two) {
        super(name, one, two);
    }

    @Override
    public double op(double v1, double v2) {
        return Math.min(v1, v2);
    }

    @Override
    public String opName() {return "&";}

    @Override
    public FuzzyAnd updatedName(String newName) {
        return new FuzzyAnd(newName, one, two);
    }

    @Override
    public String getType() {return TYPE;}

    @Override
    public FuzzyAnd updatedSensor(String updatedSensor) {
        return new FuzzyAnd(getName(), one.updatedSensor(updatedSensor), two.updatedSensor(updatedSensor));
    }

    @Override
    public FuzzyAnd updatedArg1(String arg1, DatabaseHelper db) {
        return new FuzzyAnd(getName(), db.getFuzzyFlag(arg1), two);
    }

    @Override
    public FuzzyAnd updatedArg2(String arg2, DatabaseHelper db) {
        return new FuzzyAnd(getName(), one, db.getFuzzyFlag(arg2));
    }
}
