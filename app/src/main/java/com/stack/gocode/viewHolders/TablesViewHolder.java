package com.stack.gocode.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import com.stack.gocode.R;
import com.stack.gocode.adapters.TablesAdapter;
import com.stack.gocode.localData.DatabaseHelper;
import com.stack.gocode.localData.Flag;
import com.stack.gocode.localData.Mode;
import com.stack.gocode.localData.Row;
import com.stack.gocode.localData.TransitionTable;

import java.util.ArrayList;

public class TablesViewHolder extends RecyclerView.ViewHolder {

    private Spinner flagSelect, modeSelect;
    private CheckBox toDelete;

    private ArrayList<Mode> modes;
    private ArrayList<Flag> flags;
    private TransitionTable[] table;
    private Row row;
    private TablesAdapter adapter;
    private ArrayList<Row> toBeDeleted;

    public TablesViewHolder(View itemView, ArrayList<Mode> modes, TransitionTable[] table, final ArrayList<Row> toBeDeleted, ArrayList<Flag> flags) {
        super(itemView);
        this.modes = modes;
        this.table = table;
        this.toBeDeleted = toBeDeleted;
        this.flags = flags;

        Log.i("TablesViewHolder", "Making spinners");
        flagSelect = (Spinner)  itemView.findViewById(R.id.t_flagSpinner);
        modeSelect = (Spinner)  itemView.findViewById(R.id.t_modeSpinner);
        toDelete = (CheckBox) itemView.findViewById(R.id.ttRowMarkToDelete);
        Log.i("TablesViewHolder", "Spinners made");

        toDelete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isShown()) {
                    if (isChecked) {
                        toBeDeleted.add(row);
                    } else {
                        toBeDeleted.remove(row);
                    }
                }
            }
        });

        flagSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Flag currentFlag = findFlag(flagSelect.getSelectedItem().toString());
                updateDB( getName(), getRowNum(), getName(), currentFlag, getMode());
                setRow(currentFlag, getMode());
                row.setFlag(currentFlag);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        modeSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Mode currentMode = findMode(modeSelect.getSelectedItem().toString());
                updateDB (getName(), getRowNum(), getName(), getFlag(), currentMode);
                setRow (getFlag(), currentMode);
                row.setMode(currentMode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setRow(Flag flag, Mode mode) {
        table[0].getRow(getRowNum()).setFlag(flag);
        table[0].getRow(getRowNum()).setMode(mode);

    }

    private Flag getFlag() {
        return table[0].getFlag(getRowNum());
    }

    private Mode getMode() {
        return table[0].getMode(getRowNum());
    }

    private Mode findMode(String name) {
        for (Mode m: modes) {
            if (m.getName().equals(name)) {
                return m;
            }
        }
        return new Mode();
    }

    private Flag findFlag(String name) {
        for (Flag f : flags) {
            if (f.getName().equals(name)) {
                return f;
            }
        }
        return new Flag();
    }

    private void updateDB(String oldName, int rowNum, String name, Flag flag, Mode mode) {
        DatabaseHelper db = new DatabaseHelper(itemView.getContext());
        db.updateTransitionRow(row.getRowId(), oldName, rowNum, name, flag, mode);
    }

    public void giveAdapter(TablesAdapter adapter) {
        this.adapter = adapter;
    }

    private String getName() {
        return table[0].getName();
    }

    private int getRowNum() {
        return table[0].getTriggerList().indexOf(row);
    }

    public void giveDuple(Row row) {
        this.row = row;
    }

    public CheckBox getToDelete() {
        return toDelete;
    }

    public Spinner getFlagSelect() {
        return flagSelect;
    }

    public Spinner getModeSelect() {
        return modeSelect;
    }

}
