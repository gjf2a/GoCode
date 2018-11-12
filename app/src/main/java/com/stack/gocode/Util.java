package com.stack.gocode;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.stack.gocode.localData.Named;

import java.util.ArrayList;

/**
 * Created by gabriel on 10/30/18.
 */

public class Util {
    public static void setUpSpinner(Context context, Spinner spinner, int pos, ArrayList<? extends Named> src, String targetName) {
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
    }

    public static int wrap(int index, int incr, int size) {
        return (index + incr + size) % size;
    }
}
