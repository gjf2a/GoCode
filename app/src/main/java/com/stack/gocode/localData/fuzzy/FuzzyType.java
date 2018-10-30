package com.stack.gocode.localData.fuzzy;

import com.stack.gocode.sensors.SensedValues;

import java.util.ArrayList;

/**
 * Created by gabriel on 10/30/18.
 */

public enum FuzzyType {
    FALLING {
        @Override
        public double getFuzzyValue(SensedValues sensedValues, FuzzyArgs args) {
            assertMatch(args, this);
            double sensed = args.getSensedValue(sensedValues);
            double fallStart = args.getNum(1);
            double fallEnd = args.getNum(2);
            return sensed > fallEnd ? 0.0
                    : sensed < fallStart ? 1.0 : (fallEnd - sensed) / (fallEnd - fallStart);
        }

        @Override
        public boolean isNum() {
            return true;
        }

        @Override
        public int numArgs() {
            return 2;
        }
    },
    RISING {
        @Override
        public double getFuzzyValue(SensedValues sensedValues, FuzzyArgs args) {
            assertMatch(args, this);
            double sensed = args.getSensedValue(sensedValues);
            double riseStart = args.getNum(1);
            double riseEnd = args.getNum(2);
            return sensed > riseEnd ? 1.0
                    : sensed < riseStart ? 0.0 : (sensed - riseStart) / (riseEnd - riseStart);
        }

        @Override
        public boolean isNum() {
            return true;
        }

        @Override
        public int numArgs() {
            return 2;
        }
    },
    TRIANGLE {
        @Override
        public double getFuzzyValue(SensedValues sensedValues, FuzzyArgs args) {
            assertMatch(args, this);
            double sensed = args.getSensedValue(sensedValues);
            double start = args.getNum(1);
            double peak = args.getNum(2);
            double end = args.getNum(3);
            return sensed > end ? 0.0 : sensed < start ? 0.0 : sensed < peak ? (sensed - start) / (peak - start) : (end - sensed) / (end - peak);
        }

        @Override
        public boolean isNum() {
            return true;
        }

        @Override
        public int numArgs() {
            return 3;
        }
    },
    TRAPEZOID {
        @Override
        public double getFuzzyValue(SensedValues sensedValues, FuzzyArgs args) {
            assertMatch(args, this);
            double sensed = args.getSensedValue(sensedValues);
            double start = args.getNum(1);
            double peakStart = args.getNum(2);
            double peakEnd = args.getNum(3);
            double end = args.getNum(4);
            return sensed > end ? 0.0 : sensed < start ? 0.0 : sensed > peakStart && sensed < peakEnd ? 1.0 : sensed >= peakEnd ? (end - sensed) / (end - peakEnd) : (sensed - start) / (peakStart - start);
        }

        @Override
        public boolean isNum() {
            return true;
        }

        @Override
        public int numArgs() {
            return 4;
        }
    },
    AND {
        @Override
        public double getFuzzyValue(SensedValues sensedValues, FuzzyArgs args) {
            assertMatch(args, this);
            return Math.min(args.getFlag(1).getFuzzyValue(sensedValues), args.getFlag(2).getFuzzyValue(sensedValues));
        }

        @Override
        public boolean isNum() {
            return false;
        }

        @Override
        public int numArgs() {
            return 2;
        }
    },
    OR {
        @Override
        public double getFuzzyValue(SensedValues sensedValues, FuzzyArgs args) {
            assertMatch(args, this);
            return Math.max(args.getFlag(1).getFuzzyValue(sensedValues), args.getFlag(2).getFuzzyValue(sensedValues));
        }

        @Override
        public boolean isNum() {
            return false;
        }

        @Override
        public int numArgs() {
            return 2;
        }
    },
    NOT {
        @Override
        public double getFuzzyValue(SensedValues sensedValues, FuzzyArgs args) {
            assertMatch(args, this);
            return 1.0 - args.getFlag(1).getFuzzyValue(sensedValues);
        }

        @Override
        public boolean isNum() {
            return false;
        }

        @Override
        public int numArgs() {
            return 1;
        }
    };

    public static FuzzyType findType(String typeName) {
        for (FuzzyType type: FuzzyType.values()) {
            if (typeName.equals(type.name())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Illegal fuzzy type: " + typeName);
    }

    abstract public double getFuzzyValue(SensedValues sensedValues, FuzzyArgs args);
    abstract public boolean isNum();
    abstract public int numArgs();

    private static void assertMatch(FuzzyArgs args, FuzzyType type) {
        if (args.isNum() != type.isNum()) {
            throw new IllegalArgumentException("Invoked " + typeStr(type.isNum()) + " on " + typeStr(args.isNum()) + " args");
        }
    }

    private static String typeStr(boolean num) {
        return num ? "numerical"  : "flag";
    }

    public static String[] names() {
        String[] result = new String[values().length];
        for (int i = 0; i < values().length; i++) {
            result[i] = values()[i].name();
        }
        return result;
    }
}
