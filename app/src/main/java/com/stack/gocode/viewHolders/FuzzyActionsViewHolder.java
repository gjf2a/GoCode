package com.stack.gocode.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.stack.gocode.R;
import com.stack.gocode.adapters.ActionsAdapter;
import com.stack.gocode.adapters.FuzzyActionsAdapter;
import com.stack.gocode.localData.Action;
import com.stack.gocode.localData.DatabaseHelper;
import com.stack.gocode.localData.fuzzy.FuzzyAction;
import com.stack.gocode.localData.fuzzy.FuzzyMotor;

import java.util.ArrayList;

public class FuzzyActionsViewHolder extends RecyclerView.ViewHolder {

    private EditText nameInput;
    private Spinner leftFuzz, leftDefuzz, rightFuzz, rightDefuzz;
    private CheckBox deleteCheck;
    private ImageView gripBars;
    private FuzzyAction action;
    private ArrayList<FuzzyAction> actions, toBeDeleted;
    private FuzzyActionsAdapter adapter;

    private static final String TAG = FuzzyActionsViewHolder.class.getSimpleName();

    public FuzzyActionsViewHolder(final View itemView, final ArrayList<FuzzyAction> actions, final ArrayList<FuzzyAction> toBeDeleted) {
        super(itemView);
        this.actions = actions;
        this.toBeDeleted = toBeDeleted;

        nameInput = (EditText) itemView.findViewById(R.id.fuzzy_action_name);
        leftFuzz = (Spinner) itemView.findViewById(R.id.fuzzy_flag_left_motor);
        leftDefuzz = (Spinner) itemView.findViewById(R.id.defuzzifier_left_motor);
        rightFuzz = (Spinner) itemView.findViewById(R.id.fuzzy_flag_right_motor);
        rightDefuzz = (Spinner) itemView.findViewById(R.id.defuzzifier_right_motor);
        deleteCheck = (CheckBox) itemView.findViewById(R.id.fuzzy_action_delete_check);

        gripBars = (ImageView) itemView.findViewById(R.id.fuzzy_action_grab_bar);

        deleteCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isShown()) {
                    if (isChecked) {
                        toBeDeleted.add(action);
                    } else {
                        toBeDeleted.remove(action);
                    }
                }
            }
        });

        nameInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.i(TAG, "nameInput focus change listener befor if statement. Action exists in actions: " + actionExists(action) + " Action:" + action.toString());
                if (!hasFocus && actionExists(action)) {
                    Log.i(TAG, "nameInput focus change listener if statement accured.");
                    updateName(nameInput.getText().toString());
                }
            }
        });
    }

    private boolean actionExists(FuzzyAction action) {
        return actions.contains(action);
    }

    private void preventNullInput(TextView tv) {
        if (tv.getText().toString().isEmpty()) {
            tv.setText("0");
        }
    }

    private void deleteAction(FuzzyAction action) {
        actions.remove(action);
    }

    private void updateActions(FuzzyAction newAction) {
        actions.set(actions.indexOf(action), newAction);
        action = newAction;
    }

    private void updateName(String newName) {
        char[] nameChar = newName.toCharArray();
        StringBuilder name = new StringBuilder();
        for (char c : nameChar) {
            if (Character.isLetterOrDigit(c)) {
                name.append(c);
            }
        }

        for (FuzzyAction a : actions) {
            Log.i(TAG, "FuzzyAction name: '" + a.getName() + "'");
            if (a.getName().equals(name.toString())) {
                name.delete(0, name.length());
            }
        }

        if (name.length() <= 0) {
            name.append(action.getName());
        }

        DatabaseHelper db = new DatabaseHelper(itemView.getContext());
        FuzzyAction newAction = new FuzzyAction(name.toString(), action.getLeft(), action.getRight());
        db.updateFuzzyAction(newAction, action.getName());

        updateActions(newAction);
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

    public ImageView getGripBars() { return gripBars; }

    public Spinner getLeftFuzz() {return leftFuzz;}

    public Spinner getLeftDefuzz() {return leftDefuzz;}

    public Spinner getRightFuzz() {return rightFuzz;}

    public Spinner getRightDefuzz() {return rightDefuzz;}

    public void giveFuzzyAction(FuzzyAction action) {
        this.action = action;
    }

    public void giveAdapter(FuzzyActionsAdapter adapter) {
        this.adapter = adapter;
    }
}
