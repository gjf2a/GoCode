package com.stack.gocode.localData.factory;

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
    private FuzzyFlagFactory flagger = new FuzzyFlagFactory();
    private HashMap<String,Defuzzifier> defuzzifiers = new HashMap<>();
    private HashMap<String,FuzzyAction> fuzzyActions = new HashMap<>();

    public ArrayList<FuzzyFlag> allGeneratedFlags() {
        return flagger.allGeneratedFlags();
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
            throw new IllegalStateException("No fuzzy flags created yet");
        } else if (numDefuzzifiers() == 0) {
            throw new IllegalStateException("No defuzzifiers created yet");
        }
        FuzzyFlag firstFlag = allGeneratedFlags().get(0);
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
