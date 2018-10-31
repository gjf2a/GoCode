package com.stack.gocode.localData.fuzzy;

import com.stack.gocode.localData.DatabaseHelper;
import com.stack.gocode.localData.factory.FuzzyFlagFinder;
import com.stack.gocode.localData.factory.FuzzyFlagRow;
import com.stack.gocode.sensors.SensedValues;

/**
 * Created by gabriel on 10/30/18.
 */

public class FuzzyArgs {
    public static final int NUM_FUZZY_ARGS = 4;

    private boolean isNum;
    private double[] nums = new double[NUM_FUZZY_ARGS];
    private FuzzyFlag[] flags = new FuzzyFlag[NUM_FUZZY_ARGS];
    private String sensor;

    private void assertArg(int arg) {
        if (arg < 0 || arg >= NUM_FUZZY_ARGS) {
            throw new IllegalArgumentException("Illegal argument position: " + arg);
        }
    }

    private void assertNum() {
        if (!isNum) {
            throw new IllegalArgumentException("Can't set a number with Flag args");
        }
    }

    private void assertFlag() {
        if (isNum) {
            throw new IllegalArgumentException("Can't set a Flag with numeric args");
        }
    }

    private FuzzyArgs() {}

    public FuzzyArgs duplicate() {
        FuzzyArgs copy = new FuzzyArgs();
        copy.isNum = this.isNum;
        copy.sensor = this.sensor;
        copy.nums = new double[this.nums.length];
        copy.flags = new FuzzyFlag[this.nums.length];
        for (int i = 0; i < this.nums.length; i++) {
            copy.nums[i] = this.nums[i];
            copy.flags[i] = this.flags[i];
        }
        return copy;
    }

    public FuzzyArgs(FuzzyType type, FuzzyFlagRow row, FuzzyFlagFinder db) {
        isNum = type.isNum();
        this.sensor = row.getSensor();
        for (int i = 0; i < type.numArgs(); i++) {
            set(i, row.getArg(i), db);
        }
    }

    public void setNumericalDefaults(FuzzyFlagFinder db) {
        isNum = true;
        sensor = SensedValues.SENSOR_NAMES[0];
        for (int i = 0; i < NUM_FUZZY_ARGS; i++) {
            set(0, "0", db);
        }
    }

    public void setFlagDefaults(FuzzyFlagFinder db) {
        isNum = false;
        // TODO: Not done!!!
        throw new UnsupportedOperationException("Not finished!");
    }

    public String getStr(FuzzyType type, int arg) {
        assertArg(arg);
        return arg >= type.numArgs() ? "" : isNum ? Double.toString(nums[arg]) : flags[arg].getName();
    }

    public double getNum(int arg) {
        assertNum();
        assertArg(arg);
        return nums[arg];
    }

    public double getSensedValue(SensedValues sensed) {
        assertNum();
        return sensed.getSensedValueFor(sensor);
    }

    public void setSensor(String sensor) {
        this.sensor = sensor;
    }

    public String getSensor() {
        return sensor;
    }

    public FuzzyFlag getFlag(int arg) {
        assertFlag();
        assertArg(arg);
        return flags[arg];
    }

    public void set(int arg, String value, FuzzyFlagFinder db) {
        assertArg(arg);
        if (db.fuzzyFlagExists(value)) {
            assertFlag();
            flags[arg] = db.getFuzzyFlag(value);
        } else {
            try {
                assertNum();
                nums[arg] = Double.parseDouble(value);
            } catch (NumberFormatException exc) {
                throw new IllegalArgumentException("Argument '" + value + "' cannot be processed");
            }
        }
    }

    public boolean isNum() {
        return isNum;
    }
}
