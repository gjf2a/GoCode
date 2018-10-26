package com.stack.gocode.localData.fuzzy;

import com.stack.gocode.sensors.SensedValues;

/**
 * Created by gabriel on 10/25/18.
 */

abstract public class FuzzyCombo extends FuzzyFlag {
    private FuzzyFlag one, two;

    public FuzzyCombo(String name, FuzzyFlag one, FuzzyFlag two) {
        super(name);
        this.one = one;
        this.two = two;
    }

    public double getFuzzyValue(SensedValues sensed) {
        return op(one.getFuzzyValue(sensed), two.getFuzzyValue(sensed));
    }

    @Override
    public String toString() {
        return "(" + one + opName() + two + ")";
    }

    abstract public double op(double v1, double v2);
    abstract public String opName();
}
