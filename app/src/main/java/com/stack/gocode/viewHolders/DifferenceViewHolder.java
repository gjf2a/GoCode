package com.stack.gocode.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.stack.gocode.R;
import com.stack.gocode.adapters.DefuzzifierAdapter;
import com.stack.gocode.adapters.DifferenceAdapter;
import com.stack.gocode.localData.Action;
import com.stack.gocode.localData.DatabaseHelper;
import com.stack.gocode.localData.fuzzy.Defuzzifier;
import com.stack.gocode.sensors.Symbol;

import java.util.ArrayList;
import java.util.Scanner;

public class DifferenceViewHolder extends RecyclerView.ViewHolder {

    private EditText nameInput;
    private CheckBox deleteCheck, absCheck;
    private Spinner sensor1, sensor2;
    private ImageView gripBars;
    private Symbol difference;
    private ArrayList<Symbol> differences, toBeDeleted;
    private DifferenceAdapter adapter;

    private static final String TAG = DifferenceViewHolder.class.getSimpleName();

    public DifferenceViewHolder(final View itemView, final ArrayList<Symbol> differences, final ArrayList<Symbol> toBeDeleted) {
        super(itemView);
        this.differences = differences;
        this.toBeDeleted = toBeDeleted;

        nameInput = (EditText) itemView.findViewById(R.id.difference_name);
        deleteCheck = (CheckBox) itemView.findViewById(R.id.differenceDeleteCheck);
        absCheck = (CheckBox) itemView.findViewById(R.id.difference_abs_check);
        sensor1 = (Spinner) itemView.findViewById(R.id.difference_sensor_1);
        sensor2 = (Spinner) itemView.findViewById(R.id.difference_sensor_2);
        gripBars = (ImageView) itemView.findViewById(R.id.differenceGrabBar);

        deleteCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isShown()) {
                    if (isChecked) {
                        toBeDeleted.add(difference);
                    } else {
                        toBeDeleted.remove(difference);
                    }
                }
            }
        });

        absCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isShown()) {
                    difference.setAbsoluteValue(isChecked);
                    DatabaseHelper db = new DatabaseHelper(buttonView.getContext());
                    db.updateSymbol(difference, difference.getName());
                }
            }
        });

        nameInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && symbolExists(difference)) {
                    updateName(nameInput.getText().toString());
                }
            }
        });

        sensor1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                difference.setSensorOne(sensor1.getSelectedItem().toString());
                DatabaseHelper db = new DatabaseHelper(view.getContext());
                db.updateSymbol(difference, difference.getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sensor2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                difference.setSensorTwo(sensor2.getSelectedItem().toString());
                DatabaseHelper db = new DatabaseHelper(view.getContext());
                db.updateSymbol(difference, difference.getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private boolean symbolExists(Symbol symbol) {
        return differences.contains(symbol);
    }

    private void preventNullInput(TextView tv) {
        if (tv.getText().toString().isEmpty()) {
            tv.setText("0");
        }
    }

    private void deleteAction(Defuzzifier defuzzifier) {
        differences.remove(defuzzifier);
    }

    private void updateDifference(Symbol difference) {
        differences.set(differences.indexOf(this.difference), difference);
        this.difference = difference;
    }

    private void updateName(String newName) {
        char[] nameChar = newName.toCharArray();
        StringBuilder name = new StringBuilder();
        for (char c : nameChar) {
            if (Character.isLetterOrDigit(c)) {
                name.append(c);
            }
        }

        for (Symbol a : differences) {
            if (a.getName().equals(name.toString())) {
                name.delete(0, name.length());
            }
        }

        if (name.length() <= 0) {
            name.append(difference.getName());
        }

        DatabaseHelper db = new DatabaseHelper(itemView.getContext());
        Symbol newSymbol = new Symbol(name.toString(), difference.getSensorOne(), difference.getSensorTwo(), difference.absoluteValue());
        db.updateSymbol(newSymbol, difference.getName());

        updateDifference(newSymbol);
        nameInput.setText(name.toString());
    }

    public CheckBox getDeleteCheck() {
        return deleteCheck;
    }

    public EditText getNameInput() {
        return nameInput;
    }

    public Spinner getSensorOne() {return sensor1;}

    public Spinner getSensorTwo() {return sensor2;}

    public CheckBox getAbsCheck() {return absCheck;}

    public ImageView getGripBars() { return gripBars; }

    public void giveSymbol(Symbol symbol) {
        this.difference = symbol;
    }

    public void giveAdapter(DifferenceAdapter adapter) {
        this.adapter = adapter;
    }
}
