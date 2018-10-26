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
        byte[] instruction = new byte[5];
        instruction[0] = 'A';
        instruction[1] = (byte) left.getMotorLevel(lastSensed);
        instruction[2] = (byte) right.getMotorLevel(lastSensed);
        instruction[3] = (byte) 0;
        instruction[4] = (byte) 0;
        return instruction;
    }

    @Override
    public String toString() {
        return "FuzzyAction:" + name + ";Left:" + left + ";Right:" + right;
    }
}
