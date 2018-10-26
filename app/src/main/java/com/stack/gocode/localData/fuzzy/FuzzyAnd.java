package com.stack.gocode.localData.fuzzy;

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
    public String getType() {return TYPE;}
}
