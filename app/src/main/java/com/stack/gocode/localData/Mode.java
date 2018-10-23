package com.stack.gocode.localData;

public class Mode {
    private String name;
    private Action action;
    private String ttName;
    private TransitionTable tt;

    public Mode() {
        this.name = "";
        this.action = null;
        this.ttName = "";
    }

    public Mode(String name, Action action, String tableName) {
        this.name = name;
        this.action = action;
        this.ttName = tableName;
        this.tt = null;
    }

    public Mode(String name, Action action, TransitionTable tt) {
        this(name, action, tt.getName());
        this.tt = tt;
    }

    public boolean isUsable() {return name.length() > 0 && action != null && action.isUsable() && ttName.length() > 0;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Action getAction() {
        return action;
    }

    public String getActionName() {
        return action == null ? "No Action" : action.getName();
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public TransitionTable getNextLayer() {
        return tt;
    }

    public void setNextLayer(TransitionTable nextLayer) {
        this.ttName = nextLayer.getName();
        this.tt = nextLayer;
    }

    public String getTtName() {
        return ttName;
    }

    public void setTtName(String name) {
        this.ttName = name;
        this.tt = null;
    }

    @Override
    public String toString() {
        return name + " " + action + " " + ttName;
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
