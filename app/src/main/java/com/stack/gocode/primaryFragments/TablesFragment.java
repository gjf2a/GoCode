package com.stack.gocode.primaryFragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.stack.gocode.ModalDialogs;
import com.stack.gocode.R;
import com.stack.gocode.adapters.TablesAdapter;
import com.stack.gocode.localData.Action;
import com.stack.gocode.localData.DatabaseHelper;
import com.stack.gocode.localData.Flag;
import com.stack.gocode.localData.Mode;
import com.stack.gocode.localData.Row;
import com.stack.gocode.localData.TransitionTable;
import com.stack.gocode.localData.TransitionTableWrapper;

import java.util.ArrayList;

public class TablesFragment extends Fragment { //https://www.google.com/search?q=setting+up+a+navigation+activity+android&rlz=1C1AVNE_enUS678US678&oq=setting+up+a+navigation+activity+android&aqs=chrome..69i57.11803j0j7&sourceid=chrome&ie=UTF-8#kpvalbx=1
    private View myView;
    private TablesAdapter adapter;
    private ArrayList<TransitionTable> tables;
    private TransitionTableWrapper table;
    private ArrayList<Mode> modes;

    private ArrayAdapter<String> adapterS;
    private ArrayList<String> names;
    private ArrayList<Flag> flags;
    private ArrayList<Row> toBeDeleted;

    private Spinner tableSpinner;
    private Button rowDeleter;
    private Button renamer;
    private TextView newName;

    public static final String TAG = TablesFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.i("TablesFragment", "Creating Tables Fragment");
        myView = inflater.inflate(R.layout.tables, container, false);
        DatabaseHelper db = new DatabaseHelper(myView.getContext());

        tables = db.getTransitionTableList();
        modes = db.getModeList();
        table = new TransitionTableWrapper(db.getDefaultTable());
        Log.i(TAG,"Initial Table has " + table.get().getNumRows() + " rows");
        flags = db.getFlagList();
        toBeDeleted = new ArrayList<Row>();

        RecyclerView recyclerView = (RecyclerView) myView.findViewById(R.id.tables_recycler_view);
        adapter = new TablesAdapter(this.getActivity(), tables, modes, table, flags, toBeDeleted);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        setUpTableSpinner();
        setUpNewRowAction();
        setUpTableMaker();

        tableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    DatabaseHelper db = new DatabaseHelper(view.getContext());
                    //db.logEntireTable(DatabaseHelper.TABLE_TRANSITION_ROWS);
                    Log.i("TablesFragment", "onItemSelected() start");
                    table.setTable(db.getTable(tableSpinner.getSelectedItem().toString()));
                    adapter.notifyDataSetChanged();
                    toBeDeleted.clear();
                    Log.i("TablesFragment", "onItemSelected() complete");
                } catch (Exception exc) {
                    ModalDialogs.notifyException(view.getContext(), exc);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        rowDeleter = myView.findViewById(R.id.ttDeleteButton);
        rowDeleter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    DatabaseHelper db = new DatabaseHelper(v.getContext());
                    for (Row d : toBeDeleted) {
                        db.deleteTransitionRow(table.get().getName(), d.getRowId());
                        table.get().deleteRow(d);
                    }
                    toBeDeleted.clear();
                    adapter.notifyDataSetChanged();
                } catch (Exception exc) {
                    ModalDialogs.notifyException(v.getContext(), exc);
                }
            }
        });

        newName = myView.findViewById(R.id.newTableName);
        renamer = myView.findViewById(R.id.renameButton);
        renamer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    DatabaseHelper db = new DatabaseHelper(view.getContext());
                    String updatedName = newName.getText().toString();
                    if (updatedName.length() > 0) {
                        if (nameInUse(updatedName)) {
                            ModalDialogs.notifyProblem(view.getContext(), "Name " + updatedName + " is already in use");
                        } else {
                            db.renameTableRows(table.get().getName(), updatedName);
                            table.get().setName(updatedName);
                            setUpTableSpinnerAdapter();
                            newName.setText("");
                        }
                    }
                } catch (Exception exc) {
                    ModalDialogs.notifyException(view.getContext(), exc);
                }
            }
        });

        Log.i(TAG, "Just finished construction");
        return myView;
    }

    public boolean nameInUse(String candidateName) {
        for (TransitionTable table: tables) {
            if (table.getName().equals(candidateName)) {
                return true;
            }
        }
        return false;
    }

    private void setUpTableSpinner() {
        tableSpinner = (Spinner) myView.findViewById(R.id.t_table);
        setUpTableSpinnerAdapter();
    }

    private void setUpTableSpinnerAdapter() {
        names = new ArrayList<String>();
        for (TransitionTable t : tables) {
            names.add(t.getName());
        }
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
                try {
                    Log.i("TablesFragment", "Starting newRowButton handler");
                    if (tableSpinner.getSelectedItem() != null) {
                        Log.i("TablesFragment", "An item was selected: " + tableSpinner.getSelectedItem());
                        DatabaseHelper db = new DatabaseHelper(v.getContext());
                        Row r = db.insertNewTransitionRow("default", tableSpinner.getSelectedItem().toString());
                        adapter.notifyDataSetChanged();
                        Log.i("TablesFragment", "Row added!");
                    }
                } catch (Exception exc) {
                    ModalDialogs.notifyException(v.getContext(), exc);
                }
            }
        });
    }

    private void setUpTableMaker() {
        final Button newTableButton = myView.findViewById(R.id.t_newTableButton);
        newTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    DatabaseHelper db = new DatabaseHelper(myView.getContext());
                    TransitionTable newTable = db.createNewTable("default");
                    tables.add(newTable);
                    names.add(newTable.getName());
                    adapterS.notifyDataSetChanged();
                    tableSpinner.setSelection(tables.indexOf(newTable));
                    table.setTable(newTable);

                    db.insertNewTransitionRow("default", newTable.getName());
                    adapter.notifyDataSetChanged();
                } catch (Exception exc) {
                    ModalDialogs.notifyException(v.getContext(), exc);
                }
            }
        });
    }
}
