package com.stack.gocode.localData;

import com.stack.gocode.sensors.SensedValues;

import java.util.TreeSet;

/**
 * Created by gabriel on 10/25/18.
 */

public interface InstructionCreator extends Named {
    byte[] getInstruction(SensedValues mostRecent);
    void addSensorsInUse(TreeSet<String> sensorsInUse);
}
