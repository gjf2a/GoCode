package com.stack.gocode.localData.fuzzy;

import android.content.ContentValues;

import com.stack.gocode.localData.DatabaseHelper;
import com.stack.gocode.sensors.SensedValues;

import java.util.LinkedHashSet;

/**
 * Created by gabriel on 10/25/18.
 */

abstract public class FuzzyFlag {
    private String name;

    public static final LinkedHashSet<String> allTypeNames = new LinkedHashSet<>();
    static {
        allTypeNames.add(FallingFuzzyFlag.TYPE);
        allTypeNames.add(RisingFuzzyFlag.TYPE);
        allTypeNames.add(TrapezoidFuzzyFlag.TYPE);
        allTypeNames.add(TriangleFuzzyFlag.TYPE);
        allTypeNames.add(FuzzyAnd.TYPE);
        allTypeNames.add(FuzzyOr.TYPE);
        allTypeNames.add(FuzzyNot.TYPE);
    }

    public ContentValues getContentValues(String project) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.FLAGS_PROJECT, project);
        values.put(DatabaseHelper.FLAGS_FLAG, getName());
        values.put(DatabaseHelper.FUZZY_FLAGS_TYPE, getClass().getSimpleName());
        addContentValues(values);
        return values;
    }

    abstract protected void addContentValues(ContentValues values);

    public FuzzyFlag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    abstract public String getType();

    abstract public double getFuzzyValue(SensedValues sensed);

    abstract public FuzzyFlag updatedName(String newName);
    abstract public FuzzyFlag updatedSensor(String updatedSensor);
    abstract public FuzzyFlag updatedArg1(String arg1, DatabaseHelper db);
    abstract public FuzzyFlag updatedArg2(String arg2, DatabaseHelper db);
    abstract public FuzzyFlag updatedArg3(String arg3, DatabaseHelper db);
    abstract public FuzzyFlag updatedArg4(String arg4, DatabaseHelper db);
}
