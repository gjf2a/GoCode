package com.stack.gocode.localData.flagtypes;

import android.util.Log;

import com.stack.gocode.localData.Flag;
import com.stack.gocode.sensors.SensedValues;

/**
 * Created by gabriel on 11/16/18.
 */

public class SimpleSensorFlag extends Flag {
    public static final String TAG = SimpleSensorFlag.class.getSimpleName();

    private boolean greaterThan;
    private double triggerValue;
    private String sensor;

    public SimpleSensorFlag(String name, String sensor, boolean greaterThan, double triggerValue) {
        super(name);
        this.greaterThan = greaterThan;
        this.sensor = sensor;
        this.triggerValue = triggerValue;
    }

    public boolean isGreaterThan() {
        return greaterThan;
    }

    public void setGreaterThan(boolean greaterThan) {
        this.greaterThan = greaterThan;
    }

    public String getSensor() {
        return sensor;
    }

    public void setSensor(String sensor) {
        this.sensor = sensor;
    }

    public double getTriggerValue() {
        return triggerValue;
    }

    public void setTriggerValue(double triggerValue) {
        this.triggerValue = triggerValue;
    }

    public void updateCondition(SensedValues sensedValues) {
        if (sensedValues.hasSensor(sensor)) {
            double sensorInfo = sensedValues.getSensedValueFor(sensor);
            setTrue(greaterThan ? sensorInfo > triggerValue : sensorInfo < triggerValue);
        } else {
            setTrue(false);
            Log.i(TAG, "Sensor " + sensor + " not available");
        }
    }

    @Override
    public String toString() {
        return getName() + "; " + sensor +  ((greaterThan)? " > " : " < ") + triggerValue;
    }
}
