package com.stack.gocode.adapters;
// Uses code from https://github.com/iPaulPro/Android-ItemTouchHelper-Demo/blob/v1.0/app/src/main/java/co/paulburke/android/itemtouchhelperdemo/RecyclerListAdapter.java
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
import com.stack.gocode.localData.fuzzy.Defuzzifier;
import com.stack.gocode.sensors.SensedValues;
import com.stack.gocode.sensors.Symbol;
import com.stack.gocode.viewHolders.DefuzzifierViewHolder;
import com.stack.gocode.viewHolders.DifferenceViewHolder;

import java.util.ArrayList;

public class DifferenceAdapter extends RecyclerView.Adapter<DifferenceViewHolder> implements ItemTouchHelperAdapter {

    private Context context;
    private ArrayList<Symbol> symbols, toBeDeleted;

    public interface OnStartDragListener { void onStartDrag(RecyclerView.ViewHolder viewHolder); }

    private final OnStartDragListener mDragStartListener;

    public DifferenceAdapter(Context context, ArrayList<Symbol> symbols, ArrayList<Symbol> toBeDeleted, OnStartDragListener dragStartListener) {
        this.context = context;
        this.symbols = symbols;
        this.toBeDeleted = toBeDeleted;
        this.mDragStartListener = dragStartListener;
    }

    @Override
    public DifferenceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.defuzzifiers_cardview, parent, false);
        return new DifferenceViewHolder(v, symbols, toBeDeleted);
    }

    @Override
    public void onBindViewHolder(final DifferenceViewHolder holder, int position) {
        holder.giveSymbol(symbols.get(position));
        holder.giveAdapter(this);
        holder.getDeleteCheck().setChecked(toBeDeleted.contains(symbols.get(position)));
        holder.getNameInput().setText(symbols.get(position).getName());

        holder.getSensorOne().setAdapter(makeSpinnerAdapter(SensedValues.SENSOR_NAMES));
        holder.getSensorOne().setSelection(indexOf(symbols.get(position).getSensorOne(), SensedValues.SENSOR_NAMES));

        holder.getSensorTwo().setAdapter(makeSpinnerAdapter(SensedValues.SENSOR_NAMES));
        holder.getSensorTwo().setSelection(indexOf(symbols.get(position).getSensorTwo(), SensedValues.SENSOR_NAMES));

        holder.getGripBars().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return symbols.size(); //note: determines initial population
    }

    @Override
    public void onItemDismiss(int position) { //todo Integrate with current delete system
        symbols.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) { //todo integrate with database
        Symbol prev = symbols.remove(fromPosition);
        symbols.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
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
    }
}
