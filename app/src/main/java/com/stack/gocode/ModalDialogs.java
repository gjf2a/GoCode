package com.stack.gocode;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

/**
 * Created by gabriel on 10/24/18.
 */

public class ModalDialogs {
    // Adapted from: https://stackoverflow.com/questions/26097513/android-simple-alert-dialog
    public static void notifyException(Context context, Exception exc) {
        Log.e("ModalDialogs", exc.getStackTrace().toString());
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
