package com.stack.gocode.localData;

import java.util.ArrayList;

public class TransitionTable {
    private String name;
    private ArrayList<Row> triggerList;

    public TransitionTable() {
        this.name = "";

        triggerList = new ArrayList<Row>();
    }

    public String getName() {
        return name;
    }

    public Flag getFlag(int pos) { return triggerList.get(pos).getFirst(); }

    public Mode getMode(int pos) { return triggerList.get(pos).getSecond(); }

    public void setMode(int pos, Mode mode) {
        getRow(pos).setSecond(mode);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addRow(Flag flag, Mode mode) {
        triggerList.add(new Row(flag, mode));
    }

    public void deleteRow(int position) {
        triggerList.remove(position);
    }

    public boolean deleteRow(Row row) {
        for (int i = 0; i < triggerList.size(); i++) {
            if (triggerList.get(i).equals(row)) {
                triggerList.remove(i);
                return true;
            }
        }
        return false;
    }

    public Row getRow(int position) {
        return triggerList.get(position);
    }

    public int getRowPosition(Duple<Flag, Mode> row) {
        for (int i = 0; i < triggerList.size(); i++) {
            if (triggerList.get(i).equals(row)) {
                return i;
            }
        }
        return -1;
    }

    public Mode getTriggeredMode() {
        for (Row d : triggerList) {
            if (d.getFirst().isTrue()) {
                return d.getSecond();
            }
        }
        return new Mode();
    }

    public ArrayList<Row> getTriggerList() {
        return triggerList;
    }

    public int getSize() {
        return triggerList.size();
    }


    @Override
    public String toString() {
        return name + " " + triggerList.toString();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof TransitionTable) && (toString().equals(other.toString()));
    }
}
