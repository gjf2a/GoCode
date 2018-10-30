package com.stack.gocode.localData.fuzzy;
import com.stack.gocode.localData.DatabaseHelper;
import com.stack.gocode.localData.factory.FuzzyFlagFinder;
import com.stack.gocode.localData.factory.FuzzyFlagRow;
import com.stack.gocode.sensors.SensedValues;

/**
 * Created by gabriel on 10/25/18.
 */

public class FuzzyFlag {
    private String name;
    private FuzzyType type;
    private FuzzyArgs args;

    public FuzzyFlag(FuzzyFlagRow row, FuzzyFlagFinder db) {
        this.name = row.name;
        this.type = FuzzyType.valueOf(row.type);
        this.args = new FuzzyArgs(row, db);
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
