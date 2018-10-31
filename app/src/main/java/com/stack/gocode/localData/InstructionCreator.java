package com.stack.gocode.localData;

import com.stack.gocode.sensors.SensedValues;

/**
 * Created by gabriel on 10/25/18.
 */

public interface InstructionCreator extends Named {
    public byte[] getInstruction(SensedValues mostRecent);
}
