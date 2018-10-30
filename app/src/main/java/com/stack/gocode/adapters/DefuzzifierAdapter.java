package com.stack.gocode.adapters;
// Uses code from https://github.com/iPaulPro/Android-ItemTouchHelper-Demo/blob/v1.0/app/src/main/java/co/paulburke/android/itemtouchhelperdemo/RecyclerListAdapter.java
import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.stack.gocode.R;
import com.stack.gocode.itemTouchHelperThankYouPaulBurke.ItemTouchHelperAdapter;
import com.stack.gocode.localData.Action;
import com.stack.gocode.localData.fuzzy.Defuzzifier;
import com.stack.gocode.viewHolders.ActionsViewHolder;
import com.stack.gocode.viewHolders.DefuzzifierViewHolder;

import java.util.ArrayList;

public class DefuzzifierAdapter extends RecyclerView.Adapter<DefuzzifierViewHolder> implements ItemTouchHelperAdapter {

    private Context context;
    private ArrayList<Defuzzifier> defuzzifiers, toBeDeleted;

    public interface OnStartDragListener { void onStartDrag(RecyclerView.ViewHolder viewHolder); }

    private final OnStartDragListener mDragStartListener;

    public DefuzzifierAdapter(Context context, ArrayList<Defuzzifier> actions, ArrayList<Defuzzifier> toBeDeleted, OnStartDragListener dragStartListener) {
        this.context = context;
        this.defuzzifiers = actions;
        this.toBeDeleted = toBeDeleted;
        this.mDragStartListener = dragStartListener;
    }

    @Override
    public DefuzzifierViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.defuzzifiers_cardview, parent, false);
        return new DefuzzifierViewHolder(v, defuzzifiers, toBeDeleted);
    }

    @Override
    public void onBindViewHolder(final DefuzzifierViewHolder holder, int position) {
        holder.giveDefuzzifier(defuzzifiers.get(position));
        holder.giveAdapter(this);
        holder.getDeleteCheck().setChecked(toBeDeleted.contains(defuzzifiers.get(position)));
        holder.getNameInput().setText(defuzzifiers.get(position).getName());
        holder.getFirstSpeed().setText(defuzzifiers.get(position).getSpeed1() + "");
        holder.getSecondSpeed().setText(defuzzifiers.get(position).getSpeed2() + "");

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
        return defuzzifiers.size(); //note: determines initial population
    }

    @Override
    public void onItemDismiss(int position) { //todo Integrate with current delete system
        defuzzifiers.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) { //todo integrate with database
        Defuzzifier prev = defuzzifiers.remove(fromPosition);
        defuzzifiers.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
    }

}
