package com.stack.gocode.localData.factory;

import android.util.Log;

import com.stack.gocode.localData.Flag;
import com.stack.gocode.localData.Row;
import com.stack.gocode.localData.Action;
import com.stack.gocode.localData.Mode;
import com.stack.gocode.localData.TransitionTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by gabriel on 10/26/18.
 */

/*
To begin, you are only allowed to create Flags and Actions. Once a Flag and Action are created,
a default Mode is created using an Action and a default Table, which contains one row with that
Flag as a condition and the Mode with that Action leading back to the Table as its action.

From here, creating new Modes uses that Table as a default, and creating new Rows uses the Flag
and Mode as the default. Creating a new Table creates a new default Row.

Perhaps a default Flag and Action can be auto-generated, so as to keep everything live from the
start. Those defaults can be renamed and redefined as the user sees fit.

Next idea: extend this to the Fuzzy stuff.

Also, you can't delete the last Flag or Mode, or this all falls apart.
 */

public class TransitionTableFactory {
    private LinkedHashMap<String,Flag> flags = new LinkedHashMap<>();
    private LinkedHashMap<String,Action> actions = new LinkedHashMap<>();
    private LinkedHashMap<String,TransitionTable> tables = new LinkedHashMap<>();
    private LinkedHashMap<String,Mode> modes = new LinkedHashMap<>();

    public static final String TAG = TransitionTableFactory.class.getSimpleName();

    public Flag getFlag(String name) {
        return flags.get(name);
    }

    public void addFlag(String name, String sensor, boolean greaterThan, double triggerValue) {
        flags.put(name, new Flag(name, sensor, greaterThan, triggerValue));
    }

    public void addFlag(Flag f) {
        flags.put(f.getName(), f);
    }

    public void delFlag(String name) {
        flags.remove(name);
    }

    public void replaceFlag(String oldName, Flag updated) {
        flags.remove(oldName);
        flags.put(updated.getName(), updated);
    }

    public ArrayList<Flag> getFlagList() {
        return new ArrayList<>(flags.values());
    }

    public Action getAction(String name) {
        return actions.get(name);
    }

    public void addAction(String name, int left, int right, boolean resetLeft, boolean resetRight) {
        actions.put(name, new Action(name, left, right, resetLeft, resetRight));
    }

    public void addAction(Action action) {
        actions.put(action.getName(), action);
    }

    public void delAction(String name) {
        actions.remove(name);
    }

    public void replaceAction(String oldName, Action updated) {
        actions.remove(oldName);
        actions.put(updated.getName(), updated);
    }

    public ArrayList<Action> getActionList() {
        return new ArrayList<>(actions.values());
    }

    public TransitionTable addTable() {
        return addTable("Table" + (tables.size() + 1));
    }

    public TransitionTable addTable(String name) {
        TransitionTable result = new TransitionTable(name);
        this.tables.put(name, result);
        return result;
    }

    public void addEmptyTablesFrom(ArrayList<TransitionRow> dbaseRows) {
        for (TransitionRow row: dbaseRows) {
            addTable(row.name);
        }
    }

    public void makeTableRowsFrom(ArrayList<TransitionRow> dbaseRows) {
        for (TransitionRow row: dbaseRows) {
            Log.i(TAG,"row.flagName: '" + row.flagName + "'; row.modeName: '" + row.modeName + "'");
            if (flags.containsKey(row.flagName) && modes.containsKey(row.modeName)) {
                tables.get(row.name).addRow(new Row(flags.get(row.flagName), modes.get(row.modeName), row.row, row.id));
            }
        }
    }

    public boolean hasTable(String name) {
        return tables.containsKey(name);
    }

    public ArrayList<TransitionTable> getTableList() {
        return new ArrayList<>(tables.values());
    }

    public Mode getMode(String name) {
        return modes.get(name);
    }

    public void addMode(String name, String action, String table, FuzzyFactory fuzzyFactory) {
        // TODO: Check for fuzzy actions
        modes.put(name, new Mode(name, actions.get(action), tables.get(table)));
    }

    public void addMode(Mode newMode) {
        modes.put(newMode.getName(), newMode);
    }

    public void replaceMode(String oldName, Mode mode) {
        modes.remove(oldName);
        modes.put(mode.getName(), mode);
    }

    public void delMode(String modeName) {
        modes.remove(modeName);
    }

    public ArrayList<Mode> getModeList() {
        return new ArrayList<>(modes.values());
    }

    public TransitionTable getTable(String name) {
        return tables.get(name);
    }

    public void addTableRow(String table, Row row) {
        tables.get(table).addRow(row);
    }

    public void delTableRow(String table, long id) {
        tables.get(table).deleteRow(id);
    }

    public void renameTable(String oldName, String newName) {
        tables.put(newName, tables.remove(oldName));
    }

    public void replaceRow(String tableName, int rowNum, Flag flag, Mode mode) {
        Row row = tables.get(tableName).getRow(rowNum);
        row.setFlag(flag);
        row.setMode(mode);
    }

    public int getNumFlags() {
        return flags.size();
    }

    public int getNumActions() {
        return actions.size();
    }

    public int getNumModes() {
        return modes.size();
    }

    public int getNumTables() {
        return tables.size();
    }

    public Action getDefaultAction() {
        return actions.values().iterator().next();
    }

    public TransitionTable getDefaultTable() {
        if (tables.size() > 0) {
            Log.i(TAG, "Default table present");
            return tables.values().iterator().next();
        } else {
            Log.i(TAG, "No default table; creating...");
            return addTable();
        }
    }

    public Flag getDefaultFlag() {
        return flags.values().iterator().next();
    }

    public Mode getDefaultMode() {
        return modes.values().iterator().next();
    }
}