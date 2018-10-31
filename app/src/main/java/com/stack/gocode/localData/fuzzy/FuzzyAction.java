package com.stack.gocode.localData.fuzzy;

import com.stack.gocode.localData.InstructionCreator;
import com.stack.gocode.sensors.SensedValues;
import com.stack.gocode.localData.Named;

/**
 * Created by gabriel on 10/25/18.
 */

public class FuzzyAction implements InstructionCreator, Named {
    private String name;
    private FuzzyMotor left, right;

    public FuzzyAction(String name, FuzzyMotor left, FuzzyMotor right) {
        this.name = name;
        this.left = left;
        this.right = right;
    }

    public FuzzyMotor getLeft() {return left;}
    public FuzzyMotor getRight() {return right;}

    @Override
    public byte[] getInstruction(SensedValues mostRecent) {
        return new byte[]{
                'A',
                (byte) left.getMotorLevel(mostRecent),
                (byte) right.getMotorLevel(mostRecent),
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
