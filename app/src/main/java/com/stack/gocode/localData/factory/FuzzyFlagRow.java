package com.stack.gocode.localData.factory;
import com.stack.gocode.localData.fuzzy.FuzzyArgs;
import com.stack.gocode.localData.fuzzy.FuzzyType;
import com.stack.gocode.sensors.SensedValues;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by gabriel on 10/26/18.
 */

public class FuzzyFlagRow {
    private String project, name,  type,  sensor;
    private String[] args = new String[FuzzyArgs.NUM_FUZZY_ARGS];

    public String getProject() {return project;}
    public String getName() {return name;}
    public String getType() {return type;}
    public String getSensor() {return sensor;}
    public String getArg(int arg) {return args[arg];}

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
        this.args[0] = arg1;
        this.args[1] = arg2;
        this.args[2] = arg3;
        this.args[3] = arg4;
        this.sensor = sensor;

        if (!allTypeNames.contains(type)) {
            throw new IllegalArgumentException("Type " + type + " does not exist");
        }
    }

    public ArrayList<String> dependentNames() {
        ArrayList<String> result = new ArrayList<>();
        if (!basicTypeNames.contains(type)) {
            result.add(args[0]);
            if (!type.equals(FuzzyType.NOT)) {
                result.add(args[1]);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return project + "," + name + "," + type + "," + args[0] + "," + args[1] + "," + args[2] + "," + args[3] + "," + sensor;
    }
}
