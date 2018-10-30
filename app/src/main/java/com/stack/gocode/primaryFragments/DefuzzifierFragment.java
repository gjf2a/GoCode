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
import com.stack.gocode.adapters.DefuzzifierAdapter;
import com.stack.gocode.itemTouchHelperThankYouPaulBurke.SimpleItemTouchHelperCallback;
import com.stack.gocode.localData.Action;
import com.stack.gocode.localData.DatabaseHelper;
import com.stack.gocode.localData.fuzzy.Defuzzifier;

import java.util.ArrayList;

public class DefuzzifierFragment extends Fragment implements DefuzzifierAdapter.OnStartDragListener {
    private View myView;
    private ArrayList<Defuzzifier> defuzzifiers;
    private ArrayList<Defuzzifier> toBeDeleted;
    private Button newDefuzzifier, deleteDefuzzifiers;
    private DefuzzifierAdapter adapter;
    private ItemTouchHelper mItemTouchHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.defuzzifiers, container, false);

        DatabaseHelper db = new DatabaseHelper(this.getActivity());
        defuzzifiers = db.getDefuzzifierList();
        toBeDeleted = new ArrayList<>();

        RecyclerView recyclerView = (RecyclerView) myView.findViewById(R.id.defuzzifiers_recycler_view);
        adapter = new DefuzzifierAdapter(this.getActivity(), defuzzifiers, toBeDeleted, this);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        newDefuzzifier = myView.getRootView().findViewById(R.id.add_defuzzifier);
        newDefuzzifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    DatabaseHelper db = new DatabaseHelper(getActivity());
                    defuzzifiers.add(db.insertNewDefuzzifier("default"));
                    adapter.notifyDataSetChanged();
                } catch (Exception exc) {
                    ModalDialogs.notifyException(v.getContext(), exc);
                }
            }
        });

        deleteDefuzzifiers = myView.getRootView().findViewById(R.id.delete_defuzzifiers);
        deleteDefuzzifiers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (toBeDeleted.size() > 0) {
                        DatabaseHelper db = new DatabaseHelper(v.getContext());
                        for (Defuzzifier d: toBeDeleted) {
                            db.deleteDefuzzifier(d.getName());
                            defuzzifiers.remove(d);
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