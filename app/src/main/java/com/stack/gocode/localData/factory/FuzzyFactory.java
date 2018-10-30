package com.stack.gocode.localData.factory;

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
    private FuzzyFlagFactory flagger = new FuzzyFlagFactory();
    private HashMap<String,Defuzzifier> defuzzifiers = new HashMap<>();
    private HashMap<String,FuzzyAction> fuzzyActions = new HashMap<>();

    public ArrayList<FuzzyFlag> allGeneratedFlags() {
        return flagger.allGeneratedFlags();
    }

    public void addFlagRow(FuzzyFlagRow row) {
        flagger.addFlagRow(row);
    }

    public int numFuzzyFlags() {
        return flagger.flagCount();
    }

    public boolean fuzzyFlagExists(String name) {
        return flagger.fuzzyFlagExists(name);
    }

    public FuzzyFlag generateDefaultFlag(String project) {
        return flagger.generateDefaultFlag(project);
    }

    public FuzzyFlag getFuzzyFlag(String name) {
        return flagger.getFuzzyFlag(name);
    }

    public void addFuzzyFlag(FuzzyFlag flag) {
        flagger.addFuzzyFlag(flag);
    }

    public void updateFuzzyFlag(FuzzyFlag newFlag, String oldName) {
        flagger.updateFuzzyFlag(newFlag, oldName);
    }

    public void generateFuzzyFlags() {
        while (flagger.hasPendingRows() && !flagger.isStuck()) {
            flagger.processNextRow();
        }
    }

    public void addDefuzzifier(String name, int speed0, int speed1) {
        defuzzifiers.put(name, new Defuzzifier(name, speed0, speed1));
    }

    public void addFuzzyAction(String name, String leftFlag, String leftDefuzzifier, String rightFlag, String rightDefuzzifier) {
        if (!flagger.fuzzyFlagExists(leftFlag)) {
            throw new IllegalStateException("Fuzzy flag " + leftFlag + " missing");
        } else if (!flagger.fuzzyFlagExists(rightFlag)) {
            throw new IllegalStateException("Fuzzy flag " + rightFlag + " missing");
        } else if (!defuzzifiers.containsKey(leftDefuzzifier)) {
            throw new IllegalStateException("Defuzzifier " + leftDefuzzifier + " missing");
        } else if (!defuzzifiers.containsKey(rightDefuzzifier)) {
            throw new IllegalStateException("Defuzzifier " + rightDefuzzifier + " missing");
        } else {
            fuzzyActions.put(name, new FuzzyAction(name, new FuzzyMotor(flagger.getFuzzyFlag(leftFlag), defuzzifiers.get(leftDefuzzifier)), new FuzzyMotor(flagger.getFuzzyFlag(rightFlag), defuzzifiers.get(rightDefuzzifier))));
        }
    }

    public boolean hasFuzzyAction(String name) {
        return fuzzyActions.containsKey(name);
    }

    public FuzzyAction getFuzzyAction(String name) {
        return fuzzyActions.get(name);
    }
}
