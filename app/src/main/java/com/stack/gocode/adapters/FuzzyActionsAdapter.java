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
import com.stack.gocode.Util;
import com.stack.gocode.itemTouchHelperThankYouPaulBurke.ItemTouchHelperAdapter;
import com.stack.gocode.localData.Action;
import com.stack.gocode.localData.DatabaseHelper;
import com.stack.gocode.localData.fuzzy.FuzzyAction;
import com.stack.gocode.viewHolders.ActionsViewHolder;
import com.stack.gocode.viewHolders.FuzzyActionsViewHolder;

import java.util.ArrayList;

public class FuzzyActionsAdapter extends RecyclerView.Adapter<FuzzyActionsViewHolder> implements ItemTouchHelperAdapter {

    private Context context;
    private ArrayList<FuzzyAction> actions, toBeDeleted;

    public interface OnStartDragListener { void onStartDrag(RecyclerView.ViewHolder viewHolder); }

    private final OnStartDragListener mDragStartListener;

    public FuzzyActionsAdapter(Context context, ArrayList<FuzzyAction> actions, ArrayList<FuzzyAction> toBeDeleted, OnStartDragListener dragStartListener) {
        this.context = context;
        this.actions = actions;
        this.toBeDeleted = toBeDeleted;
        this.mDragStartListener = dragStartListener;
    }

    @Override
    public FuzzyActionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.fuzzy_actions_cardview, parent, false);
        return new FuzzyActionsViewHolder(v, actions, toBeDeleted);
    }

    @Override
    public void onBindViewHolder(final FuzzyActionsViewHolder holder, int position) {
        holder.giveFuzzyAction(actions.get(position));
        holder.giveAdapter(this);
        holder.getDeleteCheck().setChecked(toBeDeleted.contains(actions.get(position)));
        holder.getNameInput().setText(actions.get(position).getName());

        DatabaseHelper db = new DatabaseHelper(context);
        Util.setUpSpinner(context, holder.getLeftFuzz(), position, db.getFuzzyFlagList(), actions.get(position).getLeft().getFlag().getName());
        Util.setUpSpinner(context, holder.getLeftDefuzz(), position, db.getDefuzzifierList(), actions.get(position).getLeft().getDefuzzifier().getName());
        Util.setUpSpinner(context, holder.getRightFuzz(), position, db.getFuzzyFlagList(), actions.get(position).getRight().getFlag().getName());
        Util.setUpSpinner(context, holder.getRightDefuzz(), position, db.getDefuzzifierList(), actions.get(position).getRight().getDefuzzifier().getName());

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
        return actions.size(); //note: determines initial population
    }

    @Override
    public void onItemDismiss(int position) { //todo Integrate with current delete system
        actions.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) { //todo integrate with database
        FuzzyAction prev = actions.remove(fromPosition);
        actions.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
    }

}
