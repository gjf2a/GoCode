package com.stack.gocode.sensors;

import com.stack.gocode.localData.Named;

/**
 * Created by gabriel on 11/1/18.
 */

public class Symbol implements Named {
    private String name;
    private String one, two;
    private boolean abs;

    public Symbol(String name) {
        this(name, SensedValues.SENSOR_NAMES[0], SensedValues.SENSOR_NAMES[0], false);
    }

    public Symbol(String name, String one, String two, boolean abs) {
        this.name = name;
        this.abs = abs;
        this.one = one;
        this.two = two;
    }

    public int computeValueFrom(SensedValues sensed) {
        int diff = sensed.getSensedValueFor(one) - sensed.getSensedValueFor(two);
        if (abs) {diff = Math.abs(diff);}
        return diff;
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean absoluteValue() {return abs;}
    public String getSensorOne() {return one;}
    public String getSensorTwo() {return two;}

    public void setName(String newName) {
        this.name = newName;
    }

    public void setAbsoluteValue(boolean newAbs) {this.abs = newAbs;}
    public void setSensorOne(String newOne) {this.one = newOne;}
    public void setSensorTwo(String newTwo) {this.two = newTwo;}

    @Override
    public String toString() {
        return "Symbol:" + getName() + ";" + getSensorOne() + ";" + getSensorTwo() + ";" + abs;
    }
}
