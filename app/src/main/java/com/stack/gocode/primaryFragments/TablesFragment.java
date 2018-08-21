package com.stack.gocode.primaryFragments;

import android.app.Fragment;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.stack.gocode.adapters.TablesAdapter;
import com.stack.gocode.localData.Action;
import com.stack.gocode.localData.DatabaseHelper;
import com.stack.gocode.localData.Duple;
import com.stack.gocode.localData.Flag;
import com.stack.gocode.localData.Mode;
import com.stack.gocode.localData.Row;
import com.stack.gocode.localData.TransitionTable;

import java.util.ArrayList;

public class TablesFragment extends Fragment { //https://www.google.com/search?q=setting+up+a+navigation+activity+android&rlz=1C1AVNE_enUS678US678&oq=setting+up+a+navigation+activity+android&aqs=chrome..69i57.11803j0j7&sourceid=chrome&ie=UTF-8#kpvalbx=1
    private View myView;
    private TablesAdapter adapter;
    private ArrayList<TransitionTable> tables;
    private TransitionTable[] table = new TransitionTable[1];
    private ArrayList<Action> actions;
    private ArrayList<Mode> modes;

    private ArrayAdapter<String> adapterS;
    private ArrayList<String> names;
    private ArrayList<Flag> flags;
    private ArrayList<Row> toBeDeleted;

    private Spinner tableSpinner;
    private Button rowDeleter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.tables, container, false);
        DatabaseHelper db = new DatabaseHelper(myView.getContext());

        tables = db.getAllTransitionTables();
        actions = db.getAllActions();
        modes = db.getAllModes();
        table[0] = new TransitionTable();
        flags = db.getAllFlags();
        toBeDeleted = new ArrayList<Row>();

        RecyclerView recyclerView = (RecyclerView) myView.findViewById(R.id.tables_recycler_view);
        adapter = new TablesAdapter(this.getActivity(), tables, actions, modes, table, flags, toBeDeleted);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        setUpTableSpinner();
        setUpNewRowAction();
        setUpTableMaker();

        tableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                table[0] = findTable(tableSpinner.getSelectedItem().toString());
                adapter.notifyDataSetChanged();
                toBeDeleted.clear();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        rowDeleter = myView.findViewById(R.id.ttDeleteButton);
        rowDeleter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper db = new DatabaseHelper(v.getContext());
                for (Row d : toBeDeleted) {
                    db.deleteTransitionRow(table[0].getName(), d.getRowId());
                    table[0].deleteRow(d);
                }
                toBeDeleted.clear();
                adapter.notifyDataSetChanged();
            }
        });

        return myView;
    }

    private void setUpTableSpinner() {
        names = new ArrayList<String>();
        for (TransitionTable t : tables) {
            names.add(t.getName());
        }

        tableSpinner = (Spinner) myView.findViewById(R.id.t_table);
        tableSpinner.setAdapter(makeSpinnerAdapter(names));

    }

    private ArrayAdapter<String> makeSpinnerAdapter(ArrayList<String> names) {
        adapterS = new ArrayAdapter<String>(this.getActivity(), R.layout.spinner_dropdown_item, names);
        adapterS.setDropDownViewResource(R.layout.spinner_dropdown_item);
        return adapterS;
    }

    private void setUpNewRowAction() {
        final Button newRowButton = myView.findViewById(R.id.t_newRow);
        newRowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tableSpinner.getSelectedItem() != null) {
                    TransitionTable temp = findTable(tableSpinner.getSelectedItem().toString());
                    temp.addRow(new Flag(), new Mode());

                    DatabaseHelper db = new DatabaseHelper(v.getContext());
                    db.insertNewTransitionRow(temp.getSize() - 1, temp.getName(), new Flag(), new Mode());

                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void setUpTableMaker() {
        final Button newTableButton = myView.findViewById(R.id.t_newTableButton);
        newTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionTable newTable = new TransitionTable();
                newTable.setName("Table" + (tables.size() + 1));
                tables.add(newTable);
                names.add(newTable.getName());
                adapterS.notifyDataSetChanged();
                tableSpinner.setSelection(tables.indexOf(newTable));
                table[0] = newTable;

                DatabaseHelper db = new DatabaseHelper(myView.getContext());
                db.insertNewTransitionRow(0, newTable.getName(), new Flag(), new Mode());

                adapter.notifyDataSetChanged();
            }
        });
    }

    private TransitionTable findTable(String name) {
        for (TransitionTable t : tables) {
            if (t.getName().equals(name)) {
                return t;
            }
        }
        return new TransitionTable();
    }
}
