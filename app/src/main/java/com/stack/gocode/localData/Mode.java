package com.stack.gocode.localData;

public class Mode {
    private String name;
    private Action action;
    private TransitionTable tt;
    private String ttName;

    public Mode() {
        name = "";
        action = new Action();
        tt = new TransitionTable();
        ttName = tt.getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public TransitionTable getNextLayer() {
        return tt;
    }

    public void setNextLayer(TransitionTable nextLayer) {
        this.tt = nextLayer;
        ttName = tt.getName();
    }

    public String getTtName() {
        return ttName;
    }

    public void setTtName(String name) {
        ttName = name;
    }

    @Override
    public String toString() {
        return name + " " + action + " " + tt.getName();
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
