package com.stack.gocode.localData.fuzzy;

import com.stack.gocode.sensors.SensedValues;

/**
 * Created by gabriel on 10/25/18.
 */

public class FuzzyMotor {
    private FuzzyFlag flag;
    private Defuzzifier defuzzifier;

    public FuzzyMotor(FuzzyFlag flag, Defuzzifier defuzzifier) {
        this.flag = flag;
        this.defuzzifier = defuzzifier;
    }

    public int getMotorLevel(SensedValues sensed) {
        return defuzzifier.defuzzify(flag.getFuzzyValue(sensed));
    }

    @Override
    public String toString() {
        return flag + ";" + defuzzifier;
    }
}
