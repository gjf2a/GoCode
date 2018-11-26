package com.stack.gocode.localData.fuzzy;
import android.util.Log;

import com.stack.gocode.localData.DatabaseHelper;
import com.stack.gocode.localData.factory.FuzzyFlagFinder;
import com.stack.gocode.localData.factory.FuzzyFlagRow;
import com.stack.gocode.sensors.SensedValues;
import com.stack.gocode.localData.Named;

import java.util.TreeSet;

/**
 * Created by gabriel on 10/25/18.
 */

public class FuzzyFlag implements Named {
    public static final String TAG = FuzzyFlag.class.getSimpleName();
    private String name;
    private FuzzyType type;
    private FuzzyArgs args;

    public FuzzyFlag(FuzzyFlagRow row, FuzzyFlagFinder db) {
        Log.i(TAG,"Initializing fuzzy flag from: " + row);
        this.name = row.getName();
        this.type = FuzzyType.valueOf(row.getType());
        this.args = new FuzzyArgs(type, row, db);
    }

    private FuzzyFlag() {}

    public FuzzyFlag duplicate() {
        FuzzyFlag copy = new FuzzyFlag();
        copy.name = this.name;
        copy.type = this.type;
        copy.args = this.args.duplicate();
        return copy;
    }

    public boolean hasCycle() {
        return allChildNames().contains(getName());
    }

    public boolean isCycleChild(FuzzyFlag candidateChild) {
        return candidateChild.getName().equals(this.getName()) || candidateChild.allChildNames().contains(getName());
    }

    public TreeSet<String> allChildNames() {
        TreeSet<String> all = new TreeSet<>();
        addChildNames(all);
        return all;
    }

    private void addChildNames(TreeSet<String> children) {
        if (!type.isNum()) {
            for (int i = 0; i < type.numArgs(); i++) {
                FuzzyFlag child = args.getFlag(i);
                if (child != null && !children.contains(child.getName())) {
                    children.add(child.getName());
                    child.addChildNames(children);
                }
            }
        }
    }

    public String getName() {return name;}

    public void setName(String name) {
        this.name = name;
    }

    public void setSensor(String sensor) {
        args.setSensor(sensor);
    }

    public String getSensor() {
        return args.getSensor();
    }

    public void setArg(int arg, String value, DatabaseHelper db) {
        args.set(arg, value, db);
    }

    public void setType(String typeName, DatabaseHelper db) {
        type = FuzzyType.findType(typeName);
        if (type.isNum() && !args.isNum()) {
            args.setNumericalDefaults(db);
        } else if (!type.isNum() && args.isNum()) {
            args.setFlagDefaults(this, db);
        }
    }

    public FuzzyType getType() {
        return type;
    }

    public String getArg(int arg) {
        return args.getStr(type, arg);
    }

    public double getFuzzyValue(SensedValues sensed) {
        Log.i(TAG,"Fuzzifying " + sensed + "; type: " + type);
        if (args.isNum()) {
            Log.i(TAG, "Sensor: " + getSensor());
        }
        double fuzz = type.getFuzzyValue(sensed, args);
        Log.i(TAG, "Fuzzy value: " + fuzz);
        return fuzz;
    }

    @Override
    public String toString() {
        return "FuzzyFlag:" + getType() + ";" + args;
    }

    public void addSensorsInUse(TreeSet<String> sensorsInUse) {
        if (type.isNum()) {
            sensorsInUse.add(args.getSensor());
        } else {
            for (int i = 0; i < type.numArgs(); i++) {
                args.getFlag(i).addSensorsInUse(sensorsInUse);
            }
        }
    }
}
