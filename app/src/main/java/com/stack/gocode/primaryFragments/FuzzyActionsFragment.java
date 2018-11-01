package com.stack.gocode.primaryFragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.stack.gocode.ModalDialogs;
import com.stack.gocode.R;
import com.stack.gocode.adapters.ActionsAdapter;
import com.stack.gocode.adapters.FuzzyActionsAdapter;
import com.stack.gocode.itemTouchHelperThankYouPaulBurke.SimpleItemTouchHelperCallback;
import com.stack.gocode.localData.Action;
import com.stack.gocode.localData.DatabaseHelper;
import com.stack.gocode.localData.fuzzy.FuzzyAction;

import java.util.ArrayList;

public class FuzzyActionsFragment extends Fragment implements FuzzyActionsAdapter.OnStartDragListener {
    private View myView;
    private ArrayList<FuzzyAction> actions;
    private ArrayList<FuzzyAction> toBeDeleted;
    private Button newAction, deleteActions;
    private FuzzyActionsAdapter adapter;
    private ItemTouchHelper mItemTouchHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fuzzy_actions, container, false);

        DatabaseHelper db = new DatabaseHelper(this.getActivity());
        actions = db.getFuzzyActionList();
        toBeDeleted = new ArrayList<>();

        RecyclerView recyclerView = (RecyclerView) myView.findViewById(R.id.fuzzy_actions_recycler_view);
        adapter = new FuzzyActionsAdapter(this.getActivity(), actions, toBeDeleted, this);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        newAction = myView.getRootView().findViewById(R.id.fuzzy_action_new);
        newAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    DatabaseHelper db = new DatabaseHelper(getActivity());
                    FuzzyAction temp = db.insertNewFuzzyAction("default");
                    actions.add(temp);
                    adapter.notifyDataSetChanged();
                } catch (Exception exc) {
                    ModalDialogs.notifyException(v.getContext(), exc);
                }
            }
        });

        deleteActions = myView.getRootView().findViewById(R.id.fuzzy_actions_delete);
        deleteActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (toBeDeleted.size() > 0) {
                        DatabaseHelper db = new DatabaseHelper(v.getContext());
                        for (FuzzyAction a : toBeDeleted) {
                            db.deleteFuzzyAction(a.getName());
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