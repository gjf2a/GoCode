package com.stack.gocode.localData;

public class Mode {
    private String name;
    private InstructionCreator action;
    private TransitionTable tt;

    public Mode(String name, InstructionCreator action, TransitionTable tt) {
        if (tt == null || action == null) {
            throw new IllegalArgumentException("null arguments to Mode constructor");
        }
        this.name = name;
        this.action = action;
        this.tt = tt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InstructionCreator getAction() {
        return action;
    }

    public String getActionName() {
        return action == null ? "No Action" : action.getName();
    }

    public void setAction(InstructionCreator action) {
        this.action = action;
    }

    public TransitionTable getNextLayer() {
        return tt;
    }

    public void setNextLayer(TransitionTable nextLayer) {
        this.tt = nextLayer;
    }

    public String getTtName() {
        return tt.getName();
    }

    @Override
    public String toString() {
        return name + " " + action + " " + getTtName();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof Mode) && (toString().equals(other.toString()));
    }
}
