package com.stack.gocode.localData.fuzzy.factory;

import com.stack.gocode.com.stack.gocode.exceptions.ItemNotFoundException;
import com.stack.gocode.localData.fuzzy.Defuzzifier;
import com.stack.gocode.localData.fuzzy.FuzzyAction;
import com.stack.gocode.localData.fuzzy.FuzzyFlag;
import com.stack.gocode.localData.fuzzy.FuzzyMotor;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by gabriel on 10/26/18.
 */

public class FuzzyFactory {
    private FuzzyFlagFactory flagger = new FuzzyFlagFactory();
    private HashMap<String,Defuzzifier> defuzzifiers = new HashMap<>();
    private HashMap<String,FuzzyAction> fuzzyActions = new HashMap<>();

    public ArrayList<FuzzyFlag> allGeneratedFlags() {
        return flagger.allGeneratedFlags();
    }

    public void addFlagRow(FuzzyFlagRow row) {
        flagger.addFlagRow(row);
    }

    public void generateFuzzyFlags() throws ItemNotFoundException {
        while (flagger.hasPendingRows() && !flagger.isStuck()) {
            flagger.processNextRow();
        }
    }

    public void addDefuzzifier(String name, int speed0, int speed1) {
        defuzzifiers.put(name, new Defuzzifier(name, speed0, speed1));
    }

    public void addFuzzyAction(String name, String leftFlag, String leftDefuzzifier, String rightFlag, String rightDefuzzifier) throws ItemNotFoundException {
        if (!flagger.containsFlag(leftFlag)) {
            throw new ItemNotFoundException("Fuzzy flag " + leftFlag);
        } else if (!flagger.containsFlag(rightFlag)) {
            throw new ItemNotFoundException("Fuzzy flag " + rightFlag);
        } else if (!defuzzifiers.containsKey(leftDefuzzifier)) {
            throw new ItemNotFoundException("Defuzzifier " + leftDefuzzifier);
        } else if (!defuzzifiers.containsKey(rightDefuzzifier)) {
            throw new ItemNotFoundException("Defuzzifier " + rightDefuzzifier);
        } else {
            fuzzyActions.put(name, new FuzzyAction(name, new FuzzyMotor(flagger.getFlag(leftFlag), defuzzifiers.get(leftDefuzzifier)), new FuzzyMotor(flagger.getFlag(rightFlag), defuzzifiers.get(rightDefuzzifier))));
        }
    }
}
