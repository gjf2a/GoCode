package com.stack.gocode.localData.factory;

import com.stack.gocode.localData.fuzzy.FuzzyFlag;
import com.stack.gocode.localData.fuzzy.FuzzyType;
import com.stack.gocode.sensors.SensedValues;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by gabriel on 10/26/18.
 */

public class FuzzyFlagFactory implements FuzzyFlagFinder {
    private ArrayDeque<FuzzyFlagRow> flagRows = new ArrayDeque<>();
    private HashMap<String,FuzzyFlag> generatedFlags = new HashMap<>();
    private int numSkipped = 0;

    public ArrayList<FuzzyFlag> allGeneratedFlags() {
       return new ArrayList<>(generatedFlags.values());
    }

    public FuzzyFlag generateDefaultFlag(String project) {
        FuzzyFlag generated = new FuzzyFlag(new FuzzyFlagRow(project, "fuzzy" + (generatedFlags.size() + 1), FuzzyType.RISING.name(), "0", "0", "0", "0", SensedValues.SENSOR_NAMES[0]), this);
        addFuzzyFlag(generated);
        return generated;
    }

    public int flagCount() {
        return generatedFlags.size();
    }

    public void addFlagRow(FuzzyFlagRow row) {
        flagRows.addLast(row);
    }

    public boolean hasPendingRows() {
        return !flagRows.isEmpty();
    }

    public boolean isStuck() {
        return numSkipped > flagRows.size();
    }

    public boolean fuzzyFlagExists(String name) {
        return generatedFlags.containsKey(name);
    }

    public FuzzyFlag getFuzzyFlag(String name) {
        return generatedFlags.get(name);
    }

    public void addFuzzyFlag(FuzzyFlag flag) {
        generatedFlags.put(flag.getName(), flag);
    }

    public void updateFuzzyFlag(FuzzyFlag newFlag, String oldName) {
        if (!oldName.equals(newFlag.getName())) {
            generatedFlags.remove(oldName);
        }
        addFuzzyFlag(newFlag);
    }

    public void processNextRow() {
        FuzzyFlagRow row = flagRows.removeFirst();
        boolean allThere = true;
        for (String dependent: row.dependentNames()) {
            if (!generatedFlags.containsKey(dependent)) {
                allThere = false;
            }
        }

        if (allThere) {
            generatedFlags.put(row.getName(), new FuzzyFlag(row, this));
            numSkipped = 0;
        } else {
            flagRows.addLast(row);
            numSkipped += 1;
        }
    }

    public void delFuzzyFlag(String name) {
        generatedFlags.remove(name);
    }
}
