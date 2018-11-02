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
import com.stack.gocode.adapters.DefuzzifierAdapter;
import com.stack.gocode.adapters.DifferenceAdapter;
import com.stack.gocode.itemTouchHelperThankYouPaulBurke.SimpleItemTouchHelperCallback;
import com.stack.gocode.localData.DatabaseHelper;
import com.stack.gocode.localData.fuzzy.Defuzzifier;
import com.stack.gocode.sensors.Symbol;

import java.util.ArrayList;

public class DifferenceFragment extends Fragment implements DifferenceAdapter.OnStartDragListener {
    private View myView;
    private ArrayList<Symbol> differences;
    private ArrayList<Symbol> toBeDeleted;
    private Button newDifference, deleteDifferences;
    private DifferenceAdapter adapter;
    private ItemTouchHelper mItemTouchHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.differences, container, false);

        DatabaseHelper db = new DatabaseHelper(this.getActivity());
        differences = db.getSymbolList();
        toBeDeleted = new ArrayList<>();

        RecyclerView recyclerView = (RecyclerView) myView.findViewById(R.id.differences_recycler_view);
        adapter = new DifferenceAdapter(this.getActivity(), differences, toBeDeleted, this);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        newDifference = myView.getRootView().findViewById(R.id.make_a_difference);
        newDifference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    DatabaseHelper db = new DatabaseHelper(getActivity());
                    differences.add(db.insertNewSymbol("default"));
                    adapter.notifyDataSetChanged();
                } catch (Exception exc) {
                    ModalDialogs.notifyException(v.getContext(), exc);
                }
            }
        });

        deleteDifferences = myView.getRootView().findViewById(R.id.delete_differences);
        deleteDifferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (toBeDeleted.size() > 0) {
                        DatabaseHelper db = new DatabaseHelper(v.getContext());
                        for (Symbol d: toBeDeleted) {
                            db.deleteSymbol(d.getName());
                            differences.remove(d);
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