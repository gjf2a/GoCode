package com.stack.gocode.localData.factory;

import com.stack.gocode.com.stack.gocode.exceptions.ItemNotFoundException;
import com.stack.gocode.localData.fuzzy.FallingFuzzyFlag;
import com.stack.gocode.localData.fuzzy.FuzzyAnd;
import com.stack.gocode.localData.fuzzy.FuzzyFlag;
import com.stack.gocode.localData.fuzzy.FuzzyNot;
import com.stack.gocode.localData.fuzzy.FuzzyOr;
import com.stack.gocode.localData.fuzzy.RisingFuzzyFlag;
import com.stack.gocode.localData.fuzzy.TrapezoidFuzzyFlag;
import com.stack.gocode.localData.fuzzy.TriangleFuzzyFlag;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by gabriel on 10/26/18.
 */

public class FuzzyFlagFactory {
    private ArrayDeque<FuzzyFlagRow> flagRows = new ArrayDeque<>();
    private HashMap<String,FuzzyFlag> generatedFlags = new HashMap<>();
    private int numSkipped = 0;

    public ArrayList<FuzzyFlag> allGeneratedFlags() {
       return new ArrayList<>(generatedFlags.values());
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

    public boolean containsFlag(String name) {
        return generatedFlags.containsKey(name);
    }

    public FuzzyFlag getFlag(String name) {
        return generatedFlags.get(name);
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
            generatedFlags.put(row.name, builder(row));
            numSkipped = 0;
        } else {
            flagRows.addLast(row);
            numSkipped += 1;
        }
    }

    FuzzyFlag builder(FuzzyFlagRow row) {
        switch (row.type) {
            case TrapezoidFuzzyFlag.TYPE: return new TrapezoidFuzzyFlag(row.name, row.sensor, Double.parseDouble(row.arg1), Double.parseDouble(row.arg2), Double.parseDouble(row.arg3), Double.parseDouble(row.arg4));
            case RisingFuzzyFlag.TYPE: return new RisingFuzzyFlag(row.name, row.sensor, Double.parseDouble(row.arg1), Double.parseDouble(row.arg2));
            case FallingFuzzyFlag.TYPE: return new FallingFuzzyFlag(row.name, row.sensor, Double.parseDouble(row.arg1), Double.parseDouble(row.arg2));
            case TriangleFuzzyFlag.TYPE: return new TriangleFuzzyFlag(row.name, row.sensor, Double.parseDouble(row.arg1), Double.parseDouble(row.arg2), Double.parseDouble(row.arg3));
            case FuzzyAnd.TYPE: return new FuzzyAnd(row.name, generatedFlags.get(row.arg1), generatedFlags.get(row.arg2));
            case FuzzyOr.TYPE: return new FuzzyOr(row.name, generatedFlags.get(row.arg1), generatedFlags.get(row.arg2));
            case FuzzyNot.TYPE: return new FuzzyNot(row.name, generatedFlags.get(row.arg1));
            default: throw new IllegalStateException("Can't handle fuzzy type " + row.type);
        }
    }
}
