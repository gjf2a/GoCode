package com.stack.gocode.viewHolders;

import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.stack.gocode.R;
import com.stack.gocode.adapters.ActionsAdapter;
import com.stack.gocode.localData.Action;
import com.stack.gocode.localData.DatabaseHelper;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ActionsViewHolder extends RecyclerView.ViewHolder {

    private EditText nameInput, leftMotorPowerInput, rightMotorPowerInput;
    private CheckBox resetLeftEncoder, resetRightEncoder, deleteCheck;
    private ImageView gripBars;
    private Action action;
    private ArrayList<Action> actions, toBeDeleted;
    private ActionsAdapter adapter;

    private static final String TAG = ActionsViewHolder.class.getSimpleName();

    public ActionsViewHolder(final View itemView, final ArrayList<Action> actions, final ArrayList<Action> toBeDeleted) {
        super(itemView);
        this.actions = actions;
        this.toBeDeleted = toBeDeleted;

        nameInput = (EditText) itemView.findViewById(R.id.editText6);
        leftMotorPowerInput = (EditText) itemView.findViewById(R.id.editText8);
        rightMotorPowerInput = (EditText) itemView.findViewById(R.id.editText9);

        resetLeftEncoder = (CheckBox) itemView.findViewById(R.id.checkBox);
        resetRightEncoder = (CheckBox) itemView.findViewById(R.id.checkBox2);

        deleteCheck = (CheckBox) itemView.findViewById(R.id.actionDeleteCheck);

        gripBars = (ImageView) itemView.findViewById(R.id.actionsGrabBar);

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

        resetLeftEncoder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isShown()) {
                    action.setResetLeftCount(isChecked);
                    DatabaseHelper db = new DatabaseHelper(buttonView.getContext());
                    db.updateAction(action, action);
                }
            }
        });

        resetRightEncoder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isShown()) {
                    action.setResetRightCount(isChecked);
                    DatabaseHelper db = new DatabaseHelper(buttonView.getContext());
                    db.updateAction(action, action);
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

        leftMotorPowerInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && actionExists(action)) {
                    preventNullInput(leftMotorPowerInput);
                    action.setLeftMotorInput(updateMotorInput(Integer.valueOf(leftMotorPowerInput.getText().toString()), leftMotorPowerInput));
                    DatabaseHelper db = new DatabaseHelper(v.getContext());
                    db.updateAction(action, action);
                }
            }
        });

        rightMotorPowerInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && actionExists(action)) {
                    preventNullInput(rightMotorPowerInput);
                    action.setRightMotorInput(
                            updateMotorInput(Integer.valueOf(rightMotorPowerInput.getText().toString()), rightMotorPowerInput));
                    DatabaseHelper db = new DatabaseHelper(v.getContext());
                    db.updateAction(action, action);
                }
            }
        });
    }

    private boolean actionExists(Action action) {
        return actions.contains(action);
    }

    private void preventNullInput(TextView tv) {
        if (tv.getText().toString().isEmpty()) {
            tv.setText("0");
        }
    }

    private void deleteAction(Action action) {
        actions.remove(action);
    }

    private void updateActions(Action newAction) {
        actions.set(actions.indexOf(action), newAction);
        action = newAction;
    }

    private void updateName(String newName) {
        char[] nameChar = newName.toCharArray();
        StringBuilder name = new StringBuilder();
        for (char c : nameChar) {
            if ((c >= 48 && c <= 57) || (c >= 65 && c <= 90) || (c >= 97 && c <= 122)) {
                name.append(c);
            }
        }

        for (Action a : actions) {
            if (a.getName().equals(name.toString())) {
                name.delete(0, name.length());
            }
        }

        if (name.length() <= 0) {
            name.append(action.getName());
        }

        DatabaseHelper db = new DatabaseHelper(itemView.getContext());
        Action newAction = new Action();
        newAction.setName(name.toString());
        newAction.setLeftMotorInput(action.getLeftMotorInput());
        newAction.setRightMotorInput(action.getRightMotorInput());
        newAction.setResetLeftCount(action.isResetLeftCount());
        newAction.setResetRightCount(action.isResetRightCount());
        db.updateAction(action, newAction);

        updateActions(newAction);
        nameInput.setText(name.toString());
    }

    private int updateMotorInput(Integer motorInput, TextView v) {
        int motorIn = (int) motorInput;

        if (motorIn < -127) {
            motorIn = -127;
        } else if (motorIn > 127) {
            motorIn = 127;
        }

        v.setText(motorIn + "");
        return motorIn;
    }

    public CheckBox getDeleteCheck() {
        return deleteCheck;
    }

    public EditText getNameInput() {
        return nameInput;
    }

    public EditText getLeftMotorPowerInput() {
        return leftMotorPowerInput;
    }

    public EditText getRightMotorPowerInput() {
        return rightMotorPowerInput;
    }

    public CheckBox getResetLeftEncoder() {
        return resetLeftEncoder;
    }

    public CheckBox getResetRightEncoder() {
        return resetRightEncoder;
    }

    public ImageView getGripBars() { return gripBars; }

    public void giveAction(Action action) {
        this.action = action;
    }

    public void giveAdapter(ActionsAdapter adapter) {
        this.adapter = adapter;
    }
}
