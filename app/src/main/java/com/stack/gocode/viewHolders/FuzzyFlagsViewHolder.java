package com.stack.gocode.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.stack.gocode.ModalDialogs;
import com.stack.gocode.R;
import com.stack.gocode.adapters.FuzzyFlagsAdapter;
import com.stack.gocode.localData.DatabaseHelper;
import com.stack.gocode.localData.fuzzy.FuzzyArgs;
import com.stack.gocode.localData.fuzzy.FuzzyFlag;
import com.stack.gocode.localData.fuzzy.FuzzyType;

import java.util.ArrayList;

public class FuzzyFlagsViewHolder extends RecyclerView.ViewHolder {

    private EditText name;
    private EditText[] thresholds = new EditText[FuzzyArgs.NUM_FUZZY_ARGS];
    private Spinner[] flagSpinners = new Spinner[FuzzyArgs.NUM_FUZZY_ARGS];
    private CheckBox deleteCheck;
    private ImageView gripBars;
    private Spinner sensorSelect;
    private Spinner typeSelect;
    private ArrayList<FuzzyFlag> flags, toBeDeleted;

    private FuzzyFlag flag;

    private FuzzyFlagsAdapter adapter;

    public FuzzyFlagsViewHolder(final View itemView, ArrayList<FuzzyFlag> flags, final ArrayList<FuzzyFlag> toBeDeleted) {
        super(itemView);
        this.flags = flags;
        this.toBeDeleted = toBeDeleted;

        name = (EditText) itemView.findViewById(R.id.fuzzyFlagName);
        thresholds[0] = (EditText) itemView.findViewById(R.id.fuzzyThreshold0);
        thresholds[1] = (EditText) itemView.findViewById(R.id.fuzzyThreshold1);
        thresholds[2] = (EditText) itemView.findViewById(R.id.fuzzyThreshold2);
        thresholds[3] = (EditText) itemView.findViewById(R.id.fuzzyThreshold3);
        flagSpinners[0] = (Spinner) itemView.findViewById(R.id.fuzzy_subflag_0);
        flagSpinners[1] = (Spinner) itemView.findViewById(R.id.fuzzy_subflag_1);
        flagSpinners[2] = (Spinner) itemView.findViewById(R.id.fuzzy_subflag_2);
        flagSpinners[3] = (Spinner) itemView.findViewById(R.id.fuzzy_subflag_3);
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

        for (int i = 0; i < thresholds.length; i++) {
            final int index = i;
            thresholds[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus && stillExists(flag)) {
                        ensureNotNull(thresholds[index]);
                        DatabaseHelper db = new DatabaseHelper(v.getContext());
                        flag.setArg(index, thresholds[index].getText().toString(), db);
                        db.updateFuzzyFlag(flag, flag.getName());
                    }
                }
            });
        }

        for (int i = 0; i < flagSpinners.length; i++) {
            final int index = i;
            flagSpinners[i].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (!flag.getType().isNum()) {
                        String argName = flagSpinners[index].getSelectedItem().toString();
                        DatabaseHelper db = new DatabaseHelper(view.getContext());
                        FuzzyFlag childFlag = db.getFuzzyFlag(argName);
                        if (flag.isCycleChild(childFlag)) {
                            ModalDialogs.notifyProblem(view.getContext(), "Selecting child " + argName + " creates a cycle.");
                        } else {
                            flag.setArg(index, argName, db);
                            db.updateFuzzyFlag(flag, flag.getName());
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

        sensorSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DatabaseHelper db = new DatabaseHelper(view.getContext());
                flag.setSensor(sensorSelect.getSelectedItem().toString());
                db.updateFuzzyFlag(flag, flag.getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        typeSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DatabaseHelper db = new DatabaseHelper(view.getContext());
                flag.setType(typeSelect.getSelectedItem().toString(), db);
                FuzzyType type = flag.getType();
                db.updateFuzzyFlag(flag, flag.getName());
                for (int i = 0; i < thresholds.length; i++) {
                    thresholds[i].setVisibility(type.isNum() && i < type.numArgs() ? View.VISIBLE : View.GONE);
                }
                for (int i = 0; i < flagSpinners.length; i++) {
                    flagSpinners[i].setVisibility(!type.isNum() && i < type.numArgs() ? View.VISIBLE : View.GONE);
                }
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

        FuzzyFlag newFlag = flag.duplicate();
        newFlag.setName(name.toString());

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

    public void giveAdapter(FuzzyFlagsAdapter adapter) {
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

    public EditText getThreshold(int i) {return thresholds[i];}

    public Spinner getFuzzyFlagSpinner(int i) {return flagSpinners[i];}

    public Spinner getTypeSelect() {return typeSelect;}
}
