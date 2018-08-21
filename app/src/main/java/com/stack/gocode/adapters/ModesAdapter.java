package com.stack.gocode.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.stack.gocode.R;
import com.stack.gocode.localData.Action;
import com.stack.gocode.localData.Mode;
import com.stack.gocode.localData.TransitionTable;
import com.stack.gocode.viewHolders.ModesViewHolder;

import java.util.ArrayList;

public class ModesAdapter extends RecyclerView.Adapter<ModesViewHolder> {

    Context context;
    ArrayList<Mode> modes, toBeDeleted;
    ArrayList<Action> actions;
    ArrayList<TransitionTable> tts;
    ArrayList<String> modeNames;
    public ModesAdapter(Context context, ArrayList<Mode> modes, ArrayList<Action> actions, ArrayList<TransitionTable> tts, ArrayList<Mode> toBeDeleted, ArrayList<String> modeNames) {
        this.context = context;
        this.modes = modes;
        this.actions = actions;
        this.tts = tts;
        this.toBeDeleted = toBeDeleted;
        this.modeNames = modeNames;
    }

    @Override
    public ModesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.modes_cardview, parent, false);
        return new ModesViewHolder(v, toBeDeleted, actions, tts, modeNames);
    }

    @Override
    public void onBindViewHolder(ModesViewHolder holder, int position) {
        holder.giveModes(modes);
        holder.giveMode(modes.get(position));
        holder.giveAdapter(this);
        holder.getDeleteCheck().setChecked(toBeDeleted.contains(modes.get(position)));
        holder.getModeName().setText( modes.get(position).getName());
        setUpActionSpinner(holder, modes.get(position));
        setUpTableSpinner(holder, modes.get(position).getTtName());
    }

    @Override
    public int getItemCount() {
        return modes.size(); //note: determines initial population
    }

    private void setUpActionSpinner(ModesViewHolder holder, Mode mode) {
        ArrayList<String> actionList = new ArrayList<String>();
        for( Action a : actions) {
            actionList.add(a.getName());
        }

        holder.getActionSelect().setAdapter(makeSpinnerAdapter(actionList));
        holder.getActionSelect().setSelection(actionList.indexOf(mode.getAction().getName()));
    }

    private void setUpTableSpinner(ModesViewHolder holder, String ttName) {
        ArrayList<String> tables = new ArrayList<String>();
        for (TransitionTable t : tts) {
            tables.add(t.getName());
        }

        holder.getTableSelect().setAdapter(makeSpinnerAdapter(tables));
        holder.getTableSelect().setSelection(tables.indexOf(ttName));
    }

    private ArrayAdapter<String> makeSpinnerAdapter(ArrayList<String> names) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_item, names);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        return adapter;
    }
}
