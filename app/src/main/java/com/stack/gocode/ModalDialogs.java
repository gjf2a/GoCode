package com.stack.gocode;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by gabriel on 10/24/18.
 */

public class ModalDialogs {
    // Adapted from: https://stackoverflow.com/questions/26097513/android-simple-alert-dialog
    public static void notifyException(Context context, Exception exc) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exc.printStackTrace(pw);
        String sStackTrace = sw.toString(); // stack trace as a string
        Log.e("ModalDialogs", sStackTrace);
        notifyProblem(context, exc.getMessage());
    }

    public static void notifyProblem(Context context, String problem) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage(problem);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }


}
