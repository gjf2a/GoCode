package com.stack.gocode.localData.fuzzy;

import android.content.ClipData;

import com.stack.gocode.com.stack.gocode.exceptions.ItemNotFoundException;
import com.stack.gocode.sensors.SensedValues;

import java.util.HashSet;

/**
 * Created by gabriel on 10/25/18.
 */

abstract public class FuzzyFlag {
    private String name;

    public static final HashSet<String> allTypeNames = new HashSet<>();
    static {
        allTypeNames.add(FallingFuzzyFlag.TYPE);
        allTypeNames.add(RisingFuzzyFlag.TYPE);
        allTypeNames.add(TrapezoidFuzzyFlag.TYPE);
        allTypeNames.add(TriangleFuzzyFlag.TYPE);
        allTypeNames.add(FuzzyAnd.TYPE);
        allTypeNames.add(FuzzyOr.TYPE);
        allTypeNames.add(FuzzyNot.TYPE);
    }

    public FuzzyFlag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    abstract public String getType();
    abstract public double getFuzzyValue(SensedValues sensed);
}
