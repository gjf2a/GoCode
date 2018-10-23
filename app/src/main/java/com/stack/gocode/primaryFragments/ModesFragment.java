package com.stack.gocode.primaryFragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.stack.gocode.R;
import com.stack.gocode.adapters.ModesAdapter;
import com.stack.gocode.localData.Action;
import com.stack.gocode.localData.DatabaseHelper;
import com.stack.gocode.localData.Mode;
import com.stack.gocode.localData.TransitionTable;

import java.util.ArrayList;

public class ModesFragment extends Fragment {  //https://www.google.com/search?q=setting+up+a+navigation+activity+android&rlz=1C1AVNE_enUS678US678&oq=setting+up+a+navigation+activity+android&aqs=chrome..69i57.11803j0j7&sourceid=chrome&ie=UTF-8#kpvalbx=1
    private View myView;
    private ModesAdapter adapter;
    private Button deleteModes;
    private Spinner startMode;

    private ArrayList<Mode> modes, toBeDeleted;
    private ArrayList<Action> actions;
    private ArrayList<TransitionTable> tables;
    private ArrayList<String> modeNames;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.modes, container, false);
        firstRunSetUp();
        modeNames = new ArrayList<String>();

        DatabaseHelper db = new DatabaseHelper(myView.getContext());

        modes = db.getAllModes();
        actions = db.getAllActions();
        tables = db.getAllTransitionTables();
        toBeDeleted = new ArrayList<Mode>();

        RecyclerView recyclerView = (RecyclerView) myView.findViewById(R.id.modes_recycler_view);
        adapter = new ModesAdapter(this.getActivity(), modes, actions, tables, toBeDeleted, modeNames);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        deleteModes = myView.findViewById(R.id.modesDeleteButton);
        deleteModes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper db = new DatabaseHelper(v.getContext());
                for (Mode m : toBeDeleted) {
                    db.deleteMode(m.getName());
                    modes.remove(m);
                    modeNames.remove(m.getName());
                }
                toBeDeleted.clear();
                adapter.notifyDataSetChanged();
            }
        });

        startMode = myView.findViewById(R.id.modesStartMode);
        setUpSpinner();
        startMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DatabaseHelper db = new DatabaseHelper(view.getContext());
                db.updateStartMode(startMode.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        setUpNewModeAction();

        return myView;
    }

    private void setUpNewModeAction() {
        final Button newModeButton = myView.findViewById(R.id.new_mode);
        newModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper db = new DatabaseHelper(getActivity());
                Mode temp = new Mode();
                temp.setName("mode" + (modes.size() + 1));
                db.insertNewMode(temp, "default");
                modes.add(temp);
                adapter.notifyDataSetChanged();
                modeNames.add(temp.getName());
            }
        });
    }

    private void setUpSpinner() {
        for (Mode m : modes) {
            modeNames.add(m.getName());
        }
        startMode.setAdapter(makeSpinnerAdapter(modeNames));

        setSpinnersInitialPlacement(modeNames);
    }

    private ArrayAdapter<String> makeSpinnerAdapter(ArrayList<String> names) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(myView.getContext(), R.layout.spinner_item, names);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        return adapter;
    }

    private String getStartingModeName() {
        DatabaseHelper db = new DatabaseHelper(myView.getContext());
        return db.getStartMode();
    }

    private void setSpinnersInitialPlacement(ArrayList<String> modeNames) {
        String startMode = getStartingModeName();
        if (!startMode.isEmpty() && modeNames.contains(startMode)) {
            this.startMode.setSelection(modeNames.indexOf(startMode));
        }
    }

    private void firstRunSetUp() {
        DatabaseHelper db = new DatabaseHelper(myView.getContext());
        if (db.getStartMode().isEmpty()) {
            db.insertNewStartMode("Empty mode", "default");
        }
    }
}
