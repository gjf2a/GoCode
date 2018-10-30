package com.stack.gocode.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.stack.gocode.R;
import com.stack.gocode.adapters.FlagsAdapter;
import com.stack.gocode.localData.DatabaseHelper;
import com.stack.gocode.localData.fuzzy.FuzzyFlag;

import java.util.ArrayList;

public class FuzzyFlagsViewHolder extends RecyclerView.ViewHolder {

    private EditText name;
    private EditText threshold1;
    private EditText threshold2;
    private EditText threshold3;
    private EditText threshold4;
    private CheckBox deleteCheck;
    private ImageView gripBars;
    private Spinner sensorSelect;
    private Spinner typeSelect;
    private ArrayList<FuzzyFlag> flags, toBeDeleted;

    private FuzzyFlag flag;

    private FlagsAdapter adapter;

    public FuzzyFlagsViewHolder(final View itemView, ArrayList<FuzzyFlag> flags, final ArrayList<FuzzyFlag> toBeDeleted) {
        super(itemView);
        this.flags = flags;
        this.toBeDeleted = toBeDeleted;

        name = (EditText) itemView.findViewById(R.id.fuzzyFlagName);
        threshold1 = (EditText) itemView.findViewById(R.id.fuzzyThreshold1);
        threshold2 = (EditText) itemView.findViewById(R.id.fuzzyThreshold2);
        threshold3 = (EditText) itemView.findViewById(R.id.fuzzyThreshhold3);
        threshold4 = (EditText) itemView.findViewById(R.id.fuzzyThreshhold4);
        gripBars = (ImageView) itemView.findViewById(R.id.fuzzyGrabBar);
        sensorSelect = (Spinner) itemView.findViewById(R.id.fuzzySensorSpinner);
        typeSelect = (Spinner) itemView.findViewById(R.id.fuzzyTypeSpinner);

        deleteCheck = (CheckBox) itemView.findViewById(R.id.flagsDeleteCheckBox);
        deleteCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isShown()) {
                    if (isChecked) {
                        toBeDeleted.add(flag);
                    } else {
                        toBeDeleted.remove(flag);
                    }
                }
            }
        });

        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && stillExists(flag)) {
                    updateName(name.getText().toString());
                }
            }
        });

        threshold1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && stillExists(flag)) {
                    ensureNotNull(threshold1);
                    DatabaseHelper db = new DatabaseHelper(v.getContext());
                    db.updateFuzzyFlag(flag.updatedArg1(threshold1.getText().toString(), db), flag.getName());
                }
            }
        });

        threshold2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && stillExists(flag)) {
                    ensureNotNull(threshold2);
                    DatabaseHelper db = new DatabaseHelper(v.getContext());
                    db.updateFuzzyFlag(flag.updatedArg2(threshold2.getText().toString(), db), flag.getName());
                }
            }
        });

        threshold3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && stillExists(flag)) {
                    ensureNotNull(threshold3);
                    DatabaseHelper db = new DatabaseHelper(v.getContext());
                    db.updateFuzzyFlag(flag.updatedArg3(threshold3.getText().toString(), db), flag.getName());
                }
            }
        });

        threshold4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && stillExists(flag)) {
                    ensureNotNull(threshold4);
                    DatabaseHelper db = new DatabaseHelper(v.getContext());
                    db.updateFuzzyFlag(flag.updatedArg4(threshold4.getText().toString(), db), flag.getName());
                }
            }
        });

        sensorSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DatabaseHelper db = new DatabaseHelper(view.getContext());
                db.updateFuzzyFlag(flag.updatedSensor(sensorSelect.getSelectedItem().toString()), flag.getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        typeSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DatabaseHelper db = new DatabaseHelper(view.getContext());
                flag = db.updateTypeOf(flag.getName(), typeSelect.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void updateName(String newName) {
        char[] nameChar = newName.toCharArray();
        StringBuilder name = new StringBuilder();
        for (char c : nameChar) {
            if (Character.isLetterOrDigit(c)) {
                name.append(c);
            }
        }

        for (FuzzyFlag f : flags) {
            if (f.getName().equals(name.toString())) {
                name.delete(0, name.length());
            }
        }

        if (name.length() <= 0) {
            name.append(flag.getName());
        }

        FuzzyFlag newFlag = flag.updatedName(name.toString());

        DatabaseHelper db = new DatabaseHelper(itemView.getContext());
        db.updateFuzzyFlag(newFlag, flag.getName());
        updateFlags(newFlag);
        this.name.setText(name.toString());
    }

    private void ensureNotNull(EditText et) {
        if (et.getText().toString().isEmpty()) {
            et.setText("0");
        }
    }

    private boolean stillExists(FuzzyFlag flag) {
        return flags.contains(flag);
    }

    private void updateFlags(FuzzyFlag newFlag) {
        flags.set(flags.indexOf(flag), newFlag);
        giveFlag(newFlag);
    }

    private void deleteFlag() {
        flags.remove(flag);
    }

    public void giveAdapter(FlagsAdapter adapter) {
        this.adapter = adapter;
    }

    public void giveFlag(FuzzyFlag flag) {
        this.flag = flag;
    }

    public CheckBox getDeleteCheck() {
        return deleteCheck;
    }

    public EditText getNameText() {
        return name;
    }

    public ImageView getGripBars() { return gripBars; }

    public Spinner getSensorSelect() { return sensorSelect; }
}
