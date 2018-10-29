package com.stack.gocode.localData.fuzzy;

import com.stack.gocode.localData.InstructionCreator;
import com.stack.gocode.sensors.SensedValues;

/**
 * Created by gabriel on 10/25/18.
 */

public class FuzzyAction implements InstructionCreator {
    private String name;
    private FuzzyMotor left, right;
    private SensedValues lastSensed;

    public FuzzyAction(String name, FuzzyMotor left, FuzzyMotor right) {
        this.left = left;
        this.right = right;
    }

    public void setLastSensed(SensedValues sensed) {
        this.lastSensed = sensed;
    }

    @Override
    public byte[] getInstruction() {
        return new byte[]{
                'A',
                (byte) left.getMotorLevel(lastSensed),
                (byte) right.getMotorLevel(lastSensed),
                (byte) 0,
                (byte) 0
        };
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "FuzzyAction:" + name + ";Left:" + left + ";Right:" + right;
    }
}
