package com.stack.gocode.localData;

import com.stack.gocode.sensors.SensedValues;

abstract public class Flag implements Comparable<Flag>, Named {
    private String name;
    private boolean isTrue;

    public Flag(String name) {
        isTrue = false;
        this.name = name;
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

    abstract public void updateCondition(SensedValues sensedValues);

    @Override
    public int compareTo(Flag flag) {
        return toString().compareTo(flag.toString());
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Flag) {
            Flag that = (Flag)other;
            return this.compareTo(that) == 0;
        } else {
            throw new UnsupportedOperationException("Invalid comparison between Flag and not-Flag");
        }
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}