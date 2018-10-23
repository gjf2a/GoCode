package com.stack.gocode.localData;

import java.util.ArrayList;
public class Flag implements Comparable{
    private boolean isTrue;
    private boolean greaterThan;

    private String sensor, name;
    private double triggerValue;

    public Flag() {
        isTrue = false;
        greaterThan = false;
        sensor = "";
        triggerValue = -1;
        name = "";
    }

    public boolean isUsable() {
        return sensor.length() > 0 && name.length() > 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isTrue() {
        return isTrue;
    }

    public void setTrue(boolean aTrue) {
        isTrue = aTrue;
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

    public boolean updateCondition(double sensorInfo) {
        isTrue = greaterThan ? sensorInfo > triggerValue : sensorInfo < triggerValue;
        return isTrue;
    }

    @Override
    public int compareTo(Object T) {
        return toString().compareTo(T.toString());
    }

    public String toString() {
        return name + "; " + sensor +  ((greaterThan)? " > " : " < ") + triggerValue;
    }
}