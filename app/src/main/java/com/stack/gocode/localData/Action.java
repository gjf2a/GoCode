package com.stack.gocode.localData;

public class Action implements InstructionCreator {
    private int leftMotorInput;
    private int rightMotorInput;
    private int rowNumber;
    private boolean resetLeftCount;
    private boolean resetRightCount;
    private String name;

    public Action(String name, int leftMotorInput, int rightMotorInput, boolean resetLeftCount, boolean resetRightCount) {
        this.leftMotorInput = leftMotorInput;
        this.rightMotorInput = rightMotorInput;
        this.resetLeftCount = resetLeftCount;
        this.resetRightCount = resetRightCount;
        this.name = name;
    }

    public int getLeftMotorInput() {
        return leftMotorInput;
    }

    public void setLeftMotorInput(int leftMotorInput) {
        this.leftMotorInput = leftMotorInput;
    }

    public int getRightMotorInput() {
        return rightMotorInput;
    }

    public void setRightMotorInput(int rightMotorInput) {
        this.rightMotorInput = rightMotorInput;
    }

    public boolean isResetLeftCount() {
        return resetLeftCount;
    }

    public void setResetLeftCount(boolean resetLeftCount) {
        this.resetLeftCount = resetLeftCount;
    }

    public boolean isResetRightCount() {
        return resetRightCount;
    }

    public void setResetRightCount(boolean resetRightCount) {
        this.resetRightCount = resetRightCount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public byte[] getInstruction() {
        byte[] instruction = new byte[5];
        instruction[0] = 'A';
        instruction[1] = (byte) leftMotorInput;
        instruction[2] = (byte) rightMotorInput;
        instruction[3] = (byte) getRLCint();
        instruction[4] = (byte) getRRCint();
        return instruction;
    }

    private int booleanToInt(boolean bool) {
        return bool ? 1 : 0;
    }

    public int getRLCint() {
        return booleanToInt(resetLeftCount);
    }

    public int getRRCint() {
        return booleanToInt(resetRightCount);
    }

    @Override
    public String toString() {
        return "Name: " + name + ";  Left Motor Input: " + leftMotorInput + ". Right Motor Input: " + rightMotorInput + ". Reset Left Motor Count? " + resetLeftCount + ". Reset Right Motor Count? " + resetRightCount;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Action) {
            return name.equals(((Action) other).getName());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
