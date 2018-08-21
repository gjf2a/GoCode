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
import com.stack.gocode.viewHolders.ActionsViewHolder;

import java.util.ArrayList;

public class ActionsAdapter extends RecyclerView.Adapter<ActionsViewHolder> implements ItemTouchHelperAdapter {

    private Context context;
    private ArrayList<Action> actions, toBeDeleted;

    public interface OnStartDragListener { void onStartDrag(RecyclerView.ViewHolder viewHolder); }

    private final OnStartDragListener mDragStartListener;

    public ActionsAdapter(Context context, ArrayList<Action> actions, ArrayList<Action> toBeDeleted, OnStartDragListener dragStartListener) {
        this.context = context;
        this.actions = actions;
        this.toBeDeleted = toBeDeleted;
        this.mDragStartListener = dragStartListener;
    }

    @Override
    public ActionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.actions_cardview, parent, false);
        return new ActionsViewHolder(v, actions, toBeDeleted);
    }

    @Override
    public void onBindViewHolder(final ActionsViewHolder holder, int position) {
        holder.giveAction(actions.get(position));
        holder.giveAdapter(this);
        holder.getDeleteCheck().setChecked(toBeDeleted.contains(actions.get(position)));
        holder.getNameInput().setText(actions.get(position).getName());
        holder.getLeftMotorPowerInput().setText(actions.get(position).getLeftMotorInput() + "");
        holder.getRightMotorPowerInput().setText(actions.get(position).getRightMotorInput() + "");
        holder.getResetLeftEncoder().setChecked(actions.get(position).getRLCint() == 1);
        holder.getResetRightEncoder().setChecked(actions.get(position).getRRCint() == 1);

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
        Action prev = actions.remove(fromPosition);
        actions.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
    }

}
