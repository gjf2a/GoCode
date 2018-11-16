package com.stack.gocode.primaryFragments;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.stack.gocode.R;
import com.stack.gocode.Util;
import com.stack.gocode.localData.DatabaseHelper;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.util.ArrayList;

/**
 * Created by gabriel on 11/7/18.
 */

public class VideoRecordingFragment extends Fragment implements CameraBridgeViewBase.CvCameraViewListener2 {
    public final static String TAG = VideoRecordingFragment.class.getSimpleName();

    private View myView;

    private Mat lastImage = null;

    private Button capture, makeNewLabel, renamer;
    private Spinner labelChooser;
    private ArrayList<String> labels;
    private EditText renamed;

    private CheckBox viewStored;
    private Button prevStored, nextStored;
    private int storedImageIndex = 0;

    private CameraBridgeViewBase mOpenCvCameraView;
    private BaseLoaderCallback mLoaderCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.video_recorder, container, false);

        mOpenCvCameraView = (CameraBridgeViewBase) myView.findViewById(R.id.video_record_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        mLoaderCallback = new BaseLoaderCallback(myView.getContext()) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS:
                    {
                        Log.i(TAG, "OpenCV loaded successfully");
                        mOpenCvCameraView.enableView();
                        DatabaseHelper db = new DatabaseHelper(getActivity());
                        db.setupImages(myView.getContext());
                        makeArrayAdapterFrom(db.getAllLabels());
                    } break;
                    default:
                    {
                        super.onManagerConnected(status);
                    } break;
                }
            }
        };

        capture = myView.findViewById(R.id.image_capture_button);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lastImage != null) {
                    DatabaseHelper db = new DatabaseHelper(getActivity());
                    if (db.imagesReady()) {
                        db.addImage(labelChooser.getSelectedItem().toString(), lastImage);
                        Log.i(TAG, "Image recorded; label " + labelChooser.getSelectedItem().toString());
                        lastImage = new Mat(lastImage.height(), lastImage.width(), lastImage.type(), new Scalar(255, 0, 0));
                    }
                }
            }
        });

        DatabaseHelper db = new DatabaseHelper(getActivity());
        labelChooser = myView.findViewById(R.id.image_label_spinner);

        makeNewLabel = myView.findViewById(R.id.new_label_button);
        makeNewLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper db = new DatabaseHelper(getActivity());
                String newLabel = db.createNewLabel();
                makeArrayAdapterFrom(db.getAllLabels());
            }
        });

        renamed = myView.findViewById(R.id.new_label_name);

        renamer = myView.findViewById(R.id.label_rename_button);
        renamer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper db = new DatabaseHelper(getActivity());
                db.updateLabel(labelChooser.getSelectedItem().toString(), renamed.getText().toString());
                makeArrayAdapterFrom(db.getAllLabels());
                renamed.setText("");
            }
        });

        viewStored = myView.findViewById(R.id.video_show_saved);
        prevStored = myView.findViewById(R.id.left_stored_button);
        prevStored.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateStoredDisplay(false);
            }
        });

        nextStored = myView.findViewById(R.id.right_stored_button);
        nextStored.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateStoredDisplay(true);
            }
        });

        return myView;
    }

    private void updateStoredDisplay(boolean forward) {
        viewStored.setChecked(true);
        DatabaseHelper db = new DatabaseHelper(getActivity());
        storedImageIndex = Util.wrap(storedImageIndex, forward ? 1 : -1, db.getNumStoredImages());
    }

    public void makeArrayAdapterFrom(ArrayList<String> labels) {
        this.labels = labels;
        ArrayAdapter<String> labelAdapter = new ArrayAdapter<String>(this.getActivity(), R.layout.spinner_dropdown_item, labels);
        labelAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        labelChooser.setAdapter(labelAdapter);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        // Rotation from: https://stackoverflow.com/questions/12949793/rotate-videocapture-in-opencv-on-android

        DatabaseHelper db = new DatabaseHelper(getActivity());
        if (db.getNumStoredImages() > 0 && viewStored.isChecked()) {
            final String label = db.getImageLabel(storedImageIndex);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    labelChooser.setSelection(labels.indexOf(label));
                }
            });
            return db.getImage(storedImageIndex);
        } else {
            lastImage = Util.flipImage(inputFrame);
            return lastImage;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, myView.getContext(), mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
}
