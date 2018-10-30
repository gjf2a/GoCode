package com.stack.gocode.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.stack.gocode.R;
import com.stack.gocode.adapters.DefuzzifierAdapter;
import com.stack.gocode.localData.Action;
import com.stack.gocode.localData.DatabaseHelper;
import com.stack.gocode.localData.fuzzy.Defuzzifier;

import java.util.ArrayList;

public class DefuzzifierViewHolder extends RecyclerView.ViewHolder {

    private EditText nameInput, firstSpeed, secondSpeed;
    private CheckBox deleteCheck;
    private ImageView gripBars;
    private Defuzzifier defuzzifier;
    private ArrayList<Defuzzifier> defuzzifiers, toBeDeleted;
    private DefuzzifierAdapter adapter;

    private static final String TAG = DefuzzifierViewHolder.class.getSimpleName();

    public DefuzzifierViewHolder(final View itemView, final ArrayList<Defuzzifier> defuzzifiers, final ArrayList<Defuzzifier> toBeDeleted) {
        super(itemView);
        this.defuzzifiers = defuzzifiers;
        this.toBeDeleted = toBeDeleted;

        nameInput = (EditText) itemView.findViewById(R.id.defuzzifier_name);
        firstSpeed = (EditText) itemView.findViewById(R.id.defuzzifierSpeed1);
        secondSpeed = (EditText) itemView.findViewById(R.id.defuzzifierSpeed2);

        deleteCheck = (CheckBox) itemView.findViewById(R.id.defuzzifierDeleteCheck);

        gripBars = (ImageView) itemView.findViewById(R.id.defuzzifierGrabBar);

        deleteCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isShown()) {
                    if (isChecked) {
                        toBeDeleted.add(defuzzifier);
                    } else {
                        toBeDeleted.remove(defuzzifier);
                    }
                }
            }
        });

        nameInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.i(TAG, "nameInput focus change listener befor if statement. Action exists in actions: " + defuzzifierExists(defuzzifier) + " Action:" + defuzzifier.toString());
                if (!hasFocus && defuzzifierExists(defuzzifier)) {
                    Log.i(TAG, "nameInput focus change listener if statement accured.");
                    updateName(nameInput.getText().toString());
                }
            }
        });

        firstSpeed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && defuzzifierExists(defuzzifier)) {
                    preventNullInput(firstSpeed);
                    defuzzifier.setSpeed1(updateMotorInput(Integer.valueOf(firstSpeed.getText().toString()), firstSpeed));
                    DatabaseHelper db = new DatabaseHelper(v.getContext());
                    db.updateDefuzzifier(defuzzifier, defuzzifier.getName());
                }
            }
        });

        secondSpeed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && defuzzifierExists(defuzzifier)) {
                    preventNullInput(secondSpeed);
                    defuzzifier.setSpeed2(
                            updateMotorInput(Integer.valueOf(secondSpeed.getText().toString()), secondSpeed));
                    DatabaseHelper db = new DatabaseHelper(v.getContext());
                    db.updateDefuzzifier(defuzzifier, defuzzifier.getName());
                }
            }
        });
    }

    private boolean defuzzifierExists(Defuzzifier defuzzifier) {
        return defuzzifiers.contains(defuzzifier);
    }

    private void preventNullInput(TextView tv) {
        if (tv.getText().toString().isEmpty()) {
            tv.setText("0");
        }
    }

    private void deleteAction(Defuzzifier defuzzifier) {
        defuzzifiers.remove(defuzzifier);
    }

    private void updateDefuzzifier(Defuzzifier defuzzifier) {
        defuzzifiers.set(defuzzifiers.indexOf(this.defuzzifier), defuzzifier);
        this.defuzzifier = defuzzifier;
    }

    private void updateName(String newName) {
        char[] nameChar = newName.toCharArray();
        StringBuilder name = new StringBuilder();
        for (char c : nameChar) {
            if (Character.isLetterOrDigit(c)) {
                name.append(c);
            }
        }

        for (Defuzzifier a : defuzzifiers) {
            if (a.getName().equals(name.toString())) {
                name.delete(0, name.length());
            }
        }

        if (name.length() <= 0) {
            name.append(defuzzifier.getName());
        }

        DatabaseHelper db = new DatabaseHelper(itemView.getContext());
        Defuzzifier newDefuzzifier = new Defuzzifier(name.toString(), defuzzifier.getSpeed1(), defuzzifier.getSpeed2());
        db.updateDefuzzifier(newDefuzzifier, defuzzifier.getName());

        updateDefuzzifier(newDefuzzifier);
        nameInput.setText(name.toString());
    }

    private int updateMotorInput(Integer motorInput, TextView v) {
        int motorIn = Action.enforceMotorRange(motorInput);
        v.setText(motorIn + "");
        return motorIn;
    }

    public CheckBox getDeleteCheck() {
        return deleteCheck;
    }

    public EditText getNameInput() {
        return nameInput;
    }

    public EditText getFirstSpeed() {
        return firstSpeed;
    }

    public EditText getSecondSpeed() {
        return secondSpeed;
    }

    public ImageView getGripBars() { return gripBars; }

    public void giveDefuzzifier(Defuzzifier defuzzifier) {
        this.defuzzifier = defuzzifier;
    }

    public void giveAdapter(DefuzzifierAdapter adapter) {
        this.adapter = adapter;
    }
}
