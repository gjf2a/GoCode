package com.stack.gocode.localData.fuzzy;

/**
 * Created by gabriel on 10/25/18.
 */

import com.stack.gocode.localData.Named;

public class Defuzzifier implements Named {
    private String name;
    private int speed1, speed2;

    public Defuzzifier(String name, int speed0, int speed1) {
        this.name = name;
        this.speed1 = speed0;
        this.speed2 = speed1;
    }

    public String getName() {return name;}
    public int getSpeed1() {return speed1;}
    public int getSpeed2() {return speed2;}

    public void setName(String newName) {
        this.name = newName;
    }

    public void setSpeed1(int newSpeed) {
        this.speed1 = newSpeed;
    }

    public void setSpeed2(int newSpeed) {
        this.speed2 = newSpeed;
    }

    public int defuzzify(double fuzzy) {
        return defuzzcalc(fuzzy, speed1, speed2);
    }

    private static int defuzzcalc(double fuzzy, double s0, double s1) {
        if (s0 > s1) {
            return defuzzcalc(1.0 - fuzzy, s1, s0);
        } else {
            return (int)(s0 + fuzzy * (s1 - s0));
        }
    }

    @Override
    public String toString() {
        return "Defuzzifier[" + speed1 + "," + speed2 + "]";
    }
}
