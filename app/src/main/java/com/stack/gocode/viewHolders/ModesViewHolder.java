package com.stack.gocode.viewHolders;

import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.stack.gocode.R;
import com.stack.gocode.adapters.ModesAdapter;
import com.stack.gocode.localData.Action;
import com.stack.gocode.localData.DatabaseHelper;
import com.stack.gocode.localData.Mode;
import com.stack.gocode.localData.TransitionTable;

import java.util.ArrayList;

public class ModesViewHolder extends RecyclerView.ViewHolder {

    private Spinner actionSelect, tableSelect;
    private EditText modeName;
    private CheckBox deleteCheck;

    private ArrayList<Mode> modes, toBeDeleted;
    private ArrayList<Action> actions;
    private ArrayList<TransitionTable> tts;
    private Mode mode;
    private ModesAdapter adapter;
    private ArrayList<String> modeNames;

    public ModesViewHolder(final View itemView, final ArrayList<Mode> toBeDeleted, ArrayList<Action> actions, ArrayList<TransitionTable> tts, ArrayList<String> modeNames) {
        super(itemView);
        this.toBeDeleted = toBeDeleted;
        this.actions = actions;
        this.tts = tts;
        this.modeNames = modeNames;

        actionSelect = (Spinner)  itemView.findViewById(R.id.m_spinner);
        tableSelect = (Spinner)  itemView.findViewById(R.id.m_spinner2);
        modeName  = (EditText) itemView.findViewById(R.id.m_textView);
        deleteCheck = (CheckBox) itemView.findViewById(R.id.modesDeleteCheckBox);

        deleteCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isShown()) {
                    if (isChecked) {
                        toBeDeleted.add(mode);
                    } else {
                        toBeDeleted.remove(mode);
                    }
                }
            }
        });

        modeName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && modes.contains(mode)) {
                    updateName(modeName.getText().toString());
                }
            }
        });

        actionSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mode.setAction(
                        findAction(
                                actionSelect.getSelectedItem().toString()));
                DatabaseHelper db = new DatabaseHelper(view.getContext());
                db.updateMode(mode, mode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tableSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TransitionTable table = findTable(tableSelect.getSelectedItem().toString());
                mode.setNextLayer(table);
                DatabaseHelper db = new DatabaseHelper(view.getContext());
                db.updateMode(mode, mode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private TransitionTable findTable(String name) {
        for (TransitionTable t : tts) {
            if (t.getName().equals(name)) {
                return t;
            }
        }
        return new TransitionTable();
    }

    private Action findAction(String name) {
        for (Action a : actions) {
            if (a.getName().equals(name)) {
                return a;
            }
        }
        return new Action();
    }

    private void updateName(String newName) {
        char[] nameChar = newName.toCharArray();
        StringBuilder name = new StringBuilder();
        for (char c : nameChar) {
            if ((c >= 48 && c <= 57) || (c >= 65 && c <= 90) || (c >= 97 && c <= 122)) {
                name.append(c);
            }
        }

        for (Mode m : modes) {
            if (m.getName().equals(name.toString())) {
                name.delete(0, name.length());
            }
        }

        if (name.length() <= 0) {
            name.append(mode.getName());
        }

        DatabaseHelper db = new DatabaseHelper(itemView.getContext());
        Mode newMode = new Mode(name.toString(), mode.getAction(), mode.getNextLayer());
        db.updateMode(mode, newMode);

        updateModes(newMode);
        modeName.setText(name.toString());
    }

    private void updateModes(Mode newMode) {
        if (modes.contains(mode)) {
            modeNames.set(modeNames.indexOf(mode.getName()), newMode.getName());
            modes.set(modes.indexOf(mode), newMode);
        }
    }

    public void giveModes(ArrayList<Mode> modes) {
        this.modes = modes;
    }

    public void giveMode(Mode mode) {
        this.mode = mode;
    }

    public void giveAdapter(ModesAdapter adapter) {
        this.adapter = adapter;
    }

    public CheckBox getDeleteCheck() {
        return deleteCheck;
    }

    public Spinner getActionSelect() {
        return actionSelect;
    }

    public Spinner getTableSelect() {
        return tableSelect;
    }

    public TextView getModeName() {
        return modeName;
    }
}
