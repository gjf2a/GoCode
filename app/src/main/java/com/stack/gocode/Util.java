package com.stack.gocode;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.stack.gocode.localData.Named;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by gabriel on 10/30/18.
 */

public class Util {
    public static final String TAG = Util.class.getSimpleName();

    public static ArrayAdapter<String> setUpSpinner(Context context, Spinner spinner, ArrayList<? extends Named> src, String targetName) {
        ArrayList<String> names = new ArrayList<String>();
        for (Named n : src) {
            names.add(n.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_item, names);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if (!targetName.isEmpty()) {
            spinner.setSelection(names.indexOf(targetName));
        }
        return adapter;
    }

    public static int wrap(int index, int incr, int size) {
        return (index + incr + size) % size;
    }

    public static Mat flipImage(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        // Rotation from: https://stackoverflow.com/questions/12949793/rotate-videocapture-in-opencv-on-android
        Mat image = inputFrame.rgba();
        Mat temp = image.t();
        Core.flip(temp, image, 1);
        temp.release(); // http://answers.opencv.org/question/77482/android-memory-leak-on-camera-rotation/
        return image;
    }

    public static String stackTrace2String(Exception exc) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exc.printStackTrace(pw);
        return sw.toString();
    }

    public ArrayAdapter<String> makeSpinnerAdapter(Activity activity, ArrayList<String> names, Spinner spinner) {
        ArrayAdapter<String> adapterS = new ArrayAdapter<String>(activity, R.layout.spinner_dropdown_item, names);
        adapterS.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapterS);
        return adapterS;
    }

    public static String file2String(String filename) {
        try {
            File f = new File(filename);
            Scanner s = new Scanner(f);
            StringBuilder sb = new StringBuilder();
            while (s.hasNextLine()) {
                sb.append(s.nextLine() + "\n");
            }
            s.close();
            return sb.toString();
        } catch (IOException ioe) {
            Log.i(TAG, stackTrace2String(ioe));
            return ioe.getMessage();
        }
    }
}
