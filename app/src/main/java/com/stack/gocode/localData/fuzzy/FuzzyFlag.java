package com.stack.gocode.localData.fuzzy;
import android.util.Log;

import com.stack.gocode.localData.DatabaseHelper;
import com.stack.gocode.localData.factory.FuzzyFlagFinder;
import com.stack.gocode.localData.factory.FuzzyFlagRow;
import com.stack.gocode.sensors.SensedValues;
import com.stack.gocode.localData.Named;

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
            args.setFlagDefaults(db);
        }
    }

    public FuzzyType getType() {
        return type;
    }

    public String getArg(int arg) {
        return args.getStr(type, arg);
    }

    public double getFuzzyValue(SensedValues sensed) {
        return type.getFuzzyValue(sensed, args);
    }
}
