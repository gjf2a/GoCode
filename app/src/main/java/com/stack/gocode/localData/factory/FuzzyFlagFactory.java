package com.stack.gocode.localData.factory;

import android.util.Log;

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
    public static final String TAG = FuzzyFlagFactory.class.getSimpleName();

    private ArrayDeque<FuzzyFlagRow> flagRows = new ArrayDeque<>();
    private HashMap<String,FuzzyFlag> generatedFlags = new HashMap<>();
    private int numSkipped = 0;

    public ArrayList<FuzzyFlag> getFuzzyFlagList() {
       return new ArrayList<>(generatedFlags.values());
    }

    public FuzzyFlag generateDefaultFlag(String project) {
        Log.i(TAG, "Generating a default fuzzy flag");
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
        Log.i(TAG, "Adding fuzzy flag: " + flag.getName());
        generatedFlags.put(flag.getName(), flag);
    }

    public void updateFuzzyFlag(FuzzyFlag newFlag, String oldName) {
        Log.i(TAG, "Updating " + oldName + " to " + newFlag.getName());
        if (!oldName.equals(newFlag.getName())) {
            generatedFlags.remove(oldName);
        }
        addFuzzyFlag(newFlag);
    }

    public void processNextRow() {
        FuzzyFlagRow row = flagRows.removeFirst();
        Log.i(TAG,"Processing row " + row);
        boolean allThere = true;
        for (String dependent: row.dependentNames()) {
            if (!generatedFlags.containsKey(dependent)) {
                Log.i(TAG, "Not yet generated for " + dependent);
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
