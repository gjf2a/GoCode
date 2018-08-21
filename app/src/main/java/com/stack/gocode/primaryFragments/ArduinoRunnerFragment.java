package com.stack.gocode.primaryFragments;

import android.app.Activity;
import android.app.Fragment;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.stack.gocode.MainActivity;
import com.stack.gocode.R;
import com.stack.gocode.communications.ArduinoTalker;
import com.stack.gocode.communications.TalkerListener;
import com.stack.gocode.localData.Action;
import com.stack.gocode.localData.DatabaseHelper;
import com.stack.gocode.localData.Flag;
import com.stack.gocode.localData.Mode;
import com.stack.gocode.localData.TransitionTable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

public class ArduinoRunnerFragment extends Fragment {

    private View myView;
    private ArduinoTalker talker;
    private Button powerButton;
    private TextView bytesSent, bytesReceived, allTrueFlags;

    private boolean run = false;

    private static final String TAG = ArduinoRunnerFragment.class.getSimpleName();

    private static final int MESSAGE_SEND_SIZE = 5;
    private static final int MESSAGE_RECEIVE_SIZE = 14;

    private ArrayList<Flag> flags;
    private ArrayList<Mode> modes;
    private ArrayList<TransitionTable> tables;
    private TreeSet<Flag> trueFlags;
    private String[] sensors = {"sonar1", "sonar2", "sonar3", "leftEncoder", "rightEncoder"};
    private int[] sensorValues = new int[sensors.length];

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.arduino_runner, container, false);

        deviceCheck();
        bytesSent = myView.findViewById(R.id.arduinoRunnerBytesSent);
        bytesReceived = myView.findViewById(R.id.arduinoRunnerBytesReceived);
        allTrueFlags = myView.findViewById(R.id.arduinoRunnerTrueFlags);
        powerButton = myView.findViewById(R.id.tempGoer);
        powerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                run = !run;
                if (run) {
                    runArduino(v);
                }
            }
        });

        return myView;
    }

    private void deviceCheck() {
        if (talker == null || !talker.connected()) {
            talker = new ArduinoTalker((UsbManager) this.getActivity().getSystemService(myView.getContext().USB_SERVICE));
            if (talker.connected()) {
                // tell user device good to go
            } else {
                //set status box with status message
            }
        } else {
            //set status box with status message
        }
    }

    public void clearResponses(View view) {
        //set response box
    }

    private int send(Action action) {
        byte[] bytes = action.getInstruction();

        Log.i(TAG, "Starting send");
        return talker.send(bytes);
    }

    private void runArduino(final View view) {

        setRun();
        modePopulator();
        new Thread(new Runnable() {public void run() {
            //todo put sentinels for valid start state


            Mode currentMode = getStartMode();
            TransitionTable currentTable = currentMode.getNextLayer();
            Action currentAction = currentMode.getAction();

            while(run) {  //Ask Dr. Ferrer: what should this loop do if no flags in the current transition table are true? no transition occurs
                int sentBytes = send(currentAction);
                Log.i(TAG, sentBytes + " bytes sent");
                if (sentBytes < 0) {
                  // Put status message on GUI
                  //break;
                    Log.e(TAG, "SentBytes < 0");
                } else {
                    byte[] received = talker.receive(MESSAGE_RECEIVE_SIZE);
                    if (received.length == 0) {
                        // Put status message on GUI
                        //break;
                        Log.e(TAG, "Error on receiving data from Arduino.");
                    } else {
                        updateSensorValues(received);
                        findTrueFlags(currentTable);

                        Log.i(TAG, "Current Mode:   " + currentMode.toString());
                        currentMode = currentTable.getTriggeredMode();
                        Log.i(TAG, "New Mode:       " + currentMode.toString());
                        Log.i(TAG, "Current Table:  " + currentTable.getName());
                        currentTable = currentMode.getNextLayer();
                        Log.i(TAG, "New Table:      " + currentTable.getName());
                        Log.i(TAG, "Current Action: " + currentAction.toString());
                        currentAction = currentMode.getAction();
                        Log.i(TAG, "New Action:     " + currentAction.toString());

                        //todo display true flags, current mode, and current TransitionRow
                        updateGUI(currentAction);

                        Log.i(TAG, " FlagChecking: " + trueFlags.toString());
                        Log.i(TAG, " FlagChecking: " + sensorValues[0] + ", " + sensorValues[1] + ", " + sensorValues[2]);
                    }
                }
            }
        }}).start();
    }

    private void setRun() {
        DatabaseHelper db = new DatabaseHelper(myView.getContext());
        flags = db.getAllFlags();
        modes = db.getAllModes();
        tables = db.getAllTransitionTables();
        trueFlags = new TreeSet<Flag>();
    }

    private Mode getStartMode() {
        DatabaseHelper db = new DatabaseHelper(myView.getContext());
        String name = db.getStartMode();
        for (Mode m : modes) {
            if (m.getName().equals(name)) {
                return m;
            }
        }
        return modes.get(0);
    }

    private void updateGUI(final Action currentAction) {
        getActivity().runOnUiThread(new Runnable() {   //https://stackoverflow.com/questions/31843577/runonuithread-method-in-fragment
            @Override
            public void run() {
                allTrueFlags.setText(trueFlags.toString());

                StringBuilder values = new StringBuilder();
                for (int i : sensorValues) {
                    values.append(i + ", ");
                }
                bytesReceived.setText(values.toString());
                bytesSent.setText(currentAction.toString());
            }
        });
    }

    private void modePopulator() {
        for (Mode m : modes) {
            m.setNextLayer(modePopulatorHelper(m, tables));
        }

        transitionTableModePopulator();
    }

    private TransitionTable modePopulatorHelper(Mode mode, ArrayList<TransitionTable> tables) {
        for (TransitionTable t : tables) {
            if (mode.getTtName().equals(t.getName())) {
                return t;
            }
        }
        return new TransitionTable();
    }

    private void transitionTableModePopulator() {
        for (TransitionTable t : tables) {
            for (int i = 0; i < t.getSize(); i++) {
                String modeName = t.getMode(i).getName();
                t.setMode(i, findMode(modeName, t));
            }
        }
    }

    private Mode findMode(String name, TransitionTable table) {
            for (Mode m : modes) {
                if (m.getName().equals(name)) {
                    return m;
                }
            }
        //String error = "Table: " + table + " attempted to call Mode: " + name + ". No such Mode exists.";
        //bytesSent.setText(error);
            return new Mode();

    }

    private void updateSensorValues(byte[] received) {
        // sonars: 3
        // encoders: 2
        // bytes per sonar: 2
        // bytes per encoder: 4
        int bytePerEncoder = 4;
        int encoderAmount = 2;
        for (int i = 0; i < sensorValues.length - encoderAmount; i++) {
            Log.i(TAG, "Index: " + i + ", received.length: " + received.length);
            sensorValues[i] = ByteBuffer.wrap(Arrays.copyOfRange(received, i * 2, (i * 2) + 2)).order(ByteOrder.LITTLE_ENDIAN).getShort();
        }
        StringBuilder values = new StringBuilder();
        for (byte b: received) {
            values.append(b + ",  ");
        }
        Log.i(TAG, "Values: " + values.toString());
        Log.i(TAG, "Decrypt encoders start.");
        sensorValues[sensorValues.length - 2] = ByteBuffer.wrap(Arrays.copyOfRange(received, 6, 10)).order(ByteOrder.LITTLE_ENDIAN).getInt();
        sensorValues[sensorValues.length - 1] = ByteBuffer.wrap(Arrays.copyOfRange(received, 10, 14)).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    private void findTrueFlags(TransitionTable currentTable) {
        trueFlags.clear();
        for (Flag f : flags) {
            int valueIndex = findFlagsSensor(sensors, f.getSensor());
            if (valueIndex >= 0 && valueIndex < sensorValues.length) {
                f.updateCondition(sensorValues[valueIndex]);
                if (f.isTrue()) {
                    trueFlags.add(f);
                }
            } else {
                //error: inform user that flag uses invalid sensor
            }

        }

        for (int i = 0; i < currentTable.getSize(); i++) {
            currentTable.getFlag(i).setTrue(
                    trueFlags.contains(currentTable.getFlag(i)));
        }
    }

    private int findFlagsSensor(String[] sensors, String sensor) {
        for (int i = 0; i < sensors.length; i++) {
            if (sensors[i].equals(sensor)) {
                return i;
            }
        }
        return -1;
    }

    private byte[] dummySensors() {
        byte[] dummyValues = new byte[MESSAGE_RECEIVE_SIZE];
        for (int i = 0; i < dummyValues.length; i++) {
            if (i % 2 == 0) {
                dummyValues[i] = (byte) Math.floor(Math.random() * 10);
            } else {
                dummyValues[i] = 0;
            }
        }
        return dummyValues;
    }
}