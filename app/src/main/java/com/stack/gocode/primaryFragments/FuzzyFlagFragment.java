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
import com.stack.gocode.adapters.FlagsAdapter;
import com.stack.gocode.adapters.FuzzyFlagsAdapter;
import com.stack.gocode.itemTouchHelperThankYouPaulBurke.SimpleItemTouchHelperCallback;
import com.stack.gocode.localData.DatabaseHelper;
import com.stack.gocode.localData.Flag;
import com.stack.gocode.localData.fuzzy.FuzzyFlag;

import java.util.ArrayList;

public class FuzzyFlagFragment extends Fragment /*implements FuzzyFlagsAdapter.OnStartDragListener*/{
    private View myView;
    private FuzzyFlagsAdapter adapter;
    private ArrayList<FuzzyFlag> flags, toBeDeleted;
    private Button newFlag, deleteFlags;
    private ItemTouchHelper mItemTouchHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.flags, container, false);

        DatabaseHelper db = new DatabaseHelper(myView.getContext());
        flags = db.getFuzzyFlagList();
        toBeDeleted = new ArrayList<FuzzyFlag>();

        RecyclerView recyclerView = myView.findViewById(R.id.flag_recycler_view);
        /*adapter = new FuzzyFlagsAdapter(this.getActivity(), flags, toBeDeleted, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        newFlag = myView.findViewById(R.id.flagMaker);
        newFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    DatabaseHelper db = new DatabaseHelper(getActivity());
                    //FuzzyFlag temp = db.insertNewFlag("default");
                    //flags.add(temp);
                    adapter.notifyDataSetChanged();
                } catch (Exception exc) {
                    ModalDialogs.notifyException(v.getContext(), exc);
                }
            }
        });

        deleteFlags = myView.findViewById(R.id.flagsDeleteButton);
        deleteFlags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    DatabaseHelper db = new DatabaseHelper(v.getContext());
                    for (FuzzyFlag f : toBeDeleted) {
                        //db.deleteFlag(f);
                        flags.remove(f);
                    }
                    toBeDeleted.clear();
                    adapter.notifyDataSetChanged();
                } catch (Exception exc) {
                    ModalDialogs.notifyException(v.getContext(), exc);
                }
            }
        });*/

        return myView;
    }

    //@Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
