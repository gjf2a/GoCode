package com.stack.gocode.localData.factory;
import com.stack.gocode.localData.fuzzy.FuzzyType;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by gabriel on 10/26/18.
 */

public class FuzzyFlagRow {
    public String project, name,  type,  arg1,  arg2,  arg3,  arg4,  sensor;

    public static final HashSet<String> basicTypeNames = new HashSet<>();

    public static final HashSet<String> allTypeNames = new HashSet<>();
    static {
        basicTypeNames.add(FuzzyType.FALLING.name());
        basicTypeNames.add(FuzzyType.RISING.name());
        basicTypeNames.add(FuzzyType.TRAPEZOID.name());
        basicTypeNames.add(FuzzyType.TRIANGLE.name());
        allTypeNames.addAll(basicTypeNames);
        allTypeNames.add(FuzzyType.AND.name());
        allTypeNames.add(FuzzyType.OR.name());
        allTypeNames.add(FuzzyType.NOT.name());
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

        if (!allTypeNames.contains(type)) {
            throw new IllegalArgumentException("Type " + type + " does not exist");
        }
    }

    public ArrayList<String> dependentNames() {
        ArrayList<String> result = new ArrayList<>();
        if (!basicTypeNames.contains(type)) {
            result.add(arg1);
            if (!type.equals(FuzzyType.NOT)) {
                result.add(arg2);
            }
        }
        return result;
    }
}
