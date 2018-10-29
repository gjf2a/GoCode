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
import com.stack.gocode.localData.Flag;
import com.stack.gocode.localData.fuzzy.FuzzyFlag;
import com.stack.gocode.sensors.SensedValues;
import com.stack.gocode.viewHolders.FlagsViewHolder;

import java.util.ArrayList;

public class FuzzyFlagsAdapter /*extends RecyclerView.Adapter<FlagsViewHolder> implements ItemTouchHelperAdapter */{
/*    private Context context;
    private ArrayList<FuzzyFlag> flags, toBeDeleted;

    public interface OnStartDragListener { void onStartDrag(RecyclerView.ViewHolder viewHolder); }

    private final OnStartDragListener mDragStartListener;

    public FuzzyFlagsAdapter(Context context, ArrayList<FuzzyFlag> flags, ArrayList<FuzzyFlag> toBeDeleted, OnStartDragListener dragStartListener) {
        this.context = context;
        this.flags = flags;
        this.toBeDeleted = toBeDeleted;
        this.mDragStartListener = dragStartListener;
    }

    @Override
    public FlagsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.flags_cardview, parent, false);
        return new FlagsViewHolder(v, flags, toBeDeleted);
    }

    @Override
    public void onBindViewHolder(final FlagsViewHolder holder, int position) {
        holder.giveFlag(flags.get(position));
        holder.giveAdapter(this);
        holder.getDeleteCheck().setChecked(toBeDeleted.contains(flags.get(position)));
        holder.getNameText().setText(flags.get(position).getName());
        holder.getThreshold().setText(flags.get(position).getTriggerValue() + "");
        holder.getGreaterThan().setChecked(flags.get(position).isGreaterThan());

        holder.getGripBars().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });

        holder.getSensorSelect().setAdapter(makeSpinnerAdapter(SensedValues.SENSOR_NAMES));
        holder.getSensorSelect().setSelection(indexOf(flags.get(position).getSensor(), SensedValues.SENSOR_NAMES));
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

    private int indexOf(String name, String[] names) {
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(name)) {
                return i;
            }
        }
        return -1;
    }*/
}
