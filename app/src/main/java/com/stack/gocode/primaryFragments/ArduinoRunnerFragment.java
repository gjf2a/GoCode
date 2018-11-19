package com.stack.gocode.primaryFragments;

import android.app.Fragment;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.stack.gocode.ModalDialogs;
import com.stack.gocode.R;
import com.stack.gocode.Util;
import com.stack.gocode.com.stack.gocode.exceptions.ItemNotFoundException;
import com.stack.gocode.communications.ArduinoTalker;
import com.stack.gocode.localData.Action;
import com.stack.gocode.localData.DatabaseHelper;
import com.stack.gocode.localData.Flag;
import com.stack.gocode.localData.InstructionCreator;
import com.stack.gocode.localData.Mode;
import com.stack.gocode.localData.TransitionTable;
import com.stack.gocode.sensors.SensedValues;
import com.stack.gocode.sensors.Symbol;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.TreeSet;

public class ArduinoRunnerFragment extends Fragment implements CameraBridgeViewBase.CvCameraViewListener2 {

    private View myView;
    private ArduinoTalker talker;
    private Button powerButton;
    private TextView bytesSent, bytesReceived, allTrueFlags, cycleTime;

    private CameraBridgeViewBase mOpenCvCameraView;

    private boolean run = false;

    private static final String TAG = ArduinoRunnerFragment.class.getSimpleName();

    private static final int MESSAGE_SEND_SIZE = 5;
    private static final int MESSAGE_RECEIVE_SIZE = 14;

    private ArrayList<Mode> modes;
    private ArrayList<TransitionTable> tables;

