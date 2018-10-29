package com.stack.gocode.localData;

import com.stack.gocode.com.stack.gocode.exceptions.ItemNotFoundException;

import java.util.ArrayList;

public class TransitionTable {
    private String name;
    private ArrayList<Row> triggerList;

    public TransitionTable(String name) {
        this.name = name;
        triggerList = new ArrayList<Row>();
    }

    public String getName() {
        return name;
    }

    public Flag getFlag(int pos) { return triggerList.get(pos).getFlag(); }

    public Mode getMode(int pos) { return triggerList.get(pos).getMode(); }

    public void setMode(int pos, Mode mode) {
        getRow(pos).setMode(mode);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addRow(Row row) {
        triggerList.add(new Row(row));
    }

    public void deleteRow(int position) {
        triggerList.remove(position);
    }

    public boolean deleteRow(long id) {
        for (int i = 0; i < triggerList.size(); i++) {
            if (triggerList.get(i).getRowId() == id) {
                triggerList.remove(i);
                return true;
            }
        }
        return false;
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

    public Mode getTriggeredMode(Mode currentMode) {
        for (Row d : triggerList) {
            if (d.getFlag().isTrue()) {
                return d.getMode();
            }
        }
        return currentMode;
    }

    public ArrayList<Row> getTriggerList() {
        return triggerList;
    }

    public int getNumRows() {
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
