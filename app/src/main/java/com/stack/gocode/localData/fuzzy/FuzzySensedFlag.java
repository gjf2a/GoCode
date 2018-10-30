package com.stack.gocode.localData.fuzzy;

import android.content.ContentValues;

import com.stack.gocode.localData.DatabaseHelper;
import com.stack.gocode.sensors.SensedValues;

/**
 * Created by gabriel on 10/25/18.
 */

abstract public class FuzzySensedFlag extends FuzzyFlag {
    private String sensor;

    public FuzzySensedFlag(String name, String sensor) {
        super(name);
        this.sensor = sensor;
    }

    public String getSensor() {
        return sensor;
    }
    public void setSensor(String sensor) {
        this.sensor = sensor;
    }

    @Override
    protected void addContentValues(ContentValues values) {
        values.put(DatabaseHelper.FLAGS_SENSOR, sensor);
        addFuzzyBounds(values);
    }

    abstract protected void addFuzzyBounds(ContentValues values);

    public double getFuzzyValue(SensedValues sensed) {
        return fuzzify(sensed.getSensedValueFor(getSensor()));
    }



    abstract public double fuzzify(double sensedValue);
}
