package com.stack.gocode.localData.fuzzy;

/**
 * Created by gabriel on 10/25/18.
 */

public class Defuzzifier {
    private String name;
    private double speed0, speed1;

    public Defuzzifier(String name, double speed0, double speed1) {
        this.name = name;
        this.speed0 = speed0;
        this.speed1 = speed1;
    }

    public int defuzzify(double fuzzy) {
        return defuzzcalc(fuzzy, speed0, speed1);
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
        return "Defuzzifier[" + speed0 + "," + speed1 + "]";
    }
}
