package com.stack.gocode.primaryFragments;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.stack.gocode.ModalDialogs;
import com.stack.gocode.R;
import com.stack.gocode.adapters.FuzzyFlagsAdapter;
import com.stack.gocode.itemTouchHelperThankYouPaulBurke.SimpleItemTouchHelperCallback;
import com.stack.gocode.localData.DatabaseHelper;
import com.stack.gocode.localData.fuzzy.FuzzyFlag;

import java.util.ArrayList;

public class FuzzyFlagFragment extends Fragment implements FuzzyFlagsAdapter.OnStartDragListener{
    private View myView;
    private FuzzyFlagsAdapter adapter;
    private ArrayList<FuzzyFlag> flags, toBeDeleted;
    private ArrayList<String> flagNames;
    private Button newFlag, deleteFlags;
    private ItemTouchHelper mItemTouchHelper;

    public static final String TAG = FuzzyFlagFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fuzzy_flags, container, false);

        DatabaseHelper db = new DatabaseHelper(myView.getContext());
        flags = db.getFuzzyFlagList();
        flagNames = new ArrayList<>();
        for (FuzzyFlag flag: flags) {flagNames.add(flag.getName());}
        toBeDeleted = new ArrayList<>();

        RecyclerView recyclerView = myView.findViewById(R.id.fuzzy_flag_recycler_view);
        adapter = new FuzzyFlagsAdapter(this.getActivity(), flags, toBeDeleted, db.getSensorAndSymbolNames(), flagNames, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        newFlag = myView.findViewById(R.id.fuzzyFlagMaker);
        newFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.i(TAG,"Creating new fuzzy flag");
                    DatabaseHelper db = new DatabaseHelper(getActivity());
                    FuzzyFlag temp = db.insertNewFuzzyFlag("default");
                    flags.add(temp);
                    flagNames.add(temp.getName());
                    adapter.notifyDataSetChanged();
                } catch (Exception exc) {
                    ModalDialogs.notifyException(v.getContext(), exc);
                }
            }
        });

        deleteFlags = myView.findViewById(R.id.fuzzyFlagsDeleteButton);
        deleteFlags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    DatabaseHelper db = new DatabaseHelper(v.getContext());
                    for (FuzzyFlag f : toBeDeleted) {
                        db.deleteFuzzyFlag(f);
                        flags.remove(f);
                        flagNames.remove(f.getName());
                    }
                    toBeDeleted.clear();
                    adapter.notifyDataSetChanged();
                } catch (Exception exc) {
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
