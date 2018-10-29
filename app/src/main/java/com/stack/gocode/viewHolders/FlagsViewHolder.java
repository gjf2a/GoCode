package com.stack.gocode.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;

import com.stack.gocode.R;
import com.stack.gocode.adapters.FlagsAdapter;
import com.stack.gocode.localData.DatabaseHelper;
import com.stack.gocode.localData.Flag;

import java.util.ArrayList;

public class FlagsViewHolder extends RecyclerView.ViewHolder {

    private EditText name;
    private EditText threshold;
    private Switch greaterThan;
    private CheckBox deleteCheck;
    private ImageView gripBars;
    private Spinner sensorSelect;
    private ArrayList<Flag> flags, toBeDeleted;

    private Flag flag;

    private FlagsAdapter adapter;

    public FlagsViewHolder(final View itemView, ArrayList<Flag> flags, final ArrayList<Flag> toBeDeleted) {
        super(itemView);
        this.flags = flags;
        this.toBeDeleted = toBeDeleted;

        name = (EditText) itemView.findViewById(R.id.editText10);
        threshold = (EditText) itemView.findViewById(R.id.flagThreshhold);
        greaterThan = (Switch) itemView.findViewById(R.id.switch1);
        gripBars = (ImageView) itemView.findViewById(R.id.flagsGrabBar);
        sensorSelect = (Spinner) itemView.findViewById(R.id.flagsSonarSpinner);

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

        threshold.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && stillExists(flag)) {
                    ensureNotNull(threshold);
                    flag.setTriggerValue(Double.valueOf(threshold.getText().toString()));
                    DatabaseHelper db = new DatabaseHelper(v.getContext());
                    db.updateFlag(flag, flag);
                }
            }
        });

        greaterThan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isShown()) {
                    flag.setGreaterThan(isChecked);
                    DatabaseHelper db = new DatabaseHelper(itemView.getContext());
                    db.updateFlag(flag, flag);
                }
            }
        });

        sensorSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                flag.setSensor(sensorSelect.getSelectedItem().toString());
                DatabaseHelper db = new DatabaseHelper(view.getContext());
                db.updateFlag(flag, flag);
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
            if ((c >= 48 && c <= 57) || (c >= 65 && c <= 90) || (c >= 97 && c <= 122)) {
                name.append(c);
            }
        }

        for (Flag f : flags) {
            if (f.getName().equals(name.toString())) {
                name.delete(0, name.length());
            }
        }

        if (name.length() <= 0) {
            name.append(flag.getName());
        }

        Flag newFlag = new Flag(name.toString(), flag.getSensor(), flag.isGreaterThan(), flag.getTriggerValue());
        newFlag.setTrue(flag.isTrue());

        DatabaseHelper db = new DatabaseHelper(itemView.getContext());
        db.updateFlag(newFlag, flag);
        updateFlags(newFlag);
        this.name.setText(name.toString());
    }

    private void ensureNotNull(EditText et) {
        if (et.getText().toString().isEmpty()) {
            et.setText("0");
        }
    }

    private boolean stillExists(Flag flag) {
        return flags.contains(flag);
    }

    private void updateFlags(Flag newFlag) {
        flags.set(flags.indexOf(flag), newFlag);
        flag = newFlag;
    }

    private void deleteFlag() {
        flags.remove(flag);
    }

    public void giveAdapter(FlagsAdapter adapter) {
        this.adapter = adapter;
    }

    public void giveFlag(Flag flag) {
        this.flag = flag;
    }

    public CheckBox getDeleteCheck() {
        return deleteCheck;
    }

    public EditText getNameText() {
        return name;
    }

    public EditText getThreshold() {
        return threshold;
    }

    public Switch getGreaterThan() {
        return greaterThan;
    }

    public ImageView getGripBars() { return gripBars; }

    public Spinner getSensorSelect() { return sensorSelect; }
}
