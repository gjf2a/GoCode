package com.stack.gocode.primaryFragments;

import android.app.Fragment;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.stack.gocode.ModalDialogs;
import com.stack.gocode.R;
import com.stack.gocode.adapters.ActionsAdapter;
import com.stack.gocode.itemTouchHelperThankYouPaulBurke.OnStartDragListener;
import com.stack.gocode.itemTouchHelperThankYouPaulBurke.SimpleItemTouchHelperCallback;
import com.stack.gocode.localData.Action;
import com.stack.gocode.localData.DatabaseHelper;

import java.util.ArrayList;

public class ActionsFragment extends Fragment implements ActionsAdapter.OnStartDragListener {
    private View myView;
    private ArrayList<Action> actions;
    private ArrayList<Action> toBeDeleted;
    private Button newAction, deleteActions;
    private ActionsAdapter adapter;
    private ItemTouchHelper mItemTouchHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.actions, container, false);

        DatabaseHelper db = new DatabaseHelper(this.getActivity());
        actions = db.getAllActions();
        toBeDeleted = new ArrayList<Action>();

        RecyclerView recyclerView = (RecyclerView) myView.findViewById(R.id.actions_recycler_view);
        adapter = new ActionsAdapter(this.getActivity(), actions, toBeDeleted, this);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        newAction = myView.getRootView().findViewById(R.id.button6);
        newAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    DatabaseHelper db = new DatabaseHelper(getActivity());
                    Action temp = new Action();
                    temp.setRowNumber(actions.size());
                    temp.setName("action" + (actions.size() + 1));
                    db.insertNewAction(temp);
                    actions.add(temp);
                    adapter.notifyDataSetChanged();
                } catch (Exception exc) {
                    ModalDialogs.notifyException(v.getContext(), exc);
                }
            }
        });

        deleteActions = myView.getRootView().findViewById(R.id.actionsDelete);
        deleteActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (toBeDeleted.size() > 0) {
                        DatabaseHelper db = new DatabaseHelper(v.getContext());
                        for (Action a : toBeDeleted) {
                            db.deleteAction(a);
                            actions.remove(a);
                        }
                        toBeDeleted.clear();
                        adapter.notifyDataSetChanged();
                    }
                }
                catch (Exception exc) {
                    ModalDialogs.notifyException(v.getContext(), exc);
                }
            }
        });

        return myView;
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}