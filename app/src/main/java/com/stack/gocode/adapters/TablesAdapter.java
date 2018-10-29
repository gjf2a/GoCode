package com.stack.gocode.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.stack.gocode.R;
import com.stack.gocode.localData.Action;
import com.stack.gocode.localData.Flag;
import com.stack.gocode.localData.Mode;
import com.stack.gocode.localData.Row;
import com.stack.gocode.localData.TransitionTable;
import com.stack.gocode.viewHolders.TablesViewHolder;

import java.util.ArrayList;

public class TablesAdapter extends RecyclerView.Adapter<TablesViewHolder> {

    private Context context;
    private ArrayList<TransitionTable> tables;
    private TransitionTable[] table;
    private ArrayList<Action> actions;
    private ArrayList<Mode> modes;
    private ArrayList<Flag> flags;
    private ArrayList<Row> toBeDeleted;

    private final static String TAG = TablesAdapter.class.getSimpleName();

    public TablesAdapter(Context context, ArrayList<TransitionTable> tables, ArrayList<Action> actions, ArrayList<Mode> modes, TransitionTable[] table, ArrayList<Flag> flags, ArrayList<Row> toBeDeleted) {
        this.context = context;
        this.tables = tables;
        this.table = table;
        this.actions = actions;
        this.modes = modes;
        this.flags = flags;
        this.toBeDeleted = toBeDeleted;
    }

    @Override
    public TablesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.tables_cardview, parent, false);
        return new TablesViewHolder(v, modes, table, toBeDeleted, flags);
    }

    @Override
    public void onBindViewHolder(TablesViewHolder holder, int position) {
        holder.giveAdapter(this);
        setUpFlagSpinner(holder, position);
        setUpModeSpinner(holder, position);
        holder.giveDuple(table[0].getRow(position));
        holder.getToDelete().setChecked(toBeDeleted.contains(table[0].getRow(position)));
    }

    @Override
    public int getItemCount() {
        return table[0].getNumRows();
    }

    private void setUpFlagSpinner(TablesViewHolder holder, int pos) {
        Log.i(TAG, "setUpFlagSpinner: pos: " + pos);
        ArrayList<String> names = new ArrayList<String>();
        for (Flag f : flags) {
            Log.i(TAG,"Adding flag " + f);
            names.add(f.getName());
        }
        Log.i(TAG, "Done adding flags");

        holder.getFlagSelect().setAdapter(makeSpinnerAdapter(names));

        Log.i(TAG, "Number of rows in table: " + table[0].getNumRows());
        Log.i(TAG, "Row at " + pos + ": " + table[0].getRow(pos));
        if (!table[0].getFlag(pos).getName().isEmpty()) {
            Log.i(TAG, "valid flag: " + table[0].getFlag(pos).getName());
            holder.getFlagSelect().setSelection(names.indexOf(table[0].getFlag(pos).getName()));
        }
    }

    private void setUpModeSpinner(TablesViewHolder holder, int pos) {
        ArrayList<String> names = new ArrayList<String>();
        for (Mode m : modes) {
            names.add(m.getName());
        }

        holder.getModeSelect().setAdapter(makeSpinnerAdapter(names));

        if (!table[0].getMode(pos).getName().isEmpty()) {
            holder.getModeSelect().setSelection(names.indexOf(table[0].getMode(pos).getName()));
        }
    }

    private ArrayAdapter<String> makeSpinnerAdapter(ArrayList<String> names) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_item, names);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        return adapter;
    }
}