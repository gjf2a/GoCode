package com.stack.gocode.primaryFragments;

import android.app.Fragment;
import android.content.Context;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.stack.gocode.R;
import com.stack.gocode.communications.ArduinoTalker;
import com.stack.gocode.communications.TalkerListener;

import static android.content.ContentValues.TAG;

public class DebuggerFragment extends Fragment implements TalkerListener { //https://www.google.com/search?q=setting+up+a+navigation+activity+android&rlz=1C1AVNE_enUS678US678&oq=setting+up+a+navigation+activity+android&aqs=chrome..69i57.11803j0j7&sourceid=chrome&ie=UTF-8#kpvalbx=1
    View myView;
    private ArduinoTalker talker;

    private SeekBar seekBarLeft, seekBarRight;
    private TextView sentBytes, recievedData, errorBox;
    private EditText editTextLeft, editTextRight, editTextP, editTextI, editTextD;
    private Button connect, sendTextBoxes, tunePIDloop, sendSliders;

    private final static int seekBarOffset = 127;
    private final static int numBytesReceiving = 3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.debugger, container, false);

        seekBarLeft   = myView.getRootView().findViewById(R.id.seekBar);
        seekBarRight  = myView.getRootView().findViewById(R.id.seekBar2);
        editTextLeft  = myView.getRootView().findViewById(R.id.editText);
        editTextRight = myView.getRootView().findViewById(R.id.editText2);
        editTextP = myView.getRootView().findViewById(R.id.editText3);
        editTextI = myView.getRootView().findViewById(R.id.editText4);
        editTextD = myView.getRootView().findViewById(R.id.editText5);
        sentBytes = myView.getRootView().findViewById(R.id.textView2);
        recievedData = myView.getRootView().findViewById(R.id.textView3);
        errorBox = myView.getRootView().findViewById(R.id.textView4);

        seekBarLeft.setProgress(seekBarOffset);
        seekBarRight.setProgress(seekBarOffset);

        connect = myView.getRootView().findViewById(R.id.button);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceCheckHandler(v);
            }
        });

        sendTextBoxes = myView.getRootView().findViewById(R.id.button2);
        sendTextBoxes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go(v);
            }
        });

        tunePIDloop = myView.getRootView().findViewById(R.id.button3);
        tunePIDloop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tunePID(v);
            }
        });

        sendSliders = myView.getRootView().findViewById(R.id.button4);
        sendSliders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSliders(v);
            }
        });

        deviceCheck();

        return myView;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (talker != null) {
            //talker.close();
        }
    }

    public void deviceCheckHandler(View view) {
        deviceCheck();
    }

    private void deviceCheck() {
        if (talker == null || !talker.connected()) {
            talker = new ArduinoTalker((UsbManager) this.getActivity().getSystemService(Context.USB_SERVICE));
            if (talker.connected()) {
                errorBox.setText("Device is good to go!");
            } else {
                errorBox.setText(talker.getStatusMessage());
            }
        } else {
            errorBox.setText(talker.getStatusMessage());
        }
    }

    public void tunePID(View view) {
        String pidTune = "P";
        String holder  = getPorIorD(editTextP);
        if (!holder.equals("error")) {
            pidTune += holder + "I";
            holder = getPorIorD(editTextI);
            if (!holder.equals("error")) {
                pidTune += holder +"D";
                holder = getPorIorD(editTextD);
                if (!holder.equals("error")) {
                    pidTune += holder;
                    sendBytes(getByteArray(pidTune.toCharArray()));
                }
            }
        }

    }

    private byte[] getByteArray(char[] carray) {
        byte[] barray = new byte[carray.length ];
        for (int i = 0; i < carray.length; i++)  {
            barray[i] = (byte) carray[i];
        }
        return barray;
    }

    private String getPorIorD(TextView tv) {
        setNullToZero(tv);
        String input = tv.getText().toString();

        try {

            Double tuneNum = Double.parseDouble(input);
            input = String.format("%7.5f", tuneNum);
            input = ensureFloatStringLength(input);

        } catch (NumberFormatException e) {
            selfUpdateError("P.I.D. inputs must be in the form of decimal numbers.");
            input = "error";
        }
        return input;
    }

    private String ensureFloatStringLength(String floater) {
        while (floater.length() < 8) {
            floater += '0';
        }
        return floater;
    }
    public void go(View view) {

        setNullToZero(editTextLeft);
        setNullToZero(editTextRight);
        int tempL = (int) Integer.valueOf(editTextLeft.getText().toString());
        int tempR = (int) Integer.valueOf(editTextRight.getText().toString());

        if (tempL <= 127 && tempL >= -127 && tempR <= 127 && tempR >= -127) {
            formatBytes('D', (byte)tempL, (byte)tempR, (byte) 0, (byte) 0);
        } else {
            selfUpdateError("Engine input must be an integer between -127 and 127 (inclusive).");
        }
    }

    private void setNullToZero(TextView tv) {
        if (tv.getText().toString().isEmpty()) {
            tv.setText("0");
        }
    }

    public void sendSliders(View view) {
        formatBytes('D', (byte) (seekBarLeft.getProgress() - seekBarOffset), (byte) (seekBarRight.getProgress() - seekBarOffset), (byte) 0, (byte) 0);
    }

    private void formatBytes(char instructionLabel, byte zero, byte one, byte two, byte three) {
        byte [] byteArray = new byte[5];
        byteArray[0] = (byte) instructionLabel;
        byteArray[1] = zero;
        byteArray[2] = one;
        byteArray[3] = two;
        byteArray[4] = three;
        sendBytes(byteArray);
    }

    private void sendBytes(byte[] b) {
        talker.send(b);
    }

    @Override
    public void sendComplete(int status) {
        update(sentBytes);
        talker.receive(numBytesReceiving);
    }

    @Override
    public void receiveComplete(byte[] received) {
        update(recievedData);
    }

    @Override
    public void error() {
        update(errorBox);
    }

    private void update(final TextView tv) {
        getActivity().runOnUiThread(new Runnable() {public void run(){
            tv.setText(talker.getStatusMessage());
        }});
    }

    private void selfUpdateError(final String errorMessage) {
        getActivity().runOnUiThread(new Runnable() {public void run(){
            errorBox.setText("Error: " + errorMessage);
        }});
    }
}