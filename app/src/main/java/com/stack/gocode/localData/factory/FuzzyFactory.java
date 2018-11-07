package com.stack.gocode.localData.factory;

import android.util.Log;

import com.stack.gocode.localData.Action;
import com.stack.gocode.localData.fuzzy.Defuzzifier;
import com.stack.gocode.localData.fuzzy.FuzzyAction;
import com.stack.gocode.localData.fuzzy.FuzzyFlag;
import com.stack.gocode.localData.fuzzy.FuzzyMotor;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by gabriel on 10/26/18.
 */

public class FuzzyFactory implements FuzzyFlagFinder {
    public static final String TAG = FuzzyFactory.class.getSimpleName();

    private FuzzyFlagFactory flagger = new FuzzyFlagFactory();
    private HashMap<String,Defuzzifier> defuzzifiers = new HashMap<>();
    private HashMap<String,FuzzyAction> fuzzyActions = new HashMap<>();

    public ArrayList<FuzzyFlag> getFuzzyFlagList() {
        return flagger.getFuzzyFlagList();
    }
    public ArrayList<Defuzzifier> allDefuzzifiers() {return new ArrayList<>(defuzzifiers.values());}
    public ArrayList<FuzzyAction> allFuzzyActions() {return new ArrayList<>(fuzzyActions.values());}

    public void addFlagRow(FuzzyFlagRow row) {
        flagger.addFlagRow(row);
    }

    public int numFuzzyFlags() {
        return flagger.flagCount();
    }
    public int numDefuzzifiers() {return defuzzifiers.size();}
    public int numFuzzyActions() {return fuzzyActions.size();}

    public boolean fuzzyFlagExists(String name) {
        return flagger.fuzzyFlagExists(name);
    }

    public FuzzyFlag generateDefaultFlag(String project) {
        return flagger.generateDefaultFlag(project);
    }

    public Defuzzifier generateDefaultDefuzzifier(String project) {
        String name = "defuzz" + (defuzzifiers.size() + 1);
        Defuzzifier generated = new Defuzzifier(name, Action.MIN_MOTOR_VALUE, Action.MAX_MOTOR_VALUE);
        defuzzifiers.put(generated.getName(), generated);
        return generated;
    }

    public FuzzyAction generateDefaultFuzzyAction(String project) {
        String name = "fuzzyAct" + (fuzzyActions.size() + 1);
        if (numFuzzyFlags() == 0) {
            generateDefaultFlag(project);
        } else if (numDefuzzifiers() == 0) {
            generateDefaultDefuzzifier(project);
        }
        FuzzyFlag firstFlag = getFuzzyFlagList().get(0);
        Defuzzifier firstDefuzzifier = defuzzifiers.values().iterator().next();
        FuzzyAction generated = new FuzzyAction(name, new FuzzyMotor(firstFlag, firstDefuzzifier), new FuzzyMotor(firstFlag, firstDefuzzifier));
        fuzzyActions.put(generated.getName(), generated);
        return generated;
    }

    public void delDefuzzifier(String name) {
        defuzzifiers.remove(name);
    }

    public FuzzyFlag getFuzzyFlag(String name) {
        return flagger.getFuzzyFlag(name);
    }

    public void addFuzzyFlag(FuzzyFlag flag) {
        flagger.addFuzzyFlag(flag);
    }

    public void delFuzzyFlag(String name) {
        flagger.delFuzzyFlag(name);
    }

    public void updateFuzzyFlag(FuzzyFlag newFlag, String oldName) {
        flagger.updateFuzzyFlag(newFlag, oldName);
    }

    public void updateDefuzzifier(Defuzzifier updated, String oldName) {
        delDefuzzifier(oldName);
        defuzzifiers.put(updated.getName(), updated);
    }

    public void delFuzzyAction(String name) {
        fuzzyActions.remove(name);
    }

    public void updateFuzzyAction(FuzzyAction updated, String oldName) {
        delFuzzyAction(updated.getName());
        fuzzyActions.put(updated.getName(), updated);
    }

    public void generateFuzzyFlags() {
        while (flagger.hasPendingRows() && !flagger.isStuck()) {
            flagger.processNextRow();
            Log.i(TAG,"Processing a row");
        }
        if (flagger.isStuck()) {
            Log.i(TAG, "Rows left over");
        } else {
            Log.i(TAG, "All rows processed");
        }
    }

    public void addDefuzzifier(String name, int speed0, int speed1) {
        defuzzifiers.put(name, new Defuzzifier(name, speed0, speed1));
    }

    public boolean hasDefuzzifier(String name) {
        return defuzzifiers.containsKey(name);
    }

    public Defuzzifier getDefuzzifier(String name) {
        return defuzzifiers.get(name);
    }

    public void addFuzzyAction(String name, String leftFlag, String leftDefuzzifier, String rightFlag, String rightDefuzzifier) {
        if (!flagger.fuzzyFlagExists(leftFlag)) {
            Log.i(TAG, "Can't find " + leftFlag);
            leftFlag = generateDefaultFlag("default").getName();
        }
        if (!flagger.fuzzyFlagExists(rightFlag)) {
            Log.i(TAG, "Can't find " + rightFlag);
            rightFlag = generateDefaultFlag("default").getName();
        }
        if (!defuzzifiers.containsKey(leftDefuzzifier)) {
            if (defuzzifiers.size() > 0) {
                leftDefuzzifier = defuzzifiers.values().iterator().next().getName();
            } else {
                leftDefuzzifier = generateDefaultDefuzzifier("default").getName();
            }
        }
        if (!defuzzifiers.containsKey(rightDefuzzifier)) {
            if (defuzzifiers.size() > 0) {
                rightDefuzzifier = defuzzifiers.values().iterator().next().getName();
            } else {
                rightDefuzzifier = generateDefaultDefuzzifier("default").getName();
            }
        }

        fuzzyActions.put(name, new FuzzyAction(name, new FuzzyMotor(flagger.getFuzzyFlag(leftFlag), defuzzifiers.get(leftDefuzzifier)), new FuzzyMotor(flagger.getFuzzyFlag(rightFlag), defuzzifiers.get(rightDefuzzifier))));
    }

    public boolean hasFuzzyAction(String name) {
        return fuzzyActions.containsKey(name);
    }

    public FuzzyAction getFuzzyAction(String name) {
        return fuzzyActions.get(name);
    }
}
