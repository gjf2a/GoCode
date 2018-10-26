package com.stack.gocode.localData.fuzzy;

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
}
