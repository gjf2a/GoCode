package com.stack.gocode.adapters;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.stack.gocode.R;
import com.stack.gocode.itemTouchHelperThankYouPaulBurke.ItemTouchHelperAdapter;
import com.stack.gocode.localData.fuzzy.FuzzyArgs;
import com.stack.gocode.localData.fuzzy.FuzzyFlag;
import com.stack.gocode.localData.fuzzy.FuzzyType;
import com.stack.gocode.sensors.SensedValues;
import com.stack.gocode.viewHolders.FuzzyFlagsViewHolder;

import java.util.ArrayList;

public class FuzzyFlagsAdapter extends RecyclerView.Adapter<FuzzyFlagsViewHolder> implements ItemTouchHelperAdapter {
    private Context context;
    private ArrayList<FuzzyFlag> flags, toBeDeleted;
    private ArrayList<String> sensorSymbolNames, fuzzyFlagNames;

    public interface OnStartDragListener { void onStartDrag(RecyclerView.ViewHolder viewHolder); }

    private final OnStartDragListener mDragStartListener;

    public FuzzyFlagsAdapter(Context context, ArrayList<FuzzyFlag> flags, ArrayList<FuzzyFlag> toBeDeleted, ArrayList<String> sensorSymbolNames, ArrayList<String> fuzzyFlagNames, OnStartDragListener dragStartListener) {
        this.context = context;
        this.flags = flags;
        this.toBeDeleted = toBeDeleted;
        this.mDragStartListener = dragStartListener;
        this.sensorSymbolNames = sensorSymbolNames;
        this.fuzzyFlagNames = fuzzyFlagNames;
    }

    @Override
    public FuzzyFlagsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.fuzzy_flags_cardview, parent, false);
        return new FuzzyFlagsViewHolder(v, flags, fuzzyFlagNames, toBeDeleted);
    }

    @Override
    public void onBindViewHolder(final FuzzyFlagsViewHolder holder, int position) {
        holder.giveFlag(flags.get(position));
        holder.giveAdapter(this);
        holder.getDeleteCheck().setChecked(toBeDeleted.contains(flags.get(position)));
        holder.getNameText().setText(flags.get(position).getName());

        for (int i = 0; i < FuzzyArgs.NUM_FUZZY_ARGS; i++) {
            holder.getThreshold(i).setText(flags.get(position).getArg(i));
            holder.getFuzzyFlagSpinner(i).setAdapter(makeSpinnerAdapter(fuzzyFlagNames));
            holder.getFuzzyFlagSpinner(i).setSelection(fuzzyFlagNames.indexOf(flags.get(position).getArg(i)));
        }

        holder.getGripBars().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });

        holder.getSensorSelect().setAdapter(makeSpinnerAdapter(sensorSymbolNames));
        holder.getSensorSelect().setSelection(sensorSymbolNames.indexOf(flags.get(position).getSensor()));

        holder.getTypeSelect().setAdapter(makeSpinnerAdapter(FuzzyType.names()));
        holder.getTypeSelect().setSelection(indexOf(flags.get(position).getType().name(), FuzzyType.names()));
    }

    @Override
    public int getItemCount() {
        return flags.size(); //note: determines initial population
    }

    @Override
    public void onItemDismiss(int position) { //todo Integrate with current delete system
        flags.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) { //todo integrate with database
        FuzzyFlag prev = flags.remove(fromPosition);
        flags.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
    }

    private ArrayAdapter<String> makeSpinnerAdapter(String[] names) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_item, names);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        return adapter;
    }

    private ArrayAdapter<String> makeSpinnerAdapter(ArrayList<String> names) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_item, names);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        return adapter;
    }

    private int indexOf(String name, String[] names) {
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(name)) {
                return i;
            }
        }
        return -1;
    }
}