    private SensedValues lastSensed = SensedValues.makeFarawayDefault();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.arduino_runner, container, false);

        deviceCheck();
        bytesSent = myView.findViewById(R.id.arduinoRunnerBytesSent);
        bytesReceived = myView.findViewById(R.id.arduinoRunnerBytesReceived);
        allTrueFlags = myView.findViewById(R.id.arduinoRunnerTrueFlags);
        cycleTime = myView.findViewById(R.id.arduinoRunnerCycleTime);
        powerButton = myView.findViewById(R.id.tempGoer);
        powerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    run = !run;
                    if (run) {
                        runArduino(v);
                    }
                } catch (Exception exc) {
                    ModalDialogs.notifyException(v.getContext(), exc);
                }
            }
        });

        mOpenCvCameraView = (CameraBridgeViewBase) myView.findViewById(R.id.video_runner_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.enableView();

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

    private int send(byte[] bytes) {
        Log.i(TAG, "Starting send");
        int sent = talker.send(bytes);
        Log.i(TAG, "send response: " + sent);
        return sent;
    }

    private int halt() {
        return send(new byte[]{'A', 0, 0, 0, 0});
    }

    private void runArduino(final View view) throws ItemNotFoundException {
        setRun();
        modePopulator();
        new Thread(new Runnable() {
            public void run() {
                //TODO: put sentinels for valid start state

                try {
                    Mode currentMode = getStartMode();
                    TransitionTable currentTable = currentMode.getNextLayer();
                    InstructionCreator currentAction = currentMode.getAction();
                    Log.i(TAG, "About to start run loop");
                    DatabaseHelper db = new DatabaseHelper(myView.getContext());
                    long start = System.currentTimeMillis();
                    long cycles = 1;

                    while (run) {  //Ask Dr. Ferrer: what should this loop do if no flags in the current transition table are true? no transition occurs
                        lastSensed.setSymbolValues(db.getSymbolList());
                        byte[] bytes = currentAction.getInstruction(lastSensed);
                        int sentBytes = send(bytes);
                        Log.i(TAG, sentBytes + " bytes sent");
                        if (sentBytes < 0) {
                            Log.e(TAG, "SentBytes < 0");
                        } else {
                            byte[] received = talker.receive(MESSAGE_RECEIVE_SIZE);
                            if (received.length == 0) {
                                // Put status message on GUI
                                //break;
                                Log.e(TAG, "Error on receiving data from Arduino.");
                            } else {
                                lastSensed.updateFromSensors(received);
                                lastSensed.addColorData(new ArrayList<>(currentTable.allReferencedSensors()), db);
                                TreeSet<Flag> trueFlags = findTrueFlags(lastSensed, currentTable);

                                Log.i(TAG, "Current Mode:   " + currentMode.toString());
                                currentMode = currentTable.getTriggeredMode(currentMode);
                                Log.i(TAG, "New Mode:       " + currentMode.toString());
                                Log.i(TAG, "Current Table:  " + currentTable.getName());
                                currentTable = currentMode.getNextLayer();
                                Log.i(TAG, "New Table:      " + currentTable.getName());
                                Log.i(TAG, "Current Action: " + currentAction.toString());
                                currentAction = currentMode.getAction();
                                Log.i(TAG, "New Action:     " + currentAction.toString());

                                double hz = (double)(System.currentTimeMillis() - start) / cycles;
                                updateGUI(trueFlags.toString(), "Motors:"+bytes[1]+":"+bytes[2]+":" + currentAction.toString(), lastSensed.toString(), hz);
                                cycles += 1;

                                Log.i(TAG, "FlagChecking: " + trueFlags.toString());
                                Log.i(TAG, "Sensed Values: " + lastSensed);
                            }
                        }
                    }
                } catch (Exception infe) {
                    Log.e(TAG, "Exception! " + infe.toString());
                    Log.e(TAG, Util.stackTrace2String(infe));
                    quit(infe.getMessage());
                }
                Log.i(TAG, "Exiting run loop; sending halt message");
                halt();
            }
        }).start();
    }

    private void setRun() {
        DatabaseHelper db = new DatabaseHelper(myView.getContext());
        modes = db.getModeList();
        tables = db.getTransitionTableList();
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

    private void updateGUI(final String trueFlagString, final String currentActionString, final String sensedString, final double hz) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {   //https://stackoverflow.com/questions/31843577/runonuithread-method-in-fragment
                @Override
                public void run() {
                    allTrueFlags.setText(trueFlagString);
                    bytesReceived.setText(sensedString);
                    bytesSent.setText(currentActionString);
                    cycleTime.setText(String.format("%4.3f hz", hz));
                }
            });
        } else {
            Log.i(TAG, "Activity was null!");
            Log.i(TAG, "true flags: " + trueFlagString);
            Log.i(TAG, "bytes received: " + sensedString);
            Log.i(TAG, "bytes sent: " + currentActionString);
            Log.i(TAG, "cycle time: " + hz);
        }
    }

    private void quit(final String message) {
        run = false;
        Log.i(TAG,"Quitting: " + message);
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {   //https://stackoverflow.com/questions/31843577/runonuithread-method-in-fragment
                @Override
                public void run() {
                    bytesSent.setText(message);
                }
            });
        }
    }

    private void modePopulator() throws ItemNotFoundException {
        for (Mode m : modes) {
            m.setNextLayer(modePopulatorHelper(m, tables));
        }

        transitionTableModePopulator();
    }

    private TransitionTable modePopulatorHelper(Mode mode, ArrayList<TransitionTable> tables) throws ItemNotFoundException {
        for (TransitionTable t : tables) {
            if (mode.getTtName().equals(t.getName())) {
                return t;
            }
        }
        Log.e(TAG, "Can't find table '" + mode.getTtName() + "'");
        throw new ItemNotFoundException("Table " + mode.getTtName());
    }

    private void transitionTableModePopulator() throws ItemNotFoundException {
        for (TransitionTable t : tables) {
            Log.i(TAG, "Table name: " + t.getName() + "; size " + t.getNumRows());
            for (int i = 0; i < t.getNumRows(); i++) {
                Log.i(TAG, "Mode " + i + ": '" + t.getMode(i) + "'");
            }
            for (int i = 0; i < t.getNumRows(); i++) {
                String modeName = t.getMode(i).getName();
                try {
                    t.setMode(i, findMode(modeName, t));
                } catch (ItemNotFoundException infe) {
                    throw new ItemNotFoundException("Mode '" + "' in table " + t.getName());
                }
            }
        }
    }

    private Mode findMode(String name, TransitionTable table) throws ItemNotFoundException {
        for (Mode m : modes) {
            if (m.getName().equals(name)) {
                return m;
            }
        }
        Log.e("ArduinoRunnerFragment", "Can't find mode '" + name + "'");
        throw new ItemNotFoundException("Mode " + name);

    }

    private TreeSet<Flag> findTrueFlags(SensedValues sensed, TransitionTable currentTable) throws ItemNotFoundException {
        TreeSet<Flag> trueFlags = new TreeSet<Flag>();
        for (int i = 0; i < currentTable.getNumRows(); i++) {
            currentTable.getFlag(i).updateCondition(sensed);
            if (currentTable.getFlag(i).isTrue()) {
                trueFlags.add(currentTable.getFlag(i));
            }
        }
        return trueFlags;
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat flipped = Util.flipImage(inputFrame);
        lastSensed.setLastImage(flipped);
        return flipped;
    }
}