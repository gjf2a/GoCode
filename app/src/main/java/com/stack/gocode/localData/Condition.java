package com.stack.gocode.localData;

public class Condition {
    boolean isTrue;
    boolean greaterThan;

    String sensor;
    double triggerValue;

    public Condition() {
        isTrue = false;
        greaterThan = false;
        sensor = "toDo";
        triggerValue = -1;
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
        isTrue = greaterThan ? triggerValue > sensorInfo : triggerValue < sensorInfo;
        return isTrue;
    }
}