package com.stack.gocode.localData.fuzzy.factory;

import com.stack.gocode.localData.fuzzy.FallingFuzzyFlag;
import com.stack.gocode.localData.fuzzy.FuzzyAnd;
import com.stack.gocode.localData.fuzzy.FuzzyFlag;
import com.stack.gocode.localData.fuzzy.FuzzyNot;
import com.stack.gocode.localData.fuzzy.FuzzyOr;
import com.stack.gocode.localData.fuzzy.RisingFuzzyFlag;
import com.stack.gocode.localData.fuzzy.TrapezoidFuzzyFlag;
import com.stack.gocode.localData.fuzzy.TriangleFuzzyFlag;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by gabriel on 10/26/18.
 */

public class FuzzyFlagRow {
    String project,  name,  type,  arg1,  arg2,  arg3,  arg4,  sensor;

    public static final HashSet<String> basicTypeNames = new HashSet<>();
    static {
        basicTypeNames.add(FallingFuzzyFlag.TYPE);
        basicTypeNames.add(RisingFuzzyFlag.TYPE);
        basicTypeNames.add(TrapezoidFuzzyFlag.TYPE);
        basicTypeNames.add(TriangleFuzzyFlag.TYPE);
    }

    public FuzzyFlagRow(String project, String name, String type, String arg1, String arg2, String arg3, String arg4, String sensor) {
        this.project = project;
        this.name = name;
        this.type = type;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.arg3 = arg3;
        this.arg4 = arg4;
        this.sensor = sensor;

        if (!FuzzyFlag.allTypeNames.contains(type)) {
            throw new IllegalArgumentException("Type " + type + " does not exist");
        }
    }

    public ArrayList<String> dependentNames() {
        ArrayList<String> result = new ArrayList<>();
        if (!basicTypeNames.contains(type)) {
            result.add(arg1);
            if (!type.equals(FuzzyNot.TYPE)) {
                result.add(arg2);
            }
        }
        return result;
    }
}
